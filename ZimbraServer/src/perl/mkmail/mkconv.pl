#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# Version: MPL 1.1
# 
# The contents of this file are subject to the Mozilla Public License
# Version 1.1 ("License"); you may not use this file except in
# compliance with the License. You may obtain a copy of the License at
# http://www.zimbra.com/license
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
# the License for the specific language governing rights and limitations
# under the License.
# 
# The Original Code is: Zimbra Collaboration Suite Server.
# 
# The Initial Developer of the Original Code is Zimbra, Inc.
# Portions created by Zimbra are Copyright (C) 2004, 2005, 2006 Zimbra, Inc.
# All Rights Reserved.
# 
# Contributor(s):
# 
# ***** END LICENSE BLOCK *****
# 

use strict;
use Statistics::Basic::Mean;
use Statistics::Basic::StdDev;
use Getopt::Long;
use Date::Format;

#
# Mail-generation perl script, attempts to generate random
# "real-looking" email It uses three data files to get input data:
#
#  num_words.txt is a set of (number_words number_bytes) sample email
#  data
#    -- this info is used to determine the size of the generated email
#       messages
#
#  wordfreq.txt is a set of (frequency, word) pairs generated from
#  sample email data
#    -- this info is used to generate the body of the email messages
#
#  headers.txt contains:
#    -- first line: total # email messages processed
#   
#    -- ( #occurences, header ) for each header in the processed email
#       set a list of indented possible header values
#
# Header generation is somehwat complicated right now:
#
#    For subject, we pick a number and variance of words, and generate
#        random text.
#    For date, we pick a random date between 0 and 2 years in the past
#    For content-type, we currently hardcode to ascii
#    For to, we hardcode user$N@example.zimbra.com 
#    
# TODOs:
#    -- attachments
#

my ($opt_help, $opt_outdir, $opt_num_users, $opt_num_convs, $opt_msgs_per_dir);

GetOptions("h" => \$opt_help,
	   "o=s" => \$opt_outdir,
	   "n=i" => \$opt_msgs_per_dir,
	   "u=i" => \$opt_num_users,
	   "c=i" => \$opt_num_convs) || die;

sub usage {
    my $arg = shift;
    print "Error: $arg\n" if ($arg);
    print "Usage: mkconv.pl -o outdir -n msgs_per_subdir " .
	"-u num_users -c conv_per_user\n";
    exit 1;
}

usage("invalid outdir") if (! -d $opt_outdir);
usage("invalid num_convs") if ($opt_num_convs < 1);
usage("invalid num_users") if ($opt_num_users < 1);
usage("invalid msgs_per_dir") if ($opt_msgs_per_dir < 1);

#
# the mean and std deviation of # words and bytes in a message.
#
my $avgBytes;
my $avgWords;
my $stdDevBytes;
my $stdDevWords;

my @wrdarray;
my $numWords;

my %header_values;
my %header_pct;

load_stats();
load_wordfreq();
load_headers();

my $convAvg = 8;
my $convStdDev = 4;

#
# main: generate the messages!
#
my $total_msgs = 0;
my $total_convs = $opt_num_users * $opt_num_convs;

for (my $convs = 0; $convs < $total_convs;) {
    my $msgs_in_conv = dist_rand_int($convAvg, $convStdDev);
    next if ($msgs_in_conv < 0);
    
    my $user = "user" . ($convs % $opt_num_users) . "\@example.zimbra.com";
    my $subject = get_subject();

    for (my $i = 0; $i <= $msgs_in_conv; $i++) {
	my $outdir = $opt_outdir . "/" . int($total_msgs / $opt_msgs_per_dir);
	my $outfile = $outdir . "/" . ($total_msgs % $opt_msgs_per_dir);
	if (! -d $outdir) {
	    mkdir $outdir || die "Couldn't mkdir $outdir";
	}
	gen_mail($subject, $user, $outfile);
	if ($i == 0) {
	    $subject = "Re: " . $subject;
	}
	$total_msgs++;
    }
    $convs++;
}
print "Generated $total_msgs messages in $total_convs conversations\n";
exit 0;

    
# 
# Subroutines
#
sub gen_mail_dummy($$$) {
    my ($subject, $user, $outfile) = @_;
    print "subj=$subject\n";
    print "user=$user\n";
    print "outf=$outfile\n";
}

sub gen_mail($$$)
{
    my ($subject, $user, $outfile) = @_;

    open(MOUT, "> $outfile") or die "Couldn't write to $outfile";
    add_headers($user);
    print(MOUT "subject: $subject\n");
    my $msg = get_random_body();
    print(MOUT "\n\n$msg\n");
    close(MOUT);
}

# load the headers.txt file and header statistics
sub load_headers
{
    open(IN, "<headers.txt") or die "Couldn't read from headers.txt";

    my $num_msgs = <IN>;

    my $cur_header;

    while(<IN>) {
        if (/^([0-9]+)\s+(\S+)/) {
            $cur_header = $2;
            $header_pct{$2} = $1 / $num_msgs;
        } else {
            if (/\t(.*)/) {
                push @{ $header_values{$cur_header} }, $1;
            }
        }
    }
    close(IN);
    $header_pct{"subject"} = undef;  # REMIND remove subject from the data file
}

# add the headers to the current email message
sub add_headers($)
{
    my ($user) = @_;
    my $num_hdrs = 0;
    foreach my $hdr (keys %header_pct) 
    {
        my $num = $header_pct{$hdr};
        if (rand() <= $num) {
            $num_hdrs++;
            my $val = add_header($hdr, $user);
            print(MOUT "$hdr: $val\n");
        }
    }
}

sub get_subject() {
    my $num = dist_rand_int(10,4);
    return get_random_words($num);
}

# add an individual header to the message
sub add_header($$)
{
    my ($hdrName, $user) = @_;

    if ($hdrName eq "date") {
        return random_date_str();        
    } elsif ($hdrName eq "content-type") {
        return "text/plain; charset=US-ASCII";
    } else {
        my @values = @{ $header_values{$hdrName} };
        my $numValues = length(@values);

        if ($hdrName eq "to") {
            return "$user, " . @values[rand_int(0, $numValues-1)];
        } else {
            return @values[rand_int(0, $numValues-1)];
        }
    }
}

# get a random message body (length determined by the statistics
# in num_words.txt)
sub get_random_body()
{
    return get_random_words(get_rand_num_body_words());
}

# get a string of random words following the frequency in wordfreq.txt
sub get_random_words($)
{
    my ($num) = @_;
    my $ret;
    
    for (; $num > 0; $num--)
    {
        $ret .= get_random_word() . " ";
    }
    return $ret;
}

# load num_words.txt
sub load_stats()
{
    open(STATS, "<num_words.txt") or die "Missing num_words.txt file\n";

    my @numWords;
    my @numBytes;

    my $wordSum = 0;
    my $count = 0;

    while(<STATS>) {        
        if (/([0-9]+)\s+([0-9]+)/) {
            push(@numWords, $1);
            $wordSum += $1;
            $count++;
            push(@numBytes, $2);
        }
    }

    $avgWords = Statistics::Basic::Mean->new(\@numWords)->query;
    $stdDevWords = Statistics::Basic::StdDev->new(\@numWords)->query;
    
    $avgBytes = Statistics::Basic::Mean->new(\@numBytes)->query;
    $stdDevBytes = Statistics::Basic::StdDev->new(\@numBytes)->query;

    close(STATS);
}

# load our word frequency database
sub load_wordfreq()
{
    open(FREQ, "<wordfreq.txt") or die "Missing word frequency file\n";
    
    while(<FREQ>) {
        if (/\s*([0-9]+)\s+(\S+)/) {
            my $i = $1;
            for (my $i = $1 ; $i > 0; $i--) {
                push(@wrdarray, $2);
            }
        }
    }
    
    $numWords = @wrdarray;
    close(FREQ);
}

# This subroutine generates random numbers that are normally
# distributed, with a standard deviation of 1 and a mean of 0.
#
# http://secu.zzu.edu.cn/book/Perl/Perl%20Bookshelf%20%5B3rd%20Ed%5D/cookbook/ch02_11.htm
sub gaussian_rand() {
    my ($u1, $u2);  # uniformly distributed random numbers
    my $w;          # variance, then a weight
    my ($g1, $g2);  # gaussian-distributed numbers

    do {
        $u1 = 2 * rand() - 1;
        $u2 = 2 * rand() - 1;
        $w = $u1*$u1 + $u2*$u2;
    } while ( $w >= 1 );

    $w = sqrt( (-2 * log($w))  / $w );
    $g2 = $u1 * $w;
    $g1 = $u2 * $w;
    # return both if wanted, else just one
    return wantarray ? ($g1, $g2) : $g1;
}

# returns a random # on a normal distribution centered around AVG with
# std dev DEV
sub dist_rand_int($$)
{
    my ($mean, $sdev) = @_;
    return int ((gaussian_rand() * $sdev + $mean));
}

# pick a random number of words to be in the message body, following
# the statistical pattern in numwords.txt
sub get_rand_num_body_words()
{
    my $ret = dist_rand_int($avgWords, $stdDevWords);
    if ($ret <= 0) {
        $ret = 1;
    }
    return $ret;
}

# pick one random word out of the word dictionary
sub get_random_word()
{
    return @wrdarray[rand_int(0,$numWords-1)];
}

# generate a random date string within the last 2 years
sub random_date_str()
{
    my $when = time() - rand_int(10,60*60*24*365*2);
    my @lt = localtime($when);
    return strftime("%a, %d %b %Y %H:%M:%S %z", @lt);
}


# return a random int between min and max INCLUSIVE
sub rand_int($$)
{
    my ($min, $max) = @_;
    my $ret = int rand($max-$min+1) + $min;
    return $ret;
}

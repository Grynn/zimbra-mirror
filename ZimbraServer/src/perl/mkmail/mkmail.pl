use strict;
use Statistics::Basic::Mean;
use Statistics::Basic::StdDev;
use Getopt::Std;
 
######################################################################
#
# Mail-generation perl script
#
# This simple perl script attempts to generate random "real-looking" email
#
# It uses three data files to get input data:
#
#    num_words.txt is a set of (number_words number_bytes) sample email data.
#      -- this info is used to determine the size of the generated email messages
#
#    wordfreq.txt is a set of (frequency, word) pairs generated from sample email data.
#      -- this info is used to generate the body of the email messages
#
#    headers.txt containts:
#      -- first line: total # email messages processed
#      -- ( #occurences, header ) for each header in the processed email set
#             a list of indented possible header values
#
#
# Header generation is somehwat complicated right now
#    For subject, we pick a number and variance of words, and generate random text.
#    For date, we pick a random date between 0 and 2 years in the past
#    For content-type, we currently hardcode to ascii
#    For to, we hardcode user1@example.zimbra.com 
#    
#
#
# TODOs:
#    -- attachments
#    -- generate "conversations" instead of just random subjects
#

my @wrdarray;
my $numWords;
my $avgBytesWord;

use vars qw/ %options /;

%options=();
getopts('b:o:n:', \%options);

my $num_mails = $options{n} if defined $options{n};
if ($num_mails < 1) {
    print("USAGE: mkmail.pl [-b avg-bytes-per-msg] [-o output_dir] -n NUMBER\n");
    exit(1);
}

my $out_dir = "out";
my $opt_avg_bytes;
$opt_avg_bytes = $options{b} if defined $options{b};
$out_dir = $options{o} if defined $options{o};

#
# the mean and std deviation of # words and bytes in a message..
#
my $avgBytes;
my $avgWords;
my $stdDevBytes;
my $stdDevWords;

my %header_values;
my %header_pct;

load_stats();
load_wordfreq();
load_headers();

#print("Loaded $numWords words avg of $avgBytesWord letters each\n");
#print("StdDev words is $stdDevWords\n");

if (defined $opt_avg_bytes) {
    $avgWords = $opt_avg_bytes / $avgBytesWord;
    $stdDevWords = 1;
    print("Creating messages with an average of $opt_avg_bytes bytes ($avgWords words) in message body\n");
} else {
    my $defBytes = $avgWords * $avgBytesWord;
    print("Defaulting to avg of $avgWords words per message ($defBytes bytes)\n");
}


# generate the messages!
for (my $i = 0; $i < $num_mails; $i++) {
    gen_mail($out_dir, $i);
    if (($i+1)%100 == 0) {
        my $n = $i+1;
        print("Generated mail $n\n");
    }
}

exit(0);

######################################################################
# 
# Subroutines
#
######################################################################

# generates an email (param: message #, message created as out/#)
sub gen_mail
{
    my ($out_dir, $mnum) = (@_);

    my $mname = "$out_dir\\".$mnum;
    open(MOUT, ">$mname") or die "Couldn't write to $mname";
    add_headers();
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
}

# add the headers to the current email message
sub add_headers
{
    my $num_hdrs = 0;
    foreach my $hdr (keys %header_pct) 
    {
        my $num = $header_pct{$hdr};
        if (rand() <= $num) {
            $num_hdrs++;
            my $val = add_header($hdr);
            print(MOUT "$hdr: $val\n");
        }
    }
}

# add an individual header to the message
sub add_header
{
    my ($hdrName) = (@_);

    if ($hdrName eq "subject") {
        my $num = dist_rand_int(10,4);
        return get_random_words($num);
        
    } elsif ($hdrName eq "date") {
        return random_date_str();
            
    } elsif ($hdrName eq "content-type") {
        return "text/plain; charset=US-ASCII";
    } else {
        my @values = @{ $header_values{$hdrName} };
        my $numValues = length(@values);

        if ($hdrName eq "to") {
            return "user1\@example.zimbra.com, " . @values[rand_int(0, $numValues-1)];
        } else {
            return @values[rand_int(0, $numValues-1)];
        }
    }
}


# get a random message body (length determined by the statistics
# in num_words.txt)
sub get_random_body
{
    return get_random_words(get_rand_num_body_words());
}

# get a string of random words following the frequency in wordfreq.txt
sub get_random_words
{
    my ($num) = (@_);
    
    my $ret;
    
    for (; $num > 0; $num--)
    {
        $ret .= get_random_word() . " ";
    }
    return $ret;
    
    
}

# load num_words.txt
sub load_stats
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
sub load_wordfreq
{
    open(FREQ, "<wordfreq.txt") or die "Missing word frequency file\n";

    my $wrdbytes = 0;
    
    while(<FREQ>) {
        if (/\s*([0-9]+)\s+(\S+)/) {
            my $i = $1;
            for (my $i = $1 ; $i > 0; $i--) {
                $wrdbytes += length($2);
                push(@wrdarray, $2);
            }
        }
    }
    
    $numWords = @wrdarray;
    $avgBytesWord = $wrdbytes / $numWords;
    close(FREQ);
}


#This subroutine generates random numbers that are normally
#distributed, with a standard deviation of 1 and a mean of 0.
#
# http://secu.zzu.edu.cn/book/Perl/Perl%20Bookshelf%20%5B3rd%20Ed%5D/cookbook/ch02_11.htm
#
sub gaussian_rand {
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
sub dist_rand_int
{
    my ($mean, $sdev) = (@_);
    
    return int ((gaussian_rand() * $sdev + $mean));
}

# pick a random number of words to be in the message body, following
# the statistical pattern in numwords.txt
sub get_rand_num_body_words
{
    my $ret = dist_rand_int($avgWords, $stdDevWords);
    if ($ret <= 0) {
        $ret = 1;
    }
    return $ret;
}

# pick one random word out of the word dictionary
sub get_random_word
{
    return @wrdarray[rand_int(0,$numWords-1)];
}

# generate a random date string within the last 2 years
sub random_date_str
{
	use POSIX qw(strftime);
    my $when = time() - rand_int(10,60*60*24*365*2);
    my $lt = strftime "%a, %d %b %Y %H:%M:%S", localtime($when);
    return $lt;
}


# return a random int between min and max INCLUSIVE
sub rand_int
{
    my ($min, $max) = (@_);
    my $ret = int rand($max-$min+1) + $min;
    return $ret;
}

use strict;
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
# Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
# All Rights Reserved.
# 
# Contributor(s):
# 
# ***** END LICENSE BLOCK *****
# 
use Mail::MboxParser;
use URI;

######################################################################
#
# Perl script to parse unix mbox files and generate email frequency
# statistics from them.
#
# Look at mkmail.pl for a description of the files this script generates.
#



my $mailbox_name = "in"; # reads from in.mbx in the current directory
headers();
mbox_stats();
mbox_to_all();
all_to_freq();
exit(0);


######################################################################
# 
# Subroutines
#
######################################################################

sub munge_uri
{
    my $url = URI->new(shift());
    
    my $newHost;
    
    if ($url->host()=~/([\w_]+)\.([\w_]+)\.([\w_]+)/) {
        if ($3) {
            $newHost .= $1 . "." . munge($2) . "." . $3;
        } else {
            $newHost .= munge($1) . "." . munge($2);
        }
    }
    
    my $np;
    foreach my $c (split(//, $url->path())) {
        if (ord($c) >= ord('a') && ord($c) <= ord('z')) {
            $c = chr(ord($c)+1);
            if (ord($c) > ord('z')) {
                $c = 'a';
            }
        }
        if (ord($c) >= ord('A') && ord($c) <= ord('Z')) {
            $c = chr(ord($c)+1);
            if (ord($c) > ord('Z')) {
                $c = 'A';
            }
        }
        $np .= $c;
    }
#    print("NewPath = $np\n");
#    print("http://$newHost$np\n");
    return "http://".$newHost.$np;
}

sub munge
{
    my $tok = shift;
    my $nt;
    
    foreach my $c (split(//, lc $tok)) {
        if (ord($c) >= ord('a') && ord($c) <= ord('z')) {
            $c = ord($c) + 1;
            if ($c > ord('z')) {
                $c = ord('a');
            }
            $c = chr($c);
        }
        $nt = $nt . $c;
    }        
    return  $nt;
}

sub headers
{
    my $parseropts = {
        enable_cache    => 1,
        enable_grep     => 1,
        cache_file_name => 'mail/cache-file',
    };

    open(OUT, ">headers.txt") or die "Couldn't write to headers.txt";
    
    my %seen_headers = ();
    my %header_values = ();
    
    my $mb = Mail::MboxParser->new($mailbox_name,
                                   decode     => 'ALL',
                                   parseropts => $parseropts);
    
    my $msg_num = 0;
    my $count = 0;
    my $body;
    while (my $msg = $mb->next_message) {
        $count++;
        my $headersRef = $msg->header;
        my @headerKeys = keys %{$headersRef};
        foreach my $key (@headerKeys) {
            $seen_headers{$key}++;
            my $val = ${$headersRef}{$key};
            if (($key ne "content-type") &&
                ($key ne "content-transfer-encoding") &&
                ($key ne "date") &&
                ($key ne "to")
                )
            {
                $val = munge($val);
            }
            push @{ $header_values{$key} }, $val;
        }
        $msg_num++;
        if ($msg_num % 100 == 0) {
            print("On Message $msg_num\n");
        }
    }

    print(OUT "$msg_num\n");
    
    foreach my $hdr (keys %seen_headers) {
        my $num = $seen_headers{$hdr};
        printf(OUT "%d %s\n", $num, $hdr);
        if ($hdr eq "subject") {
            foreach my $val (@{ $header_values{$hdr} })
            {
                my $numWords = 0;
                while ($val=~/(\w[\w\-:.\/]*)/g) {
                    $numWords++;
                }
                print(OUT "\t$numWords\n");
            }
        } else {
            foreach my $val (@{ $header_values{$hdr} }) {
                print(OUT "\t$val\n");
            }
        }
    }
    
    close(OUT);
}


# strips the headers and writes all the message bodies into a big file, "all.txt"
sub mbox_to_all()
{
    my $parseropts = {
        enable_cache    => 1,
        enable_grep     => 1,
        cache_file_name => 'mail/cache-file',
    };
    my $mb = Mail::MboxParser->new($mailbox_name,
                                   decode     => 'BODY',
                                   parseropts => $parseropts);
    
    open(OUT, ">all.txt") or die "Couldn't write to all.txt";

    my $msg_num = 0;
    my $count = 0;
    my $body;
    while (my $msg = $mb->next_message) {
        $count++;
        print OUT  $msg->body;
        $msg_num++;
        if ($msg_num % 100 == 0) {
            print("On Message $msg_num\n");
        }
    }
    close(OUT);
}

sub mbox_stats()
{
    my $parseropts = {
        enable_cache    => 1,
        enable_grep     => 1,
        cache_file_name => 'mail/cache-file',
    };
    my $mb = Mail::MboxParser->new($mailbox_name,
                                   decode     => 'BODY',
                                   parseropts => $parseropts);
    
    open(OUT, ">num_words.txt") or die "Couldn't write to num_words.txt";
    
    my $msg_num = 0;
    my $count = 0;
    my $body;
    while (my $msg = $mb->next_message) {
        my $body = $msg->body;
        my $numWords = 0;
        while ($body=~/(\w[\w\-:.\/]*)/g) {
#            print("word $numWords - \"$1\"\n");
            $numWords++;
        }
        my $numBytes = length($body);
        if ($numWords > 0) {
            print OUT "$numWords $numBytes\n";
        }
        if ($msg_num % 100 == 0) {
            print("Stats for Message $msg_num\n");
        }
        $msg_num++;
    }
    close(OUT);
}

# parses "all.txt" and writes out word frequencies
sub all_to_freq()
{
    open(IN, "<all.txt") or die "Couldn't read from all.txt";
    open(OUT, ">wordfreq.txt") or die "Couldn't write to wordfreq.txt";
    
    my %seen = ();
    my $count = 0;
    my $body;
    while (<IN>) {
        $count++;
        while (/(\w[\w\-:.\/]*)/g) {
            my $tok = $1;
            my $ending;
            
            if ($tok=~/http:\/\/.*/) {
                $tok = munge_uri($tok);
                print("URI munged to $tok\n");
            }
            $seen{lc $tok}++;
        }
        if ($count % 10000 == 0) {
            print("On line $count\n");
        }
        if ($count > 50000) {
            goto done;
        }
    }

  done:
    
#foreach my $word (keys %seen) {
    foreach my $word (sort { $seen{$b} <=> $seen{$a} } keys %seen) {
        my $toPrint = sprintf("%5d %s\n", $seen{$word}, $word);
        print OUT $toPrint;
    }
    
    close(OUT);
    close(IN);
}


#! /usr/bin/perl -w
# 
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Zimlets
# Copyright (C) 2007, 2009, 2010 Zimbra, Inc.
# 
# The contents of this file are subject to the Zimbra Public License
# Version 1.3 ("License"); you may not use this file except in
# compliance with the License.  You may obtain a copy of the License at
# http://www.zimbra.com/license.
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# ***** END LICENSE BLOCK *****
# 

use strict;

use HTML::TreeBuilder;
use LWP::UserAgent;
use JSON;

my @urls = qw( http://messenger.yahoo.com/emoticons.php
               http://messenger.yahoo.com/hiddenemoticons.php );

my $json = new JSON(pretty => 1);
my @emoticons = ();

sub doUrl {
    my ($url) = @_;
    my $ua = LWP::UserAgent->new;
    my $res = $ua->get($url);
    if ($res->is_success) {
        my $content = $res->content;
        my $root = HTML::TreeBuilder->new;
        $root->parse_content($content);
        $root = $root->elementify();
        my @rows = $root->look_down(_tag => 'tr',
                                    class => qr/^ymsgremotrow[ab]$/);
        foreach my $tr (@rows) {
            my $img = $tr->address('.0.0');

            my $text = $tr->address('.1')->as_text;
            my $src = $img->attr('src');
            my $width = $img->attr('width');
            my $height = $img->attr('height');
            my $alt = $img->attr('alt');

            $alt =~ s/\s*-\s*new!//ig;

            my $obj = { src => $src,
                        text => $text,
                        width => $width,
                        height => $height,
                        alt => $alt };

            push (@emoticons, $obj);
        }
    } else {
        die "Couldn't fetch $url: " . $res->status_line;
    }
}

doUrl($_) foreach @urls;

@emoticons = sort { length($b->{text}) <=> length($a->{text}) } @emoticons;
my %result = ();
my @re_array = ();
foreach my $i (@emoticons) {
    ($i->{regexp} = $i->{text}) =~ s/([]()^?.*|\$\\[])/\\$1/g;
    $i->{regexp} =~ s/"/\\x22/g;
    $i->{regexp} =~ s/'/\\x27/g;
    $i->{regexp} =~ s/\//\\x2f/g;
    $result{lc $i->{text}} = $i;
    push @re_array, $i->{regexp};
}

my $full_regexp = '(' . join('|', @re_array) . ')';
print "Com_Zimbra_YMEmoticons.REGEXP = /$full_regexp/ig;\n\n";
print 'Com_Zimbra_YMEmoticons.SMILEYS = ' . $json->objToJson(\%result) . ";\n";

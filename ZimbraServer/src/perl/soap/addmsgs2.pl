#!/usr/bin/perl -w

#
# Simple SOAP test-harness for the AddMsg API
#

use strict;

use lib '.';

use LWP::UserAgent;

use XmlElement;
use XmlDoc;
use Soap;

my $ACCTNS = "urn:zimbraAccount";
my $MAILNS = "urn:zimbraMail";

#my $url = "http://localhost:7070/service/soap/";
my $url = "http://token:7070/service/soap/";


my $SOAP = $Soap::Soap12;

sub authenticate
{
    my $username = shift();
    
    my $d = new XmlDoc;
    $d->start('AuthRequest', $ACCTNS);
    $d->add('account', undef, { by => "name"}, $username);
    $d->add('password', undef, undef, "mypassWord");
    $d->end();
    
    my $authResponse = $SOAP->invoke($url, $d->root());
#    print "AuthResponse = ".$authResponse->to_string("pretty")."\n";
    my $authToken = $authResponse->find_child('authToken')->content;
#    print "authToken($authToken)\n";
    my $context = $SOAP->liquidContext($authToken);
    return $context;
}

#
# <AddMsgRequest>
#    <m t="{tags}" l="{folder}" >
#    ...
#    </m>
# </AddMsgRequest>
#     
# <AddMsgResponse>
#    <m id="..." />
# </AddMsgResponse>
#

my %msgAttrs;
$msgAttrs{'l'} = "/INBOX";
$msgAttrs{'t'} = "\\unseen";

my $g_msg;

#my $dirname = "c:\\archive_mail\\out";
#opendir (DIR, $dirname) or die "couldn't open $dirname";
#open(IN, "c:\\archive_mail\\archive_32303");
open(IN, "$ARGV[0]");
$g_msg = next_file("user1\@liquidsys.com");
my $context = authenticate("user1\@liquidsys.com");

my $num = 2;

do {

my $d = new XmlDoc;
$d->start('AddMsgRequest', $MAILNS);
$d->start('m', undef, \%msgAttrs, undef);


my $usernum  = $num%100;
my $username = "user$num\@liquidsys.com";
print("Adding mail for user: $username\n");
$g_msg = next_file($username);
$context = authenticate($username);
#print("Message is: $g_msg\n");

#setup_msg();

$d->start('content', undef, undef, $g_msg);

$d->end(); # 'content'
$d->end(); # 'm'
$d->end(); # 'AddMsgRequest'

#print "\nOUTGOING XML:\n-------------\n";
#print $d->to_string("pretty"),"\n";

$num++;
my $response = $SOAP->invoke($url, $d->root(), $context);

#print "\nRESPONSE:\n--------------\n";
#print $response->to_string("pretty");
#if ($num % 20 == 0)  {
    print $response->to_string()."\n";
#}
#$g_msg = next_file();



} while(defined($g_msg));


sub next_file
{
    my $username = shift();
    my $ret = "";
    my $found_to = 0;
    
    while (<IN>) {
        if (/^From \?\?\?\@\?\?\?.*/) {
            return $ret;
        }
        s/tim\@gurge.com/$username/ig;
        s/tim\@symphonatic.com/$username/ig;
        s/tim\@curple.com/$username/ig;
        if (($found_to == 0) && (~/^To:/)) {
            $found_to = 1;
            if (!($_ =~ /$username/)) {
                $_ = "To: user1\@liquidsys.com\n";
            }
        }
        s/\000//g;
        s/\0x1b//g;

        $ret .= $_;
    }
    exit(1);
        
#     my $filename;
#     do {
#         $filename = readdir(DIR);
#     } while(defined($filename) && -d $filename);
# #    print("Opening file $filename\n");
#     if (!defined($filename)) {
#         return;
#     }

#     open(IN, "<c:\\archive_mail\\out\\$filename");
#     my $ret;
#     sysread(IN, $ret, 99999999);
#     close(IN);
#     $ret =~ s/^From \?\?\?\@\?\?\?.*\n//;
#     $ret =~ s/\r\n/\n/g;
#     return $ret;
}


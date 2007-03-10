#!/usr/bin/perl
use strict;
use warnings;
use lib '.';

use LWP::UserAgent;
use Getopt::Long;
use XmlDoc;
use Soap;
use ZimbraSoapTest;

my $ACCTNS = "urn:zimbraAdmin";
my $MAILNS = "urn:zimbraAdmin";

# If you're using ActivePerl, you'll need to go and install the Crypt::SSLeay
# module for htps: to work...
#
#         ppm install http://theoryx5.uwinnipeg.ca/ppms/Crypt-SSLeay.ppd
#
# specific to this app
my ($waitSet);

#standard options
my ($user, $pw, $host, $help); #standard
my ($name, $value);
GetOptions("u|user=s" => \$user,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           # add specific params below:
           "w=s" => \$waitSet,
          );

if (!defined($user) || defined($help) || !defined($waitSet)) {
  my $usage = <<END_OF_USAGE;
    
USAGE: $0 -u USER -w WaitSetId
END_OF_USAGE
    die $usage;
}

my $z = ZimbraSoapTest->new($user, $host, $pw);
$z->doAdminAuth();

my $d = new XmlDoc;
  
$d->start("DestroyWaitSetRequest", $MAILNS, { 'waitSet' => "$waitSet" });
$d->end(); # 'CreateWaitSetRequest'
  
my $response = $z->invokeAdmin($d->root());

print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);

          

#!/usr/bin/perl
use strict;
use warnings;
use lib '.';

use LWP::UserAgent;
use Getopt::Long;
use XmlDoc;
use Soap;
use ZimbraSoapTest;

# If you're using ActivePerl, you'll need to go and install the Crypt::SSLeay
# module for htps: to work...
#
#         ppm install http://theoryx5.uwinnipeg.ca/ppms/Crypt-SSLeay.ppd
#
# specific to this app
my ($defTypes, $accounts, $admin, $allAccounts);

#standard options
my ($user, $pw, $host, $help); #standard
my ($name, $value);
GetOptions("u|user=s" => \$user,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           # add specific params below:
           "d=s"  => \$defTypes,
           "a=s@" => \$accounts,
           "admin" => \$admin,
           "allAccounts" => \$allAccounts,
          );

if (!defined($user) || defined($help) || !defined($defTypes)) {
  my $usage = <<END_OF_USAGE;
    
USAGE: $0 -u USER -d defTypes [-admin [-allAccounts]] [-a account -a account...]
END_OF_USAGE
    die $usage;
}

my $z = ZimbraSoapTest->new($user, $host, $pw);

if (defined($admin)) {
  $z->doAdminAuth();
} else {
  $z->doStdAuth();
}

my %args =  (  'defTypes' => "$defTypes" );

if (defined $allAccounts) {
  $args{'allAccounts'} = "1";
}
              

my $d = new XmlDoc;
  
$d->start("CreateWaitSetRequest", "urn:zimbraMail", \%args);

if (defined $accounts) {
  $d->start("add");
  {
    foreach my $a (@$accounts) {
      $d->add("a", undef, { 'id' => $a });
    }
  } $d->end(); # add
}
$d->end(); # 'CreateWaitSetRequest'
  
my $response = $z->invokeMail($d->root());

print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);

          

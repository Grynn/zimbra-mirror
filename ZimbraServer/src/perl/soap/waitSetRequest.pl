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
my ($defTypes, $accountsRem, $accountsUp, $accountsAdd, $waitSet, $seq, $block, $admin);

#standard options
my ($user, $pw, $host, $help); #standard
my ($name, $value);
GetOptions("u|user=s" => \$user,
           "pw=s" => \$pw,
           "h|host=s" => \$host,
           "help|?" => \$help,
           # add specific params below:
           "admin" => \$admin,
           "w=s" => \$waitSet,
           "s=s" => \$seq,
           "block" => \$block,
           "a=s@" => \$accountsAdd,
           "m=s@" => \$accountsUp,
           "r=s@" => \$accountsRem,
           "d=s"  => \$defTypes,
          );

if (!defined($user) || defined($help) || !defined($waitSet) || !defined($seq)) {
  my $usage = <<END_OF_USAGE;
    
USAGE: $0 -u USER -w waitSetId [-d defTypes] [-admin] -s lastKnownSeqNo [-block] [-a accountAdd -a...] [-m accountModify -m...] [-r accountRemove -r...]
END_OF_USAGE
    die $usage;
}

my $z = ZimbraSoapTest->new($user, $host, $pw);

if (defined($admin)) {
  $z->doAdminAuth();
} else {
  $z->doStdAuth();
}

my $d = new XmlDoc;

my %args = ( 'waitSet' => $waitSet,
             'seq' => $seq,
             );

if (defined $defTypes) {
  $args{'defTypes'} = $defTypes;
}

if (defined $block && $block ne "0") {
  $args{'block'} = "1";
}
  
$d->start("WaitSetRequest", "urn:zimbraMail", \%args);
if (defined $accountsAdd) {
  $d->start("add");
  {
    foreach my $a (@$accountsAdd) {
      (my $aid, my $tok) = split /,/,$a;
      if (!defined $tok) {
        $d->add("a", undef, { 'id' => $a, }); #'token'=>"608"
      } else {
        $d->add("a", undef, { 'id' => $aid, 'token'=>$tok}); 
      }
    }
    
  } $d->end(); # add
}

if (defined $accountsUp) {
  $d->start("update");
  {
    foreach my $a (@$accountsUp) {
      (my $aid, my $tok) = split /,/,$a;
      if (!defined $tok) {
        $d->add("a", undef, { 'id' => $a, }); #'token'=>"608"
      } else {
        $d->add("a", undef, { 'id' => $aid, 'token'=>$tok}); 
      }
    }
  } $d->end(); #update
}
    
if (defined $accountsRem) {
  $d->start("remove");
  {
    foreach my $a (@$accountsRem) {
      $d->add("a", undef, { 'id' => $a });
    }
  } $d->end(); #remove
  
}
$d->end(); # 'WaitMultipleAccountsRequest'
  
my $response = $z->invokeMail($d->root());

print "REQUEST:\n-------------\n".$z->to_string_simple($d);
print "RESPONSE:\n--------------\n".$z->to_string_simple($response);

          

#!/usr/bin/perl -w

#
# Simple SOAP test-harness for the AddMsg API
#

use Time::HiRes qw ( time );
use strict;

use lib '.';

use LWP::UserAgent;

use XmlElement;
use XmlDoc;
use Soap;

my $apptName;
my $startTime;
my $endTime;

if (defined $ARGV[2] && $ARGV[2] ne "") {
    $apptName = $ARGV[0];
    $startTime = $ARGV[1];
    $endTime  = $ARGV[2];
} else {
    die "Usage createAppt APPTNAME START END";
}

my $ACCTNS = "urn:zimbraAccount";
my $MAILNS = "urn:zimbraMail";

my $url = "http://localhost:7070/service/soap/";

my $SOAP = $Soap::Soap12;
my $d = new XmlDoc;
$d->start('AuthRequest', $ACCTNS);
$d->add('account', undef, { by => "name"}, 'user1');
$d->add('password', undef, undef, "test123");
$d->end();

my $authResponse = $SOAP->invoke($url, $d->root());

print "AuthResponse = ".$authResponse->to_string("pretty")."\n";

my $authToken = $authResponse->find_child('authToken')->content;
#print "authToken($authToken)\n";

my $sessionId = $authResponse->find_child('sessionId')->content;
#print "sessionId = $sessionId\n";

my $context = $SOAP->liquidContext($authToken, $sessionId);

my $contextStr = $context->to_string("pretty");
#print("Context = $contextStr\n");

#######################################################################
#
# CreateAppointment
#
my $now  = int(time());
print("now is ".$now."\n");

my $includeMyself = 0;
my $includeCurpleAddr = 0;
my $includeDogfoodAddr = 1;

my $nowPlus1Hr = $now+60*60;
my $nowPlus2Hr = $now+2*60*60;

# convert from secs to msecs for server
$nowPlus1Hr *= 1000;
$nowPlus2Hr *= 1000;


#print "1 hour from now is ".($nowPlus2Hr)."\n";


$d = new XmlDoc;

$d->start('CreateAppointmentRequest', $MAILNS);

$d->start('m', undef, { 'l' => "/INBOX" }, undef);

if ($includeMyself) {
    $d->add('e', undef,
            {
                'a' => "user1\@timbre.liquidsys.com",
                't' => "t"
                } );
}
$d->add('e', undef,
        {
            'a' => "user2\@timbre.liquidsys.com",
            't' => "t"
            } );

if ($includeCurpleAddr) {
    $d->add('e', undef,
            {
                'a' => "user3\@curple.com",
                't' => "t"
                } );
}

if ($includeDogfoodAddr) {
    $d->add('e', undef,
            {
                'a' => "tim\@liquidsys.com",
                't' => "t",
            } );
}

$d->add('su', undef, undef, $apptName);


$d->start('mp', undef, { 'ct' => "multipart/alternative" });
$d->start('mp', undef, { 'ct' => "text/plain" });
$d->add('content', undef, undef, "This is the body text for appointment $apptName");
$d->end(); #mp (text/plain )
$d->start('mp', undef, { 'ct' => "text/html" });
$d->add('content', undef, undef, "<p><b>This</b> is the html text for appointment $apptName");
$d->end(); #mp (text/html )
$d->end(); #mp (multipart/mixed



$d->start('inv', undef, { 'type' => "event",
                          'allday' => "false",
#                          's' => $nowPlus1Hr,
#                          'e' => $nowPlus2Hr,
                          'name' => $apptName,
                          'loc' => "test location for $apptName"
                          });

#dtstart
$d->add('s', undef, { 'd', => $startTime,
                      'tz', => "(GMT-08.00) Pacific Time (US & Canada) / Tijuana",
                  });

if ($endTime =~ /^([+-])?P(\d+W)$/) {
    print ("EndTime is $1 $2 weeks!\n");
    #duration
#    $d->add('d', undef, { 'dur', => "$1P$2W"});
    my %atts;
    if (defined($1) && $1 eq "-") {
        $atts{'neg'} = "1";
    }
    $atts{'w'} = $2;
    
    $d->add('dur', undef, %atts);
} elsif ($endTime =~ /^([+-])?P(?:(\d+)D)?(?:T(?:(\d+)H)?(?:(\d+)M)?(?:(\d+)S)?)?$/) {
#    print("EndTime is $1 $2 Days $3 Hours $4 Minutes $5 Seconds\n");
    #duration

    my %atts;
    if (defined($1)) {
    }
    
    my $dur = "";
    if (defined $1 && $1 eq "-") {
        $atts{'neg'} = "1";
        $dur .= $1;
    }
    $dur .= "P";
    if (defined($2)) {
        $atts{'d'} = "$2";
        $dur .= "$2D";
    }
    if (defined($3) || defined($4) || defined($5)) {
        $dur .= "T";
    }
    if (defined($3)) {
        $atts{'h'} = "$3";
        $dur .= "$3H";
    }
    if (defined($4)) {
        $atts{'m'} = "$4";
        $dur .= "$4M";
    }
    if (defined($5)) {
        $atts{'s'} = "$5";
        $dur .= "$5S";
    }
    print "Parsed Duration: $dur\n";
    print "Atts 'd' is ".$atts{'d'};
#    $d->add('d', undef, { 'dur', => $dur});
    
    $d->add('dur', undef, \%atts);
    
} elsif ($endTime =~ /^[+-]?P/) {
    printf("Error!  Illegal format for ENDTIME: $endTime\n");
    exit 3;
} else {
    print("EndTime is a DTEND!\n");
    #dtend
    $d->add('e', undef, { 'd', => $endTime,
                          #                      'tz', => "US/Eastern",
                      });
}

$d->add('or', undef, { 'd' => "user1", 'a' => "user1\@timbre.liquidsys.com" } );

$d->add('at', undef, { 'd' => "user2",
                       'a' => "user2\@timbre.liquidsys.com",
                       'role' => "REQ",
                       'ptst' => "NE",
#                       'rsvp' => "1"
                       });

$d->add('at', undef, { 'd' => "user3",
                       'a' => "user3\@curple.com",
                       'role' => "REQ",
                       'ptst' => "NE",
#                       'rsvp' => "1"
                       });

if ($includeDogfoodAddr) {
    $d->add('at', undef, { 'd' => "tim",
                           'a' => "tim\@liquidsys.com",
                           'role' => "REQ",
                           'ptst' => "NE",
#                           'rsvp' => "1",
                       });
}
    
if (1) {
    $d->start('recur');

    $d->start('add');

#     $d->add('date', undef,
#             {
#                 'd' => "20051031T01020304Z",
#             });
    
#    $d->add('date', undef,
#            {
#                'd' => "20050615",
#            });
#    $d->add('date', undef,
#            {
#                'd' => "20050616",
#            });
#    $d->add('date', undef,
#            {
#                'd' => "20050617",
#            });

#     $d->add('date', undef,
#             {
#                 'd' => "20051031T01020304",
#                 'tz' => "America/Chicago",
#             });


#     $d->add('date', undef,
#             {
#                 'd' => "20051031T01020304",
#                 'tz' => "US/Eastern",
#             });

    $d->start('rule', undef, { 'freq' =>"WEE", 'ival'=> "1" });
    $d->end(); #rule
    
    $d->end(); # add

    
#    $d->start('exclude');

#    $d->add('date', undef,
#            {
#                'd' => "20050616",
#            });

#    $d->start('rule', undef, { 'freq' =>"DAI", 'ival'=> "1" });
#    $d->add('until', undef, { 'd' => "20060101T1230", 'tz' => "US/Eastern" }); 
 #   $d->end(); #exrule
    

#    $d->end(); # exclude
    
    
    $d->end(); # recur

}


$d->end(); # inv
$d->end(); # m
$d->end(); # ca


print "\nOUTGOING XML:\n-------------\n";
my $out =  $d->to_string("pretty")."\n";
$out =~ s/ns0\://g;
print $out."\n";

my $start = time;
my $firstStart = time;
my $response;

my $i = 0;
my $end;
my $avg;
my $elapsed;

#do {

$start = time;
#    $msgAttrs{'sortby'} = "subjasc";
$response = $SOAP->invoke($url, $d->root(), $context);
#    $end = time;
#    $elapsed = $end - $start;
#    $avg = $elapsed *1000;
#    print("Ran iter in $elapsed time ($avg ms)\n");

#    $start = time;
#    $msgAttrs{'sortby'} = "subjdesc";
#$response = $SOAP->invoke($url, $d->root(), $context);
#    $end = time;
#    $elapsed = $end - $start;
#    $avg = $elapsed *1000;
#    print("Ran iter in $elapsed time ($avg ms)\n");

#$i++;
#} while($i < 50) ;

#my $lastEnd = time;
#$avg = ($lastEnd - $firstStart) / $i * 1000;
print "\nRESPONSE:\n--------------\n";
$out =  $response->to_string("pretty")."\n";
#$out =~ s/ns0\://g;
print $out."\n";

# print("\nRan $i iters in $elapsed time (avg = $avg ms)\n");

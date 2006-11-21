if ($^O ne 'MSWin32') {
    print STDERR "This program only runs on MSWin32 platform.\n";
    print STDERR "You are on $^O.\n";
    exit(1);
}

use strict;
use Getopt::Long;
use Win32::TieRegistry( Delimiter => '/', ArrayValues => 0 );

my $NOW_DATETIMEZ;

sub signedNum($) {
    my $val = shift;
    my $len = length($val);
    my $template;
    if ($len == 2) {
	$template = 's';
    } elsif ($len == 4) {
	$template = 'l';
    } else {
	print STDERR "Invalid short/long length $len\n";
	return undef;
    }

    my $result;
    if ($len == 2) {
	$result = unpack('v', $val);
	if ($result >= 0x8000) {
	    $result = (((~$result) & 0x0000FFFF) + 1) * -1;
	}
    } else {
	$result = unpack('V', $val);
	if ($result >= 0x80000000) {
	    $result = (((~$result) & 0xFFFFFFFF) + 1) * -1;
	}
    }
    return $result;
}

sub getByDay($$) {
    my ($week, $dayOfWeek) = @_;
    my @DAY_OF_WEEK = ('SU', 'MO', 'TU', 'WE', 'TH', 'FR', 'SA');
    if ($week > 4) {
	$week = 4 - $week;
    }
    return $week . $DAY_OF_WEEK[$dayOfWeek];
}

sub minsToHHMM($) {
    my $mins = shift;
    my $sign = $mins >= 0 ? '+' : '-';
    $mins = abs($mins);
    my $hh = sprintf("%02d", int($mins / 60));
    my $mm = sprintf("%02d", $mins % 60);
    return "$sign$hh$mm";
}

sub parseOnset($) {
    my $systemTime = shift;
    my $mon = signedNum(substr($systemTime, 2, 2));
    if ($mon == 0) {
	return (undef, '16010101T000000');
    }
    my $year = signedNum(substr($systemTime, 0, 2));
    my $dayOfWeek = signedNum(substr($systemTime, 4, 2));
    my $day = signedNum(substr($systemTime, 6, 2));
    my $hour = signedNum(substr($systemTime, 8, 2));
    my $min = signedNum(substr($systemTime, 10, 2));
    my $sec = signedNum(substr($systemTime, 12, 2));
    my $msec = signedNum(substr($systemTime, 14, 2));

    my $dtStart = sprintf("16010101T%02d%02d%02d", $hour, $min, $sec);
    my $rule = sprintf("FREQ=YEARLY;WKST=MO;INTERVAL=1;BYMONTH=%d;BYDAY=%s",
		       $mon, getByDay($day, $dayOfWeek));

#    print <<_ONSET_;
#year = $year, month = $mon, day = $day, dayOfWeek = $dayOfWeek
#hour = $hour, min = $min, sec = $sec, msec =$msec
#_ONSET_

    return ($rule, $dtStart);
}

sub parseTZI($) {
    my $tzi = shift;
    my $bias = signedNum(substr($tzi, 0, 4));
    my $stdBias = signedNum(substr($tzi, 4, 4));
    my $dstBias = signedNum(substr($tzi, 8, 4));
    my $stdOnsetRaw = substr($tzi, 12, 16);
    my $dstOnsetRaw = substr($tzi, 28, 16);

    my ($stdRule, $stdDtStart) = parseOnset($stdOnsetRaw);
    my ($dstRule, $dstDtStart) = parseOnset($dstOnsetRaw);

    my $hasDST = defined($stdRule) && defined($dstRule);

    my ($stdOffset, $dstOffset);
    $stdOffset = minsToHHMM(-1 * ($bias + $stdBias));
    if ($hasDST) {
	$dstOffset = minsToHHMM(-1 * ($bias + $dstBias));
    } else {
	$dstOffset = $stdOffset;
    }

    my %standard = ('name' => 'STANDARD',
		    'DTSTART' => $stdDtStart,
		    'TZOFFSETTO' => $stdOffset,
		    'TZOFFSETFROM' => $dstOffset,
		    'RRULE' => $stdRule);
    if (!$hasDST) {
	return (\%standard, undef);
    } else {
	my %daylight = ('name' => 'DAYLIGHT',
			'DTSTART' => $dstDtStart,
			'TZOFFSETTO' => $dstOffset,
			'TZOFFSETFROM' => $stdOffset,
			'RRULE' => $dstRule);
	return (\%standard, \%daylight);
    }
}

sub sanitizeTZName($) {
    my $name = shift;
    $name =~ s/^\(GMT([\+-]\d\d):(\d\d)\)/\(GMT$1.$2\)/;
    $name =~ s/:/\-/g;
    $name =~ s/[;,]/ \//g;
    return $name;
}

sub parseTZ($) {
    my $tzRegistry = shift;
    my %hash;
    my @keys = ('Display', 'Std', 'Dlt', 'MapID', 'TZI');
    foreach my $k (@keys) {
	$hash{$k} = $tzRegistry->{$k};
    }
    $hash{'Index'} = hex($tzRegistry->{'Index'});
    $hash{'TZID'} = sanitizeTZName($hash{'Display'});
    my $tzi = $hash{'TZI'};
    my ($std, $dst) = parseTZI($tzi);
    $hash{'STANDARD'} = $std;
    if (defined($dst)) {
	$hash{'DAYLIGHT'} = $dst;
    }

    return \%hash;
}

sub toVTIMEZONE($) {
    my $tz = shift;
    my @comps;
    push(@comps, $tz->{'STANDARD'});
    my $daylight = $tz->{'DAYLIGHT'};
    if (defined($daylight)) {
	push(@comps, $daylight);
    }

    my $vtimezone = "BEGIN:VTIMEZONE\n";
    my $tzid = $tz->{'TZID'};
    $vtimezone .= "TZID:$tzid\n";
    $vtimezone .= "LAST-MODIFIED:$NOW_DATETIMEZ\n";
    foreach my $comp (@comps) {
	my $name = $comp->{'name'};
	my $dtStart = $comp->{'DTSTART'};
	my $offsetTo = $comp->{'TZOFFSETTO'};
	my $offsetFrom = $comp->{'TZOFFSETFROM'};
	my $rule = $comp->{'RRULE'};

	$vtimezone .= "BEGIN:$name\n";
        $vtimezone .= "DTSTART:$dtStart\n";
	$vtimezone .= "TZOFFSETTO:$offsetTo\n";
	$vtimezone .= "TZOFFSETFROM:$offsetFrom\n";
	if (defined($rule)) {
	    $vtimezone .= "RRULE:$rule\n";
	}
	$vtimezone .= "END:$name\n";
    }
    $vtimezone .= "END:VTIMEZONE\n";
    return $vtimezone;
}

sub toLDIF($) {
    my $tz = shift;
    my $standard = $tz->{'STANDARD'};
    my $daylight = $tz->{'DAYLIGHT'};

    my $tzid = $tz->{'TZID'};
    my $dnSafe = $tzid;
    $dnSafe =~ s/\+/\\\+/g;

    my $ldif = "# $tzid\n";
    if (defined($daylight)) {
	$ldif .= "# (supports Daylight Savings Time)\n";
    }
    $ldif .= "dn: cn=$dnSafe,cn=timezones,cn=config,cn=zimbra\n";
    $ldif .= "objectclass: zimbraTimeZone\n";
    $ldif .= "cn: $tzid\n";
    my $stdDtStart = $standard->{'DTSTART'};
    my $stdOffset = $standard->{'TZOFFSETTO'};
    $ldif .= "zimbraTimeZoneStandardDtStart: $stdDtStart\n";
    $ldif .= "zimbraTimeZoneStandardOffset: $stdOffset\n";
    my $stdRule = $standard->{'RRULE'};
    if (defined($stdRule)) {
	$ldif .= "zimbraTimeZoneStandardRRule: $stdRule\n";
    }

    if (defined($daylight)) {
	my $dstDtStart = $daylight->{'DTSTART'};
	my $dstOffset = $daylight->{'TZOFFSETTO'};
	$ldif .= "zimbraTimeZoneDaylightDtStart: $dstDtStart\n";
	$ldif .= "zimbraTimeZoneDaylightOffset: $dstOffset\n";
	my $dstRule = $daylight->{'RRULE'};
	if (defined($dstRule)) {
	    $ldif .= "zimbraTimeZoneDaylightRRule: $dstRule\n";
	}
    } else {
	$ldif .= "zimbraTimeZoneDaylightDtStart: $stdDtStart\n";
	$ldif .= "zimbraTimeZoneDaylightOffset: $stdOffset\n";
    }

    return $ldif;
}

sub tzComparator {
    my $tzA = $a;
    my $tzB = $b;

    my $offsetA = $tzA->{'STANDARD'}->{'TZOFFSETTO'};
    my $offsetB = $tzB->{'STANDARD'}->{'TZOFFSETTO'};
    my $offsetComp = $offsetA <=> $offsetB;
    if ($offsetComp != 0) {
	return $offsetComp;
    }

    my $nameA = $tzA->{'TZID'};
    my $nameB = $tzB->{'TZID'};
    $nameA =~ s/^\(GMT[^\)]*\)//;
    $nameB =~ s/^\(GMT[^\)]*\)//;
    my $nameComp = $nameA cmp $nameB;
    if ($nameComp != 0) {
	return $nameComp;
    }

    return $tzA->{'Index'} <=> $tzB->{'Index'};
}

sub doTZ($) {
    my $tzRegistry = shift;
    my $tz = parseTZ($tzRegistry);
    my $index = $tz->{'Index'};
    my $displayName = $tz->{'Display'};
    my $vtimezone = toVTIMEZONE($tz);
    my $ldif = toLDIF($tz);
    print "[[ $displayName ($index) ]]\n$vtimezone\n$ldif\n";
}

sub usage() {
    print <<_USAGE_;
Usage: dumpWindowsTimeZones.pl [--ldif] [--icalendar]
Dump time zone definitions in Windows registry to stdot, in either LDIF
or iCalendar format.  Default is LDIF.  Only one format must be used.
format.
_USAGE_
    exit(1);
}


#
# main
#

my ($ICAL, $LDIF, $USAGE);

my $good = GetOptions('icalendar' => \$ICAL,
		      'ldif' => \$LDIF,
		      'help' => \$USAGE);
if (!$good || $USAGE) {
    usage();
}
if (defined($ICAL) && defined($LDIF)) {
    usage();
}
if (!defined($ICAL) && !defined($LDIF)) {
    $LDIF = 1;
}
$LDIF |= 0;
$ICAL |= 0;

my ($sec, $min, $hour, $mday, $mon, $year, $wday, $yday, $isdst) = gmtime();
$NOW_DATETIMEZ = sprintf("%04d%02d%02dT%02d%02d%02dZ",
                         $year + 1900, $mon + 1, $mday,
                         $hour, $min, $sec);

my $TZROOT = "HKEY_LOCAL_MACHINE/SOFTWARE/Microsoft/Windows NT/CurrentVersion/Time Zones/";
my $tzRoot = $Registry->{$TZROOT} or die "Can't open registry";

my @tzlist;
foreach my $tzname ($tzRoot->SubKeyNames()) {
    my $tzRegistry = $tzRoot->{$tzname};
    my $tz = parseTZ($tzRegistry);
    push(@tzlist, $tz);
}
@tzlist = sort tzComparator @tzlist;

if ($ICAL) {
    print "BEGIN:VCALENDAR\n";
    print "PRODID:Zimbra-Calendar-Provider\n";
    print "VERSION:2.0\n";
    print "METHOD:PUBLISH\n";
    my $first = 1;
    foreach my $tz (@tzlist) {
	print toVTIMEZONE($tz);
    }
    print "END:VCALENDAR\n";
}

if ($LDIF) {
    my $first = 1;
    foreach my $tz (@tzlist) {
	if ($first) {
	    $first = 0;
	} else {
	    print "\n";
	}
	print toLDIF($tz);
    }
}

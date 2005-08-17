#!/usr/bin/perl

package serviceInfo;

use strict;

use liquidlog;

use FileHandle;

my $NUM_ITEMS = 50;

require Exporter;

my @ISA = qw(Exporter);

sub new
{
	my ($class, $host) = @_;
	return $class if ref ($class);
	
	my $self = bless {},  $class;
	
	if (defined $host) {
		if (ref ($host)) {
			$self->{host} = $host;
		} else {
			$self->{host} = $::Cluster->getHostByName($host);
		}
	}
	
	$self->{uts} = 0;

	$self->{serviceDir} = "$::Basedir/service";
	
	$self->{lastFileName} = "";
	
#	if (defined $host) {
#		liquidlog::Log ("debug","Created serviceInfo for host ".$self->{host}->{name});
#	} else {
#		liquidlog::Log ("debug","Created serviceInfo");
#	}
	return $self;
}

sub readServiceInfo
{
#	liquidlog::Log ("debug","readServiceInfo");
	# readServiceInfo reads info written by liquidStatusMon
	my $self = shift;
	
	# Race condition fun - make sure the file we're reading doesn't get wiped.
	my $done = 0;
	while (! $done)
	{
		my $fn = $self->getLastFileName();
		
		my @lines;
		
		if (rename $fn, "$fn.reading")
		{
			$done = 1;

			my $fh = new FileHandle;
			
			$fh->open ("$fn.reading");
			
			@lines = <$fh>;

			$fh->close();

			rename "$fn.reading", $fn;
		} else {
			return;
		}
		
		foreach (@lines)
		{
			chomp;
			my ($key, $val) = split ':', $_, 2;
			# TODO MEM fix special cases for services
			# $s .= "ServiceStatus:".$_.":$service ".$self->{ServiceStatus}{$service}."\n";	
			if ($key eq 'ServiceStatus')
			{
				my ($service, $stuff) = split ' ', $val, 2;
				$self->{ServiceStatus}{$service} = $stuff;
			} else {
				$self->{$key} = $val; 
			}
		}
			
	}
}

sub getLastFileName
{
	my $self = shift;
	
	my $fileName;
	
	opendir DIR, $self->{serviceDir};
	
	my @fns = grep !/tmp/, sort map {"$self->{serviceDir}/$_"} readdir DIR;
	
	closedir DIR;
	
#	foreach (@fns) {
#		liquidlog::Log ("debug","getLastFileName ".$_);
#	}

#	liquidlog::Log ("debug","getLastFileName ".$fns[$#fns]);
	
	return $fns[$#fns];
}

sub writeServiceInfo
{
	my $self = shift;
	
	my $t = time();
	
	my $serviceFileName = $self->getServiceFilename($t)."tmp";
	liquidlog::Log ("info","writeServiceInfo ".$serviceFileName);
	
	my $fh = new FileHandle;
	
	$fh->open (">$serviceFileName");
	
	my $s = $self->prettyPrint();
	
	print $fh $s;
	
	$fh->close();
	
	rename ($serviceFileName, $self->{serviceFileName});
	
	$self->cleanupServiceDir();
}

sub getServiceFilename
{
	my $self = shift;
	my $t = shift;
	my $fn = $self->{serviceDir};
	$fn .= "/service.".$t.".".$$;
#	liquidlog::Log ("debug","getServiceFilename ".$fn);
	
	$self->{serviceFileName} = $fn;
	
	return $fn;
}

sub cleanupServiceDir
{
	my $self = shift;
#	liquidlog::Log ("debug","cleanupServiceDir ".$self->{lastFileName});

	if ($self->{lastFileName} ne "")
	{
		unlink ($self->{lastFileName});	
	}
	$self->{lastFileName} = $self->{serviceFileName};
}

sub prettyPrint
{
#	liquidlog::Log ("debug","prettyPrint");
	# getServiceInfo gets info to be written by liquidStatusMon
	my $self = shift;
	
	my $s;
	# TODO MEM fix special cases
	foreach (keys %{$self})
	{
		if (	/^service/ 
			|| /^lastFile/ 
			|| /^host$/ 
			|| /^load/) 
		{next;}
		if (	/^ServiceStatus$/)
		{
			my $service;
			foreach $service (keys %{$self->{ServiceStatus}}) {
				$s .= "ServiceStatus:".$service." ".$self->{ServiceStatus}{$service}."\n";	
			}
			next;
		}
		$s .= $_.":".$self->{$_}."\n";	
	}
	
	return $s;
}

sub getServiceInfo
{
#	liquidlog::Log ("debug","getServiceInfo");
	my $self = shift;

	$self->{uts} = time();
	
	$self->{ts} = `date +%Y%m%d%H%M%S`;
	
	chomp $self->{ts};
	
	my $s;
	
	liquidControl::getLocalServices();
	
	foreach $s (@::localservices)
	{
		$s = liquidControl::getServiceByName($s);
##		liquidlog::Log ("debug", "getServiceInfo: ".$s->prettyPrint());
		$self->getServiceStatus(\$s);
	}
	
	# No longer write service info, get current status every time.	
	#$self->writeServiceInfo();
		
}

sub getServiceStatus
{
	my $self = shift;
	my $service = shift;
	
	my $sname = $$service->{name};
	
	my $syntax = $$service->{syntax};
	
	####
	# instead of talking to the main server, we'll poll the apps directly
	####
#	liquidlog::Log ("debug", "STATUS: serviceInfo::getServiceStatus $sname from app directly");
	my @apps = ::getAppByServiceName ($sname);
	my $status = $::StatusStopped;
	my $info = "";
	%{$self->{ServiceStatus}{$sname}} = ();

	if (defined (@apps)) {
		foreach (@apps) {
			my $name;
			if (ref ($_)) {$name = $_->{name};}
			else {$name = $_;}

			liquidlog::Log("debug", 
			"STATUS: $name monitor for $self->{host}->{name} from $::Cluster->{LocalHost}{name}");

			my $hn = undef;
			if (defined $self->{host} && $self->{host}{name} ne $::Cluster->{LocalHost}{name}) {
				$hn = $self->{host}{name};
			}

			my $retval = liquidControl::runSyntaxCommand("liquidsyntax", "$name"."_status", $hn);
			if ($retval) {
				$status = $::StatusStopped;
			} elsif (defined ($retval)) {
				$status = $::StatusRunning;
			} else {
				$status = undef;
				liquidlog::Log("debug", "STATUS: No network monitor for $name defined");
			}
			if ($self->{ServiceStatus}{$sname}{status} != $::StatusStopped) {
					$self->{ServiceStatus}{$sname}{status} = $status;
			}

			$info = liquidControl::runSyntaxCommand("liquidsyntax", "$name"."_info", $hn);
#			liquidlog::Log("debug", "STATUS: Reporting info for $sname: $info");
			if (defined $info) {
				$self->{ServiceStatus}{$sname}{info} = $info;
			} else {
				liquidlog::Log("debug", "STATUS: No network info command for $name defined");
			}
#			liquidlog::Log("debug", "STATUS: Reporting info for $sname:".$self->{ServiceStatus}{$sname}{info});
		}
	}
#	liquidlog::Log("debug", "STATUS: Reporting status for $sname: $status");
#	liquidlog::Log("debug", "STATUS: Reporting info for $sname: $info");
}

1


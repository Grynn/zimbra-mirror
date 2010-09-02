#!/usr/bin/perl

use Cwd 'abs_path';
use Net::Domain 'hostfqdn';
use File::Copy 'move';


my $javacom = "/usr/lib/jvm/java-6-sun/bin/java";
my $hostname = hostfqdn;

my $jarfolder=abs_path("jars");
my $seleniumjar="$jarfolder/zimbraselenium.jar";

my $configfile1=abs_path("conf/config.properties");
my $configfile2=abs_path("conf/tms.properties");


sub GET_CLASSPATH
{
	my $cp;

	opendir(DIR, $jarfolder);
	@files = grep(/\.jar$/,readdir(DIR));
	closedir(DIR);

	foreach $j (@files) {
		$cp=$cp . ":$jarfolder/$j";
	}

	$cp;

}

sub CREATE_CONFIG
{

	# Move the file out of the way
	move($configfile1, $configfile2)
		or die "unable to move $configfile1 to $configfile2: $!";

	# Open the moved file and write back to the original file
	open INFILE, "< $configfile2"
		or die "unable to open $configfile2: $!";
	open OUTFILE, "> $configfile1"
		or die "unable to open $configfile1: $!";

	while (<INFILE>) {
		chomp;
		if (/^seleniumMode=/) {
			print OUTFILE "seleniumMode=Remote\n";
		} elsif (/^serverName=/) {
			print OUTFILE "serverName=10.20.141.166\n";
		} elsif (/^server=/) {
			print OUTFILE "server=$hostname\n";
		} else {
			print OUTFILE "$_\n";
		}
	}

	close(INFILE);
	close(OUTFILE);


}

sub MAIN
{

	&CREATE_CONFIG;

	$ENV{CLASSPATH} = $ENV{CLASSPATH} . ":" . &GET_CLASSPATH;
	my $command = "$javacom framework.core.ExecuteHarnessMain -j $seleniumjar -p projects.zcs.tests -g always,sanity -l conf/log4j.properties >harness.out 2>harness.err";

	print "$command\n";

	system($command);

}


&MAIN;


print "\n";
exit 0;

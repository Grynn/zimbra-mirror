#!/usr/bin/perl -w
# 
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2013 Zimbra, Inc.
# 
# The contents of this file are subject to the Zimbra Public License
# Version 1.4 ("License"); you may not use this file except in
# compliance with the License.  You may obtain a copy of the License at
# http://www.zimbra.com/license.
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# ***** END LICENSE BLOCK *****
# 

use File::Copy qw(move);
use File::Copy qw(copy);
my ($dev,$ino,$mode,$nlink,$uid,$gid,$rdev,$size,$atime,$mtime,$ctime,$blksize,$blocks);
my $username = getpwuid($<);

if ($username ne "root")
{
	die "Error!  Must run this script as root\n";
}

doPreflightChecks();
removeVAMI();
configureVirtualConsole();
cleanEnvironment();
setVars();
cleanProfile();
removeVamilocale();
cleanRC();
removeVAMIInitScripts();
cleanVMtools();
tuneFilesystems();

print "Completed removal of ZCA VAMI.\n";

sub doPreflightChecks
{
	print "Doing Preflight checks.  Making sure required files exist and can be opened.\n";
	open our $orig_tty, "< /etc/init/tty2.conf" or print "Cannot open /etc/init/tty2.conf!\n";
	open our $new_tty, "> /tmp/tty1.conf" or print "Cannot open /tmp/tty1.conf!\n";
	open our $orig_environment, "< /etc/environment" or print "Cannot open /etc/environment!\n";
	open our $new_environment, "> /tmp/environment" or print "Cannot open /tmp/environment!\n";
	open our $orig_profile, "< /etc/profile" or print "Cannot open /etc/profile!\n";
	open our $new_profile, "> /tmp/profile" or print "Cannot open /tmp/profile!\n";
	open our $orig_rc, "< /etc/init.d/rc" or die "Cannot open /etc/init.d/rc!\n";
	open our $new_rc, "> /tmp/rc" or die "Cannot open /tmp/rc!\n";
	open our $orig_vmware_tools, "< /etc/vmware-tools/tools.conf" or print "Cannot open /etc/vmware-tools/tools.conf!\n";
	open our $new_vmware_tools, "> /tmp/tools.conf" or print "Cannot open /tmp/tools.conf!\n";
}

sub removeVAMI
{
	print "Removing VMware VAMI packages\n";
	system("/usr/bin/dpkg -r vmware-studio-appliance-config vmware-studio-init vmware-studio-provagent vmware-studio-vami-cimom vmware-studio-vami-lighttpd vmware-studio-vami-login vmware-studio-vami-service-core vmware-studio-vami-service-network vmware-studio-vami-service-system vmware-studio-vami-service-update vmware-studio-vami-service-zimbra vmware-studio-vami-servicebase vmware-studio-vami-tools vmware-zca-installer 2>/dev/null");
	print "Purging configuratio files for VMware VAMI packages\n";
	system("/usr/bin/dpkg -P vmware-studio-appliance-config vmware-studio-init vmware-studio-provagent vmware-studio-vami-cimom vmware-studio-vami-lighttpd vmware-studio-vami-login vmware-studio-vami-service-core vmware-studio-vami-service-network vmware-studio-vami-service-system vmware-studio-vami-service-update vmware-studio-vami-service-zimbra vmware-studio-vami-servicebase vmware-studio-vami-tools vmware-zca-installer 2>/dev/null");
}

sub configureVirtualConsole
{
	print "Configuring virtual console configuration\n";
	move "/etc/init/tty1.conf", "/etc/init/tty1.conf.vami";
	($dev,$ino,$mode,$nlink,$uid,$gid,$rdev,$size,$atime,$mtime,$ctime,$blksize,$blocks)=stat("/etc/init/tty2.conf");
	while(<$orig_tty>)
	{
		$_ =~ s/tty2/tty1/g;
			print $new_tty $_;
	}
	chmod $mode,$new_tty;
	close($new_tty);
	close($orig_tty);
	move "/tmp/tty1.conf","/etc/init/tty1.conf";
}

sub cleanEnvironment
{
	print "Cleaning up /etc/environment\n";
	($dev,$ino,$mode,$nlink,$uid,$gid,$rdev,$size,$atime,$mtime,$ctime,$blksize,$blocks)=stat("/etc/environment");
	while(<$orig_environment>)
	{
		if ($_ =~ "^PATH")
		{
			$_ =~ s/:\/opt\/vmware\/bin//;
			print $new_environment $_;
		}
		else
		{
			print $new_environment $_;
		}
	}
	chmod $mode,$new_environment;
	close($new_environment);
	close($orig_environment);
	move "/tmp/environment","/etc/environment";
}

sub setVars
{
	print "Setting variables in /etc/sysctl.d/60-zcs.conf\n";
	qx(sysctl -w vm.swappiness=0);
	qx(sysctl -w vm.oom_dump_tasks=1);
	qx(sysctl -w net.ipv4.tcp_fin_timeout=15);
	qx(sysctl -w net.ipv4.tcp_tw_reuse=1);
	qx(sysctl -w net.ipv4.tcp_tw_recycle=1);
	qx(echo vm.swappiness=0 > /etc/sysctl.d/60-zcs.conf);
	qx(echo vm.oom_dump_tasks=1 >> /etc/sysctl.d/60-zcs.conf);
	qx(echo net.ipv4.tcp_fin_timeout=15 >> /etc/sysctl.d/60-zcs.conf);
	qx(echo net.ipv4.tcp_tw_reuse=1 >> /etc/sysctl.d/60-zcs.conf);
	qx(echo net.ipv4.tcp_tw_recycle=1 >> /etc/sysctl.d/60-zcs.conf);
}
	

sub cleanProfile
{
	print "Cleaning up /etc/profile\n";
	($dev,$ino,$mode,$nlink,$uid,$gid,$rdev,$size,$atime,$mtime,$ctime,$blksize,$blocks)=stat("/etc/profile");
	while(<$orig_profile>)
	{
		if ($_ =~ "^PATH")
		{
			$_ =~ s/:\/opt\/vmware\/bin//;
			print $new_profile $_;
		}
		else
		{
			print $new_profile $_;
		}
	}
	chmod $mode,$new_profile;
	close($new_profile);
	close($orig_profile);
	move "/tmp/profile","/etc/profile";
}

sub removeVamilocale
{
	print "Removing vamilocale profile\n";
	unlink("/etc/profile.d/zzzz-vamilocale.sh");
}

sub cleanRC
{
	print "Cleaning up /etc/init.d/rc\n";
	($dev,$ino,$mode,$nlink,$uid,$gid,$rdev,$size,$atime,$mtime,$ctime,$blksize,$blocks)=stat("/etc/init.d/rc");
	while(<$orig_rc>)
	{
		if ($_ !~ "\/opt\/vmware\/bin")
		{
			print $new_rc $_;
		}
	}
	chmod $mode,$new_rc;
	close($new_rc);
	close($orig_rc);
	move "/tmp/rc","/etc/init.d/rc";
}

sub removeVAMIInitScripts
{
	print "Removing VAMI init scripts\n";
	unlink("/etc/rc0.d/K20vami-lighttp");
	unlink("/etc/rc0.d/K20vami-sfcb");
}

sub cleanVMtools
{
	print "Cleaning up VMware-tools\n";
	($dev,$ino,$mode,$nlink,$uid,$gid,$rdev,$size,$atime,$mtime,$ctime,$blksize,$blocks)=stat("/etc/vmware-tools/tools.conf");
	while(<$orig_vmware_tools>)
	{
		chop($_);
		if ($_ ne "\[powerops\]" && $_ ne "poweron-script=\/opt\/vmware\/share\/vami\/vami_poweron_vm_script" && $_ ne "resume-script=\/opt\/vmware\/share\/vami\/vami_resume_vm_script")
		{
			print $new_vmware_tools "$_\n";
		}
	}
	chmod $mode,$new_vmware_tools;
	close($new_vmware_tools);
	close($orig_vmware_tools);
	move "/tmp/tools.conf","/etc/vmware-tools/tools.conf";
}

sub tuneFilesystems
{
	print "Checking for filesystems to tune...\n";
	@filesystems=qx(mount | grep \' ext[2-4] \' | cut -d " " -f 1);
	for ($i=0;$i<$#filesystems;$i++)
	{
		chop($filesystems[$i]);
		print "Tuning filesystem $filesystems[$i]...";
		qx(/sbin/tune2fs -c 0 $filesystems[$i]);
		print "done.\n";
	}
	print "Finished tuning filesystems.\n";
}


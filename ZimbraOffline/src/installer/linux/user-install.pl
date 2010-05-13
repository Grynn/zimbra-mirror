#!/usr/bin/perl

#/*
# * ***** BEGIN LICENSE BLOCK *****
# * Zimbra Collaboration Suite Server
# * Copyright (C) 2009, 2010 Zimbra, Inc.
# * 
# * The contents of this file are subject to the Zimbra Public License
# * Version 1.3 ("License"); you may not use this file except in
# * compliance with the License.  You may obtain a copy of the License at
# * http://www.zimbra.com/license.
# * 
# * Software distributed under the License is distributed on an "AS IS"
# * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# * ***** END LICENSE BLOCK *****
# */
#
# ZD installer for user data files
#

use strict;
use warnings;

my $locale = "en_US";
my $home_dir = $ENV{HOME} || die("Error: unable to get user home directory");
my $app_root;

my $messages = {
    en_US => {
        ChooseDataRoot => "Choose the folder where you would like to install Zimbra Desktop's user data files",
        ChooseIconDir => "Choose the folder where you would like to create desktop icon",
        Configuring => "Initializing user data...",
        CreateIcon => "Creating desktop icon...",
		Installing => "Installing user data files...",
        InvalidDataRoot1 => "*** Error: User data directory can not be the same as, or a subdirectory of, the application directory.",
        InvalidDataRoot2 => "*** Error: User data directory can not be a parent directory of the application directory.",
        Done => "done",
        RunCommand => "You can start Zimbra Desktop by double-clicking the desktop icon or by running the following command:",
		RunWithAbsPath => '*** Error: You must run user-install.pl with absolute path.',
        Success => 'Zimbra Desktop has been installed successfully for user {0}.'
    }
};

sub get_message($;$) {
    my ($key, $vars) = @_;

    my $msgs = $messages->{$locale};
    $msgs = $messages->{'en_US'} unless ($msgs);
    my $msg = $msgs->{$key};
    return '' unless ($msg);
    
    if ($vars) {
        my $c = 0;
        for my $v (@$vars) {
            my $k = '{' . $c . '}';
            my $pos = index($msg, $k);
            substr($msg, $pos, length($k)) = $v if ($pos >= 0);
            $c++;
        }
    }
    return $msg;
}

sub get_input($;$$) {
    my ($prompt, $default, $allow_null) = @_;
    my $ret = '';

    print "------------------------------\n";
    while (!$ret) {
	print $prompt;
	print " [$default]" if ($default);
	print ": ";

	$| = 1;
	$_ = <STDIN>;
	chomp();
	$ret = $default ? ($_ ? $_ : $default) : $_;
	last if ($allow_null);
    }
    print "\n\n";
    return $ret;
}

sub find_and_replace($$) {
    my ($file, $tokens) = @_;
    my $tmpfile = $file . '.tmp';
    
    open(FIN, "<$file") or die("Error: cannot open file $file\n");
    open(FOUT, ">$tmpfile") or die("Error: cannot open file $tmpfile\n");
    
    my $line;
    while($line = <FIN>){
        foreach my $key (keys(%$tokens)) {
            my $pos = index($line, $key);
            while ($pos >= 0) {
                substr($line, $pos, length($key), $tokens->{$key});    
                $pos = index($line, $key);   
            }
        }
        print FOUT $line;
    }
    
    close FIN;
    close FOUT;

    my (undef, undef, $mode) = stat($file);
    unlink $file;
    rename $tmpfile, $file;
    chmod $mode, $file;
}

sub get_random_id() {
    my @n;
    srand(time ^ ($$ + ($$ << 15)) ^ int(rand(0xFFFF)));
    push(@n, sprintf("%04x", int(rand(0xFFFF)))) for (0..7);
    return "$n[0]$n[1]-$n[2]-$n[3]-$n[4]-$n[5]$n[6]$n[7]";
}

sub move_no_overwrite($$) {
	my ($src, $dest) = @_;
	my @files;
	
	if (! opendir(DH, $src)) {
		print "Unable to open directory $src\n";
		return;
	}
	@files = readdir(DH);
	closedir(DH);

	foreach my $file (@files) {
		next if ( $file eq "." || $file eq ".." );
		unless ( -e "$dest/$file") {
			system("mv \"$src/$file\" \"$dest\"");
		}
	}
}

sub dialog_data_root() { 
    my $dr;

    while (1) {   
        $dr = get_input(get_message("ChooseDataRoot"), "$home_dir/zdesktop");
        if (index($dr, $app_root) >= 0) {
            print get_message("InvalidDataRoot1"), "\n";
        } elsif (index($app_root, $dr) >= 0) {
            print get_message("InvalidDataRoot2"), "\n";
        } else {
            return $dr;
        }
    }
}

sub dialog_desktop_icon() { 
    return get_input(get_message("ChooseIconDir"), "$home_dir/Desktop");
}

# main
my ($data_root, $icon_dir);

my $script_path = $0;
if ($script_path eq 'user-install.pl' || $script_path eq './user-install.pl') {
	$script_path = `pwd`;
	chomp($script_path);
	$script_path .= '/user-install.pl';
}

unless ($script_path =~ /^\/.+/) {
	print get_message('RunWithAbsPath'), "\n";
	exit 1;
}

$app_root = substr($script_path, 0, length($script_path) - 22); # 22: "/linux/user-install.pl"
chdir($app_root);

$data_root = dialog_data_root();
$icon_dir = dialog_desktop_icon();

my $tmpdir = "$data_root" . ".tmp";
my @user_files = ("index", "store", "sqlite", "log", "zimlets-properties", "zimlets-deployed",
    "conf/keystore", "profile/prefs.js", "profile/persdict.dat", "profile/localstore.json");

my $is_upgrade = 0;
if (-e $data_root) {
	$is_upgrade = 1;	

	# backup user data	
    mkdir($tmpdir);
    system("rm -rf \"$tmpdir/*\"");
    mkdir("$tmpdir/profile");
    mkdir("$tmpdir/conf");

    for (@user_files) {
        my $src = "$data_root/$_";
        system("mv -f \"$src\" \"$tmpdir/$_\"") if (-e $src);
    }

    system("rm -rf \"$data_root\"");
}

# copy files;
print "\n", get_message('Installing');
exit 1 if system("mkdir -p \"$data_root\"");
exit 1 if system("cp -r -p ./data/* \"$data_root\"");
print get_message('Done'), "\n";

my $tokens = {
	'@install.app.root@' => $app_root, 
	'@install.data.root@' => $data_root,
	'@install.key@' => get_random_id(),
	'@install.locale@' => 'en-US',
	'#@install.linux.java.home@' => "JAVA_HOME=\"$app_root/linux/jre\""
};

# fix data files
print get_message('Configuring');
find_and_replace("$data_root/conf/localconfig.xml", $tokens);
find_and_replace("$data_root/jetty/etc/jetty.xml", $tokens);
find_and_replace("$data_root/bin/zdesktop", $tokens);
find_and_replace("$data_root/zdesktop.webapp/webapp.ini", $tokens);
find_and_replace("$data_root/profile/user.js", $tokens);
print get_message('Done'), "\n";

# create desktop icon
print get_message('CreateIcon');
exit 1 if system("cp -f -p \"$app_root/linux/zd.desktop\" \"$icon_dir\"");
find_and_replace("$icon_dir/zd.desktop", $tokens);
print get_message('Done'), "\n";

if ($is_upgrade) {
	for (@user_files) {
        my $src = "$tmpdir/$_";
        next if (! -e $src);

        my $dest = "$data_root/$_";
        if ((-d $src) && (-e $dest)) {
            system("mv -f \"$src\"/* \"$dest\""); # must move '/*' outside the quote
        } else {
            system("mv -f \"$src\" \"$dest\"");
        }
    }
	
    system("rm -rf \"$tmpdir\"");
}

system("chmod 700 \"$data_root\"");

print get_message('Success', [$ENV{USER}]), "\n\n";
print get_message('RunCommand'), "\n";
print "\"$app_root/linux/prism/zdclient\" -webapp \"$data_root/zdesktop.webapp\" -override \"$data_root/zdesktop.webapp/override.ini\" -profile \"$data_root/profile\"\n\n";


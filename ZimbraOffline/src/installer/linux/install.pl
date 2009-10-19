#!/usr/bin/perl

use strict;
use warnings;

my $license = "PLEASE READ THIS AGREEMENT CAREFULLY BEFORE USING THE SOFTWARE. YAHOO! INC., ON BEHALF OF ITS ZIMBRA BUSINESS UNIT, (\"ZIMBRA\") WILL ONLY LICENSE THIS SOFTWARE TO YOU IF YOU FIRST ACCEPT THE TERMS OF THIS AGREEMENT. BY DOWNLOADING OR INSTALLING THE SOFTWARE, OR USING THE PRODUCT, YOU ARE CONSENTING TO BE BOUND BY THIS AGREEMENT. IF YOU DO NOT AGREE TO ALL OF THE TERMS OF THIS AGREEMENT, THEN DO NOT DOWNLOAD, INSTALL OR USE THE PRODUCT.\n\nLicense Terms for this Zimbra Collaboration Suite Software: http://www.zimbra.com/license/zimbra_public_eula_2.1.html";

my $locale = "en_US";
my $home_dir = $ENV{HOME} || die("Error: unable to get user home directory");
my ($app_root, $data_root, $icon_dir);

my $messages = {
    en_US => {
        AcceptDecline => "(A)ccept or (D)ecline",
        ChooseAppRoot => "Choose the folder where you would like to install Zimbra Desktop's application files",
        ChooseDataRoot => "Choose the folder where you would like to install Zimbra Desktop's user data files",
        ChooseIconDir => "Choose the folder where you would like to create desktop icon",
        Configuring => "Configuring...",
        Continue => "Press enter to continue",
        CreateIcon => "Creating desktop icon...",
        Done => "done",
        Installing => "Installing files...",
        RunCommand => "You can start Zimbra Desktop by double-clicking the desktop icon or by running the following from command line:",
        Success => 'Zimbra Desktop is installed successfully.',
        Welcome => "Welcome to Zimbra Desktop setup wizard. This will install Zimbra Desktop on you computer.",
        YesNo => "(Y)es or (N)o"
    }
};

sub get_message($) {
    my $key = shift();

    my $msgs = $messages->{$locale};
    $msgs = $messages->{'en_US'} unless ($msgs);
    return $msgs->{$key} || '';
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

sub dialog_welcome() {
    print "\n\n";
    print get_message('Welcome'), "\n";
    get_input(get_message('Continue'), '', 1);
}

sub dialog_license() {
    print $license, "\n\n";
    my $in = lc(get_input(get_message('AcceptDecline'), 'A'));
    exit 1 if (substr($in, 0, 1) ne 'a');
}

sub dialog_app_root() {
    return get_input(get_message("ChooseAppRoot"), '/opt/zimbra/zdesktop');
}

sub dialog_data_root() { 
    return get_input(get_message("ChooseDataRoot"), "$home_dir/zdesktop");
}

sub dialog_desktop_icon() { 
    return get_input(get_message("ChooseIconDir"), "$home_dir/Desktop");
}

# main
my $script_dir = substr($0, 0, length($0) - 11);
chdir($script_dir);

dialog_welcome();
dialog_license();
$app_root = dialog_app_root();
$data_root = dialog_data_root();
$icon_dir = dialog_desktop_icon();

# copy files;
print "\n", get_message('Installing');
unless (-e $app_root) {
    exit 1 if system("mkdir -p $app_root");
}
unless (-e $data_root) {
    exit 1 if system("mkdir -p $data_root");
}
exit 1 if system("cp -r -p ./app/* $app_root");
exit 1 if system("cp -r -p ./app/data/* $data_root");
print get_message('Done'), "\n";

my $tokens = {
	'@install.app.root@' => $app_root, 
	'@install.data.root@' => $data_root,
	'#@install.linux.java.home@' => "JAVA_HOME=\"$app_root/linux/jre\""
};

# fix data files
print get_message('Configuring');
find_and_replace("$data_root/conf/localconfig.xml", $tokens);
find_and_replace("$data_root/jetty/etc/jetty.xml", $tokens);
find_and_replace("$data_root/bin/zdesktop", $tokens);
unlink("$data_root/zdesktop.webapp/override.ini");
rename("$data_root/zdesktop.webapp/override.ini.linux", "$data_root/zdesktop.webapp/override.ini");
print get_message('Done'), "\n";

# create desktop icon
print get_message('CreateIcon');
find_and_replace("$app_root/linux/zd.desktop", $tokens);
exit 1 if system("cp -p $app_root/linux/zd.desktop $icon_dir");
print get_message('Done'), "\n";

print get_message('Success'), "\n\n";
print get_message('RunCommand'), "\n";
print "\"$app_root/linux/prism/zdclient\" -webapp \"$data_root/zdesktop.webapp\" -override \"$data_root/zdesktop.webapp/override.ini\" -profile \"$data_root/profile\"\n\n";


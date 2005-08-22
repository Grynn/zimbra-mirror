#!/usr/bin/perl

#-----------------------------------------------------------------------------#
# File: license.pl
# Author: Conrad Damon
# Date: 8/19/05
#
# This file prepends (or replaces) a block of license text to text files
# under the given directory.
#
# ./license.pl -l license -c config -d directory -D -n
#
# license: text file containing license block
# config: config file that controls behavior, defaults to "license.cfg"
# directory: start directory, defaults to "."
# -D: debug - don't do any p4 commands
# -n: do nothing - don't rewrite any files
#
# The config file controls which directories and files get processed. There
# are white and black lists (of regexes) for both.
#
# Dotfiles are ignored.
#
# TODO: Handle license insertion for more file types
#-----------------------------------------------------------------------------#

require "getopts.pl";

&Getopts('l:c:d:Dn');

my $license_file = $opt_l;
my $config_file = $opt_c ? $opt_c : 'license.cfg';
my $start_dir = $opt_d ? $opt_d : '.';
my $do_nothing = $opt_n;
my $debug = $opt_D;

&usage() unless ($opt_l);

my $cfg = &get_config($config_file);
my $license = &get_license($license_file);
my $license_ver = &get_license_version($license);
&process($start_dir, $cfg, $license);

exit 0;

# Reads the config file into a key/value hash.
sub get_config {

	my $file = shift;

	my $cfg = {};
	return $cfg if (!$file);
	open(F, $file) or die "Open of config file $file failed: $!\n";
	while (<F>) {
		chomp;
		next unless (/\S+/);
		next if (/^#/);
		my ($field, $value) = split(/\s*=\s*/, $_);
		if ($value) {
			$value =~ s/[\n\r]//g;
			$cfg->{$field} = $value;
		}
	}
	close F;
	return $cfg;
}

# Finds the license version in a file.
sub get_license {

	my $file = shift;

	open(F, $file) or die "Open of license file $file failed: $!\n";
	my @lines = <F>;
	close F;
	return join("", @lines);
}

# Replaces or adds the license to the files in the given directory.
sub process {

	my ($dir, $cfg, $license) = @_;

	opendir(DIR, $dir) or die "Could not open $dir: $!\n";
	my @files = grep(!/^\./, readdir DIR);

	my $fb = $cfg->{FileBlack};
	my $fw = $cfg->{FileWhite};
	my $db = $cfg->{DirBlack};
	my $dw = $cfg->{DirWhite};
	my $file;
	my @dirs;
	foreach $file (@files) {
		my $path = "$dir/$file";
		if (-f $path) {
			# it's a regular file
			if (($fb && ($file =~ /$fb/)) || ($fw && ($file !~ /$fw/))) {
				next;
			}
			if (!$do_nothing) {
				&set_license($path, $license);
			}
			print "$path\n";
		} elsif (-d $path) {
			# it's a directory
			if (($db && ($file =~ /$db/)) || ($dw && ($file !~ /$dw/))) {
				next;
			}
			push(@dirs, $path);
		}
	}
	my $d;
	foreach $d (@dirs) {
		&process($d, $cfg, $license);
	}
}

# Adds/replaces the license within a file.
sub set_license {

	my ($path, $license) = @_;
	
	unless (open(F, $path)) {
		warn "Open of $path failed: $!\n";
		return;
	}
	my @lines = <F>;
	close F;
	my $text = join("", @lines);
	$text =~ s/\r\n/\n/g;	# convert PC returns
	$text =~ s/\r/\n/g;		# convert Mac returns
	my $file_lic_ver = &get_license_version($text);
	if ($file_lic_ver eq $license_ver) {
		return;
	}
	if (!$debug) {
		system "p4 edit $path";
	}
	if ($text =~ /\*\*\*\*\* BEGIN LICENSE BLOCK \*\*\*\*\*/) {
		$text =~ s/\*\*\*\*\* BEGIN LICENSE BLOCK \*\*\*\*\*.+\*\*\*\*\* END LICENSE BLOCK \*\*\*\*\*/$license/;
	} else {
		$text = &add_license($text, $path, $license);
	}
	my $tmp = $path . '.tmp';
	unless (open(F, ">$tmp")) {
		warn "Open of $tmp failed: $!\n";
		return;
	}
	print F $text;
	close F;
	system "mv $tmp $path";
}

# Finds the license version in a block of text.
sub get_license_version {
	
	my $text = shift;
	
	my ($ver) = $text =~ /\*\*\*\*\* BEGIN LICENSE BLOCK \*\*\*\*\*\s+Version:\s*([\w\. ]+)/;
	return $ver;
}

# Adds the license to a file. The file type must be determined so the license can be wrapped
# in the proper comment sequence.
sub add_license {
	
	my ($text, $path, $license) = @_;

	if ($path =~ /\.(js|css|java)$/) {
		$text = "/*\n" . $license . "*/\n\n" . $text;
	} elsif ($path =~ /\.xml(\.\w+)?$/) {
		if ($text =~ /^<\?xml/) {
			my $lic_block = "\n<!-- \n$license" . "-->\n\n";
			$text =~ s/(<\?xml.+\?>\n)/\1\n$lic_block/;
		} else {
			$text = "<!-- \n" . $license . "-->\n\n" . $text;
		}
	} elsif ($path =~ /\.(html|jsp)$/) {
		$text = "<!-- \n" . $license . "-->\n\n" . $text;
	} elsif ($path =~ /\.(pl|pm)$/) {
		$text = "=begin\n" . $license . "=end\n=cut\n\n" . $text;
	}
	return $text;
}

# Usage statement
sub usage {
	print "You need to provide a license file.\n";
	exit 1;
}

#/*
# * ***** BEGIN LICENSE BLOCK *****
# * 
# * Zimbra Desktop
# * Copyright (C) 2009 Zimbra, Inc.
# * 
# * The contents of this file are subject to the Yahoo! Public License
# * Version 1.0 ("License"); you may not use this file except in
# * compliance with the License.  You may obtain a copy of the License at
# * http://www.zimbra.com/license.
# * 
# * Software distributed under the License is distributed on an "AS IS"
# * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# * 
# * ***** END LICENSE BLOCK *****
# */
#
# A build tool that fills in content elements for Wix Project file (.wxs)
#
# Usage: wxs-get-contents.pl <build root>
#

use strict;
use warnings;
use Digest::MD5 qw(md5_hex);

# Generate GUID for a given string
sub gen_guid {
    my $seed = shift;
    my $md5 = uc md5_hex($seed);
    my @octets = $md5 =~ /(.{2})/g;

    substr $octets[6], 0, 1, '4'; # GUID Version 4
    substr $octets[8], 0, 1, '8'; # draft-leach-uuids-guids-01.txt GUID variant
    my $GUID = "@octets[0..3]-@octets[4..5]-@octets[6..7]-@octets[8..9]-@octets[10..15]";

    $GUID =~ s/ //g;
    return $GUID;
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
    unlink $file;
    rename $tmpfile, $file;
}

my $build_root = lc($ARGV[0]);
my $build_root_len = length($build_root);
my $wxs_file = "$build_root\\build\\win_installer.wxs";
my $dist_dir = "$build_root\\build\\dist";
my ($xml_app, $xml_refs) = ("", "");
my $seed_prefix = "zimbra_desktop_";
my $cid_counter = 20000;

sub handler_zdrun_vbs {
	my ($ind, $xml) = @_;
	
	$$xml .= ">\r\n";
	$$xml .= "$ind  <Shortcut Id=\"ID_42446FE2_8C85_4ca0_8AEB_4143383960E8\" Directory=\"ProgramMenuDir\" Advertise=\"no\" Name=\"Zimbra Desktop\" Icon=\"ID_BB30ED2D_2B17_4c39_B242_BE52A559A470\">\r\n";
    $$xml .= "$ind    <Icon Id=\"ID_BB30ED2D_2B17_4c39_B242_BE52A559A470\" SourceFile=\"$dist_dir\\app\\data\\zdesktop.webapp\\icons\\default\\launcher.ico\" />\r\n";
    $$xml .= "$ind  </Shortcut>\r\n";
    $$xml .= "$ind</File>\r\n";
}

my $handlers = {
	"$dist_dir\\app\\win32\\zdrun.vbs" => \&handler_zdrun_vbs,
};

sub next_cid {
	$cid_counter++;
	my $cid = 'ID' . "$cid_counter";
	$xml_refs .= "      <ComponentRef Id=\"$cid\" />\r\n";
	return $cid;
}

sub guid_by_path {
	my $path = shift;
	my $seed = $seed_prefix . substr($path, $build_root_len);
	return gen_guid($seed);	
}

sub traverse {
    my ($dir, $indent, $id, $name, $xml) = @_;
    my ($dh, $path, $n, $p);
    my @subdirs = ();
    
    if (!opendir($dh, $dir)){
        print "Warning: cannot open directory $dir\n";
        return;
    }
    
    my $guid = guid_by_path($dir);
    my $dirid = $id ? $id : "DIRID_$guid";
    $dirid =~ tr/-/_/;
    my $cid = next_cid();
    $$xml .= "$indent<Directory Id=\"$dirid\" Name=\"$name\">\r\n";
    
    $$xml .= "$indent  <Component Id=\"$cid\" Guid=\"$guid\">\r\n";
    $$xml .= "$indent    <CreateFolder />\r\n";   
    while($n = readdir($dh)) {
        next if ($n eq '.' || $n eq '..');
        
        $path = "$dir\\$n";        
        if (-d $path) {
            push(@subdirs, $n);
        } else {
        	my $fileid = 'FILEID_' . guid_by_path($path);
        	$fileid =~ tr/-/_/;
            $$xml .= "$indent    <File Id=\"$fileid\" Source=\"$path\" DiskId=\"1\" Name=\"$n\"";
            
            my $handler = $handlers->{lc($path)};
            if ($handler) {
            	$handler->("$indent    ", $xml);
            } else {
            	$$xml .= " />\r\n";
            }
        }
    }
    close($dh);  
    $$xml .= "$indent  </Component>\r\n";
	
	foreach my $subdir (@subdirs) {
		traverse("$dir\\$subdir", "$indent  ", "", $subdir, $xml);	
	}
	
	$$xml .= "$indent</Directory>\r\n";
}

traverse("$dist_dir\\app", "          ", "APPLICATIONFOLDER", "Zimbra Desktop", \$xml_app);

my $tokens = {
	'@build.application.folder.contents@' => $xml_app, 
	'@build.component.references@' => $xml_refs
};
find_and_replace($wxs_file, $tokens);

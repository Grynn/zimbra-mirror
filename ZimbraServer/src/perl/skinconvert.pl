#!/usr/bin/perl
# 
# ***** BEGIN LICENSE BLOCK *****
# Version: MPL 1.1
# 
# The contents of this file are subject to the Mozilla Public License
# Version 1.1 ("License"); you may not use this file except in
# compliance with the License. You may obtain a copy of the License at
# http://www.zimbra.com/license
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
# the License for the specific language governing rights and limitations
# under the License.
# 
# The Original Code is: Zimbra Collaboration Suite Server.
# 
# The Initial Developer of the Original Code is Zimbra, Inc.
# Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
# All Rights Reserved.
# 
# Contributor(s):
# 
# ***** END LICENSE BLOCK *****
# 

# Conversion steps
#
# 1) Rename "{skin}.xml" to "manifest.xml"
#
# 2) Insert the following <script> section into manifest.xml
#    within the <skin> tag:
#
#	<script>
#		<file>../_base/base/ZmSkin.js</file>
#		<file>../_base/base/BaseSkin.js</file>
#		<file>../_base/base/skin.js</file>
#	</script>
#
# 3) Replace the following string patterns in manifest.xml:
#
#      _base/base/base_subs.txt  ->  _base/base/skin.properties
#      _base/base/base.css       ->  _base/base/skin.css
#      _base/base/base.html      ->  _base/base/skin.html
#      {skin}_subs.txt           ->  skin.properties
#      {skin}.css                ->  skin.css
#
# 4) Replace string patterns in skin.properties and skin.css
#
#      _BaseColor_    ->  AppC
#      _BaseColorD##  ->  AppC+##
#      _BaseColorL##  ->  AppC-##
#      _BaseColor_    ->  AppC
#      _BaseColorD##  ->  AppC+##
#      _BaseColorL##  ->  AppC-##
#
# This step also adds the following entries to skin.properties:
#
#      AltC    = @AppC@
#      AltC+## = @AppC+##@
#      AltC-## = @AppC-##@

# Step -1: Check params

die "usage: $0 skin_manifest_file\n" if @ARGV != 1;

# Step 0: Make sure that this skin can be converted

$ofilename = $ARGV[0];
@dirparts = split("/", $ofilename);
$dirname = join("/", @dirparts[0 .. $#dirparts - 1]);
$skinname = @dirparts[-2];

$mfilename = join("/", $dirname, "manifest.xml");
$sfilename = join("/", $dirname, "skin.properties");
$cfilename = join("/", $dirname, "skin.css"); 

%filenames = (
  manifest => {
    content => "", old => $ofilename, new => $mfilename
  },
  substitutions => {
    content => "", old => join("/", $dirname, "${skinname}_subs.txt"), new => $sfilename
  },
  style => {
    content => "", old => join("/", $dirname, "${skinname}.css"), new => $cfilename
  }
);

die "error: Skin already converted" if $ofilename eq $mfilename;

open(FILE, $ofilename) || die "error: Cannot open $ofilename\n";
die "error: This skin cannot be converted\n" if !grep(m#_base/base/base.html#, <FILE>);
close FILE;

# Step 1: Rename "{skin}.xml" to "manifest.xml"

print "Renaming files\n";

foreach $filename (keys %filenames) {
  $filename1 = $filenames{$filename}{old};
  $filename2 = $filenames{$filename}{new};
  rename($filename1, $filename2) || print STDERR "warning: unable to rename $filename1\n";
}

# Step 1.5: Read contents of files

foreach $map (values %filenames) {
  $filename = $map->{new};

  if (open(FILE, $filename)) {
    $map->{contents} = join("", <FILE>);
    close FILE;
  }
  else {
    print STDERR "warning: unable to read $filename\n";
  }
}

# Step 2: Insert <script> section into manifest.xml

print "Inserting <script> section in manifest\n";

$script = join("\n",
	"", 
	"\t<script>",
	"\t\t<file>../_base/base/ZmSkin.js</file>",
	"\t\t<file>../_base/base/BaseSkin.js</file>",
	"\t\t<file>../_base/base/skin.js</file>",
	"\t</script>"
);

$manifest = $filenames{manifest};
$manifest->{contents} =~ s/(<skin>)/$1$script/;

# Step 3: Replace string patterns in manifest.xml

print "Converting filenames in manifest\n";

$manifest->{contents} =~ s#([^/]+)/\1_subs.txt#$1/skin.properties#;
$manifest->{contents} =~ s#([^/]+)/\1.css#$1/skin.css#;
$manifest->{contents} =~ s#([^/]+)/\1.html#$1/skin.html#;

$manifest->{contents} =~ s/${skinname}_subs.txt/skin.properties/;
$manifest->{contents} =~ s/${skinname}.css/skin.css/;

# Step 4: Replace string patterns in skin.properties

print "Converting color key names in substitution and style file\n";

@filenames = ( $filenames{substitutions}, $filenames{style} );

foreach $map (@filenames) {
  next unless defined $map->{contents};
  
  $map->{contents} =~ s/_BaseColor_/AppC/mg;
  $map->{contents} =~ s/_BaseColor([DL])(\d+)_/"AppC".($1 eq "D" ? "+" : "-").sprintf("%02d",$2)/emg;
  $map->{contents} =~ s/_SelColor_/SelC/mg;
  $map->{contents} =~ s/_SelColor([DL])(\d+)_/"SelC".($1 eq "D" ? "+" : "-").sprintf("%02d",$2)/emg;
}

$substitutions = $filenames{substitutions};
if (defined $substitutions->{contents}) {
  @altc = ();
  for ($i = 95; $i > 0; $i -= 5) {
    push(@altc, sprintf("AltC+%02d = \@AppC+%02d\@", $i, $i));
  }
  for ($i = 5; $i < 100; $i += 5) {
    push(@altc, sprintf("AltC-%02d = \@AppC-%02d\@", $i, $i));
  }
  $altc = join("\n", @altc);

  $substitutions->{contents} .= "\n$altc\n";
}

#### TODO: do the same for all the CSS files ####

# Step 5: Finish

print "Saving changes\n";

foreach $map (values %filenames) {
  next unless defined $map->{contents};

  open(FILE, ">$map->{new}");
  print FILE $map->{contents};
  close FILE;
}

print "Done.\n";
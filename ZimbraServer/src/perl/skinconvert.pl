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
# 2) Create file "skin.js" with contents:
#
#      skin = new BaseSkin();
#
# 3) Insert the following <script> section into manifest.xml
#    within the <skin> tag:
#
#	<script>
#		<file>../_base/base/ZmSkin.js</file>
#		<file>../_base/base/BaseSkin.js</file>
#		<file>skin.js</file>
#	</script>
#
# 4) Replace the following string patterns in manifest.xml:
#
#      _base/base/base_subs.txt  ->  _base/base/skin.properties
#      _base/base/base.css       ->  _base/base/skin.css
#      _base/base/base.html      ->  _base/base/skin.html

# Step -1: Check params

die "usage: $0 skin_manifest_file\n" if @ARGV != 1;

# Step 0: Make sure that this skin can be converted

$ofilename = $ARGV[0];
@dirparts = split("/", $ofilename);
$dirname = join("/", @dirparts[0 .. $#dirparts - 1]);
$nfilename = join("/", $dirname, "manifest.xml");

die "error: Skin already converted" if $ofilename eq $nfilename;

open(FILE, $ofilename) || die "error: Cannot open $ofilename\n";
die "error: This skin cannot be converted\n" if !grep(m#_base/base/base.html#, <FILE>);
close FILE;

# Step 1: Rename "{skin}.xml" to "manifest.xml"

print "Renaming manifest file\n";

rename $ofilename, $nfilename || die "error: Unable to move $ofilename\n";

# Step 2: Create file "skin.js"

print "Creating skin source file\n";

$filename = join("/", $dirname, "skin.js");

open(FILE, ">$filename") || die "error: Unable to create $filename\n";
print FILE "skin = new BaseSkin();\n";
close FILE;

# Step 3: Insert <script> section into manifest.xml

print "Inserting <script> section in manifest\n";

open(FILE, $nfilename) || die "error: unable to open $nfilename\n";
@file = <FILE>;
$file = join("", @file);
close FILE;

$script = join("\n", 
	"", 
	"\t<script>",
	"\t\t<file>../_base/base/ZmSkin.js</file>",
	"\t\t<file>../_base/base/BaseSkin.js</file>",
	"\t\t<file>skin.js</file>",
	"\t</script>"
);
$file =~ s/(<skin>)/$1$script/;

# Step 4: Replace string patterns in manifest.xml

print "Converting filenames in manifest\n";

$file =~ s#([^/]+)/\1_subs.txt#$1/skin.properties#;
$file =~ s#([^/]+)/\1.css#$1/skin.css#;
$file =~ s#([^/]+)/\1.html#$1/skin.html#;

# Step 5: Finish

print "Saving changes\n";

open(FILE, ">$nfilename");
print FILE $file;
close FILE;

print "Done.\n";
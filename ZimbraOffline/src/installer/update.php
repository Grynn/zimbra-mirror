<?php
// production update url  : https://www.zimbra.com/aus/zdesktop/update.php
// development update url : http://devel.zimbra.com/aus/desktop/nightly/update.php

$download_url_prefix = "http://files.zimbra.com/downloads/zdesktop/updates";
$details_url = "http://wiki.zimbra.com/index.php?title=Zimbra_Desktop";
$license_url = "http://www.zimbra.com/license/zimbra_public_eula_2.1.html";
$version = "@version@";
$buildid = @buildid@;

$size_win32 = @size_win32@;
$size_macos = @size_macos@;
$size_linux = @size_linux@;

$hash_win32 = "@hash_win32@";
$hash_macos = "@hash_macos@";
$hash_linux = "@hash_linux@";

$file_prefix = "zdesktop_" . str_replace(".", "_", $version) . "_build_" . $buildid;

$oldchn = $_REQUEST['chn'];
$oldver = $_REQUEST['ver'];
$oldbid = $_REQUEST['bid'];
$target = $_REQUEST['bos'];
$locale = $_REQUEST['loc'];

$file_suffix;
$size;
$hash;
if ($target == "macos") {
  $file_suffix = "@suffix_macos@";
  $size = $size_macos;
  $hash = $hash_macos;
} else if ($target == "linux") {
  $file_suffix = "@suffix_linux@";
  $size = $size_linux;
  $hash = $hash_linux;
} else {
  $file_suffix = "@suffix_win32@";
  $size = $size_win32;
  $hash = $hash_win32;
}

$download_url = $download_url_prefix . "/" . $buildid . "/" . $file_prefix . "_" . $file_suffix;

header('Content-Type: text/xml');
header('Cache-Control: no-cache');

echo "<?xml version=\"1.0\"?>\n";
?>
<updates>
<?php if ($buildid > $oldbid) { ?>
  <update type="minor" version="<?php echo $version?>" detailsURL="<?php echo $details_url?>" licenseURL="<?php echo $license_url?>">
    <patch type="complete" URL="<?php echo $download_url?>" hashFunction="md5" hashValue="<?php echo $hash?>" size="<?php echo $size?>"/>
  </update>
<?php } ?>
</updates>

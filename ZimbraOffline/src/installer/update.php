<?php
// update url  : https://www.zimbra.com/aus/zdesktop2/update.php

$download_url_prefix = "http://files2.zimbra.com/downloads/zdesktop";
$details_url = "http://www.zimbra.com/support/documentation/zd-documentation.html";
$license_url = "http://www.zimbra.com/license/zimbra-public-eula-2-3.html";
$version = "@version@";
$extension_version = "2.0.1";
$buildid = @buildid@;

$size_win32 = @size_win32@;
$size_macos = @size_macos@;
$size_linux = @size_linux@;

$hash_win32 = "@hash_win32@";
$hash_macos = "@hash_macos@";
$hash_linux = "@hash_linux@";

$oldchn = $_REQUEST['chn'];
$oldver = $_REQUEST['ver'];
$oldbid = $_REQUEST['bid'];
$target = $_REQUEST['bos'];

$file_media;
$size;
$hash;
if ($target == "macos") {
  $file_media = "@media_macos@";
  $size = $size_macos;
  $hash = $hash_macos;
} else if ($target == "linux") {
  $file_media = "@media_linux@";
  $size = $size_linux;
  $hash = $hash_linux;
} else {
  $file_media = "@media_win32@";
  $size = $size_win32;
  $hash = $hash_win32;
}

$download_url = $download_url_prefix . "/" . $version . "/b" . $buildid . "/" . $file_media;

header('Content-Type: text/xml');
header('Cache-Control: no-cache');

echo "<?xml version=\"1.0\"?>\n";
?>
<updates>
<?php if ($buildid > $oldbid) { ?>
  <update type="minor" version="<?php echo $version?>" extensionVersion="<?php echo $extension_version?>" detailsURL="<?php echo $details_url?>" licenseURL="<?php echo $license_url?>">
    <patch type="complete" URL="<?php echo $download_url?>" hashFunction="md5" hashValue="<?php echo $hash?>" size="<?php echo $size?>"/>
  </update>
<?php } ?>
</updates>

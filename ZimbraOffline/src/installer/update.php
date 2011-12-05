<?php
require_once("zd-versions.php");

// update url  : https://www.zimbra.com/aus/zdesktop2/update.php

$buildid = 0;

$oldchn = $_REQUEST['chn'];
$oldver = $_REQUEST['ver'];
$oldbid = $_REQUEST['bid'];

$os = "macos"; //remove after testing

if (isset($_REQUEST["bos"])) {
  $os = $_REQUEST['bos'];
}

$channels = array("release");

if (strcasecmp($oldchn, "beta") == 0) {
  $channels = array("beta","release");
}

$type = "ZD";

$zd = $versions["ZD"];
foreach($channels as &$chn) {
  if (!empty($zd[$chn])) {
    if (!empty($zd[$chn][$os])) {
      if ($zd[$chn][$os]["buildnum"] > $buildid)
      {
        $currentBuild = $zd[$chn][$os];
        $buildid = $currentBuild["buildnum"];
      }
    }
  }
} 



$download_url = $currentBuild["download_url_prefix"] . "/" . $currentBuild["shortversion"] . "/b" . $buildid . "/" . $currentBuild["file_media"];
$build_text = $currentBuild["shortversion"] . " build " . $buildid;

header('Content-Type: text/xml');
header('Cache-Control: no-cache');

echo "<?xml version=\"1.0\"?>\n";
?>
<updates>
<?php if ($buildid > $oldbid) { ?>
  <update type="minor" version="<?php echo $build_text?>" extensionVersion="<?php echo $currentBuild["extension_version"]?>" detailsURL="<?php echo $currentBuild["details_url"]?>" licenseURL="<?php echo $currentBuild["license_url"]?>">
    <patch type="complete" URL="<?php echo $download_url?>" hashFunction="md5" hashValue="<?php echo $currentBuild["hash"]?>" size="<?php echo $currentBuild["size"]?>"/>
  </update>
<?php } ?>
</updates>

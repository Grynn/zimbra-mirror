<?php
echo "<?xml version=\"1.0\"?>\n";
$majorUpdate; //only latest
$minorUpdate; //only latest
$microUpdates = array(); //all available

if(isset($_REQUEST["majorversion"]) && 
	isset($_REQUEST["minorversion"]) && 
	isset($_REQUEST["microversion"]) && 
	isset($_REQUEST["platform"]) && 
	isset($_REQUEST["type"])) {
	$versions = array (
		"NETWORK" => array (
			"RHEL4"=>array (
				6=>array (
					0=>array (
						2=>array(
							"shortversion"=>"6.0.2",
							"critical"=>1,
							"updateURL"=>"http://www.zimbra.com/community/downloads.html",
							"release"=>"20090921024654",
							"version"=>"6.0.2_GA_1841.RHEL4.NETWORK",
							"description"=>" "
						)					
					)					
				)
			),
			"RHEL5"=>array(),
			"RHEL5_64"=>array(),
			"MACOSXx86"=>array(),
			"UBUNTU8"=>array(),
			"UBUNTU6"=>array()
		),
		"FOSS" => array()
	);
	
	$type = $_REQUEST["type"];
	$platform = $_REQUEST["platform"];
	$majorversion = intval($_REQUEST["majorversion"]);
	$minorversion = intval($_REQUEST["minorversion"]);
	$microversion = intval($_REQUEST["microversion"]);
	
	if(!empty($versions[$type])) {
		$my_type = $versions[$type]; 
		if(!empty($my_type[$platform])) {
			$my_platform = $my_type[$platform];
			foreach($my_platform as $major_key=>$my_major) {
				if(intval($major_key)==$majorversion) {
					//look for minor updates
					foreach($my_major as $minor_key=>$my_minor) {
						if($minor_key == $minorversion) {
							//look for micro updates
							foreach($my_minor as $micro_key=>$my_micro) {
								if($micro_key>$microversion) {
									array_push($microUpdates,$my_micro);
								}
							}
						} else if($minor_key > $minorversion) {
							//minor update available
							$latestMicroKey = 0;
							foreach($my_minor as $micro_key=>$my_micro) {
								if($latestMicroKey <= $micro_key) {
									//find the latest minor update
									$latestMicroKey = $micro_key;
								}
							}
							$minorUpdate = $my_minor[$latestMicroKey]; 							
						}
					}
				} else if(intval($major_key)>$majorversion) {
					//major update available
					$latestMinorKey = 0;
					foreach($my_major as $minor_key=>$my_minor) {
						if($minor_key >= $latestMinorKey) {
							$latestMinorKey = $minor_key; 
						} 
					}
					if(!empty($my_major[$latestMinorKey])) {
						$my_minor = $my_major[$latestMinorKey];
						$latestMicroKey = 0;
						foreach($my_minor as $micro_key=>$my_micro) {
							if($latestMicroKey <= $micro_key) {
								//find the latest minor update
								$latestMicroKey = $micro_key;
							}
						}
						if(!empty($my_minor[$latestMicroKey])) {
							$majorUpdate = 	$my_minor[$latestMicroKey];								
						}
					}
				}
			}
		}
	}
}
if(!empty($majorUpdate) || !empty($minorUpdate) || !empty($microUpdates)) {
?>
<versionCheck status="1">
<updates>
<?php
	if(!empty($majorUpdate)) {
?><update type="major" shortversion="<?php echo $majorUpdate["shortversion"]; ?>" version="<?php 
	echo $majorUpdate["version"]; 
	?>" release="<?php
		echo $majorUpdate["release"]; 
	?>" critical="<?php 
		echo $majorUpdate["critical"]; 
	?>" updateURL="<?php 
		echo $majorUpdate["updateURL"]; 
	?>" description="<?php
		echo $majorUpdate["description"]; 
	?>"/>
<?php	
	}
	if(!empty($minorUpdate)) {
?><update type="minor" shortversion="<?php 
		echo $minorUpdate["shortversion"]; 
	?>" version="<?php 
		echo $minorUpdate["version"]; 
	?>" release="<?php
		echo $minorUpdate["release"]; 
	?>" critical="<?php
		echo $minorUpdate["critical"]; 
	?>" updateURL="<?php 
		echo $minorUpdate["updateURL"]; 
	?>" description="<?php 
		echo $minorUpdate["description"]; 
	?>"/>	
<?php	
	}
	if(!empty($microUpdates)) {
		foreach($microUpdates as $key=>$val) {
			?><update type="micro" shortversion="<?php
				echo $val["shortversion"];
			?>" version="<?php
				echo $val["version"];
			?>" release="<?php
				echo $val["release"];
			?>" critical="<?php
				echo $val["critical"];
			?>" updateURL="<?php
				echo $val["updateURL"];
			?>" description="<?php
				echo $val["description"];
			?>"/>
<?php			
		}
	}
?>
</updates>
<?php
} else {
?>
<versionCheck status="0">
<?php
}
?>
</versionCheck>
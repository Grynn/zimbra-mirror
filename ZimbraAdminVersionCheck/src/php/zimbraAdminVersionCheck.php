<?php
require_once("versions.php");
echo "<?xml version=\"1.0\"?>\n";
$majorUpdate; //only latest
$minorUpdate; //only latest
$microUpdate = array(); //only latest
$buildUpdates = array(); //all available

if(isset($_REQUEST["majorversion"]) && 
	isset($_REQUEST["minorversion"]) && 
	isset($_REQUEST["microversion"]) && 
	isset($_REQUEST["platform"]) && 
	isset($_REQUEST["type"]) && 
	isset($_REQUEST["buildnum"])) {
	
	$type = $_REQUEST["type"];
	$platform = $_REQUEST["platform"];
	$majorversion = intval($_REQUEST["majorversion"]);
	$minorversion = intval($_REQUEST["minorversion"]);
	$microversion = intval($_REQUEST["microversion"]);
	$buildnum = intval($_REQUEST["buildnum"]);
	
	if(!empty($versions[$type])) {
		$my_type = $versions[$type]; 
		if(!empty($my_type[$platform])) {
			$my_platform = $my_type[$platform];
			foreach($my_platform as $major_key=>$my_major) {
				if(intval($major_key)==$majorversion) {
					//look for minor updates
					foreach($my_major as $minor_key=>$my_minor) {
						if(intval($minor_key) == $minorversion) {
							//look for micro updates
							foreach($my_minor as $micro_key=>$my_micro) {
								if($micro_key==$microversion) {
									//look for new builds
									foreach($my_micro as $build_key=>$my_build) {
										if($build_key>$buildnum) {
											array_push($buildUpdates,$my_build);
										}
									}
									
								} else if($micro_key>$microversion) {
									//micro updates available
									$latestBuildKey = 0;
									foreach($my_micro as $build_key=>$my_build) {
										if($latestBuildKey <= $build_key) {
											//find the latest micro update
											$latestBuildKey = $build_key;
										}
									}
									if(!empty($my_micro[$latestBuildKey])) {								
										$microUpdate = $my_micro[$latestBuildKey];
									}
								}
							}
						} else if($minor_key > $minorversion) {
							//minor update available
							$latestMicroKey = 0;
							$latestBuildKey = 0;
							foreach($my_minor as $micro_key=>$my_micro) {
								if($latestMicroKey <= $micro_key) {
									//find the latest micro version in the minor update
									$latestMicroKey = $micro_key;
								}
							}
							if(!empty($my_minor[$latestMicroKey])) {
								$my_micro = $my_minor[$latestMicroKey];
								foreach($my_micro as $build_key=>$my_build) {
									if($latestBuildKey <= $build_key) {
										//find the latest build in the micro update
										$latestBuildKey = $build_key;
									}
								}
							}
							if(!empty($my_minor[$latestMicroKey][$latestBuildKey])) {
							 	$minorUpdate = $my_minor[$latestMicroKey][$latestBuildKey]; 						
							}
						}
					}
				} else if(intval($major_key)>$majorversion) {
					//major update available
					$latestMinorKey = 0;
					$latestMicroKey = 0;
					$latestBuildKey = 0;
					
					foreach($my_major as $minor_key=>$my_minor) {
						if($minor_key >= $latestMinorKey) {
							$latestMinorKey = $minor_key; 
						} 
					}
					if(!empty($my_major[$latestMinorKey])) {
						$my_minor = $my_major[$latestMinorKey];
						
						foreach($my_minor as $micro_key=>$my_micro) {
							if($latestMicroKey <= $micro_key) {
								//find the latest minor update
								$latestMicroKey = $micro_key;
							}
						}
						if(!empty($my_minor[$latestMicroKey])) {
							$my_micro = $my_minor[$latestMicroKey];
							foreach($my_micro as $build_key=>$my_build) {
								if($latestBuildKey <= $build_key) {
									$latestBuildKey = $build_key;
								}
							}
						}
					}
					if(!empty($my_major[$latestMinorKey][$latestMicroKey][$latestBuildKey])) {
						$majorUpdate = 	$my_minor[$latestMicroKey];								
					}
					
				}
			}
		}
	}
}
if(!empty($majorUpdate) || !empty($minorUpdate) || !empty($microUpdates) || !empty($buildUpdates)) {
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
	?>" platform="<?php 
		echo $minorUpdate["platform"]; 
	?>" buildtype="<?php 
		echo $minorUpdate["buildtype"]; 
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
	?>" platform="<?php 
		echo $minorUpdate["platform"]; 
	?>" buildtype="<?php 
		echo $minorUpdate["buildtype"]; 
	?>"/>	
<?php	
	}
	if(!empty($microUpdate)) {
?><update type="micro" shortversion="<?php 
		echo $microUpdate["shortversion"]; 
	?>" version="<?php 
		echo $microUpdate["version"]; 
	?>" release="<?php
		echo $microUpdate["release"]; 
	?>" critical="<?php
		echo $microUpdate["critical"]; 
	?>" updateURL="<?php 
		echo $microUpdate["updateURL"]; 
	?>" description="<?php 
		echo $microUpdate["description"]; 
	?>" platform="<?php 
		echo $microUpdate["platform"]; 
	?>" buildtype="<?php 
		echo $microUpdate["buildtype"]; 
	?>"/>	
<?php	
	}
	if(!empty($buildUpdates)) {
		foreach($buildUpdates as $key=>$val) {
			?><update type="build" shortversion="<?php
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
			?>"  platform="<?php 
				echo $val["platform"]; 
			?>" buildtype="<?php 
				echo $val["buildtype"]; 
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
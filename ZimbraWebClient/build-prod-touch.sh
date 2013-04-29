#!/bin/sh
# Touch client production build script.
# Uses the build-prod-touch ant script to initiate touch client production build.

# This script is executed through the ZWC build script, under target "build-prod".
# Sencha production build needs to be executed from the directory where the sencha configuration exists(valid application directory). In our case, the  build script for the client exists in ZWC and 
# our touch client application exists in ZWC/WebRoot/t. As a result, if we trigger the command to build the touch client from the main ant script, it fails because the current directory doesn't 
# contain the Sencha application.

# This is a way around to solve this issue. The root ant script executes this script which in turn executes the touch client build script to build it in production mode.

cd WebRoot/t
echo "[Touch client production build started]"
ant -buildfile build-prod-touch.xml -Dbuild.mode=$1
echo "[Touch client production build ended]"
cd ../..

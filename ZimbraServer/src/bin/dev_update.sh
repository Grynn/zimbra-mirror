#!/bin/sh

SERVICE_TAR=dev-service.tar
LIQUID_TAR=dev-liquid.tar
MON_TAR=dev-mon.tar

TOMCAT_DIR=jakarta-tomcat-5.0.28
WEBAPPS_DIR=$TOMCAT_DIR/webapps
BACKUP_DIR=$TOMCAT_DIR/webapps.bak

if [ x`whoami` != xliquid ]; then
    echo Error: must be run as liquid user
    exit 1
fi

cd
CWD=`pwd`

if [ -e $MON_TAR ]; then
	echo Unpacking $MON_TAR...
	tar xf $MON_TAR
fi

if [ ! -e $SERVICE_TAR ]; then
    echo Missing $CWD/$SERVICE_TAR file
    echo '(This file is built with dev-dist target of LiquidArchive build.xml)'
    exit 1
fi

if [ ! -e $LIQUID_TAR ]; then
    echo Missing $CWD/$LIQUID_TAR file
    echo '(This file is built with dev-dist target of LiquidConsole build.xml)'
    exit 1
fi

echo Killing tomcat...
TOMCAT_PID=`ps -ef | grep tomcat | grep java | grep liquid | grep -v grep | awk '{ print $2; }'`
if [ x$TOMCAT_PID != x ]; then
    kill -9 $TOMCAT_PID
fi

echo Backing up current service/liquid files into $BACKUP_DIR ...
mkdir -p $BACKUP_DIR
rm -rf $BACKUP_DIR/service $BACKUP_DIR/service.war
rm -rf $BACKUP_DIR/liquid $BACKUP_DIR/liquid.war
mv $WEBAPPS_DIR/service $BACKUP_DIR/.
mv $WEBAPPS_DIR/liquid $BACKUP_DIR/.
mv $WEBAPPS_DIR/service.war $BACKUP_DIR/.
mv $WEBAPPS_DIR/liquid.war $BACKUP_DIR/.

echo Unpacking $SERVICE_TAR...
tar xf $SERVICE_TAR
echo Unpacking $LIQUID_TAR...
tar xf $LIQUID_TAR

echo Update finished.
echo

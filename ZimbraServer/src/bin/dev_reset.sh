#!/bin/sh

if [ x`whoami` != xliquid ]; then
    echo Error: must be run as liquid user
    exit 1
fi

cd

echo Killing tomcat...
TOMCAT_PID=`ps -ef | grep tomcat | grep liquid | grep -v grep | awk '{ print $2; }'`
if [ "x$TOMCAT_PID" != x ]; then
    echo kill -9 $TOMCAT_PID
    kill -9 $TOMCAT_PID > /dev/null
fi
rm -f tomcat/logs/*
rm -f log/liquid.log
echo Killing mysql...
ps -ef | grep bin/mysqld | grep liquid | grep -v grep | awk '{ print $2; }' | xargs kill -9
rm -f db/mysql.sock
rm -f log/mysqld.*

echo Removing old mysql data files...
rm -rf db/data/*

echo Removing old blob store and index files...
rm -rf store/* index/*

rm -rf /dev/shm/liquid/store/* /dev/shm/liquid/index/*
rm -rf /dev/shm/liquid/repl/* /dev/shm/liquid/redolog/*
rm -rf /LQDATA/db/data/* /LQMSGS/db/data/*
rm -rf /LQMSGS/store/* /LQMSGS/index/*
rm -rf /LQMSGS/redolog/* /LQMSGS/repl/*
rm -rf /LQREDO/redolog/* /LQREDO/repl/*

echo Initializing new datastore...
bin/lqmyinit > /dev/null

echo Reset finished.

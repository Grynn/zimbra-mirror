#!/bin/sh

timeout=20

while [ $timeout -gt 0 ]; do
  pid=`ps -fe | grep java | grep 'Launcher start com.zimbra.cs.offline.start.Main' | awk '{print $2}'`
  if [ -z $pid ]; then
    exit 0
  fi
  sleep 1
  timeout=$[timeout-1]
done

kill -9 $pid

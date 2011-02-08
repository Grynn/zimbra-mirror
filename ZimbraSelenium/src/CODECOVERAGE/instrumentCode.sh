#!/bin/sh
userName=`whoami`
if [ $userName != "root" ]; then
    echo -e "\e[0;31m Command should be run with Root User .... Exiting \e[m"
    exit
fi
echo -e "\e[1;32mStopping zimbra services \e[m"
sudo su - zimbra -c "zmcontrol stop"
echo -e "\e[1;32mInstrumenting Java Script Code \e[m"
jscoverage --no-instrument=help/ /opt/zimbra/jetty/webapps/zimbra/ /opt/zimbra/jetty/webapps/zimbra_instrumented/
echo -e "\e[1;32mReplacing Zimbra Java Script Code with Instrumented Code \e[m"
mv /opt/zimbra/jetty/webapps/zimbra /opt/zimbra/jetty/webapps/zimbra_original
mv /opt/zimbra/jetty/webapps/zimbra_instrumented/ /opt/zimbra/jetty/webapps/zimbra
echo -e "\e[1;32mStarting zimbra services \e[m"
sudo su - zimbra -c "zmcontrol start"
echo -e "\e[1;32mJava Script Code Instrumentation Completed \e[m"

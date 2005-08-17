#!/bin/sh -x

su - liquid -c '/opt/liquid/bin/tomcat stop'
/opt/liquid/bin/postfix stop
/opt/liquid/bin/mysql.server stop
/opt/liquid/bin/ldap stop

rpm -e --noscripts liquid-store 
rpm -e --noscripts liquid-mta 
rpm -e --noscripts liquid-ldap 
rpm -e --noscripts liquid-snmp
rpm -e --noscripts liquid-qatest
rpm -e --noscripts liquid-core

userdel liquid
userdel postfix
groupdel postdrop

rm -fr /opt/liquid/*

cat /etc/sudoers | grep -v postfix > /tmp/sudoers
cat /tmp/sudoers > /etc/sudoers

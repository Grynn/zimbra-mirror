#!/bin/bash
#
# On dev machines, after you have run 'lqmyinit' to create the
# database, you have to change some settings so build.xml is happy.
# Use this script to make those changes:
#
# - Change passsword for root and liquid mysql users to be "liquid"
#
# - Allow root@localhost, root@localhost.localdomain to also be valid
#   root user.
#

source `dirname $0`/lqshutil || exit 1
lqsetvars liquid_home mysql_root_password

#
# All possible host names for localhost, including two defaults.
#
hosts="`hostname` `hostname -a` localhost localhost.localdomain"
hosts=`(for i in $hosts; do echo $i; done) | sort -u`

#
# Grant privileges more liquid user from all hosts.
#
echo '*' Granting privileges to liquid
(echo 'grant all on *.* to "liquid" identified by "liquid";';
 echo 'grant file on *.* to "liquid";';
 echo 'flush privileges;') | \
        ${liquid_home}/bin/mysql -u root --password="${mysql_root_password}"
for host in $hosts; do
    echo '*' Granting privileges to liquid@$host
    (echo 'grant all on *.* to "liquid"@"'$host'" identified by "liquid";';
     echo 'grant file on *.* to "liquid"@"'$host'";';
     echo 'flush privileges;') | \
        ${liquid_home}/bin/mysql -u root --password="${mysql_root_password}"
done

#
# Change liquid password
#
echo '*' Changing mysql liquid user password to dev default
${liquid_home}/bin/lqmypasswd liquid

#
# Grant privileges to mysql root user connecting through the network
# as build.xml requires this.  Note 1: on production systems you can
# not login as mysql root user through the loopback interface - we can
# change that if we want.  Note 2: the extra localhost is first on the
# list (though it appears elsewhere on the list) because we have to
# first change the password for the user that 'mysql -u root' really
# logs in here as - which is root@localhost.
#
for host in localhost $hosts; do
    echo '*' Granting privileges to root@$host
    (echo 'grant all on *.* to "root"@"'$host'" identified by "liquid" with grant option;';
     echo 'flush privileges;') | \
        ${liquid_home}/bin/mysql -u root --password="${mysql_root_password}"
    mysql_root_password=liquid
done

#
# Change it in local config.
#
echo '*' Saving new passwords to local config
${liquid_home}/bin/lqlocalconfig -f -e mysql_root_password=liquid

**************************************************************************
ReadMe.txt for Zimbra To Zimbra Migrator migration script (Version 1.0)
***************************************************************************
Introduction
************
Zimbra to Zimbra migration tool (zmztozmig) is developed to migrate the Zimbra mail boxes to another Zimbra Server. It does not create the user accounts on destination ZCS server so all the accounts which are supposed to be migrated should be pre provisioned.
An input file is needed to provide parameters to it. The default input file is /opt/zimbra/conf/ztozmig.conf . It contains details about source and ddestination ZCS server (ZCS IP/Name, port, admin user name/password), Number of threads to run simultaneoulsy, directory path for dump and logs, new ZimbraMailTransport value (optional), domain map and accounts list. Please refer to sample config file at /opt/zimbra/conf/ztozmig.conf .
A common log file (ztozlog*.log) will be created for complete process and separate log files will be created for each mail box migration too (user1@mydomain.org*.log). 

usage:
*********************
zmztozmig -[options]
Options details:
-v --version                    Prints version
-h --help                       Shows help
-f --ConfigFile                 Config file path
                                [default file -> /opt/zimbra/conf/zmztozmig.conf]
-d --debug                      prints versbose debug messages

Important Notes:
*****************************************
1. Include the 'ZimbraMailTransport' parameter and set its value which will be used to change the successfully maigrated account's 'ZimbraMailTransport' attribute. e.g.
ZimbraMailTransport=smtp:mta.zcs.mail.mydomain.org

2. If mail accounts are migrated from one domain to another different domain, create alias for original account to work correctly with Calednar appointments which still have old domain addresses.

3. If more than one domain map is required, create the multiple domain map entries. e.g.
DomainMap=myorg1.com myorg2.com
DomainMap=google.com yahoo.com
DomainMap=myorg.net myorg2.net






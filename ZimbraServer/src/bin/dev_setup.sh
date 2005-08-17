#!/bin/bash
if [ `whoami` != liquid ]; then
    echo `basename $0` must be run as liquid.
    exit 1
fi
set -x

#
# Initialize database.  This also starts mysql.
#
lqmyinit

#
# Initialize ldap.  This also starts openldap.
#
lqldapinit

#
# DOMAIN is email domain.  SERVER is DNS resolvable name of the server
# on which mailboxes live - we will redirect users to the server and
# postfix delivers mail to via lmtp to there.
# 
DOMAIN=`hostname -s`.liquidsys.com
SERVER=${DOMAIN}

#
# Provision server.
#
lqlocalconfig -e liquid_server_hostname="${SERVER}"
lqprov cs ${SERVER}

#
# Provision mail domain.
#
lqprov cd ${DOMAIN}

#
# Provision admin account.
#
lqprov ca admin@${DOMAIN} test123 \
    liquidIsAdminAccount TRUE \
    liquidMailHost ${SERVER} \
    liquidMailStatus enabled

#
# Provision user account.
#
lqprov ca user1@${DOMAIN} test123 \
    liquidMailHost ${SERVER} \
    liquidMailStatus enabled

#
# Generating postfix ldap*.cf files referred to by main.cf.
#
lqmtainit ${SERVER}

#
# Start other services.
#
tomcat start
lqconvertctl start
postfix start

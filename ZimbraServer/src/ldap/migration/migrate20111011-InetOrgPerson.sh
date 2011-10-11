#!/bin/bash
sed -i.orig -e '/^objectClass: organizationalPerson/ a objectClass: inetOrgPerson' -e 's/^structuralObjectClass: organizationalPerson/structuralObjectClass: inetOrgPerson/' /opt/zimbra/data/ldap/ldap.bak

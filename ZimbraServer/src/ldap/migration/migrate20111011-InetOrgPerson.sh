#!/bin/bash
sed -i .bak -e '/^objectClass: organizationalPerson/ a objectClass: inetOrgPerson' -e 's/^structuralObjectClass: organizationalPerson/structuralObjectClass: inetOrgPerson/' /opt/zimbra/data/ldap/ldap.bak

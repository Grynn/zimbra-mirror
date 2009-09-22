.bail ON
.read "@ZIMBRA_INSTALL@db/db.sql"
.read "@ZIMBRA_INSTALL@db/directory.sql"
.read "@ZIMBRA_INSTALL@db/wildfire.sql"
.read "@ZIMBRA_INSTALL@db/versions-init.sql"
.read "@ZIMBRA_INSTALL@db/default-volumes.sql"
INSERT INTO config(name, value, description) VALUES ('offline.db.version', '3', 'offline db schema version');

.exit

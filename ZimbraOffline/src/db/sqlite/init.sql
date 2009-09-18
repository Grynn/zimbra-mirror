.bail ON
.read "@ZIMBRA_HOME@/db/db.sql"
.read "@ZIMBRA_HOME@/db/directory.sql"
.read "@ZIMBRA_HOME@/db/wildfire.sql"
.read "@ZIMBRA_HOME@/db/versions-init.sql"
.read "@ZIMBRA_HOME@/db/default-volumes.sql"
INSERT INTO config(name, value, description) VALUES ('offline.db.version', '3', 'offline db schema version');

.exit

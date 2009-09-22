.bail ON
.read "@ZIMBRA_INSTALL@db/db.sql"
.read "@ZIMBRA_INSTALL@db/wildfire.sql"
.read "@ZIMBRA_INSTALL@db/versions-init.sql"
.read "@ZIMBRA_INSTALL@db/default-volumes.sql"
.exit

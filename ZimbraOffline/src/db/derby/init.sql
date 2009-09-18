CONNECT 'jdbc:derby:@ZIMBRA_HOME@/derby;create=true';

RUN '@ZIMBRA_HOME@/db/db.sql';
RUN '@ZIMBRA_HOME@/db/directory.sql';
RUN '@ZIMBRA_HOME@/db/wildfire.sql';
RUN '@ZIMBRA_HOME@/db/versions-init.sql';
RUN '@ZIMBRA_HOME@/db/default-volumes.sql';
INSERT INTO config(name, value, description) VALUES ('offline.db.version', '3', 'offline db schema version');

EXIT;


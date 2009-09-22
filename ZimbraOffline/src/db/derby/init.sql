CONNECT 'jdbc:derby:@ZIMBRA_INSTALL@derby;create=true';

RUN '@ZIMBRA_INSTALL@db/db.sql';
RUN '@ZIMBRA_INSTALL@db/directory.sql';
RUN '@ZIMBRA_INSTALL@db/wildfire.sql';
RUN '@ZIMBRA_INSTALL@db/versions-init.sql';
RUN '@ZIMBRA_INSTALL@db/default-volumes.sql';
INSERT INTO config(name, value, description) VALUES ('offline.db.version', '3', 'offline db schema version');

EXIT;


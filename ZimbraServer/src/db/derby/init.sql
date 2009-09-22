CONNECT 'jdbc:derby:@ZIMBRA_INSTALL@derby;create=true';

RUN '@ZIMBRA_INSTALL@db/db.sql';
RUN '@ZIMBRA_INSTALL@db/wildfire.sql';
RUN '@ZIMBRA_INSTALL@db/versions-init.sql';
RUN '@ZIMBRA_INSTALL@db/default-volumes.sql';

EXIT;


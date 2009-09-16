CONNECT 'jdbc:derby:@ZIMBRA_HOME@/derby;create=true';

RUN '@ZIMBRA_HOME@/db/db.sql';
RUN '@ZIMBRA_HOME@/db/wildfire.sql';
RUN '@ZIMBRA_HOME@/db/versions-init.sql';
RUN '@ZIMBRA_HOME@/db/default-volumes.sql';

EXIT;


create table perf2 (
 id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
 created TIMESTAMP(8),
 name VARCHAR(35),
 appid INT,
 buildid INT,
 browserid INT,
 clientid INT,
 milestoneid INT,
 start BIGINT,
 launched BIGINT,
 loaded BIGINT,
 delta BIGINT,
 delta_internal BIGINT,
 messageid INT
 );


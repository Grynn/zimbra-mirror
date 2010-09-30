
CREATE DATABASE zimbra_antispam;
ALTER DATABASE zimbra_antispam DEFAULT CHARACTER SET utf8;

USE zimbra_antispam;

GRANT ALL ON zimbra_antispam.* TO 'zimbra' IDENTIFIED BY 'zimbra';
GRANT ALL ON zimbra_antispam.* TO 'zimbra'@'127.0.0.1' IDENTIFIED BY 'zimbra';
GRANT ALL ON zimbra_antispam.* TO 'zimbra'@'localhost' IDENTIFIED BY 'zimbra';
GRANT ALL ON zimbra_antispam.* TO 'zimbra'@'localhost.localdomain' IDENTIFIED BY 'zimbra';
GRANT ALL ON zimbra_antispam.* TO 'root'@'localhost.localdomain' IDENTIFIED BY 'zimbra';


CREATE TABLE bayes_expire (
  id int(11) NOT NULL default '0',
  runtime int(11) NOT NULL default '0',
  KEY bayes_expire_idx1 (id)
) ENGINE = InnoDB;

CREATE TABLE bayes_global_vars (
  variable varchar(30) NOT NULL default '',
  value varchar(200) NOT NULL default '',
  PRIMARY KEY  (variable)
) ENGINE = InnoDB;

INSERT INTO bayes_global_vars VALUES ('VERSION','3');

CREATE TABLE bayes_seen (
  id int(11) NOT NULL default '0',
  msgid varchar(200) binary NOT NULL default '',
  flag char(1) NOT NULL default '',
  PRIMARY KEY  (id,msgid)
) ENGINE = InnoDB;

CREATE TABLE bayes_token (
  id int(11) NOT NULL default '0',
  token char(5) NOT NULL default '',
  spam_count int(11) NOT NULL default '0',
  ham_count int(11) NOT NULL default '0',
  atime int(11) NOT NULL default '0',
  PRIMARY KEY  (id, token),
  INDEX bayes_token_idx1 (id, atime)
) ENGINE = InnoDB;

CREATE TABLE bayes_vars (
  id int(11) NOT NULL AUTO_INCREMENT,
  username varchar(200) NOT NULL default '',
  spam_count int(11) NOT NULL default '0',
  ham_count int(11) NOT NULL default '0',
  token_count int(11) NOT NULL default '0',
  last_expire int(11) NOT NULL default '0',
  last_atime_delta int(11) NOT NULL default '0',
  last_expire_reduce int(11) NOT NULL default '0',
  oldest_token_age int(11) NOT NULL default '2147483647',
  newest_token_age int(11) NOT NULL default '0',
  PRIMARY KEY  (id),
  UNIQUE bayes_vars_idx1 (username)
) ENGINE = InnoDB;


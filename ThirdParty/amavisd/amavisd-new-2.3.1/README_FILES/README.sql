USING SQL FOR LOOKUPS, LOG/REPORTING AND QUARANTINE
===================================================

This text contains general SQL-related documentation. For aspects
specific to using SQL database for lookups, please see README.lookups .

Since amavisd-new-20020630 SQL is supported for lookups.
Since amavisd-new-2.3.0 SQL is also supported for storing information
about processed mail (logging/reporting) and optionally for quarantining
to a SQL database.

The amavisd.conf variables and @storage_sql_dsn control access
to a SQL server and specify a database (dsn = data source name).
The @lookup_sql_dsn enables and specifies a database for lookups,
the @storage_sql_dsn enables and specifies a database for reporting
and quarantining. Both settings are independent.

Interpretation of @lookup_sql_dsn and @storage_sql_dsn lists is as follows:
- empty list disables the function and is a default;
- if both lists are empty no SQL support code will be compiled-in on startup,
  reducing the amount of virtual memory needed for each child process;
- a list can contain one or more triples: [dsn,user,passw]; more than one
  triple may be specified to list multiple (backup) SQL servers - the first
  that responds will be used;
- if both lists contain refs to the _same_ triples (not just equal triples),
  only one connection to a SQL server will be used; otherwise two independent
  connections to databases will be used, possibly to different SQL servers,
  which may even be of different type (e.g. SQLlite for lookups (read-only),
  and MySQL for transactional reporting, offering fine lock granularity).

Example setting:
  @lookup_sql_dsn =
  ( ['DBI:mysql:database=mail;host=127.0.0.1;port=3306', 'user1', 'passwd1'],
    ['DBI:mysql:database=mail;host=host2', 'username2', 'password2'],
    ['DBI:Pg:host=host1;dbname=mail'],
    ["DBI:SQLite:dbname=$MYHOME/sql/mail_prefs.sqlite", '', ''] );

  @storage_sql_dsn = @lookup_sql_dsn;  # none, same, or separate database

See man page for the Perl module DBI, and corresponding DBD module
man pages (DBD::mysql, DBD::Pg, DBD::SQLite, ...) for syntax of the
first argument.

Since version 2.3.0 amavisd-new also offers quarantining to a SQL database,
along with a mechanism to release quarantined messages. To enable
quarantining to SQL, the @storage_sql_dsn must be enabled (facilitating
quarantine management), and some or all variables $virus_quarantine_method,
$spam_quarantine_method, $banned_files_quarantine_method and
$bad_header_quarantine_method should specify the value 'sql:'.
Specifying 'sql:' as a quarantine method without also specifying
a database in @storage_sql_dsn is an error.

When setting up access controls to a database, keep in mind that amavisd-new
only needs read-only access to the database used for lookups, the permission
to do a SELECT suffices. For security reasons it is undesirable to permit
other operations such as INSERT, DELETE or UPDATE to a dataset used for
lookups. For managing the lookups database one should preferably use a
different username with more privileges.

The database specified in @storage_sql_dsn needs to provide read/write access
(SELECT, INSERT, UPDATE), and a database server offering transactions
must be used.


Below is an example that can be used with MySQL or PostgreSQL or SQLite.
The provided schema can be cut/pasted or fed directly into the client program
to create a database. The '--' introduces comments according to SQL specs.

-- MySQL notes:
--   - the attribute SERIAL was introduced with MySQL 4.1.0;
--     with earlier versions one can use INT UNSIGNED NOT NULL AUTO_INCREMENT

-- PostgreSQL notes (by Phil Regnauld):
--   - remove the 'unsigned' throughout,
--   - create an amavis username and the database (choose name, e.g. mail)
--       $ creatuser -U pgsql --no-adduser --createdb amavis
--       $ createdb -U amavis mail
--   - populate the database using the schema below:
--       $ psql -U amavis mail < amavisd-pg.sql

-- SQLite notes:
--   - SQLite is well suited for lookups database, but is not appropriate
--     for @storage_sql_dsn due to coarse lock granularity;
--   - replace SERIAL by INTEGER,
--   - leave out 'KEY ...'

-- local users
CREATE TABLE users (
  id         SERIAL PRIMARY KEY,
  priority   integer      NOT NULL DEFAULT '7',  -- 0 is low priority
  policy_id  integer unsigned NOT NULL DEFAULT '1',
  email      varchar(255) NOT NULL,
  fullname   varchar(255) DEFAULT NULL,    -- not used by amavisd-new
  local      char(1),     -- Y/N  (optional field, see note further down)
  KEY email (email)
);
CREATE UNIQUE INDEX users_idx_email ON users (email);

-- any e-mail address, external or local, used as senders in wblist
CREATE TABLE mailaddr (
  id         SERIAL PRIMARY KEY,
  priority   integer      NOT NULL DEFAULT '7',  -- 0 is low priority
  email      varchar(255) NOT NULL,
  KEY email (email)
);
CREATE UNIQUE INDEX mailaddr_idx_email ON mailaddr(email);

-- per-recipient whitelist and/or blacklist,
-- puts sender and recipient in relation wb  (white or blacklisted sender)
CREATE TABLE wblist (
  rid        integer unsigned NOT NULL,  -- recipient: users.id
  sid        integer unsigned NOT NULL,  -- sender: mailaddr.id
  wb         varchar(10)  NOT NULL,  -- W or Y / B or N / space=neutral / score
  PRIMARY KEY (rid,sid)
);

CREATE TABLE policy (
  id         SERIAL PRIMARY KEY,    -- this is the _only_ required field
  policy_name      varchar(32),     -- not used by amavisd-new

  virus_lover          char(1),     -- Y/N
  spam_lover           char(1),     -- Y/N
  banned_files_lover   char(1),     -- Y/N
  bad_header_lover     char(1),     -- Y/N

  bypass_virus_checks  char(1),     -- Y/N
  bypass_spam_checks   char(1),     -- Y/N
  bypass_banned_checks char(1),     -- Y/N
  bypass_header_checks char(1),     -- Y/N

  spam_modifies_subj   char(1),     -- Y/N

  virus_quarantine_to      varchar(64) default NULL,
  spam_quarantine_to       varchar(64) default NULL,
  banned_quarantine_to     varchar(64) default NULL,
  bad_header_quarantine_to varchar(64) default NULL,

  spam_tag_level  float default NULL,  -- higher score inserts spam info headers
  spam_tag2_level float default NULL,  -- inserts 'declared spam' header fields
  spam_kill_level float default NULL,  -- higher score activates evasive actions, e.g.
                                       -- reject/drop, quarantine, ...
                                     -- (subject to final_spam_destiny setting)
  spam_dsn_cutoff_level float default NULL,

  addr_extension_virus      varchar(64) default NULL,
  addr_extension_spam       varchar(64) default NULL,
  addr_extension_banned     varchar(64) default NULL,
  addr_extension_bad_header varchar(64) default NULL,

  warnvirusrecip      char(1)     default NULL, -- Y/N
  warnbannedrecip     char(1)     default NULL, -- Y/N
  warnbadhrecip       char(1)     default NULL, -- Y/N
  newvirus_admin      varchar(64) default NULL,
  virus_admin         varchar(64) default NULL,
  banned_admin        varchar(64) default NULL,
  bad_header_admin    varchar(64) default NULL,
  spam_admin          varchar(64) default NULL,
  spam_subject_tag    varchar(64) default NULL,
  spam_subject_tag2   varchar(64) default NULL,
  message_size_limit  integer     default NULL, -- size in bytes
  banned_rulenames    varchar(64) default NULL  -- comma-separated list of ...
        -- names mapped through %banned_rules to actual banned_filename tables
);



-- R/W part of the dataset (optional)
--   May reside in the same or in a separate database as lookups database;
--   requires support for transactions; specified in @storage_sql_dsn

-- provide unique id for each e-mail address, avoids storing copies
CREATE TABLE maddr (
  id         SERIAL PRIMARY KEY,
  email      varchar(255) NOT NULL,    -- full mail address
  domain     varchar(255) NOT NULL     -- only domain part of the email address
                                       -- with subdomain fields in reverse
) ENGINE=InnoDB;
CREATE UNIQUE INDEX maddr_idx_email     ON maddr   (email);
CREATE        INDEX maddr_idx_domain    ON maddr   (domain);

-- information pertaining to each processed message as a whole
CREATE TABLE msgs (
  mail_id    varchar(12)   NOT NULL,    -- long-term unique mail id
  secret_id  varchar(12)   DEFAULT '',
  am_id      varchar(20)   NOT NULL,
  time_num   integer unsigned NOT NULL, -- rx_time: second since Unix epoch
  time_iso   char(16)      NOT NULL,    -- rx_time: ISO8601 UTC ascii time
  sid        integer unsigned NOT NULL, -- sender: maddr.id
  policy     varchar(255)  DEFAULT '',  -- policy bank path (like macro %p)
  client_addr varchar(255) DEFAULT '',  -- SMTP client IP address (IPv4 or v6)
  size       integer unsigned NOT NULL, -- message size in bytes
  content    char(1),                   -- content type: V/B/S/H/O/C, is NULL
                                        -- ...on partially processed mail
  quar_type  char(1),                   -- quarantined as: ' '/F/Z/B/Q/M
                                        --  none/file/zipfile/bsmtp/sql/mailbox
  dsn_sent   char(1),                   -- was DSN sent? Y/N/q (q=quenched)
  spam_level float,                     -- base message spam level (no boosts)
  message_id varchar(255)  DEFAULT '',  -- mail Message-ID header field
  from_addr  varchar(255)  DEFAULT '',  -- mail From header field,    UTF8
  subject    varchar(255)  DEFAULT '',  -- mail Subject header field, UTF8
  host       varchar(255)  NOT NULL,    -- hostname where amavisd is running
  PRIMARY KEY (mail_id),
  KEY sid (sid)
) ENGINE=InnoDB;
);
CREATE INDEX msgs_idx_sid ON msgs (sid);

-- per-recipient information related to each processed message
CREATE TABLE msgrcpt (
  mail_id    varchar(12)   NOT NULL,     -- (must allow duplicates)
  rid        integer unsigned NOT NULL,  -- recipient: maddr.id
  ds         char(1)       NOT NULL,     -- delivery status: P/R/B/D/T
                                         -- pass/reject/bounce/discard/tempfail
  rs         char(1)       NOT NULL,     -- release status: initialized to ' '
  bl         char(1)       DEFAULT ' ',  -- sender blacklisted by this recip
  wl         char(1)       DEFAULT ' ',  -- sender whitelisted by this recip
  bspam_level float,                     -- spam level + per-recip boost
  smtp_resp  varchar(255)  DEFAULT '',
  KEY rid (rid)
) ENGINE=InnoDB;
CREATE INDEX msgrcpt_idx_mail_id ON msgrcpt (mail_id);
CREATE INDEX msgrcpt_idx_rid     ON msgrcpt (rid);

-- mail quarantine in SQL, enabled by $*_quarantine_method='sql:'
CREATE TABLE quarantine (
  mail_id    varchar(12)   NOT NULL,    -- long-term unique mail id
  chunk_ind  integer unsigned  NOT NULL,-- chunk number, starting with 1
  mail_text  text          NOT NULL,    -- store mail as chunks up to 16 kB
  PRIMARY KEY (mail_id,chunk_ind)
) ENGINE=InnoDB;


-- field msgrcpt.rs is primarily intended for use by quarantine management
-- software; the value initially assigned by amavisd is a space;
-- a short _preliminary_ list of possible values:
--   'V' => viewed (marked as read)
--   'R' => released (delivered) to this recipient
--   'p' => pending (a status given to messages when the admin received the
--                   request but not yet released; targeted to banned parts)
--   'D' => marked for deletion; a cleanup script may delete it


-- =====================
-- Example data follows:
-- =====================
INSERT INTO users VALUES ( 1, 9, 5, 'user1+foo@y.example.com','Name1 Surname1', 'Y');
INSERT INTO users VALUES ( 2, 7, 5, 'user1@y.example.com', 'Name1 Surname1', 'Y');
INSERT INTO users VALUES ( 3, 7, 2, 'user2@y.example.com', 'Name2 Surname2', 'Y');
INSERT INTO users VALUES ( 4, 7, 7, 'user3@z.example.com', 'Name3 Surname3', 'Y');
INSERT INTO users VALUES ( 5, 7, 7, 'user4@example.com',   'Name4 Surname4', 'Y');
INSERT INTO users VALUES ( 6, 7, 1, 'user5@example.com',   'Name5 Surname5', 'Y');
INSERT INTO users VALUES ( 7, 5, 0, '@sub1.example.com', NULL, 'Y');
INSERT INTO users VALUES ( 8, 5, 7, '@sub2.example.com', NULL, 'Y');
INSERT INTO users VALUES ( 9, 5, 5, '@example.com',      NULL, 'Y');
INSERT INTO users VALUES (10, 3, 8, 'userA', 'NameA SurnameA anywhere', 'Y');
INSERT INTO users VALUES (11, 3, 9, 'userB', 'NameB SurnameB', 'Y');
INSERT INTO users VALUES (12, 3,10, 'userC', 'NameC SurnameC', 'Y');
INSERT INTO users VALUES (13, 3,11, 'userD', 'NameD SurnameD', 'Y');
INSERT INTO users VALUES (14, 3, 0, '@sub1.example.net', NULL, 'Y');
INSERT INTO users VALUES (15, 3, 7, '@sub2.example.net', NULL, 'Y');
INSERT INTO users VALUES (16, 3, 5, '@example.net',      NULL, 'Y');
INSERT INTO users VALUES (17, 7, 5, 'u1@example.org',    'u1', 'Y');
INSERT INTO users VALUES (18, 7, 6, 'u2@example.org',    'u2', 'Y');
INSERT INTO users VALUES (19, 7, 3, 'u3@example.org',    'u3', 'Y');

-- INSERT INTO users VALUES (20, 0, 5, '@.',             NULL, 'N');  -- catchall

INSERT INTO policy (id, policy_name,
  virus_lover, spam_lover, banned_files_lover, bad_header_lover,
  bypass_virus_checks, bypass_spam_checks,
  bypass_banned_checks, bypass_header_checks, spam_modifies_subj,
  spam_tag_level, spam_tag2_level, spam_kill_level) VALUES
  (1, 'Non-paying',    'N','N','N','N', 'Y','Y','Y','N', 'Y', 3.0,   7, 10),
  (2, 'Uncensored',    'Y','Y','Y','Y', 'N','N','N','N', 'N', 3.0, 999, 999),
  (3, 'Wants all spam','N','Y','N','N', 'N','N','N','N', 'Y', 3.0, 999, 999),
  (4, 'Wants viruses', 'Y','N','Y','Y', 'N','N','N','N', 'Y', 3.0, 6.9, 6.9),
  (5, 'Normal',        'N','N','N','N', 'N','N','N','N', 'Y', 3.0, 6.9, 6.9),
  (6, 'Trigger happy', 'N','N','N','N', 'N','N','N','N', 'Y', 3.0,   5, 5),
  (7, 'Permissive',    'N','N','N','Y', 'N','N','N','N', 'Y', 3.0,  10, 20),
  (8, '6.5/7.8',       'N','N','N','N', 'N','N','N','N', 'N', 3.0, 6.5, 7.8),
  (9, 'userB',         'N','N','N','Y', 'N','N','N','N', 'Y', 3.0, 6.3, 6.3),
  (10,'userC',         'N','N','N','N', 'N','N','N','N', 'N', 3.0, 6.0, 6.0),
  (11,'userD',         'Y','N','Y','Y', 'N','N','N','N', 'N', 3.0,   7, 7);

-- sender envelope addresses needed for white/blacklisting
INSERT INTO mailaddr VALUES (1, 5, '@example.com');
INSERT INTO mailaddr VALUES (2, 9, 'owner-postfix-users@postfix.org');
INSERT INTO mailaddr VALUES (3, 9, 'amavis-user-admin@lists.sourceforge.net');
INSERT INTO mailaddr VALUES (4, 9, 'makemoney@example.com');
INSERT INTO mailaddr VALUES (5, 5, '@example.net');
INSERT INTO mailaddr VALUES (6, 9, 'spamassassin-talk-admin@lists.sourceforge.net');
INSERT INTO mailaddr VALUES (7, 9, 'spambayes-bounces@python.org');

-- whitelist for user 14, i.e. default for recipients in domain sub1.example.net
INSERT INTO wblist VALUES (14, 1, 'W');
INSERT INTO wblist VALUES (14, 3, 'W');

-- whitelist and blacklist for user 17, i.e. u1@example.org
INSERT INTO wblist VALUES (17, 2, 'W');
INSERT INTO wblist VALUES (17, 3, 'W');
INSERT INTO wblist VALUES (17, 6, 'W');
INSERT INTO wblist VALUES (17, 7, 'W');
INSERT INTO wblist VALUES (17, 5, 'B');
INSERT INTO wblist VALUES (17, 4, 'B');

-- $sql_select_policy setting in amavisd.conf tells amavisd
-- how to fetch per-recipient policy settings.
-- See comments there. Example:
--
-- SELECT *,users.id FROM users,policy
--   WHERE (users.policy_id=policy.id) AND (users.email IN (%k))
--   ORDER BY users.priority DESC;
--
-- $sql_select_white_black_list in amavisd.conf tells amavisd
-- how to check sender in per-recipient whitelist/blacklist.
-- See comments there. Example:
--
-- SELECT wb FROM wblist,mailaddr
--   WHERE (wblist.rid=?) AND (wblist.sid=mailaddr.id) AND (mailaddr.email IN (%k))
--   ORDER BY mailaddr.priority DESC;



Example commands for periodic cleaning of r/w sql tables (MySQL commands)
(discards messages older than a week and data pertaining to these messages):

DELETE FROM msgs WHERE UNIX_TIMESTAMP()-time_num > 7*24*60*60;
DELETE quarantine FROM quarantine LEFT JOIN msgs USING(mail_id)
  WHERE msgs.mail_id IS NULL;
DELETE msgrcpt    FROM msgrcpt    LEFT JOIN msgs USING(mail_id)
  WHERE msgs.mail_id IS NULL;
DELETE FROM maddr
  WHERE NOT EXISTS (SELECT sid FROM msgs    WHERE sid=id)
    AND NOT EXISTS (SELECT rid FROM msgrcpt WHERE rid=id);

An example of a query:

SELECT
  UNIX_TIMESTAMP()-time_num AS age, SUBSTRING(policy,1,2) as pb,
  content AS c, dsn_sent as dsn, ds, bspam_level AS level, size,
  SUBSTRING(sender.email,1,18) AS s,
  SUBSTRING(recip.email,1,18)  AS r,
  SUBSTRING(msgs.subject,1,10) AS subj
  FROM msgs LEFT JOIN msgrcpt         ON msgs.mail_id=msgrcpt.mail_id
            LEFT JOIN maddr AS sender ON msgs.sid=sender.id
            LEFT JOIN maddr AS recip  ON msgrcpt.rid=recip.id
  WHERE content IS NOT NULL AND UNIX_TIMESTAMP()-time_num < 100
  ORDER BY msgs.time_num DESC LIMIT 15;


An example of a log/report database housekeeping - delete old/unused records:

DELETE FROM msgs WHERE UNIX_TIMESTAMP()-time_num > 7*24*60*60;
DELETE FROM msgs WHERE UNIX_TIMESTAMP()-time_num > 60*60 AND content IS NULL;

DELETE quarantine FROM quarantine LEFT JOIN msgs USING(mail_id)
  WHERE msgs.mail_id IS NULL;

DELETE msgrcpt    FROM msgrcpt    LEFT JOIN msgs USING(mail_id)
  WHERE msgs.mail_id IS NULL;

DELETE FROM maddr
  WHERE NOT EXISTS (SELECT sid FROM msgs    WHERE sid=id)
    AND NOT EXISTS (SELECT rid FROM msgrcpt WHERE rid=id);

OPTIMIZE TABLE msgs, msgrcpt, maddr, quarantine;

# 
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
# 
# The contents of this file are subject to the Yahoo! Public License
# Version 1.0 ("License"); you may not use this file except in
# compliance with the License.  You may obtain a copy of the License at
# http://www.zimbra.com/license.
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
# ***** END LICENSE BLOCK *****
# 
package Migrate;

use strict;
use DBI;
use FileHandle;
use POSIX qw(:signal_h :errno_h :sys_wait_h);

#############

my $MYSQL = "mysql";
my $LOGMYSQL = "/opt/zimbra/bin/logmysql";
my $DB_USER = "zimbra";
my $DB_PASSWORD = "zimbra";
my $LOGGER_DB_PASSWORD = "zimbra";
my $DATABASE = "zimbra";
my $LOGGER_DATABASE = "zimbra_logger";
my $ZIMBRA_HOME = $ENV{ZIMBRA_HOME} || '/opt/zimbra';
my $ZMLOCALCONFIG = "$ZIMBRA_HOME/bin/zmlocalconfig";
my $SQLLOGFH;

if ($^O !~ /MSWin/i) {
    $DB_PASSWORD = `$ZMLOCALCONFIG -s -m nokey zimbra_mysql_password`;
    chomp $DB_PASSWORD;
    $DB_USER = `$ZMLOCALCONFIG -m nokey zimbra_mysql_user`;
    chomp $DB_USER;
    $LOGGER_DB_PASSWORD = `$ZMLOCALCONFIG -s -m nokey zimbra_logger_mysql_password`;
    chomp $LOGGER_DB_PASSWORD;
    $MYSQL = "/opt/zimbra/bin/mysql";
}

sub getSchemaVersion {
    my $versionInDb = (runSql("SELECT value FROM config WHERE name = 'db.version'"))[0];
	return $versionInDb;
}

sub getBackupVersion {
    my $versionInDb = (runSql("SELECT value FROM config WHERE name = 'backup.version'"))[0];
	return $versionInDb;
}
sub getRedologVersion {
    my $versionInDb = (runSql("SELECT value FROM config WHERE name = 'redolog.version'"))[0];
	return $versionInDb;
}
sub getLoggerSchemaVersion {
    my $versionInDb = (runLoggerSql("SELECT value FROM config WHERE name = 'db.version'"))[0];
	return $versionInDb;
}

sub verifySchemaVersion($) {
    my ($version) = @_;
    my $versionInDb = getSchemaVersion();
    if ($version != $versionInDb) {
        Migrate::myquit(1,"Schema version mismatch.  Expected version $version.  Version in the database is $versionInDb.\n");
    }
    Migrate::log("Verified schema version $version.");
}

sub verifyLoggerSchemaVersion($) {
    my ($version) = @_;
    my $versionInDb = getLoggerSchemaVersion();
    if ($version != $versionInDb) {
        Migrate::myquit(1,"Schema version mismatch.  Expected version $version.  Version in the database is $versionInDb.\n");
    }
    Migrate::log("Verified schema version $version.");
}
sub verifyBackupVersion($) {
    my ($version) = @_;
    my $versionInDb = getBackupVersion();
    if ($version != $versionInDb) {
        Migrate::myquit(1,"Version mismatch.  Expected version $version.  Version in the database is $versionInDb.\n");
    }
    Migrate::log("Verified backup version $version.");
}
sub verifyRedologVersion($) {
    my ($version) = @_;
    my $versionInDb = getRedologVersion();
    if ($version != $versionInDb) {
        Migrate::myquit(1,"Version mismatch.  Expected version $version.  Version in the database is $versionInDb.\n");
    }
    Migrate::log("Verified redolog version $version.");
}

sub updateLoggerSchemaVersion($$) {
    my ($oldVersion, $newVersion) = @_;
    verifyLoggerSchemaVersion($oldVersion);

    my $sql = <<SET_SCHEMA_VERSION_EOF;
UPDATE zimbra_logger.config SET value = '$newVersion' WHERE name = 'db.version';
SET_SCHEMA_VERSION_EOF

    Migrate::log("Updating logger DB schema version from $oldVersion to $newVersion.");
    runLoggerSql($sql);
}

sub updateSchemaVersion($$) {
    my ($oldVersion, $newVersion) = @_;
    verifySchemaVersion($oldVersion);

    my $sql = <<SET_SCHEMA_VERSION_EOF;
UPDATE $DATABASE.config SET value = '$newVersion' WHERE name = 'db.version';
SET_SCHEMA_VERSION_EOF

    Migrate::log("Updating DB schema version from $oldVersion to $newVersion.");
    runSql($sql);
}

sub insertRedologVersion($) {
  my ($version) = @_;
  my $sql = <<SET_SCHEMA_VERSION_EOF;
INSERT INTO $DATABASE.config (name, description, value) VALUES ('redolog.version', 'redolog version', '$version');
SET_SCHEMA_VERSION_EOF
    Migrate::log("Inserting Redolog schema version $version.");
    runSql($sql);
}

sub insertBackupVersion($) {
  my ($version) = @_;
  my $sql = <<SET_SCHEMA_VERSION_EOF;
INSERT INTO $DATABASE.config (name, description, value) VALUES ('backup.version', 'backup version', '$version');
SET_SCHEMA_VERSION_EOF
    Migrate::log("Inserting Backup schema version $version.");
    runSql($sql);
}
sub updateBackupVersion($$) {
    my ($oldVersion, $newVersion) = @_;
    verifyBackupVersion($oldVersion) if ($oldVersion ne "");

    my $sql = <<SET_SCHEMA_VERSION_EOF;
UPDATE $DATABASE.config SET value = '$newVersion' WHERE name = 'backup.version';
SET_SCHEMA_VERSION_EOF

    Migrate::log("Updating Backup schema version from $oldVersion to $newVersion.");
    runSql($sql);
}
sub updateRedologVersion($$) {
    my ($oldVersion, $newVersion) = @_;
    verifyRedologVersion($oldVersion) if ($oldVersion ne "");

    my $sql = <<SET_SCHEMA_VERSION_EOF;
UPDATE $DATABASE.config SET value = '$newVersion' WHERE name = 'redolog.version';
SET_SCHEMA_VERSION_EOF

    Migrate::log("Updating Redolog schema version from $oldVersion to $newVersion.");
    runSql($sql);
}

sub getMailboxIds() {
    return runSql("SELECT id FROM mailbox ORDER BY id");
}

sub getMailboxGroups() {
    return runSql("SHOW DATABASES LIKE 'mboxgroup%'");
}

#
# Return a hash where the keys are mailboxID and the values are group_id
#
sub getMailboxes() {
  my @rows = runSql("SELECT id,group_id from mailbox");
  my %toRet;
  foreach my $row (@rows) {
    if ($row =~ /([^\t\s]+)\t+([^\t\s]+)/) {
      $toRet{$1} = $2;
    }
  }
  return %toRet;
}

sub runSql(@) {
    my ($script, $logScript) = @_;

	  $logScript = 1;

	  Migrate::logSql($script)
      if ($logScript);

    # Run the mysql command and redirect output to a temp file
    my $tempFile = "/tmp/mysql.out.$$";
    my $command = "$MYSQL --user=$DB_USER --password=$DB_PASSWORD " .
        "--database=$DATABASE --batch --skip-column-names";
    unless (open(MYSQL, "| $command > $tempFile")) {
       Migrate::myquit(1, "Unable to run $command");
    }
    print(MYSQL $script);
    close(MYSQL);

    Migrate::myquit(1, "Error while running '$command'.")
      if ($? != 0);

    # Process output
    unless (open(OUTPUT, $tempFile)) {
     Migrate::myquit(1, "Could not open $tempFile");
    }
    my @output;
    while (<OUTPUT>) {
        s/\s+$//;
        push(@output, $_);
        chomp;
        Migrate::logSql($_);
    }

    unlink($tempFile);
    return @output;
}

sub runLoggerSql(@) {
    my ($script, $logScript) = @_;

	  $logScript = 1
      if (! defined($logScript));

	  Migrate::log($script)
      if ($logScript);

    # Run the mysql command and redirect output to a temp file
    my $tempFile = "/tmp/mysql.out.$$";
    my $command = "$LOGMYSQL --user=$DB_USER --password=$LOGGER_DB_PASSWORD " .
        "--database=$LOGGER_DATABASE --batch --skip-column-names";
    unless (open(MYSQL, "| $command > $tempFile")) {
      Migrate::myquit(1, "Unable to run $command");
    }
    print(MYSQL $script);
    close(MYSQL);

    my @output;
    if ($? != 0) {
		# Hack for missing config
		push @output, 0;
		return @output;
    }

    # Process output
    unless (open(OUTPUT, $tempFile)) {
      Migrate::myquit(1, "Could not open $tempFile");
    }
    while (<OUTPUT>) {
        s/\s+$//;
        push(@output, $_);
        chomp;
        Migrate::logSql($_);
    }

    unlink($tempFile);
    return @output;
}

sub runSqlParallel(@) {
  my ($procs, @statements) = @_;
  my $debug = 0;    # debug output
  my $verbose = 0;  # incremental verbage
  my $progress = 1; # output little .'s
  my $quiet = 0;    # no output (overrides verbose but not debug)
  my $started = 0;  # internal counter
  my $finished = 0; # internal counter
  my $running = 0;  # internal counter
  my $prog_cnt = 0; # internal counter
  my $timeout = 0;   # alarm timeout
  my $delay = 0;   # delay x seconds before launching new command;
  $procs = 1 unless $procs;    # number of simultaneous connections
  my ($progress_cnt, $numItems);
  
  $quiet = 1 if $progress;
  $verbose = 0 if ($quiet);
  my %pids; 
  if ($debug || $verbose gt 1) {
    print "Timeout  => $timeout\n";
    print "verbose  => $verbose\n";
    print "debug    => $debug\n";
    print "items    => ", scalar@statements, "\n";
    print "procs    => $procs\n";
  }
  return if (scalar @statements == 0);
  chomp(@statements);

  # break up each into $procs chunks
  my $split = int(scalar @statements / $procs); 
  my ($i, $j,@items);
  $i = $j = 0;
  for (0..$#statements) {
    push(@{$items[$i]}, $statements[$_]);
    $j++;
    if ($j == $split) {
      $j=0; $i++;
    }
  }

  foreach my $array (@items) {
    next if (@$array == 0);
    $prog_cnt = &progress(scalar @statements, $prog_cnt);

    # The child process, core here.
    unless ($pids{$array} = fork()) {
      # set an alarm in case the command hangs.
      $SIG{ALRM} = sub { &alarm_handler($array,$timeout,$quiet) };
      alarm($timeout);
      my $data_source = "dbi:mysql:database=$DATABASE;mysql_read_default_file=/opt/zimbra/conf/my.cnf;mysql_socket=/opt/zimbra/db/mysql.sock";
      my $dbh;
      until ($dbh) {
        $dbh = DBI->connect($data_source, $DB_USER, $DB_PASSWORD, { PrintError => 0 }); 
        sleep 1;
      }
      foreach my $statement (@$array) {
        Migrate::logSql($statement);
        unless ($dbh->do($statement) ) {
          Migrate::myquit(1,"DB: $statement: $DBI::errstr\n");
        }
      }
      $dbh->disconnect;
      # execute the statement
      alarm(0);
      exit;
    }

    # parent house keeping
    ++$started;
    $running = $started - $finished;
    if ($running >= $procs) {
      $finished++ if (&mywaiter);
    }
    sleep($delay) if $delay;
  }

  # make sure everything finished
  until ($finished >= $started) {
    print "Final wait: Finished $finished of $started\n" if $verbose;
    $finished++ if (&mywaiter);
    sleep 1;
  }
  print "\n" if $progress;
}


sub alarm_handler {
  my ($item,$to,$q) = $_[0];
  Migrate::myquit(1,"$item => Cmd exceeded $to seconds.\n") unless $q;
}

sub mywaiter {
  my ($pid,$exit_value, $signal_num, $dumped_core);
  $pid = wait;
  return undef if ($pid == -1);

  $exit_value = $? >> 8;
  $signal_num = $? & 127;
  $dumped_core = $? & 128;

  die unless $exit_value == 0;
  if (defined $exit_value) {
    return 1;
  } else {
    return 0;
  }
}

sub progress($$) {
  my ($total, $current) = @_;
  my $norm = $total/80;

  $current++;
  if ($current >= $norm) {
    print ".";
    $current = 0;
  }
  return $current;
}
   
sub myquit($$) {
  my ($status, $msg) = @_;
  Migrate::log($msg);
  $SQLLOGFH->close if (defined $SQLLOGFH);
  exit $status;
}

sub log($) {
  my ($input) = @_;
  Migrate::logSql($input);
  my $output = scalar(localtime()).": $input\n";
  print $output;
}

sub logSql($) {
  my ($input) = @_;
  unless (defined($SQLLOGFH)) {
    $SQLLOGFH = new FileHandle ">> /opt/zimbra/log/sqlMigration.log";    
    select $SQLLOGFH;
    $|=1;
    chmod 0644, "/opt/zimbra/log/sqlMigration.log";
    select STDOUT;
  }
  my $output = scalar(localtime()).": $input\n";
  print $SQLLOGFH $output;
}

1;

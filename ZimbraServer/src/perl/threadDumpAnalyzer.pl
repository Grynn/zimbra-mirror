#!/usr/bin/perl -w
#
# ***** BEGIN LICENSE BLOCK *****
#
# Zimbra Collaboration Suite Server
# Copyright (C) 2004, 2005, 2006, 2007 Zimbra, Inc.
#
# The contents of this file are subject to the Yahoo! Public License
# Version 1.0 ("License"); you may not use this file except in
# compliance with the License.  You may obtain a copy of the License at
# http://www.zimbra.com/license.
#
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
#
# ***** END LICENSE BLOCK *****
#
use strict;
use warnings;
use Getopt::Long;
use ThreadDumpAnalyzer;

my $filename = $ARGV[0];
my ($dumpLocks, $dumpThreads, $searchThreadStack, $searchThreadId, $stackFrames, $sort, $filterByState, $allLocks, $negSearchThreadStack);
my ($waiting);

$stackFrames = 10;
$sort = "state";

GetOptions(
           "dl" => \$dumpLocks,
           "dt" => \$dumpThreads,
           "frames=s" => \$stackFrames,
           "sort=s" => \$sort,
           "id=s" => \$searchThreadId,
           "state=s" => \$filterByState,
           "stack=s" => \$searchThreadStack,
           "ns=s" => \$negSearchThreadStack,
           "waiting=s" => \$waiting,
           "all" => \$allLocks,
          );

sub dumpLocks();
sub handleThread();
sub dumpThreads();
sub usage();
sub readFile($);
sub getBlockedThreads($);

if (!defined $filename) {
  usage();
}

readFile($filename);

if (defined $dumpLocks) {
  dumpLocks();
} elsif (defined $dumpThreads) {
  dumpThreads();
} else {
  usage();
}

exit(0);

sub usage() {
  my $usage = <<END_OF_USAGE;

USAGE:
    $0 FILENAME -dl [-frames #_ stack_frames] [-id REGEXP] [-all]
    $0 FILENAME -dt [-frames #_ stack_frames] [-sort id|state] [-stack REGEXP] [-id REGEXP] [-state REGEXP] [-waiting NUMBER]

    frames:  controls the # lines of stack trace included in the output
    id:      only include where the id matches REGEXP

    all:     include ALL locks (not just ones that other threads are blocked-on)

    sort:    controls the order threads are printed out (locks always printed in lock ID order)
    stack:   only include threads where the thread's stack output matches REGEXP
    state:   only include threads where the thread state (e.g. RUNNABLE) matches REGEXP
    waiting: only inclure threads are blocking other threads

 Examples:

    $0 threads.txt -dt -stack MailboxIndex.java -state RUNNABLE -f 20
        -- dumps all RUNNABLE threads with MailboxIndex.java on the stack (1st 20 lines of the stack)

    $0 threads.txt -dl -f 0
        -- dumps the list of locks in the system that are blocking at least one thread

    $0 threads.txt -dt -f 0 -sort state
        -- dumps a list of all the threads in the system and tells you their run state

    $0 threads.txt -dt -f 20 -w 1
        -- dumps a list of all the threads in the system that are blocking other threads (quick way to hunt for contention)


END_OF_USAGE
  die $usage;
}

sub mySort($$) {
  my ($a, $b) = @_;
  if ($sort eq "state") {
    my $state1 = ThreadDumpAnalyzer::getThreadState($a);
    my $state2 = ThreadDumpAnalyzer::getThreadState($b);
    if (!defined $state1) {
      return 1;
    } elsif (!defined $state2) {
      return -1;
    }
    return $state1 cmp $state2;
  } else {
    return $a cmp $b;
  }
}

sub padToWidth($$) {
  (my $str, my $width) = @_;
  if (!defined($str)) {
    $str = "";
  }
  return sprintf "%-".$width.".".$width."s", $str;
}

sub formatStackTrace($$) {
  my ($stack, $indent) = @_;
  my $ret;
  if ($stackFrames == 0) { return ""; }
  my $num = $stackFrames;

  foreach my $line (split /\n/, $stack) {
    if ($num <= 0) {
      return $ret;
    }
    $ret .= $indent.$line."\n";
    $num--;
  }
  return $ret;
}

sub formatLock($) {
  my $lockId = shift;
  my $output = "$lockId - ";
  my $numData = 0;
  my %lock = %{ ThreadDumpAnalyzer::getLock($lockId) };
  for my $data (sort keys %lock) {
    $output .= " $data=";
    $output .= $lock{$data};
    $numData++;
  }
  if ($numData == 0) { $output .= " UNKNOWN"; }
  return $output;
}

sub formatThread($) {
  my $threadId = shift;
  if (!defined $threadId) { return ""; }
  my $foo = padToWidth($threadId, 50);
  if (!defined($foo) || $foo eq "") {
    $foo = "ASDF";
  }
  my $bar = ThreadDumpAnalyzer::getThreadState($threadId);
  if (!defined($bar) || $bar eq "") {
    $bar = "HJKL";
  }
  my $ret = $foo.$bar."\n";
  my $waiting = ThreadDumpAnalyzer::getThreadWaitingOnLock($threadId);
  if (defined $waiting) {
    $ret .= "\tWaiting for: ".formatLock($waiting)."\n";
  }

  my @blockedThreads = ThreadDumpAnalyzer::getBlockedThreads($threadId);
  for my $blockedThread (sort @blockedThreads) {
    $ret .= "\t$blockedThread is waiting on this thread\n";
  }

  $ret .= formatStackTrace(ThreadDumpAnalyzer::getThreadStack($threadId), "\t  ");
  if ($stackFrames > 0) {
    $ret .= "\n";
  }
  return $ret;
}

sub dumpLocks() {
  foreach my $lockId (ThreadDumpAnalyzer::getLockIds()) {
    my $ret = "";
    my $numWaiters = 0;
    $ret .=  "LOCK: $lockId   ";
    my %lock = %{ ThreadDumpAnalyzer::getLock($lockId) };
    for my $data (keys %lock) {
      $ret .=  "$data=";
      $ret .=  $lock{$data};
      $ret .=  ", ";
    }
    $ret .= "\n";
    foreach my $threadId (ThreadDumpAnalyzer::getThreadIds()) {
      my $waiting = ThreadDumpAnalyzer::getThreadWaitingOnLock($threadId);
      if (defined $waiting) {
        if ($waiting eq $lockId) {
          $ret .= "\tThread $threadId is waiting for this lock\n";
          $numWaiters++;
        }
      }
    }
    my $lockOwnerId = ThreadDumpAnalyzer::getLockOwner($lockId);
    if (defined($lockOwnerId)) {
      my $stack = ThreadDumpAnalyzer::getThreadStack($lockOwnerId);
      if (defined($stack)) {
	$ret .= formatStackTrace($stack, "\t");
      }
    }

    if ((!defined $searchThreadId) || ($lockId =~ /$searchThreadId/)) {
      if ($numWaiters > 0 || defined $allLocks) {
        if ($stackFrames > 0) {
          $ret .= "\n";
        }
        print $ret;
      }
    }
  }
}

sub dumpThreads() {
  foreach my $threadId ( sort { mySort($a, $b) } ThreadDumpAnalyzer::getThreadIds() ) {
    if (!defined $threadId) {
      # continue
    } elsif (defined $negSearchThreadStack && (ThreadDumpAnalyzer::getThreadStack($threadId) =~ /$negSearchThreadStack/)) {
      #continue
    } elsif (defined $searchThreadStack && !(ThreadDumpAnalyzer::getThreadStack($threadId) =~ /$searchThreadStack/)) {
      # continue
    } elsif (defined $searchThreadId && !($threadId =~ /$searchThreadId/)) {
      # continue
    } elsif (defined $filterByState && !(ThreadDumpAnalyzer::getThreadState($threadId) =~ /$filterByState/)) {
      # continue
    } elsif (defined $waiting && (getBlockedThreads($threadId) < $waiting)) {
      # continue
    } else {
      print formatThread($threadId);
    }
  }
}

sub readFile($) {
  my $filename = shift;

  open IN, "<$filename" or die "couldn't open $filename";
  my @lines;
  while (<>) {
    push(@lines, $_);
  }

  close IN;
  ThreadDumpAnalyzer::initialize(@lines);
}

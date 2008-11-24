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
package ThreadDumpAnalyzer;

use strict;
use warnings;

my %locks;   # by LockID - hash w/ 'owner','type'
my %threads; # by threadId - hash w/ 'state', 'waitingOnLock', 'stack'

sub getBlockedThreads($);

sub getThread($) {
    my $threadId = shift();
    return $threads{$threadId};
}

sub getThreadIds() {
    return sort keys %threads;
}

sub sortThreadIdsByWaiters($$) {
    my ($a, $b) = @_;
    my $numWaiters1 = scalar(getBlockedThreads($a));
    my $numWaiters2 = scalar(getBlockedThreads($b));
    return $numWaiters2 <=> $numWaiters1;
}

sub getThreadIdsSortedByWaiters() {
    my @threadIds = keys %threads;
    return sort { sortThreadIdsByWaiters($a, $b) } @threadIds;
}

sub getThreadState($) {
    my $threadId = shift();
    return $threads{$threadId}{state};
}

sub getThreadWaitingOnLock($) {
    my $threadId = shift();
    return $threads{$threadId}{waitingOnLock};
}

sub getThreadStack($) {
    my $threadId = shift();
    return $threads{$threadId}{stack};
}

sub getLockIds() {
    return sort keys %locks;
}

sub sortLockIdsByWaiters($$) {
    my ($a, $b) = @_;
    my $numWaiters1 = scalar(getLockWaiters($a));
    my $numWaiters2 = scalar(getLockWaiters($b));
    return $numWaiters2 <=> $numWaiters1;
}

sub getLockIdsSortedByWaiters() {
    my @lockIds = keys %locks;
    return sort { sortLockIdsByWaiters($a, $b) } @lockIds;
}

sub getLock($) {
    my $lockId = shift();
    return $locks{$lockId};
}

sub getLockType($) {
    my $lockId = shift();
    return $locks{$lockId}{type};
}

# given a lockId, return a list of the threads that are blocked on it
sub getLockWaiters($) {
    my $lockId = shift;
    my @ret;

    foreach my $threadId ( keys %threads ) {
	if (defined $threads{$threadId}{waitingOnLock}) {
	    if ($threads{$threadId}{waitingOnLock} eq $lockId) {
		push @ret, $threadId;
	    }
	}
    }
    return @ret;
}

sub getLockOwner($) {
    my $lockId = shift();
    if (defined $locks{$lockId}) {
	if (defined $locks{$lockId}{owner}) {
	    return $locks{$lockId}{owner};
	} else {
	    return "";
	}
    } else {
	return "";
    }
}

# given a threadId, get a list of all other threads that are blocked
# on locks it is holding
sub getBlockedThreads($) {
    my $threadId = shift;
    my @ret;
    foreach my $lockId ( sort keys %locks ) {
	if (getLockOwner($lockId) eq $threadId) { # a lock we own
	    my @blockedThreads = getLockWaiters($lockId);
	    foreach my $blockedThread (@blockedThreads) {
		push @ret, $blockedThread;
		push @ret, getBlockedThreads($blockedThread); #recurse!
	    }
	}
    }
    return @ret;
}

sub initialize(@) {
    my @curThread;

    for (@_) {
	chomp;
	s/\r//g; # Handle DOS line endings
	if ($_ eq "") {
	    # Line break between thread dumps
	    if (@curThread) {
		my $threadId;
		my $waitingOnLock;
		my $threadState;
		my $output;

		my $firstLineState;

		# 1stline
		my $line = shift @curThread;
		$output .= $line."\n";
		if ($line =~ /^"(.*)"/) {
		    $threadId = $1;
		    if ($line =~/nid=0x[0-9a-f]+\s([a-zA-Z\s\.()]+)/) {
			$threads{$threadId}{state} = $1;
		    }
		} else {
		    next;
		}
		if ($threadId eq "") {
		    $threadId = "none";
		}

		# 2nd line
		$line = shift @curThread;
		if (defined $line) {
		    $output .= $line."\n";
		    if ($line =~ /State: ([A-Z_]+)/) {
			$threadState = $1;
			$threads{$threadId}{state} = $1;
		    }

		    foreach $line (@curThread) {
			$output .= $line."\n";
			if ($line =~ /locked <(0x[0-9a-f]+)>\s*\(a ([^\)]+)/) {
			    $locks{$1}{owner} = $threadId;
			    $locks{$1}{type} = $2;
			} elsif ($line =~ /- waiting to lock <(0x[0-9a-f]+)>\s*\(a ([^\)]+)/) {
			    $waitingOnLock = $1;
			    $threads{$threadId}{waitingOnLock} = $1;
			    if (!defined($locks{$1})) {
				# Handle the edge case where there is no "locked"
				# line in the thread dump.  This probably happens
				# because the lock was released as we were taking
				# the thread dump.
				$locks{$1}{owner} = "undetermined";
				$locks{$1}{type} = $2;
			    }
			}
		    }
		} else {
		    $threads{$threadId}{state} = "unknown";
		}

		$threads{$threadId}{stack} = $output;
		undef @curThread;
	    }
	} else {
	    push @curThread, $_;
	}
    }
}

1;

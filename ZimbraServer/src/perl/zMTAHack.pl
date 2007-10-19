#!/usr/bin/perl

#
# For windows with ActivePerl, you'll need to 'ppm install' these modules:
#    Net-LMTP
#    SMTP-Server
#    Net-DNS
#

use Net::SMTP::Server;
use Net::SMTP::Server::Client;
use Net::SMTP::Server::Relay;
use Net::LMTP;
use Carp;
use strict;

#
# Braindead hack by Tim -- listens on the SMTP port and if the TO address has a
# domain which maches the value of the ZIMBRA_HOSTNAME environment variable) then it 
# fwds the message to the LMTP on the zimbra server...otherwise it relays this message
# via this machine's default SMTP (ie exch1)
#

$SIG{CHLD} = 'IGNORE';  # zombie prevention

my $server = new Net::SMTP::Server('localhost', 25) ||
    croak("[$$] Unable to handle client connection: $!\n");

my $zimbra_hostname  = $ENV{"ZIMBRA_HOSTNAME"};

if (!defined($zimbra_hostname)) {
    die "\"ZIMBRA_HOSTNAME\" must be set and must contain the Zimbra Server which you want to route email to";
}

print "[$$] Starting SMTP Hack on port 25\n";

while(my $conn = $server->accept()) {
    # We can perform all sorts of checks here for spammers, ACLs,
    # and other useful stuff to check on a connection.

    # Handle the client's connection and spawn off a new parser.
    # This can/should be a fork() or a new thread,
    # but for simplicity...
    my $client = new Net::SMTP::Server::Client($conn) ||
        croak("Unable to handle client connection: $!\n");

    # Process the client.  This command will block until
    # the connecting client completes the SMTP transaction.
    $client->process || next;

    my %rcptsByDomain;
    foreach my $rcpt (@{$client->{TO}}) {
        my $domain;

        # Strip <> if present
        $rcpt =~ s/^<//;
        $rcpt =~ s/>$//;

        if ($rcpt =~ /\@([^\@<>]+)/) {
            $domain = $1;
            my $rcptList = $rcptsByDomain{$domain};
            if (!defined($rcptList)) {
                $rcptList = [];
                $rcptsByDomain{$domain} = $rcptList;
            }
            push(@$rcptList, $rcpt);
        }
    }

    # Initialize sender and strip <> if present
    my $sender = $client->{FROM};
    $sender =~ s/^<//;
    $sender =~ s/>$//;
    my $senderForLogging = "unknown sender";
    if (length($sender) > 0) {
        $senderForLogging = $sender;
    }

    foreach my $domain (keys(%rcptsByDomain)) {
        my $rcptList = $rcptsByDomain{$domain};
        my $rcptStr = join(', ', @$rcptList);
        my $pid = fork();
        if (!defined($pid)) {
            print STDERR "[$$] Unable to fork child process; dropping message from $senderForLogging to $rcptStr\n";
            next;
        }
        if ($pid != 0) {
            # parent process
            print "[$$] Forked child process $pid for message from $senderForLogging to $rcptStr\n";
            next;
        }

        # child process
        if (lc($domain) eq lc($zimbra_hostname)) {
            print "    [$$] Got a local message from $senderForLogging to $rcptStr\n";
            my $lmtp = Net::LMTP->new('localhost', 7025, Hello => 'localhost');
            if (!defined($lmtp)) {
            	print STDERR "    [$$] ERROR: Can't connect to LMTP server: $!\n";
            	exit(1);
            }

            $lmtp->mail($sender, Size => length($client->{MSG}));
            foreach my $rcpt (@$rcptList) {
                $lmtp->to($rcpt);
            }

            $lmtp->data();
            $lmtp->datasend($client->{MSG});
            $lmtp->dataend();

            $lmtp->quit;
        } else {
            print "    [$$] Relaying message from $client->{FROM} to $rcptStr\n";
            my $relay = new Net::SMTP::Server::Relay($client->{FROM},
                                                     $rcptList,
                                                     $client->{MSG});

            # if the app hangs before getting here it is likely trying to connect to
            # itself...this machine's default SMTP server must be a different box (e.g. Exch1)
            print "    [$$] Message sent!\n";
        }
        print "    [$$] Exiting child process $$\n";
        exit(0);  # child process exit
    }
}

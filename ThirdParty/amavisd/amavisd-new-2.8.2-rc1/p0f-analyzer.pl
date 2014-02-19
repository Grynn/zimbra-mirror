#!/usr/bin/perl -T

#------------------------------------------------------------------------------
# This is p0f-analyzer.pl, a program to continuously read log reports from p0f
# utility, keep results in cache for a couple of minutes, and answer queries
# over UDP from some program (like amavisd-new) about collected data.
#
# Author: Mark Martinec <Mark.Martinec@ijs.si>
# Copyright (C) 2006,2012,2013  Mark Martinec,  All Rights Reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#
# * Redistributions of source code must retain the above copyright notice,
#   this list of conditions and the following disclaimer.
# * Redistributions in binary form must reproduce the above copyright notice,
#   this list of conditions and the following disclaimer in the documentation
#   and/or other materials provided with the distribution.
# * Neither the name of the author, nor the name of the "Jozef Stefan"
#   Institute, nor the names of contributors may be used to endorse or
#   promote products derived from this software without specific prior
#   written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
# "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
# LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
# PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
# OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
# EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
# PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
# OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
# WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
# OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
# ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#
#(the license above is the new BSD license, and pertains to this program only)
#
# Patches and problem reports are welcome.
# The latest version of this program is available at:
#   http://www.ijs.si/software/amavisd/
#------------------------------------------------------------------------------

  use strict;
  use re 'taint';
  use Errno qw(EAGAIN EINTR ENOENT EACCES);
  use POSIX ();
  use Socket;
  use IO::File qw(O_RDONLY);
  use vars qw($VERSION);
  $VERSION = '1.501';

# Example usage with p0f v3:
#   p0f -i eth0 'tcp and dst host mail.example.org' 2>&1 | p0f-analyzer.pl 2345
#
# Example usage with old p0f v2:
#   p0f -l -i eth0 'tcp and dst host mail.example.org' 2>&1 | p0f-analyzer.pl 2345
#
# In the p0f filter expression above specify an IP address of the host where
# your MTA is listening for incoming mail (in place of host.example.com above).
# Match the UDP port number (like 2345 above) with the port number to which a
# client will be sending queries ($os_fingerprint_method in amavisd.conf).


use vars qw($io_socket_module_name $have_inet4 $have_inet6);
BEGIN {
  # prefer using module IO::Socket::IP if available,
  # otherwise fall back to IO::Socket::INET6 or to IO::Socket::INET
  #
  if (eval { require IO::Socket::IP }) {
    $io_socket_module_name = 'IO::Socket::IP';
  } elsif (eval { require IO::Socket::INET6 }) {
    $io_socket_module_name = 'IO::Socket::INET6';
  } elsif (eval { require IO::Socket::INET }) {
    $io_socket_module_name = 'IO::Socket::INET';
  }

  $have_inet4 =  # can we create a PF_INET socket?
    defined $io_socket_module_name && eval {
      my $sock =
        $io_socket_module_name->new(LocalAddr => '0.0.0.0', Proto => 'tcp');
      $sock->close or die "error closing socket: $!"  if $sock;
      $sock ? 1 : undef;
    };

  $have_inet6 =  # can we create a PF_INET6 socket?
    defined $io_socket_module_name &&
    $io_socket_module_name ne 'IO::Socket::INET' &&
    eval {
      my $sock =
        $io_socket_module_name->new(LocalAddr => '::', Proto => 'tcp');
      $sock->close or die "error closing socket: $!"  if $sock;
      $sock ? 1 : undef;
    };
}

  # argument should be a free UDP port where queries will be accepted on
  @ARGV or die <<'EOD';
Usage:
  p0f-analyzer.pl socket-spec ...

where socket-spec is an UDP port number optionally preceded by an IP address
(or a host name) and a colon. An IPv6 address must be enclosed in square
brackets so that the port-delimiting colon is unambiguous. To listen on
all interfaces specify an asterisk in place of an IP address, e.g. '*:2345'.
A host name 'localhost' implies binding to a loopback interface on any
available protocol family (IPv4 or IPv6) and is a default when only a port
number is specified.

Example usage, all three examples are equivalent:
  p0f -i eth0 'tcp dst port 25' 2>&1 | p0f-analyzer.pl 2345
  p0f -i eth0 'tcp dst port 25' | p0f-analyzer.pl localhost:2345
  p0f -i eth0 'tcp dst port 25' | p0f-analyzer.pl [::1]:2345 127.0.0.1:2345
EOD

  my(@listen_sockets, @inet_acl, $retention_time, $log_level, %src);

  @listen_sockets = map(untaint($_), @ARGV);

  # list of IP addresses from which queries will be accepted, others ignored
  @inet_acl = ('::1', '127.0.0.1');

  # time in seconds to keep collected information in cache
  $retention_time = 10*60;

  $log_level = 0;


# Return untainted copy of a string (argument can be a string or a string ref)
sub untaint($) {
  return undef  if !defined $_[0];  # must return undef even in a list context!
  no re 'taint';
  local $1;  # avoids Perl taint bug: tainted global $1 propagates taintedness
  (ref($_[0]) ? ${$_[0]} : $_[0]) =~ /^(.*)\z/s;
  $1;
}

sub ll($) {
  my($level) = @_;
  $level <= $log_level;
}

# write log entry
sub do_log($$;@) {
  my($level,$errmsg,@args) = @_;
  if ($level <= $log_level) {
    $errmsg = sprintf($errmsg,@args)  if @args;
    print STDERR $errmsg,"\n";
  }
  1;
}

# ip_to_vec() takes an IPv6 or IPv4 address with optional prefix length
# (or an IPv4 mask), parses and validates it, and returns it as a 128-bit
# vector string that can be used as operand to Perl bitwise string operators.
# Syntax and other errors in the argument throw exception (die).
# If the second argument $allow_mask is 0, the prefix length or mask
# specification is not allowed as part of the IP address.
#
# The IPv6 syntax parsing and validation adheres to RFC 4291 (ex RFC 3513).
# All the following IPv6 address forms are supported:
#   x:x:x:x:x:x:x:x        preferred form
#   x:x:x:x:x:x:d.d.d.d    alternative form
#   ...::...               zero-compressed form
#   addr/prefix-length     prefix length may be specified (defaults to 128)
# Optionally an "IPv6:" prefix may be prepended to an IPv6 address
# as specified by RFC 5321 (ex RFC 2821). Brackets enclosing the address
# are optional, e.g. [::1]/128 .
#
# The following IPv4 forms are allowed:
#   d.d.d.d
#   d.d.d.d/prefix-length  CIDR mask length is allowed (defaults to 32)
#   d.d.d.d/m.m.m.m        network mask (gets converted to prefix-length)
# If prefix-length or a mask is specified with an IPv4 address, the address
# may be shortened to d.d.d/n or d.d/n or d/n. Such truncation is allowed
# for compatibility with earlier version, but is deprecated and is not
# allowed for IPv6 addresses.
#
# IPv4 addresses and masks are converted to IPv4-mapped IPv6 addresses
# of the form ::FFFF:d.d.d.d,  The CIDR mask length (0..32) is converted
# to an IPv6 prefix-length (96..128). The returned vector strings resulting
# from IPv4 and IPv6 forms are indistinguishable.
#
# NOTE:
#   d.d.d.d is equivalent to ::FFFF:d.d.d.d (IPv4-mapped IPv6 address)
#   which is not the same as ::d.d.d.d      (IPv4-compatible IPv6 address)
#
# A quadruple is returned:
#  - an IP address represented as a 128-bit vector (a string)
#  - network mask derived from prefix length, a 128-bit vector (string)
#  - prefix length as an integer (0..128)
#  - interface scope (for link-local addresses), undef if non-scoped
#
sub ip_to_vec($;$) {
  my($ip,$allow_mask) = @_;
  my($ip_len, @ip_fields, $scope);
  local($1,$2,$3,$4,$5,$6);
  $ip =~ s/^[ \t]+//; $ip =~ s/[ \t\r\n]+\z//s;  # trim
  my $ipa = $ip;
  ($ipa,$ip_len) = ($1,$2)  if $allow_mask && $ip =~ m{^ ([^/]*) / (.*) \z}xs;
  $ipa = $1  if $ipa =~ m{^ \[ (.*) \] \z}xs;  # discard optional brackets
  my $have_ipv6;
  if ($ipa =~ s/^IPv6://i) { $have_ipv6 = 1 }
  elsif ($ipa =~ /:.*:/s)  { $have_ipv6 = 1 }
  $scope = $1  if $ipa =~ s/ ( % [A-Z0-9:._-]+ ) \z//xsi;  # scoped address
  if ($have_ipv6 &&
      $ipa =~ m{^(.*:) (\d{1,3}) \. (\d{1,3}) \. (\d{1,3}) \. (\d{1,3})\z}xsi){
    # IPv6 alternative form x:x:x:x:x:x:d.d.d.d
    my(@d) = ($2,$3,$4,$5);
    !grep($_ > 255, @d)
      or die "Invalid decimal field value in IPv6 address: [$ip]\n";
    $ipa = $2 . sprintf('%02x%02x:%02x%02x', @d);
  } elsif (!$have_ipv6 &&
           $ipa =~ m{^ \d{1,3} (?: \. \d{1,3}){0,3} \z}xs) {  # IPv4
    my(@d) = split(/\./,$ipa,-1);
    !grep($_ > 255, @d)
      or die "Invalid field value in IPv4 address: [$ip]\n";
    defined($ip_len) || @d==4
      or die "IPv4 address [$ip] contains fewer than 4 fields\n";
    $ipa = '::ffff:' . sprintf('%02x%02x:%02x%02x', @d);  # IPv4-mapped IPv6
    if (!defined($ip_len)) { $ip_len = 32;  # no length, defaults to /32
    } elsif ($ip_len =~ /^\d{1,9}\z/) {     # /n, IPv4 CIDR notation
    } elsif ($ip_len =~ /^(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\z/) {
      my(@d) = ($1,$2,$3,$4);
      !grep($_ > 255, @d)
        or die "Illegal field value in IPv4 mask: [$ip]\n";
      my $mask1 = pack('C4', @d);           # /m.m.m.m
      my $len = unpack('%b*', $mask1);      # count ones
      my $mask2 = pack('B32', '1' x $len);  # reconstruct mask from count
      $mask1 eq $mask2
        or die "IPv4 mask not representing a valid CIDR mask: [$ip]\n";
      $ip_len = $len;
    } else {
      die "Invalid IPv4 network mask or CIDR prefix length: [$ip]\n";
    }
    $ip_len<=32 or die "IPv4 network prefix length greater than 32: [$ip]\n";
    $ip_len += 128-32;  # convert IPv4 net mask length to IPv6 prefix length
  }
  # now we presumably have an IPv6 compressed or preferred form x:x:x:x:x:x:x:x
  if ($ipa !~ /^(.*?)::(.*)\z/s) {  # zero-compressing form used?
    @ip_fields = split(/:/,$ipa,-1);  # no, have preferred form
  } else {                          # expand zero-compressing form
    my($before,$after) = ($1,$2);
    my(@bfr) = split(/:/,$before,-1); my(@aft) = split(/:/,$after,-1);
    my $missing_cnt = 8-(@bfr+@aft);  $missing_cnt = 1  if $missing_cnt<1;
    @ip_fields = (@bfr, ('0') x $missing_cnt, @aft);
  }
  @ip_fields >= 8  or die "IPv6 address [$ip] contains fewer than 8 fields\n";
  @ip_fields <= 8  or die "IPv6 address [$ip] contains more than 8 fields\n";
  !grep(!/^[0-9a-zA-Z]{1,4}\z/, @ip_fields)  # this is quite slow
    or die "Invalid syntax of IPv6 address: [$ip]\n";
  my $vec = pack('n8', map(hex($_),@ip_fields));
  if (!defined($ip_len)) {
    $ip_len = 128;
  } elsif ($ip_len !~ /^\d{1,3}\z/) {
    die "Invalid prefix length syntax in IP address: [$ip]\n";
  } elsif ($ip_len > 128) {
    die "IPv6 network prefix length greater than 128: [$ip]\n";
  }
  my $mask = pack('B128', '1' x $ip_len);
# do_log(5, "ip_to_vec: %s => %s/%d\n",     # unpack('B*',$vec)
#           $ip, join(':',unpack('(H4)*',$vec)), $ip_len);
  ($vec, $mask, $ip_len, $scope);
}

sub add_entry($$$$;$) {
  my($now, $src_ip, $src_port, $descr, $attr_ref) = @_;
  if ($src_ip =~ /:.*:/) {  # normalize an IPv6 address to a preferred form
    my($vec, $mask, $ip_len, $scope) = ip_to_vec($src_ip);
    $src_ip = lc join(':',unpack('(H4)*',$vec));  # full preferred form
    $src_ip =~ s/\b 0{1,3}//xsg;  # suppress leading zeroes in each field
  }
  my $key = "[$src_ip]:$src_port";

  my $entry = $src{$key};
  $entry = {}  if !$entry;
  $entry->{t} = $now;
  $entry->{d} = $descr;
  do_log(2, "%s [%s]:%d %s",
            exists($src{$key}) ? 'added:' : 'new:  ',
            $src_ip, $src_port,
            !$attr_ref ? '' : join('; ', keys %$attr_ref))  if ll(2);
  if ($attr_ref && %$attr_ref) {
    # replace attributes while keeping existing ones
    for my $attr_name (keys %$attr_ref) {
      $entry->{a}{$attr_name} = $attr_ref->{$attr_name};
    }
  }
  $src{$key} = $entry;
}


# main program starts here
  $SIG{INT}  = sub { die "\n" };  # do the END code block when interrupted
  $SIG{TERM} = sub { die "\n" };  # do the END code block when killed
  umask(0027);  # set our preferred umask

  my(%fileno_to_socket, @unix_socket_paths_to_be_removed, $rout, $rin);
  $rin = '';

  for (@listen_sockets) {
    my $sock_spec = $_;

    if (m{^/.+\z}s) {
      # looks like a Unix socket absolute path specification
      $sock_spec = $_;
      die "Unix datagram sockets are currently not supported\n";

#     # test for a stale Unix socket
#     my(@stat_list) = stat($sock_spec); my $errn = @stat_list ? 0 : 0+$!;
#     if ($errn == ENOENT) {  # no such socket
#       # good, Unix socket does not exist yet
#     } elsif ($errn) {  # some other error
#       die "File $sock_spec is inaccessible: $!\n";
#     } elsif (!-S _) {
#       die "File $sock_spec exists but is not a socket\n";
#     } elsif (IO::Socket::UNIX->new(  # try binding to it
#                Peer => $sock_spec, Type => &SOCK_STREAM)) {
#       die "Socket $sock_spec is already in use\n";
#     } else {
#       do_log(1, "Removing stale socket %s", $sock_spec);
#       unlink $sock_spec
#         or do_log(-1, "Error unlinking socket %s: %s", $sock_spec, $!);
#     }
#
#     # create a new Unix socket
#     # umask(0007);  # affects protection of a Unix socket
#     my $sock = IO::Socket::UNIX->new(
#                  Type => &SOCK_DGRAM, Listen => &SOMAXCONN,
#                  Local => $sock_spec);
#     $sock or die "Binding to $_ failed: $!";
#     # umask(0027);  # restore our preferred umask
#     push(@unix_socket_paths_to_be_removed, $sock_spec);
#
#     my $fileno = $sock->fileno;
#     vec($rin,$fileno,1) = 1;
#     $fileno_to_socket{$fileno} = $sock;
#     do_log(0, "Listening for queries on %s, fn %d", $sock_spec, $fileno);

    } else {  # assume an INET or INET6 socket

      my(@host, $port);
      if (m{^ \d+ \z}xs) {
        # port specification only, assume a loopback interface
        @host = 'localhost'; $port = $_;
      } elsif (m{^ \[ ( [^\]]* ) \] (?: : (\d+) )? \z}xs ||
               m{^    ( [^/:]* )    (?: : (\d+) )? \z}xs) {
        # explicit host & port specified
        @host = $1; $port = $2;
      } else {
        die "Invalid socket specification: $_\n";
      }
      $port or die "Invalid socket specs, a port number is required: $_\n";

      # map hostnames 'localhost' and '*' to their equivalents
      if (@host == 1) {
        if (lc($host[0]) eq 'localhost') { @host = ('::1', '127.0.0.1') }
        elsif ($host[0]  eq '*')         { @host = ('::',  '0.0.0.0') }
      }

      # filter IP addresses according to available protocol families
      @host = grep { /^\d+\.\d+\.\d+\.\d+\z/s ? $have_inet4 :
                     /:.*:/s ? $have_inet6 : 1 } @host;

      for my $h (@host) {
        my %sockopt = (
          LocalAddr => $h, LocalPort => $port,
          Type => &SOCK_DGRAM, Proto => 'udp', ReuseAddr => 1,
        );
        $sockopt{V6Only} = 1  if $io_socket_module_name eq 'IO::Socket::IP'
                                 && IO::Socket::IP->VERSION >= 0.09;
        my $sock = $io_socket_module_name->new(%sockopt);
        $sock or die "Binding to socket [$h]:$port failed ".
                     "(using $io_socket_module_name): $!";
        my $fileno = $sock->fileno;
        vec($rin,$fileno,1) = 1;
        $fileno_to_socket{$fileno} = $sock;
        do_log(0, "Listening for queries on [%s]:%s, fn %d",
                  $h, $port, $fileno);
      }
    }
  }

  binmode(STDIN)  or die "Can't set binmode on STDIN: $!";
  my $fn_input = fileno(STDIN);
  vec($rin,$fn_input,1) = 1;

  do_log(0, "p0f-analyzer version %s starting.", $VERSION);

  my $p0f_version;
  my $cnt_since_cleanup = 0; my $p0f_buff = '';
  my($src_ip, $src_port, $src_t, $src_d, %attr);
  for (;;) {
    my($nfound,$timeleft) = select($rout=$rin, undef, undef, undef);
    defined $nfound && $nfound >= 0  or die "Select failed: $!";
    next if !$nfound;
    my $now = time;

    for my $fileno (keys %fileno_to_socket) {
      next if !vec($rout,$fileno,1);
      # accept a query
      my $sock = $fileno_to_socket{$fileno};
      $sock or die "panic: no socket, fileno=$fileno";
      my($query_source, $inbuf);
      my $paddr = $sock->recv($inbuf, 64, 0);
      if (!defined($paddr)) {
        if ($!==EAGAIN || $!==EINTR) {
          # false alarm, nothing can be read
        } else {
          die "recv: $!";
        }
      } else {
        my $clientaddr = $sock->peerhost;
        my $clientport = $sock->peerport;
        if (!defined($clientaddr)) {
          do_log(1, "query from unknown client");
        } elsif (!grep($_ eq $clientaddr, @inet_acl)) {
          do_log(1, "query from non-approved client: %s:%s",
                    $clientaddr, $clientport);
        } elsif ($clientport < 1024 || $clientport == 2049 ||
                 $clientport > 65535) {
          do_log(1, "query from questionable port: %s:%s",
                    $clientaddr, $clientport);
        } elsif ($inbuf !~ /^([^ ]+) (.*)$/s) {
          do_log(1, "invalid query syntax from %s", $query_source);
        } else {
          $query_source = "[$clientaddr]:$clientport";
          my($query, $nonce) = ($1, $2);
          my($src_ip, $src_port);
          if ($query =~ /^ \[ ([^\]]*) \] (?: : (\d{1,5}) )? \z/xs) {
            $src_ip = $1; $src_port = $2;
            if ($src_ip =~ /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\z/) {
              # IPv4
            } elsif ($src_ip =~ /^
                       (?: (?: IPv6: )? 0{0,4} (?: : 0{0,4} ){1,4} : ffff : )?
                       ( \d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3} )\z/xsi) {
              $src_ip = $1;  # IPv4-mapped IPv6 address, alternative form
            } elsif ($src_ip =~ /^ (?: IPv6: )?
                                   [0-9a-f]{0,4} (?: : [0-9a-f]{0,4} ){2,7}
                                 \z/xsi) {
              $src_ip =~ s/^IPv6://i;
            } elsif ($src_ip =~ /^ (?: IPv6: )?
                                   [0-9a-f]{0,4} (?: : [0-9a-f]{0,4} ){1,5} :
                                   \d{1,3} (?: \. \d{1,3} ){3} \z/xsi) {
              $src_ip =~ s/^IPv6://i;
            } else { undef $src_ip }
          }
          $src_port = 0  if !defined $src_port;
          if (length($nonce) > 1024) {
            do_log(1, "invalid query from %s, nonce too long: %d chrs",
                      $query_source, length($nonce));
          } elsif ($nonce !~ /^([\040-\177]*)\z/s) {
            do_log(1, "invalid query from %s, forbidden char in nonce",
                      $query_source);
          } elsif (!defined($src_ip) || $src_port > 65535) {
            do_log(1, "invalid query from %s, bad IP address or port: %s",
                      $query_source, $query);
          } else {
            if ($src_ip =~ /:.*:/) {  # normalize an IPv6 address in a query
              my($vec, $mask, $ip_len, $scope) = ip_to_vec($src_ip);
              $src_ip = lc join(':',unpack('(H4)*',$vec));  # preferred form
              $src_ip =~ s/\b 0{1,3}//xsg;  # suppress leading zeroes
            }
            do_log(2, "query from  %s: %s", $query_source, $inbuf);
            my $resp = '';
            if ($src_port > 0 && exists $src{"[$src_ip]:$src_port"}) {
              my $attr_ref = $src{"[$src_ip]:$src_port"}{a};
              if ($attr_ref) {
                my %tmp_attr = %$attr_ref;
                # partial compatibility with v2 format: place OS first
                my $os = delete $tmp_attr{os};
                $resp = join('; ', $os, map("$_: $tmp_attr{$_}",
                                            sort keys %tmp_attr));
              } else {  # old p0f (v2)
                $resp = $src{"[$src_ip]:$src_port"}{d};
              }
            }
            $resp = $query.' '.$nonce.' '.$resp;
            do_log(1, "response to %s: %s", $query_source, $resp);
            defined $sock->send($resp."\015\012", 0, $paddr)
              or die "send failed: $!";
          }
        }
      }
    }

    if (vec($rout,$fn_input,1)) {
      # accept more input from p0f
      $cnt_since_cleanup++; $! = 0;
      my $nbytes = sysread(STDIN, $p0f_buff, 8192, length $p0f_buff);
      if (!defined($nbytes)) {
        if ($!==EAGAIN || $!==EINTR) {
          # false alarm, nothing can be read
        } else {
          die "Read: $!";
        }
      } elsif ($nbytes < 1) {  # sysread returns 0 at eof
        last;  # eof
      } else {
        while (index($p0f_buff,"\012") >= 0) {
          local($1,$2,$3,$4,$5,$6);
          my($dst_ip,$dst_port);
          if ((!defined $p0f_version || $p0f_version < 3) &&
              $p0f_buff =~ s/^ (\d+\.\d+\.\d+\.\d+) : (\d+) [ -]* (.*)
                             \ ->\  (\d+\.\d+\.\d+\.\d+) : (\d+) \s* (.*)
                             \015? \012//x) {
            # looks like a old version (v2) of p0f
            $p0f_version = 2  if !defined $p0f_version;
            ($src_ip,$src_port,$src_t,$dst_ip,$dst_port,$src_d) =
              ($1,$2,$3,$4,$5,$6);
            add_entry($now, $src_ip, $src_port, "$src_t, $src_d");
          } elsif ($p0f_buff =~ s/^ \|? \s* \015? \012//x) {
            # empty
          } elsif ($p0f_buff =~ s/^ --- .*? \015? \012//x) {
            # info
          } elsif ($p0f_buff =~ s/^ \[ [+!] \] .*? \015? \012//x) {
            # info
          } elsif ($p0f_buff =~ s/^ \.-\[ \s* (.*?) \s* \] - \015? \012//x) {
            # new entry
            %attr = (); ($src_ip, $src_port, $src_t, $src_d) = (undef) x 4;
          } elsif ($p0f_buff =~ s/^ \| \s* (.*?) \015? \012//x) {
            my($attr_name, $attr_val) = split(/\s*=\s*/, $1, 2);
            if (!defined $attr_val) {
              # ignore
            } elsif ($attr_name eq 'client' || $attr_name eq 'server') {
              ($src_ip, $src_port) = split(m{/}, $attr_val, 2);
            } else {
              $attr{$attr_name} = $attr_val;
            }
          } elsif ($p0f_buff =~ s/^ \` -+ \015? \012//x) {
            add_entry($now, $src_ip, $src_port, '', \%attr);
            $p0f_version = 3  if !defined $p0f_version && %attr;
            %attr = (); ($src_ip, $src_port, $src_t, $src_d) = (undef) x 4;
          } elsif ($p0f_buff =~ s/^ (.*?) \015? \012//x) {
            do_log(1, "UNRECOGNIZED <%s>", $1);
          } else {
            do_log(0, "SHOULDN'T HAPPEN <%s>", $p0f_buff);
            $p0f_buff = '';
          }
        }
      }
      if ($cnt_since_cleanup > 50) {
        for my $k (keys %src) {
          if (ref $src{$k} ne 'ARRAY') {
            if ($src{$k}{t} + $retention_time < $now) {
              do_log(2, "EXPIRED: %s, age = %d s", $k, $now - $src{$k}{t});
              delete $src{$k};
            }
          } else {
            my @kept = grep($_->{t} + $retention_time >= $now, @{$src{$k}});
            if (!@kept) {
              do_log(2, "EXPIRED: %s, age = %d s", $k, $now - $src{$k}[0]{t});
              delete $src{$k};
            } elsif (@kept != @{$src{$k}}) {
              do_log(2, "SHRUNK: %s, %d -> %d",
                        $k, scalar(@{$src{$k}}), scalar(@kept));
              @{$src{$k}} = @kept;
            }
          }
        }
        $cnt_since_cleanup = 0;
      }
    }
  }
  do_log(1, "normal termination");

END {
  # remove Unix sockets we created
  if (@unix_socket_paths_to_be_removed) {
    do_log(1, 'Removing socket %s',
              join(', ', @unix_socket_paths_to_be_removed));
    unlink $_ for @unix_socket_paths_to_be_removed;  # ignoring errors
  }
}

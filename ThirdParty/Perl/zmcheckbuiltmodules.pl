#!/usr/bin/perl 
# 
# ***** BEGIN LICENSE BLOCK *****
# 
# Zimbra Collaboration Suite Server
# Copyright (C) 2008 Zimbra, Inc.
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
use lib "./zimbramon/lib";

my @modules =qw(AnyDBM_File Archive::Tar Archive::Zip Array::Compare Benchmark BerkeleyDB Bit::Vector Cache::FastMmap Carp Compress::Raw::Zlib Compress::Zlib Config::IniFiles Convert::ASN1 Convert::TNEF Convert::UUlib Crypt::OpenSSL::Random Crypt::OpenSSL::RSA Crypt::SaltedHash Crypt::SSLeay Cwd DB_File DBD::mysql DBD::SQLite DBI Data::Dumper Data::UUID Date::Calc Date::Format Date::Manip Date::Parse Device::SerialPort Digest::HMAC Digest::HMAC_MD5 Digest::MD5 Digest::SHA Digest::SHA1 Encode English Errno Error Exporter Fcntl File::Basename File::Copy File::Find File::Grep File::Path File::Spec File::Tail File::Temp FileHandle FindBin Getopt::Easy Getopt::Long Getopt::Std HTML::Parser HTML::Tagset HTTP::Parser HTTP::Request IO IO::Compress::Base IO::Compress::Gzip IO::File IO::Handle IO::Select IO::Socket IO::Socket::INET IO::Socket::INET6 IO::Socket::SSL IO::Socket::UNIX IO::Stringy IO::Wrap IO::Zlib IPC::Open3 List::Compare LWP::UserAgent MIME::Base64 MIME::Entity MIME::Lite MIME::Parser MIME::Words Mail::DKIM Mail::Mailer Mail::SpamAssassin Mail::SpamAssassin::ArchiveIterator Mail::SpamAssassin::Message Mail::SpamAssassin::PerMsgLearner Mail::SpamAssassin::Util::Progress Math::BigFloat Net::DNS::Resolver Net::HTTP Net::IP Net::LDAP Net::LDAP::Entry Net::LDAP::LDIF Net::LDAPapi Net::Ping Net::SMTP Net::SSLeay Net::Server Net::Telnet POSIX Package::Constants Parallel::ForkManager Pod::Usage Proc::ProcessTable Scalar::Util SOAP::Lite SOAP::Transport::HTTP Socket Socket6 Sub::Uplevel Swatch::Actions Swatch::Throttle Sys::Hostname Term::ReadLine Test::Exception Test::Warn Time::HiRes Time::Local Tree::DAG_Node Unix::Syslog URI XML::Parser XML::SAX::Base XML::SAX::Expat XML::Simple bytes constant lib re sigtrap strict subs vars warnings);

my $exit_status=0;

foreach my $m (@modules) {
  load_module($m); 
}

exit $exit_status;

sub load_module($) {
  my ($m) = @_;
  local($_) = $m;
  $_ .= /^auto::/ ? '.al' : '.pm'  if !m{^/} && !m{\.(pm|pl|al|ix)\z};
  s{::}{/}g;
  eval { require $_; } 
  or do {
    my($eval_stat) = $@ ne '' ? $@ =~ /(\S+\s\S+\s\S+)/ : "errno=$!";  chomp $eval_stat;
    print "$m: $eval_stat\n";
    $exit_status=1;
  }
}

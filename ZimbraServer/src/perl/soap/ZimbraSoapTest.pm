# 
# ***** BEGIN LICENSE BLOCK *****
# Zimbra Collaboration Suite Server
# Copyright (C) 2006, 2007 Zimbra, Inc.
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

# ZimbraSoapTest package
#
# a bunch of not-general-purpose code to make it easy to write short
# perl test scripts against the Zimbra server
package ZimbraSoapTest;

use strict;
use warnings;
use XmlElement;
use XmlDoc;
use Soap;

sub new {
    my ($class, $user, $host, $pw, $opts, $adminHost) = @_;
    my $self  = {};
    $self->{SOAP} = $Soap::Soap12;
    $self->{USER} = $user;
    $self->{OPTIONS} = $opts;

    if (!defined($pw)) {
        $self->{PW} = "test123";
        # check in the environment for a passwd
        my $pwe = "ZIMBRA_PW_".$self->{USER};
        if (defined($ENV{$pwe} && $ENV{$pwe} ne "")) {
            $self->{PW} = $ENV{$pwe};
        }
    } else {
        $self->{PW} = $pw;
    }
 
    if (!defined($host)) {
        $self->{BASE_MAIL_URL} = "http://localhost:7070";
        $self->{BASE_ADMIN_URL} = "https://localhost:7071";

        if (defined($ENV{'ZIMBRA_MAIL_BASEURL'} && $ENV{'ZIMBRA_MAIL_BASEURL'} ne "")) {
            $self->{BASE_MAIL_URL} = $ENV{'ZIMBRA_MAIL_BASEURL'};
        }

        if (defined($ENV{'ZIMBRA_ADMIN_BASEURL'} && $ENV{'ZIMBRA_ADMIN_BASEURL'} ne "")) {
            $self->{BASE_ADMIN_URL} = $ENV{'ZIMBRA_ADMIN_BASEURL'};
        }
    } else {
        $self->{BASE_MAIL_URL} = $host;
        if (defined $adminHost) {
          $self->{BASE_ADMIN_URL} = $adminHost;
        } else {
          $self->{BASE_ADMIN_URL} = $host;
        }
    }
    
    $self->{CONTEXT} = undef;
    $self->{MAIL_URL} = $self->{SOAP}->getMailUrl($self->{BASE_MAIL_URL});
    $self->{ADMIN_URL} = $self->{SOAP}->getAdminUrl($self->{BASE_ADMIN_URL});

    bless ($self, $class);
    
    return $self;
}

sub verbose {
  my ($self, $level) = @_;

  if (!defined($level)) {
    $self->{SOAP}->setLogLevel(0,0);
  } elsif ($level == 0) {
    $self->{SOAP}->setLogLevel(0,0);
  } elsif ($level == 1) {
    $self->{SOAP}->setLogLevel(1,0);
  } elsif ($level == 2) {
    $self->{SOAP}->setLogLevel(0,1);
  } else {
    $self->{SOAP}->setLogLevel(1,1);
  }
}

#
# hacky helper: strip the ns: out for readability
#
sub to_string_simple {
    my ($self, $document) = @_;
    my $toRet = $document->to_string("pretty")."\n";
    $toRet =~ s/ns0\://g;
    return $toRet;
}

sub invokeMail
{
    my ($self, $document) = @_;
    return $self->soap()->invoke($self->mailUrl(), $document, $self->{CONTEXT}, $self->{OPTIONS});
}

sub invokeAdmin
{
    my ($self, $document) = @_;
    return $self->soap()->invoke($self->adminUrl(), $document, $self->{CONTEXT}, $self->{OPTIONS});
}

sub setAuthContext
{
  my ($self, $authtoken, $sessionId, $opts) = @_;
  $self->{CONTEXT} = $self->soap()->zimbraContext($authtoken, $sessionId, 1, $opts);
}


sub doStdAuth
{
  my ($self) = @_;
  $self->{CONTEXT} = $self->{SOAP}->stdAuthByName($self->mailUrl(),
                                                  $self->user(),
                                                  $self->pw(),
                                                  $self->{OPTIONS});
}

sub doAdminAuth
  {
  my ($self) = @_;
  $self->{CONTEXT} = $self->{SOAP}->adminAuthByName($self->adminUrl(),
                                                    $self->user(),
                                                    $self->pw(),
                                                    $self->{OPTIONS});                                                    
}


##############################
#
# Accessors
#
sub soap
{
    my $self = shift;
    return $self->{SOAP};
}
sub user
{
    my $self = shift;
    return $self->{USER};
}
sub pw
{
    my $self = shift;
    return $self->{PW};
}

sub mailUrl
{
    my $self = shift;
    return $self->{MAIL_URL};
}

sub adminUrl
{
    my $self = shift;
    return $self->{ADMIN_URL};
}

sub context
  {
    my $self = shift;
    return $self->{CONTEXT};
  }


1;

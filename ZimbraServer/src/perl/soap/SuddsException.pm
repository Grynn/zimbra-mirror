# 
# ***** BEGIN LICENSE BLOCK *****
# Version: MPL 1.1
# 
# The contents of this file are subject to the Mozilla Public License
# Version 1.1 ("License"); you may not use this file except in
# compliance with the License. You may obtain a copy of the License at
# http://www.zimbra.com/license
# 
# Software distributed under the License is distributed on an "AS IS"
# basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
# the License for the specific language governing rights and limitations
# under the License.
# 
# The Original Code is: Zimbra Collaboration Suite Server.
# 
# The Initial Developer of the Original Code is Zimbra, Inc.
# Portions created by Zimbra are Copyright (C) 2004, 2005 Zimbra, Inc.
# All Rights Reserved.
# 
# Contributor(s):
# 
# ***** END LICENSE BLOCK *****
# 
package SuddsException;

use strict;
use warnings;

use UNIVERSAL qw(isa);

use overload '""' => \&to_string;

BEGIN {
    use Exporter   ();
    our ($VERSION, @ISA, @EXPORT, @EXPORT_OK, @ErrorNames);

    # set the version for version checking
    $VERSION     = 1.00;
    @ISA         = qw(Exporter);
    @EXPORT      = qw();
    @EXPORT_OK   = ();
}

our @EXPORT_OK;

sub new {
    my ($type, $mesg, $doc) = @_;
    my $self = {};
    bless $self, $type;
    $self->{'mesg'} = $mesg;
    $self->{'doc'} = $doc;
    return $self;
}

sub message {
    my $self = shift;
    return $self->{'mesg'};
}

sub document {
    my $self = shift;
    return $self->{'doc'};
}

sub verbose_message {
    my $self = shift;
    my $m = $self->{'mesg'};
    my $doc = $self->{'doc'};
    my $msg = "SuddsException: $m";
    #$msg .= ": doc: ".$doc->to_string() if (defined($doc));
    return $msg;
}

sub to_string {
    my ($self) = @_;
    return $self->verbose_message();
}

1;

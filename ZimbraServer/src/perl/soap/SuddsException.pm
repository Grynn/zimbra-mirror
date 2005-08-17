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

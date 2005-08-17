package Soap;

use strict;
use warnings;

use XML::Parser;

use LWP::UserAgent;
use XmlElement;
use XmlDoc;
use Soap12;
use Soap11;
 
#use overload '""' => \&to_string;

BEGIN {
    use Exporter   ();
    our ($VERSION, @ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS);

    # set the version for version checking
    $VERSION     = 1.00;
    @ISA         = qw(Exporter);
    @EXPORT      = qw();
    %EXPORT_TAGS = ( );     # eg: TAG => [ qw!name1 name2! ],

    # your exported package globals go here,
    # as well as any optionally exported functions
    @EXPORT_OK   = qw();
}

our @EXPORT_OK;

our $Soap12 = new Soap12;
our $Soap11 = new Soap11;

#
# given a XmlElement, wrap it in a SOAP envelope and return the envelope
#

sub soapEnvelope {
    die "must override";
}

#
# Given a RemoteException, wrap it in a soap fault return the 
# soap fault document.
#
#public abstract XmlObject soapFault(java.rmi.RemoteException re);

#
# Given an XmlObject that represents a fault (i.e,. isFault returns 
# true on it), construct a SuddsFaultException from it.
#
#public abstract SuddsFaultException suddsFault(XmlObject doc);

#
# Return Content-Type header
#

sub getContentType() {
    die "must override";
}

#
# Return the namespace String
#

sub getNamespace {
    die "must override";
}

#
# Return charset encoding for converting from bytes/strings
#

sub getCharSet {
    return "UTF-8";
}

#
# Convert a SOAP message in a String to bytes
#

sub convertToBytes {
    die "not implemented yet";
}

#
# Convert a SOAP message in bytes to a String 
#

sub convertToString {
    die "not implemented yet";
}

#
# return the first child in the soap body
#

sub getElement {
    die "must override";
}

#
# Returns true if this element represents a SOAP fault
#

sub isFault {
    die "must override";
}

#
# Returns true if this soap envelope has a SOAP fault as the
# first child of its body.     
#

sub hasFault {
    my ($self, $e) = @_;
    return $self->isFault($e->child(0));
}

#
# determine if given element is Soap11 or Soap12 envelope,
# and returns the Soap11 or Soap12 instance, or undef if neither.
#

sub determineProtocol {
    my $e = shift;
    return undef unless $e->name() eq "Envelope";
    return $Soap12 if ($e->ns() eq $Soap12->getNamespace());
    return $Soap11 if ($e->ns() eq $Soap11->getNamespace());
    return undef;
}

#
# Whether or not to include a HTTP SOAPActionHeader. (Gag)
#

sub hasSOAPActionHeader {
    die "must override";
}

#
# returns the version as a string (e.g, "1.1" or "1.2")
#

sub getVersion {
    die "must override";
}

#
sub toString {
    my $self = shift;
    return "SOAP ".$self->getVersion();
}
 
sub liquidContext {
	my ($self, $authtoken, $session, $wantcontext) = @_;
	my $context = new XmlElement("context", "urn:liquid");
	my $auth = new XmlElement("authToken");
	$auth->content($authtoken);
	$context->add_child($auth);
    if ($session ne "") {
        my $sessionElt = new XmlElement("sessionId");
        $sessionElt->content($session);
        $context->add_child($sessionElt);
    }
	if (! defined ($wantcontext) || ! $wantcontext) {
		my $want = new XmlElement("nonotify");
		$want->content("");
		$context->add_child($want);
	}
	return $context;		
}

# simple invoke method for now, this will get replaced

sub invoke {
    my ($self, $uri, $doc, $context) = @_;

    my $env = $self->soapEnvelope($doc, $context);
    my $soap = $env->to_string();
    my $ua = new LWP::UserAgent();
    my $req = new HTTP::Request(POST=> $uri);

    $req->content_type($self->getContentType());
    $req->content_length(length($soap));
    if ($self->hasSOAPActionHeader()) {
	$req->header("SOAPAction" => $uri);
    }
    $req->add_content($soap);
    my $res = $ua->request($req);

#    print $res->content;

    my $xml = XmlElement::parse($res->content);

    my $rsoap = Soap::determineProtocol($xml);

    die "unable to determine soap protocol" unless defined $rsoap;
    die "unexpected soap version in response" unless $rsoap == $self;

    # FIXME: fault handling here
    return $self->getElement($xml);
}

1;

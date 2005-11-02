use strict;
use warnings;

package CamelBones::NSRange;

our @ISA = qw(Exporter);

sub getLocation {
	my ($self) = @_;
	my ($location, $length) = unpack('II', $$self);
	return $location;
}

sub getLength {
	my ($self) = @_;
	my ($location, $length) = unpack('II', $$self);
	return $length;
}

sub setLocation {
	my ($self, $newLocation) = @_;
	my ($location, $length) = unpack('II', $$self);
	$$self = pack('II', $newLocation, $length);
}

sub setLength {
	my ($self, $newLength) = @_;
	my ($location, $length) = unpack('II', $$self);
	$$self = pack('II', $location, $newLength);
}

sub setAll {
	my ($self, $newLocation, $newLength) = @_;
	$$self = pack('II', $newLocation, $newLength);
}

sub getHashref {
	my ($self) = @_;
	my ($location, $length) = unpack('II', $$self);
	
	return {
		'location' => $location,
		'length' => $length,
	};
}

sub getArrayref {
	my ($self) = @_;
	my ($location, $length) = unpack('II', $$self);
	
	return [$location, $length];
}

1;

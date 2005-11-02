use strict;
use warnings;

package CamelBones::NSPoint;

our @ISA = qw(Exporter);

sub getX {
	my ($self) = @_;
	my ($x, $y) = unpack('ff', $$self);
	return $x;
}

sub getY {
	my ($self) = @_;
	my ($x, $y) = unpack('ff', $$self);
	return $y;
}

sub setX {
	my ($self, $newx) = @_;
	my ($x, $y) = unpack('ff', $$self);
	$$self = pack('ff', $newx, $y);
}

sub setY {
	my ($self, $newy) = @_;
	my ($x, $y) = unpack('ff', $$self);
	$$self = pack('ff', $x, $newy);
}

sub setAll {
	my ($self, $newx, $newy) = @_;
	$$self = pack('ff', $newx, $newy);
}

sub getHashref {
	my ($self) = @_;
	my ($x, $y) = unpack('ff', $$self);

	return {
		'x' => $x,
		'y' => $y,
	};
}

sub getArrayref {
	my ($self) = @_;
	my ($x, $y) = unpack('ff', $$self);
	
	return [$x, $y];
}

1;

use strict;
use warnings;

package CamelBones::NSRect;

our @ISA = qw(Exporter);

sub getX {
	my ($self) = @_;
	my ($x, $y, $width, $height) = unpack('ffff', $$self);
	return $x;
}

sub getY {
	my ($self) = @_;
	my ($x, $y, $width, $height) = unpack('ffff', $$self);
	return $y;
}

sub getWidth {
	my ($self) = @_;
	my ($x, $y, $width, $height) = unpack('ffff', $$self);
	return $width;
}

sub getHeight {
	my ($self) = @_;
	my ($x, $y, $width, $height) = unpack('ffff', $$self);
	return $height;
}

sub setX {
	my ($self, $newX) = @_;
	my ($x, $y, $width, $height) = unpack('ffff', $$self);
	$$self = pack('ffff', $newX, $y, $width, $height);
}

sub setY {
	my ($self, $newY) = @_;
	my ($x, $y, $width, $height) = unpack('ffff', $$self);
	$$self = pack('ffff', $x, $newY, $width, $height);
}

sub setWidth {
	my ($self, $newWidth) = @_;
	my ($x, $y, $width, $height) = unpack('ffff', $$self);
	$$self = pack('ffff', $x, $y, $newWidth, $height);
}

sub setHeight {
	my ($self, $newHeight) = @_;
	my ($x, $y, $width, $height) = unpack('ffff', $$self);
	$$self = pack('ffff', $x, $y, $width, $newHeight);
}

sub setAll {
	my ($self, $newX, $newY, $newWidth, $newHeight) = @_;
	$$self = pack('ffff', $newX, $newY, $newWidth, $newHeight);
}

sub getHashref {
	my ($self) = @_;
	my ($x, $y, $width, $height) = unpack('ffff', $$self);
	
	return {
		'x' => $x,
		'y' => $y,
		'width' => $width,
		'height' => $height,
	};
}

sub getArrayref {
	my ($self) = @_;
	my ($x, $y, $width, $height) = unpack('ffff', $$self);
	
	return [$x, $y, $width, $height];
}

1;

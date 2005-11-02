use strict;
use warnings;

package CamelBones::NSSize;

our @ISA = qw(Exporter);

sub getWidth {
	my ($self) = @_;
	my ($width, $height) = unpack('ff', $$self);
	return $width;
}

sub getHeight {
	my ($self) = @_;
	my ($width, $height) = unpack('ff', $$self);
	return $height;
}

sub setWidth {
	my ($self, $newWidth) = @_;
	my ($width, $height) = unpack('ff', $$self);
	$$self = pack('ff', $newWidth, $height);
}

sub setHeight {
	my ($self, $newHeight) = @_;
	my ($width, $height) = unpack('ff', $$self);
	$$self = pack('ff', $width, $newHeight);
}

sub setAll {
	my ($self, $newWidth, $newHeight) = @_;
	$$self = pack('ff', $newWidth, $newHeight);
}

sub getHashref {
	my ($self) = @_;
	my ($width, $height) = unpack('ff', $$self);

	return {
		'width' => $width,
		'height' => $height,
	};
}

sub getArrayref {
	my ($self) = @_;
	my ($width, $height) = unpack('ff', $$self);

	return [$width, $height];
}

1;

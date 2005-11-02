#
# TiedArray
#
# Part of CamelBones - copyright 2004 Sherm Pendley
# Released under the terms of the Lesser GNU Public License (LGPL)

package CamelBones::TiedArray;

use Tie::Array;

use strict;
use warnings;

our @ISA = qw(Tie::Array);

sub nativeArray {
	my ($self) = @_;
	return $self->{'native'};
}

sub TIEARRAY {
	my ($class, $native) = @_;
	return bless({ 'native' => $native }, $class);
}

sub FETCH {
	my ($self, $index) = @_;
	return $self->{'native'}->objectAtIndex($index);
}

sub FETCHSIZE {
	my ($self) = @_;
	return $self->{'native'}->count();
}

sub STORE {
	my ($self, $index, $value) = @_;
	unless (defined $value) {
		$value = NSNull->null();
	}
	$self->{'native'}->insertObject_atIndex($value, $index);
}

sub STORESIZE {
	my ($self, $count) = @_;
	my $native = $self->{'native'};
	my $rel = $count <=> $native->count();
	
	if ($rel == 0) { return; }
	elsif ($rel == -1) {
		my $range = {
			'location' => $count-1,
			'length' => $native->count()-$count,
		};
		$native->removeObjectsInRange($range);
	} else {
		my $null = NSNull->null();
		while($native->count()<$count) {
			$native->addObject($null);
		}
	}
}

sub EXISTS {
	my ($self, $index) = @_;
	my $native = $self->{'native'};
	if ($index >= $native->count() &&
		$native->objectAtIndex($index)->className != 'NSNull') {
		return 1;
	}
	return 0;
}

sub DELETE {
	my ($self, $index) = @_;
	$self->{'native'}->removeObjectAtIndex($index);
}

1;

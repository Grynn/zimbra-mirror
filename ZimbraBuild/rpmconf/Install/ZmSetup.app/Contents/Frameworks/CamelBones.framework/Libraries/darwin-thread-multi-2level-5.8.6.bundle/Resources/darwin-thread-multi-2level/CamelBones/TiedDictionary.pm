#
# TiedDictionary
#
# Part of CamelBones - copyright 2004 Sherm Pendley
# Released under the terms of the Lesser GNU Public License (LGPL)

package CamelBones::TiedDictionary;

use Tie::Hash;

use strict;
use warnings;

our @ISA = qw(Tie::Hash);

sub nativeDictionary {
	my ($self) = @_;
	return $self->{'native'};
}

sub TIEHASH {
	my ($class, $native) = @_;
	return bless({ 'native' => $native }, $class);
}

sub STORE {
	my ($self, $key, $value) = @_;
	my $native = $self->{'native'};
	$native->setObject_forKey($value, $key);
}

sub FETCH {
	my ($self, $key) = @_;
	my $native = $self->{'native'};
	return $native->objectForKey($key);
}

sub FIRSTKEY {
	my ($self) = @_;
	my $native = $self->{'native'};
	my $keys = $native->allKeys();
	return $keys->objectAtIndex(0);
}

sub NEXTKEY {
	my ($self, $lastkey) = @_;
	my $native = $self->{'native'};
	my $keys = $native->allKeys();
	my $lastKeyIndex = $keys->indexOfObject($lastkey);
	if ($lastKeyIndex < $keys->count()-1) {
		return $keys->objectAtIndex($lastKeyIndex+1);
	} else {
		return undef;
	}
}

sub EXISTS {
	my ($self, $key) = @_;
	my $native = $self->{'native'};
	if (defined $native->objectForKey($key)) {
		return 1;
	} else {
		return 0;
	}
}

sub DELETE {
	my ($self, $key) = @_;
	my $native = $self->{'native'};
	$native->removeObjectForKey($key);
}

sub CLEAR {
	my ($self) = @_;
	my $native = $self->{'native'};
	$native->removeAllObjects();
}

1;

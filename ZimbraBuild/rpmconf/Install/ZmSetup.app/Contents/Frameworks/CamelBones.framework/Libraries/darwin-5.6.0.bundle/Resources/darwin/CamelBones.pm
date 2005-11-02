use strict;
use warnings;

package CamelBones;
require Exporter;

use CamelBones::Foundation qw(:All);
use CamelBones::Foundation::Constants;
use CamelBones::AppKit qw(:All);
use CamelBones::AppKit::Constants;

use CamelBones::NSPoint;
use CamelBones::NSRange;
use CamelBones::NSRect;
use CamelBones::NSSize;

use CamelBones::TiedArray;
use CamelBones::TiedDictionary;

use Config;

our @ISA = qw(Exporter);
our $VERSION = '1.0.0';
our @EXPORT = qw(class);
our @EXPORT_OK = (	@CamelBones::Foundation::EXPORT_OK,
                    @CamelBones::Foundation::Constants::EXPORT,
                    @CamelBones::Foundation::Globals::EXPORT,
                    @CamelBones::AppKit::EXPORT_OK,
                    @CamelBones::AppKit::Constants::EXPORT,
                    @CamelBones::AppKit::Globals::EXPORT,
                    'class', 'CBCreateAccessor', 'CBPoke',
                );
our %EXPORT_TAGS = (
    'All'		=> [@EXPORT_OK],
    'Foundation' => [
            @CamelBones::Foundation::EXPORT,
            @CamelBones::Foundation::Constants::EXPORT,
            @CamelBones::Foundation::Globals::EXPORT,
        ],
    'AppKit' => [
            @CamelBones::AppKit::EXPORT,
            @CamelBones::AppKit::Constants::EXPORT,
            @CamelBones::AppKit::Globals::EXPORT,
        ],
);

# Defaults for options

# Outlet typing options
our $StrictTypeChecking = 1;
our $VeryStrictTypeChecking = 1;

# Warnings
our $ShowUnhandledTypeWarnings = 0;

# Conversion options
our $ReturnStringsAsObjects = 0;

require XSLoader;
XSLoader::load('CamelBones', $VERSION);
CamelBones::CBInit($Config{'archname'}.'-'.$Config{'version'});
CamelBones::Foundation::Globals->import;
CamelBones::AppKit::Globals->import;

# Add a bundle loader to @INC
push @INC, sub {
	my ($self, $filename) = @_;
	(my $shortname = $filename) =~ s/.pm$//;
	my @framework_path;
	if ($ENV{'DYLD_FRAMEWORK_PATH'}) {
		push @framework_path, split(/:/, $ENV{'DYLD_FRAMEWORK_PATH'});
	}
	push @framework_path, qw(/System/Library/Frameworks /Library/Frameworks /Network/Frameworks);
	push @framework_path, $ENV{'HOME'} . '/Library/Frameworks';
	if ($ENV{'DYLD_FALLBACK_FRAMEWORK_PATH'}) {
	    push @framework_path, split(/:/, $ENV{'DYLD_FALLBACK_FRAMEWORK_PATH'});
	}
	foreach my $path (@framework_path) {
		my $framework = "$path/$shortname.framework";
		if (-d $framework) {
			my $bundle = NSBundle->bundleWithPath($framework);
			if ($bundle) {
				$bundle->load();
				my $module_path = $bundle->pathForResource_ofType($shortname, 'pm');
				if ($module_path && open(my $fh, '<', $module_path)) {
					return $fh;
				} else {
					my $cb_bundle = NSBundle->bundleForClass('CBPerl');
					$module_path = $cb_bundle->pathForResource_ofType('CBDummyModule', 'pm');
					if ($module_path) {
						open($fh, '<', $module_path) && return $fh;
						warn "Could not open $module_path: $!";
						return undef;
					} else {
						warn "CBDummyModule.pm not found";
						return undef;
					}
				}
			}
			last;
		}
	}
	return undef;
};

sub CBCreateAccessor {
    my ($class, $property, $type) = @_;
    {
        no strict 'refs';
        
        # Setter
        my $setter = 'set'.ucfirst($property);
        unless ( UNIVERSAL::can($class, $setter) ) { # don't overwrite existing method
            *{ $class.'::'.$setter } = sub {
                my ($self, $set) = @_;
                if ($CamelBones::StrictTypeChecking != 0) {
                    unless ($self->UNIVERSAL::isa($type)) {
                        CamelBones::Foundation::NSLog("Can't set $property to $set - object must be a $type");
                        if ($CamelBones::VeryStrictTypeChecking != 0) {
                            return;
                        }
                    }
                }
                $self->{$property} = $set;
            };
        
            # Export the setter to objc
            ${$class.'::OBJC_EXPORT'}{$setter . ':'}={'args'=>'@', 'return'=>'v'};
        }
        
        # getter
        my $getter = $property;
        unless ( UNIVERSAL::can($class, $getter) ) {
            *{ $class.'::'.$getter } = sub {
                my ($self) = @_;
                $self->{$property};
            };
            
            # Export it to objc
            ${$class.'::OBJC_EXPORT'}{$getter}={'args'=>'', 'return'=>'@'};
        }
    }
}

# Register a Perl class with the runtime
sub class {
    # We need symrefs to work with the symbol table
    no strict 'refs';

    my $package = (caller)[0]; # the package the caller is in
    my $objc_class;
    my $super_class;
    
    # See if the class was specified, or assumed from package name
    if (@_) {
        $objc_class = shift;
    } else {
        $objc_class = $package;
    }
    
    # Check for a second argument
    if (@_) {
        my $class_definition = shift;
        
        # If superclass was specified, use it
        if (exists $class_definition->{'super'}) {
            $super_class = $class_definition->{'super'};
        }

        # If properties were listed, create accessors for them
        if (exists $class_definition->{'properties'}) {
            if (ref $class_definition->{'properties'} eq 'HASH') {
                foreach my $property (keys %{$class_definition->{'properties'}}) {
                    my $type = $class_definition->{'properties'}->{$property} || 'NSObject';
                    CBCreateAccessor($objc_class, $property, $type);
                }
            } elsif (ref $class_definition->{'properties'} eq 'ARRAY') {
                foreach my $property (@{$class_definition->{'properties'}}) {
                    CBCreateAccessor($objc_class, $property, 'NSObject');
                }
            }
        }
    }
    
    # If it wasn't set above, look for first registered class in @ISA
    unless ($super_class) {
        my @isas = @{$objc_class .'::ISA'};
        
        my %checked = ();
        while ($_ = shift @isas) {
            next if (exists $checked{$_});

            if (CBIsClassRegistered($_)) {
                $super_class = $_;
                last;
            }

            $checked{$_} = 1;

            push @isas, @{$_ . '::ISA'};
        }
        
        # Default to NSObject
        $super_class ||= 'NSObject';
    }
    
    # Create @ISA if needed
    unless ( grep /$super_class/, @{$objc_class.'::ISA'} ) {
        push @{$objc_class.'::ISA'}, $super_class;
    }
    
    # Register the caller class with the runtime if needed
    unless (CBIsClassRegistered($objc_class)) {
    
        if (defined $super_class) {
            CBRegisterClassWithSuperClass($objc_class, $super_class);
        }
    }

    # Look again for class registration - it might have failed the checks above
    if (CBIsClassRegistered($objc_class)) {

        # Look for unregistered methods        
        my @class_method_reg_list;
        my @object_method_reg_list;

        my $exports = \%{$package.'::OBJC_EXPORT'};
        foreach my $sel (keys %{$package.'::OBJC_EXPORT'}) {
			my $method = $exports->{$sel};
			my $signature = $method->{'return'} . '@:' . $method->{'args'};
			if ($method->{'static'}) {
				push @class_method_reg_list, { 'name' => $sel, 'signature' => $signature };
			} else {
				push @object_method_reg_list, { 'name' => $sel, 'signature' => $signature };
			}
        }

        # Register any unregistered methods
        if (@object_method_reg_list) {
			my $list_count = @object_method_reg_list;
            CBRegisterObjectMethodsForClass($package, \@object_method_reg_list, $objc_class);
        }
        if (@class_method_reg_list) {
            CBRegisterClassMethodsForClass($package, \@class_method_reg_list, $objc_class );
        }
    }
}

# NSObject defines autoloading behavior for all classes
package NSObject;
@NSObject::ISA = qw(Exporter);
@NSObject::EXPORT = qw();
$NSObject::VERSION = $CamelBones::VERSION;

use overload
	'==' => \&CB_EQUALITY,
	'eq' => \&CB_EQUALITY,
	'bool' => \&CB_BOOL;

sub CB_EQUALITY {
	my ($a, $b) = @_;
	return 0 unless (defined $a && defined $b);
	
	return ($a->{'NATIVE_OBJ'} == $b->{'NATIVE_OBJ'});
}

sub CB_BOOL {
	return (defined($_[0]) && defined($_[0]->{'NATIVE_OBJ'}));
}

sub AUTOLOAD {
	no strict;
	no warnings;

    my $subName = $NSObject::AUTOLOAD;
    my $selString = $subName;
    my $native = undef;
    my $class = undef;
    my $returnObject = undef;

    $selString =~ s/^\w+:://;
    $selString =~ s/_/:/g;
    if (@_ > 1 && $selString !~ /:$/) {
        $selString .= ':';
    }

    my $isSuperMethod = undef;

    if ($selString =~ /^SUPER::/) {
        $selString =~ s/^SUPER:://;
        $isSuperMethod = 1;
    }

	# Strip away the class name from the selector
	$selString =~ s/^.*::(.*)/$1/;

    my $self = shift;
    $returnObject = CamelBones::CBCallNativeMethod(
                $self, $selString, \@_, $isSuperMethod);

	if (wantarray && ref($returnObject)) {
		if ($returnObject->isa('NSArray')) {
			my @array;
			tie(@array, 'CamelBones::TiedArray', $returnObject);
			return(@array);
		} elsif ($returnObject->isa('NSDictionary')) {
			my %hash;
			tie(%hash, 'CamelBones::TiedDictionary', $returnObject);
			return (%hash);
		} else {
			return $returnObject;
		}
	} else {
		return $returnObject;
	}
}

sub UNIVERSAL::MODIFY_CODE_ATTRIBUTES {
	my ($class, $sub, @attrs) = @_;
	my $selector = '';
	my $props = {};
	my @unknown = ();

	# Iterate over each attribute, handling the known ones
	foreach (@attrs) {
		if (/Selector\((.*)\)/) {
			$selector = $1;
		} elsif ($_ eq 'IBAction') {
			$props->{'args'} = '@';
			$props->{'return'} = 'v';
		} elsif (/ArgTypes\((.*)\)/) {
			$props->{'args'} = $1;
		} elsif (/ReturnType\((.*)\)/) {
			$props->{'return'} = $1;
		} elsif ($_ eq 'Class') {
			$props->{'static'} = 1;
		} else {
			push(@unknown, $_);
		}
	}
	
	# If a selector was found, export it
	if ($selector ne '') {
		no strict 'refs';

        # Default to no args, void return
		unless (exists $props->{'args'}) { $props->{'args'} = ''; }
		unless (exists $props->{'return'}) { $props->{'return'} = 'v'; }
		
		${$class.'::OBJC_EXPORT'}{$selector} = $props;

		my $method = $selector;
		$method =~ s/:/__/g;
		${$class.'::OBJC_EXPORT'}{$selector}{'method'} = $method;
		*{$class.'::'.$method} = $sub;

	} else {
		warn("Warning - Method not exported, attributes used without Selector");
	}

	return @unknown;
}

# Happy Perl
1;

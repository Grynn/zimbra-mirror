# ZmSetup version 0.1, Copyright 2005 __MyCompanyName__.

use strict;
use warnings;

package ZmSetup;

use CamelBones qw(:All);

use ZmSetupWindowController;

class ZmSetup {
    'super' => 'NSObject',
    'properties' => [ 'wc' ],
};

sub applicationWillFinishLaunching : Selector(applicationWillFinishLaunching:)
        ArgTypes(@) ReturnType(v) {

    my ($self, $notification) = @_;

    # Create the new controller object
    $self->setWc(ZmSetupWindowController->alloc()->init());

    return 1;
}

1;

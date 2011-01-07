//
//  RotatingTabBarController.m
//  Zimbra
//
//  Created by Qin An on 12/23/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import "RotatingTabBarController.h"


@implementation RotatingTabBarController

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Always returning YES means the view will rotate to accomodate any orientation.
	NSLog(@"shouldAutorotateToInterfaceOrientation");
    return YES;
}

- (void)didRotateFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation{
    [self adjustViewsForOrientation:self.interfaceOrientation];
}

- (void) adjustViewsForOrientation:(UIInterfaceOrientation)orientation {
    if (orientation == UIInterfaceOrientationLandscapeLeft || orientation == UIInterfaceOrientationLandscapeRight) {
        NSLog(@"Subview Landscape");
        //Do Your Landscape Changes here
    }
    else if (orientation == UIInterfaceOrientationPortrait || orientation == UIInterfaceOrientationPortraitUpsideDown) {
        NSLog(@"Subview Portrait");
        //Do Your Portrait Changes here
    }
}

- (void) willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
    [self adjustViewsForOrientation:toInterfaceOrientation];    
}

@end

//
//  BrightnessController.m
//  Zimbra
//
//  Created by Qin An on 12/12/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import "BrightnessController.h"
#import "BoxMaker.h"


@implementation BrightnessController

#define MAXDEPTH 8

- (void) doAction
{
}

-(BrightnessController *) initWithBrightness: (int) aBrightness
{
	self = [super init];
	brightness = aBrightness;
	self.title = [NSString stringWithFormat:@"%d%%", brightness * 10];
	[self.tabBarItem initWithTitle:self.title image:createImage(((float) brightness / 10.0f)) tag:0];
	//[self.tabBarItem initWithTitle:self.title image:(BoxMaker.createImage(((float) brightness / 10.0f))) tag:0];
	return self;
}

- (void)loadView
{
	UIView *contentView = [[UIView alloc] init];
	float percent = brightness * 0.1;
	contentView.backgroundColor = [UIColor colorWithRed:percent green:percent blue:percent alpha:1.0];
	contentView.autoresizesSubviews = YES;
	contentView.autoresizingMask = (UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight);
	self.view = contentView;
    [contentView release];
}


@end

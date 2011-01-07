//
//  UICustomSwitch.m
//  Zimbra
//
//  Created by Qin An on 12/15/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import "UICustomSwitch.h"

@interface UISwitch (extended)
- (void) setAlternateColors:(BOOL) boolean;
@end

@interface _UISwitchSlider : UIView
@end

@implementation UICustomSwitch

- (_UISwitchSlider *) slider { 
	return [[self subviews] lastObject]; 
}
- (UIView *) textHolder { 
	return [[[self slider] subviews] objectAtIndex:2]; 
}
- (UILabel *) leftLabel { 
	return [[[self textHolder] subviews] objectAtIndex:0]; 
}
- (UILabel *) rightLabel { 
	return [[[self textHolder] subviews] objectAtIndex:1]; 
}
- (void) setLeftLabelText: (NSString *) labelText { 
	//[[self leftLabel] setText:labelText]; 
	self.leftLabel.text = labelText;
}
- (void) setRightLabelText: (NSString *) labelText { 
	//[[self rightLabel] setText:labelText]; 
	self.rightLabel.text = labelText;
}
- (void) setLeftTextFont: (UIFont*) font {
	[[self leftLabel] setFont:font];
}
- (void) setRightTextFont: (UIFont*) font {
	[[self rightLabel] setFont:font];
}
- (void) setLeftTextColor: (UIColor *) color {
	[[self leftLabel] setTextColor:color];
}
- (void) setRightTextColor: (UIColor *) color {
	[[self rightLabel] setTextColor:color];
}
@end

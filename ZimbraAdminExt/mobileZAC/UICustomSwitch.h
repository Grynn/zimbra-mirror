//
//  UICustomSwitch.h
//  Zimbra
//
//  Created by Qin An on 12/15/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface UICustomSwitch : UISwitch {

}

- (void) setLeftLabelText: (NSString *) labelText;
- (void) setRightLabelText: (NSString *) labelText;
- (void) setLeftTextFont: (UIFont*) font;
- (void) setRightTextFont: (UIFont*) font;
- (void) setLeftTextColor: (UIColor *) color;
- (void) setRightTextColor: (UIColor *) color;


@end

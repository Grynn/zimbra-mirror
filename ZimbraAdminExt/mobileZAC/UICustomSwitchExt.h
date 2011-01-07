//
//  UICustomSwitchExt.h
//  Zimbra
//
//  Created by Qin An on 12/30/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface UICustomSwitchExt : UISlider {
	BOOL on;
	UIColor *tintColor;
	UIView *clippingView;
	UILabel *rightLabel;
	UILabel *leftLabel;
	
	// private member
	BOOL m_touchedSelf;
}

@property(nonatomic,getter=isOn) BOOL on;
@property (nonatomic,retain) UIColor *tintColor;
@property (nonatomic,retain) UIView *clippingView;
@property (nonatomic,retain) UILabel *rightLabel;
@property (nonatomic,retain) UILabel *leftLabel;

+ (UICustomSwitchExt *) switchWithLeftText: (NSString *) tag1 andRight: (NSString *) tag2;

- (void)setOn:(BOOL)on animated:(BOOL)animated;
- (void)scaleSwitch:(CGSize)newSize;

@end

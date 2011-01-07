//
//  BrightnessController.h
//  Zimbra
//
//  Created by Qin An on 12/12/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface BrightnessController : UIViewController {
	int brightness;
}

-(BrightnessController *) initWithBrightness: (int) aBrightness;

@end

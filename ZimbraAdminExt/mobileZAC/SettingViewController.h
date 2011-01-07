//
//  SettingViewController.h
//  Zimbra
//
//  Created by Qin An on 1/5/11.
//  Copyright 2011 VMware. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface SettingViewController : UITableViewController <UITextFieldDelegate> {
	NSString *zcsServer;
	NSString *zcsPort;
	NSString *sslSupport;
	NSString *x509Support;
	NSString *autolockSupport;
	
	UITextField *servertf;
	UITextField *porttf;
}

@end

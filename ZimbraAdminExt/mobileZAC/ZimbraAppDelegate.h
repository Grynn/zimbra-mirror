//
//  ZimbraAppDelegate.h
//  Zimbra
//
//  Created by Qin An on 12/11/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import <UIKit/UIKit.h>

@class SecurityController;

@interface ZimbraAppDelegate :  NSObject <UIApplicationDelegate, UITabBarControllerDelegate, UIAlertViewDelegate> {
	NSUInteger previousTab;
	NSUInteger currentTab;
	NSMutableArray *currentAcct;  // for edit controller
	NSMutableArray *allAcct; 
	NSString *apnBadge;
	NSMutableArray *controllers;
	
	SecurityController *securityController;
}

@property (retain, retain) SecurityController *securityController;
@property (retain, retain) NSString *apnBadge;
@property (retain, retain) NSMutableArray *controllers;

+ (ZimbraAppDelegate *)sharedAppDelegate;
- (void) setCurrentAccount: (NSMutableArray*) info;
- (NSMutableArray*) getCurrentAccount;
- (void) setAllAccount: (NSMutableArray*) info;
- (NSMutableArray*) getAllAccount;
- (SecurityController*) getSecurityController;

@end

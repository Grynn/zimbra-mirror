//
//  SecurityController.h
//  Zimbra
//
//  Created by Qin An on 12/20/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import <Foundation/Foundation.h>

@class GetController;


@interface SecurityController : NSObject <UIAlertViewDelegate> {
	NSString *appStatus; // "active", "retired","..."
	NSString *authToken;
	NSString *dlgType;   // "MainApp", "ChildApp", "..."
	
	GetController *getController;
	UIViewController *viewController;
}

@property (nonatomic, retain) NSString *appStatus;
@property (nonatomic, retain) NSString *authToken;
@property (nonatomic, retain) NSString *dlgType;

@property (nonatomic, retain) GetController *getController;
@property (retain, nonatomic) UIViewController *viewController;

- (void) loginMain;
- (void) executeConfirm;

- (void) setMyViewController:(UIViewController *) vcontroller;

@end

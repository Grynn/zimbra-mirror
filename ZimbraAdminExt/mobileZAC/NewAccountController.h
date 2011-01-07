//
//  NewAccountController.h
//  Zimbra
//
//  Created by Qin An on 12/14/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import <UIKit/UIKit.h>

@class PutController;

@interface NewAccountController : UIViewController <UITextFieldDelegate> {
	BOOL isLoadData;
	UIImageView *contentView;
	UITextField *namefield;
	UITextField *acctfield;
	UITextField *passfield;
	UITextField *rpassfield;
	
	UISwitch *adminSwitch;
	UISwitch *chgPassSwitch;
	UISwitch *statusSwitch;
	
	PutController *putController;
	
	NSInteger indexOfTab; // for edit mode, indexing current accountView
	
	NSString *nameOld;
	NSString *passOld;
	NSInteger adminOld;
	NSInteger chgpassOld;
	NSInteger statusOld;
	
}
@property (nonatomic, readwrite) BOOL isLoadData;
@property (nonatomic, readwrite) NSInteger indexOfTab;

-(NewAccountController *) initWithTitle: (NSString *)title loadData: (BOOL) isLoad;
- (void) CommitOperation;

@property (nonatomic, retain) PutController *putController;

@end

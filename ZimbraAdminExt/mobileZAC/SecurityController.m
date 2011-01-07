//
//  SecurityController.m
//  Zimbra
//
//  Created by Qin An on 12/20/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import "SecurityController.h"
#import "ZmprovController.h"
#import "AccountStatusController.h"
#import "ChangePasswordController.h"
#import "NewAccountController.h"
#import "AccountListViewControllerExt.h"
#import "StatusListViewController.h"


@implementation SecurityController

@synthesize appStatus;
@synthesize authToken;
@synthesize dlgType;
@synthesize getController;
@synthesize viewController;

- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
	char *pAcctName = [[[alertView textFieldAtIndex:0] text] cStringUsingEncoding:1];
	char *pPassWord = [[[alertView textFieldAtIndex:1] text] cStringUsingEncoding:1];

	NSString *acctName = [[NSString alloc]initWithCString:pAcctName]; 
	NSString *passWord = [[NSString alloc]initWithCString:pPassWord]; 
	
	NSLog(@"User Pressed Button %d", buttonIndex + 1);
	NSLog(@"account = %@, passwd = %@",acctName, passWord);
	
	if (buttonIndex == 0) {
		if ([dlgType isEqualToString:@"MainApp"]) {
			// user pressed exit button, exit App
			exit(0);
		} else {
			// user pressed cancel button, exit to app
			return;
		}

		//[[NSThread mainThread] exit];
	}
	
	if (acctName != nil && passWord != nil 
		&& [acctName isEqualToString:@"admin"]
		&& [passWord isEqualToString:@"test123"]) {
		appStatus = @"active";
		[alertView release];
		
		if (viewController !=nil) {
			if ([viewController isKindOfClass:[ZmprovController class]]) {
				ZmprovController* pController = (ZmprovController*) viewController;
				[pController CommitOperation];
			} else if ([viewController isKindOfClass:[AccountStatusController class]]) {
				AccountStatusController* pController = (AccountStatusController*) viewController;
				[pController CommitOperation];
			} else if ([viewController isKindOfClass:[ChangePasswordController class]]) {
				ChangePasswordController* pController = (ChangePasswordController*) viewController;
				[pController CommitOperation];
			} else if ([viewController isKindOfClass:[NewAccountController class]]) {
				NewAccountController* pController = (NewAccountController*) viewController;
				[pController CommitOperation];
			} else if ([viewController isKindOfClass:[AccountListViewControllerExt class]]) {
				AccountListViewControllerExt* pController = (AccountListViewControllerExt*) viewController;
				[pController CommitOperation];
			} else if ([viewController isKindOfClass:[StatusListViewController class]]) {
				StatusListViewController* pController = (StatusListViewController*) viewController;
				[pController CommitOperation];
			}
			viewController = nil;
		}

	} else {
		[alertView release];
		[self loginDialog:TRUE];
	}

}

- (void) loginDialog: (BOOL) isRepeat
{
	NSString *alertTitleLogin = @"Login";
	NSString *alertTitleConfirm = @"Confirm Authorization";
	NSString *msgHintShow = @"Specify the username and password.";
	NSString *msgPwdRetype = @"Incorrect username and password. Try again.";
	BOOL isLoginType = [dlgType isEqualToString:@"MainApp"];
	
	UIAlertView *alert = [[UIAlertView alloc] 
						  initWithTitle: (isLoginType? alertTitleLogin:alertTitleConfirm)
						  message:(isRepeat? msgPwdRetype:msgHintShow)
						  delegate:self
						  cancelButtonTitle:(isLoginType?@"Exit":@"Cancel")
						  otherButtonTitles:@"OK", nil];
	[alert addTextFieldWithValue:@"" label:@"Enter Login Name"];
	[alert addTextFieldWithValue:@"" label:@"Enter Login Password"];
	
	// Name field
	UITextField *tf = [alert textFieldAtIndex:0];
	tf.clearButtonMode = UITextFieldViewModeWhileEditing;
	tf.keyboardType = UIKeyboardTypeAlphabet;
	tf.keyboardAppearance = UIKeyboardAppearanceAlert;
	tf.autocapitalizationType = UITextAutocapitalizationTypeWords;
	tf.autocorrectionType = UITextAutocorrectionTypeNo;
	
	// Pass field
	tf = [alert textFieldAtIndex:1];
	tf.clearButtonMode = UITextFieldViewModeWhileEditing;
	tf.keyboardType = UIKeyboardTypeURL;
	tf.secureTextEntry = YES;
	tf.keyboardAppearance = UIKeyboardAppearanceAlert;
	tf.autocapitalizationType = UITextAutocapitalizationTypeNone;
	tf.autocorrectionType = UITextAutocorrectionTypeNo;
	
	[alert show];
}

- (void) loginMain
{
	dlgType = @"MainApp";	
	[self loginDialog:FALSE];
}

- (void) executeConfirm
{	
	dlgType = @"ChildApp";
	[self loginDialog:FALSE];
}

- (void) setMyViewController:(UIViewController *) vcontroller
{
	viewController = vcontroller;
}

@end

//
//  ChangePasswordController.h
//  Zimbra
//
//  Created by Qin An on 12/15/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import <UIKit/UIKit.h>

@class PutController;

@interface ChangePasswordController : UIViewController <UITextFieldDelegate, UITextViewDelegate> {
	UIImageView *contentView;
	UITextField *passfield;
	UITextField *rpassfield;
	UISwitch *chgPassSwitch;

	UITextView *nameView;
	UITextView *emailView;

	PutController *putController;
}

@property (nonatomic, retain) PutController *putController;

- (void) CommitOperation;

@end

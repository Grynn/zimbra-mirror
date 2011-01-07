//
//  ZmprovController.h
//  Zimbra
//
//  Created by Qin An on 12/15/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import <UIKit/UIKit.h>

@class GetController;
@class PutController;

@interface ZmprovController :  UIViewController <UITextFieldDelegate, UITextViewDelegate> {
	UIImageView *contentView;
	UITextField *cmdfield;
	UITextView *outputView;
	
	BOOL inProgressing;
	NSString *preOutput;
	NSTimer *receiveTimer;
	
	GetController *getController;
	PutController *putController;
}

@property (nonatomic, retain) GetController *getController;
@property (nonatomic, retain) PutController *putController;
@property (nonatomic, retain) NSString *preOutput;

- (void) CommitOperation;

@end

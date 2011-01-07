//
//  AccountViewControllerExt.h
//  Zimbra
//
//  Created by Qin An on 12/16/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface AccountViewControllerExt : UIViewController <UITextViewDelegate> {
	UIImageView *contentView;
	NSString *displayname;
	NSString *emailValue;
	NSString *cosValue;
	NSString *adminValue;
	NSString *statusValue;
}

@property (nonatomic, retain) NSString *displayname;
@property (nonatomic, retain) NSString *emailValue;
@property (nonatomic, retain) NSString *cosValue;
@property (nonatomic, retain) NSString *adminValue;
@property (nonatomic, retain) NSString *statusValue;

- (void) initData: (NSMutableArray*) acctinfo;

-(AccountViewControllerExt *) initWithAcctInfo: (NSString *)title info: (NSMutableArray*) acctinfo;

@end

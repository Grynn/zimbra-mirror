//
//  AccountStatusController.h
//  Zimbra
//
//  Created by Qin An on 12/15/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import <UIKit/UIKit.h>

@class GetController;
@class PutController;

@interface AccountStatusController : UITableViewController {
	NSMutableArray *tableTitles;
	NSMutableArray *acctStatus;
	NSMutableArray *newStatus;
	NSMutableArray *acctName;
	int ithTitle;
	
	GetController *getController;
	PutController *putController;

}

@property (nonatomic, retain) NSMutableArray *tableTitles;
@property (nonatomic, retain) NSMutableArray *acctStatus;
@property (nonatomic, retain) NSMutableArray *acctName;
@property (nonatomic, retain) NSMutableArray *newStatus;

@property (nonatomic, retain) GetController *getController;
@property (nonatomic, retain) PutController *putController;

- (void) buildAccountDataTable;
- (void) CommitOperation;

@end

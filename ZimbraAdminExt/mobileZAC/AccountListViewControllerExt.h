//
//  AccountListViewControllerExt.h
//  Zimbra
//
//  Created by Qin An on 12/14/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import <UIKit/UIKit.h>

@class GetController;
@class PutController;

@interface AccountListViewControllerExt : UITableViewController <UISearchBarDelegate> {
	NSMutableArray *tableTitles;
	NSMutableArray *filterTitles;
	NSMutableArray *deleteTitles;
	UISearchBar *search;
	int ithTitle;
	GetController *getController;
	PutController *putController;
	NSTimer *freshTimer;
	
	// 2d table
	NSMutableArray *acctTable;
	NSMutableArray *fieldList;
	NSMutableArray *nameList;

}

@property (nonatomic, retain) NSMutableArray *tableTitles;
@property (nonatomic, retain) NSMutableArray *filterTitles;
@property (nonatomic, retain) NSMutableArray *deleteTitles;
@property (nonatomic, retain) GetController *getController;
@property (nonatomic, retain) PutController *putController;

@property (nonatomic, retain) NSMutableArray *acctTable;
@property (nonatomic, retain) NSMutableArray *nameList;
@property (nonatomic, retain) NSMutableArray *fieldList;

- (void) buildAccountDataTable;
- (void) buildSearchArrayFrom: (NSString *) matchString;
- (void) CommitOperation;

@end

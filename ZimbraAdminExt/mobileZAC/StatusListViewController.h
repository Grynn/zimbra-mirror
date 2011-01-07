//
//  StatusListViewController.h
//  Zimbra
//
//  Created by Qin An on 12/11/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import <UIKit/UIKit.h>

@class GetController;
@class PutController;

@interface StatusListViewController : UITableViewController <UIAlertViewDelegate> {
	NSMutableArray *statNames;
	NSMutableArray *statValues;
	NSString *parentItem;
	int ithTitle;
	NSString *selectedService;
	
	GetController *getController;
	PutController *putController;
	NSTimer *freshTimer;
	
	BOOL hasDown;
}

@property (nonatomic, retain) NSMutableArray *statNames;
@property (nonatomic, retain) NSMutableArray *statValues;
@property (nonatomic, retain) NSString *parentItem;
@property (nonatomic, retain) NSString *selectedService;

@property (nonatomic, retain) GetController *getController;
@property (nonatomic, retain) PutController *putController;

-(StatusListViewController *) initWithTitleItems: (NSString *)title 
										itemlist:(NSMutableArray*) items
									   valuelist:(NSMutableArray*) values
										  parent:(NSString*) pItem
									  getHandler: (GetController*)ghandler;

- (void) buildServiceDataTable;

- (void) CommitOperation;

@end

//
//  ServerListViewController.h
//  Zimbra
//
//  Created by Qin An on 12/12/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import <UIKit/UIKit.h>

@class GetController;

@interface ServerListViewController : UITableViewController {
	NSMutableArray *tableTitles;
	int ithTitle;
	GetController *getController;
	
	NSTimer *freshTimer;
}

@property (nonatomic, retain) NSMutableArray *tableTitles;
@property (nonatomic, retain) GetController *getController;

@end

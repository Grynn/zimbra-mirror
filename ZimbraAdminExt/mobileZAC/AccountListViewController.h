//
//  AccountListViewController.h
//  Zimbra
//
//  Created by Qin An on 12/14/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface AccountListViewController : UITableViewController <UISearchBarDelegate> {
	NSMutableArray *searchArray;
	UISearchBar *search;
	NSMutableArray *colorArray;
	NSMutableArray *tableTitles;
}

@property (nonatomic, retain) NSMutableArray *searchArray;
@property (nonatomic, retain) NSMutableArray *colorArray;
@property (nonatomic, retain) NSMutableArray *tableTitles;


@end

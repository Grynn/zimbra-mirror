//
//  AccountStatusController.m
//  Zimbra
//
//  Created by Qin An on 12/15/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import "ZimbraAppDelegate.h"
#import "AccountStatusController.h"
#import "BoxMaker.h"
#import "UICustomSwitch.h"
#import "GetController.h"
#import "PutController.h"
#import "SecurityController.h"

#define NCELLS 4

@implementation AccountStatusController

@synthesize tableTitles;
@synthesize acctStatus;
@synthesize acctName;
@synthesize newStatus;
@synthesize getController;
@synthesize putController;

-(AccountStatusController *) initWithTitle: (NSString *)title
{
	self = [super init];
	self.title = title;
	//[self.tabBarItem initWithTitle:self.title image:createImage(((float) 75.5f / 10.0f)) tag:0];
	UIImage *tabIcon = [UIImage imageNamed:@"lock.png"];
	CGSize sz = CGSizeMake(32,32);
	UIImage *resized_icon = scaleImage(tabIcon, sz);
	self.tabBarItem.image = resized_icon;
	return self;
}
/*
-(AccountStatusController *) initWithTitle: (NSString *)title parentIdx: (NSInteger) pIdx
{
	self = [super init];
	self.title = title;
	[self.tabBarItem initWithTitle:self.title image:createImage(((float) 75.5f / 10.0f)) tag:0];
	return self;
}
*/
#pragma mark Data Source methods
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView 
{
	return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section 
{
	return [tableTitles count];
}

- (void) switchAction: (UIControl *) sender
{
	UISwitch *cellswitch = (UISwitch*) sender;
	UITableViewCell *cell = (UITableViewCell *)[sender superview];
	NSString *acctname = cell.detailTextLabel.text;
	NSInteger idx = [tableTitles indexOfObject:acctname];
	//NSString *statusvalue = nil;
	
	//if (cellswitch.on) statusvalue = @"locked";
	//else statusvalue = @"active";
	

	//NSLog(@"Item[%d] '%@' is changed to '%@'",idx,acctname,statusvalue);
	[newStatus replaceObjectAtIndex:idx withObject:(cellswitch.on)?@" locked":@" active"];
	
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
	// Recover section and row info
	NSInteger row = [indexPath row];
	//NSInteger section = [indexPath section];
	UIImage *img_blue = [UIImage imageNamed:@"account_blue.png"];
	UIImage *img_red = [UIImage imageNamed:@"account_red.png"];
	CGSize sz = CGSizeMake(32,32);
	UIImage *resized_blue = scaleImage(img_blue, sz);
	UIImage *resized_red = scaleImage(img_red, sz);
	
	UICustomSwitch *switchView = NULL;
	UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"any-cell"];
	if (!cell) {
		//cell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:@"any-cell"] autorelease];
		//cell.selectionStyle = UITableViewCellSelectionStyleGray;
		cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle
                                       reuseIdentifier: @"any-cell"] autorelease];
		cell.indentationLevel = 0;
		
		switchView = [[UICustomSwitch alloc] initWithFrame: CGRectMake(220.0f, 13.0f, 100.0f, 28.0f)];
		[switchView setLeftLabelText: @"LOCK"];
		[switchView setRightLabelText: @"ACT"];
		[switchView setLeftTextFont:[UIFont fontWithName:@"Georgia" size:14.0f]];
		[switchView setRightTextFont:[UIFont fontWithName:@"Georgia" size:14.0f]];
		[switchView setTag:997];
		[cell addSubview:switchView];
		[switchView release];
	}
	switchView = [cell viewWithTag:997];
	
	NSString *displayname = [acctName objectAtIndex:row];
	NSString *acctname = [tableTitles objectAtIndex:row];
	NSString *status = [acctStatus objectAtIndex:row];
	if (displayname != nil && [displayname length] > 0)
		cell.text = displayname;
	else cell.text = [[acctname componentsSeparatedByString:@"@"] objectAtIndex:0];
	cell.detailTextLabel.text = acctname;
	
	if ([status isEqualToString:@" active"]) {
		cell.imageView.image = resized_blue;
		[switchView setOn:NO animated:NO];
	}else {
		cell.imageView.image = resized_red;
		[switchView setOn:YES animated:NO];
	}
	
	
	// add handler to switch
	[switchView addTarget:self action:@selector(switchAction:) forControlEvents:UIControlEventValueChanged];
	
	return cell;
}

#pragma mark Delegate Methods

/*
// Add a new item
- (void) add
{
	[tableTitles addObject:[NSString stringWithFormat:@"Account #%d", ++ithTitle]];	
	[self.tableView reloadData];
}
*/

- (void) deselect
{	
	[self.tableView deselectRowAtIndexPath:[self.tableView indexPathForSelectedRow] animated:YES];
}


// Respond to a user tap
- (void)tableView:(UITableView *)tableView selectionDidChangeToIndexPath:(NSIndexPath *)newIndexPath fromIndexPath:(NSIndexPath *)oldIndexPath 
{
	printf("User selected row %d\n", [newIndexPath row] + 1);
	[self performSelector:@selector(deselect) withObject:nil afterDelay:0.5f];
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath 
{
	// Disable delete
	
	//return self.editing ;
	return FALSE;
}


// Respond to deletion
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle 
forRowAtIndexPath:(NSIndexPath *)indexPath 
{
	printf("About to delete item %d\n", [indexPath row]);
	[tableTitles removeObjectAtIndex:[indexPath row]];
	[self.tableView reloadData];
}	 

- (void) loadDataToUI
{
	NSMutableArray *acctTable = [[ZimbraAppDelegate sharedAppDelegate] getAllAccount];
	if (acctTable) {
		tableTitles = [[NSMutableArray alloc] init];
		acctStatus = [[NSMutableArray alloc] init];
		acctName = [[NSMutableArray alloc] init];
		newStatus = [[NSMutableArray alloc] init];
		for (int i = 0; i < [acctTable count]; i++) {
			NSMutableArray *acct =[acctTable objectAtIndex:i];
			if (acct == nil) continue;
			[tableTitles addObject:[acct objectAtIndex:0]];
			[acctStatus addObject:[acct objectAtIndex:3]];
			[acctName addObject:[acct objectAtIndex:2]];
			[newStatus addObject:[acct objectAtIndex:3]];
		}
		[self.tableView reloadData];
	}

}

- (void)loadView
{
	[super loadView];
	
	self.navigationItem.leftBarButtonItem = [[[UIBarButtonItem alloc] 
											  initWithBarButtonSystemItem:UIBarButtonSystemItemCancel 
											  target:self action:@selector(CancelAction)] autorelease];
	
	self.navigationItem.rightBarButtonItem = [[[UIBarButtonItem alloc]
											   initWithTitle:@"Commit" 
											   style:UIBarButtonItemStylePlain 
											   target:self 
											   action:@selector(CommitAccount)] autorelease];
	
	self.navigationController.navigationBar.tintColor = [UIColor blackColor];

	
	// Initialize the titles
	tableTitles = [[NSMutableArray alloc] init];
	
	ithTitle = NCELLS;
	for (int i = 1; i <= NCELLS; i++) 
		[tableTitles addObject:[[NSString stringWithFormat:@"Account #%d", i] retain]];
	
	// check whether account info is loaded
	if ([[ZimbraAppDelegate sharedAppDelegate] getAllAccount] == nil) {
		//initate GetController
		NSString * kGetURLText = @"http://10.117.4.35:8080/mytest/zimbra-cluster-acctinfo.txt";
		GetController *kgetController = [[GetController alloc] initWithGetURL:kGetURLText];
		self.getController = kgetController;
		self.getController.viewController = self;
		[self.getController startGetController];
	}
	
	// initiate PutController
	NSString * kPutURLText = @"http://10.117.4.35:8080/mytest/";
	NSString * kPutCmdFile = @"zimbra-cluster-cmds-req-acctstatus.txt";
	PutController *pController = [[PutController alloc] initWithPutURL:kPutURLText cmdFile:kPutCmdFile];
	self.putController = pController;
		
}

-(void) viewWillAppear:(BOOL)animated {
	
	[self loadDataToUI];
	[super viewWillAppear:animated];
	
}

- (void) CancelAction
{
	printf("User presses the cancel button\n");
	self.tabBarController.selectedIndex = 1;
}

- (void) CommitAccount
{
	printf("User presses the commit button\n");
	//NSLog([newStatus description]);
	// check execute auth
	[[[ZimbraAppDelegate sharedAppDelegate] getSecurityController] setMyViewController:self];
	[[[ZimbraAppDelegate sharedAppDelegate] getSecurityController] executeConfirm];
}

- (void) CommitOperation
{
	BOOL isUpdate = FALSE;
	int oldnum = [acctStatus count];
	int newnum = [newStatus count];
	
	if (oldnum != newnum)
		return;
	
	// check whether it is updated
	for (int i = 0; i < oldnum ; i++) {
		if (![[acctStatus objectAtIndex:i] isEqualToString:[newStatus objectAtIndex:i]]) {
			isUpdate = TRUE;
			break;
		}
	}
	
	if (isUpdate) {
		NSString *cmd = @"";
		for (int i = 0; i < oldnum; i++) {
			NSString *oldstatus = [acctStatus objectAtIndex:i];
			NSString *newstatus = [newStatus objectAtIndex:i];
			NSString *acct = [tableTitles objectAtIndex:i];
			if (![oldstatus isEqualToString:newstatus]) {
				cmd = [NSString stringWithFormat:@"%@zmprov ma %@ zimbraAccountStatus %@\n",cmd, acct, newstatus];
			}
		}
		NSLog(@"cmd = %@",cmd);
		//send command
		[self.putController startPutController:cmd];
	}
	self.tabBarController.selectedIndex = 1;
}

- (void) buildAccountDataTable
{
	NSMutableArray *allNames = self.getController.statNames;
	NSMutableArray *allValues= self.getController.statValues;
	NSMutableArray *allIndexs = self.getController.statIndex;
	NSMutableArray *fieldList = [NSMutableArray arrayWithObjects:
								 @"owner", @"mail", @"displayname", 
								 @"zimbraaccountstatus", @"zimbraisadminaccount",
								 nil];
	int nc = [allNames count];
	int ic = [allIndexs count];
	
	if (nc == 0 || ic == 0)
		return;

	NSMutableArray *acctTable = [[NSMutableArray alloc] init];
	NSMutableArray *nameList = [[NSMutableArray alloc] init];
	NSInteger tmp;
	for (int i = 0; i < nc && i < ic; i++) {
		tmp = [[allIndexs objectAtIndex:i] intValue];
		
		if (tmp == 0) {
			NSString* obj = [[allNames objectAtIndex:i] copy];
			[nameList addObject:obj];
			NSMutableArray *items = [NSMutableArray arrayWithObjects:@"", @"", @"", @"", @"",nil];
			[items replaceObjectAtIndex:0 withObject:obj];
			int j = i+1;
			for (; j < nc && j < ic && [[allIndexs objectAtIndex:j] intValue] == 2; j++) {
				NSString *itemname = [allNames objectAtIndex:j];
				NSInteger idx = [fieldList indexOfObject:[itemname lowercaseString]];
				if (idx < 0 || idx > ic || idx > nc) continue;
				obj = [[allValues objectAtIndex:j] copy];
				[items replaceObjectAtIndex:idx withObject:obj];
			}
			[acctTable addObject:items];
			//[items release];
			i = j-1;
		}
	}
	[[ZimbraAppDelegate sharedAppDelegate] setAllAccount:acctTable];
	[self loadDataToUI];
}


-(void) dealloc
{
	[tableTitles release];
	[super dealloc];
}


@end

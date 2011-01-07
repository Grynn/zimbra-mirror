//
//  ServerListViewController.m
//  Zimbra
//
//  Created by Qin An on 12/12/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import "ZimbraAppDelegate.h"
#import "ServerListViewController.h"
//#import "StatusViewController.h"
#import "StatusListViewController.h"
#import "StatisticsController.h"
#import "BoxMaker.h"
#import "GetController.h"

#define NCELLS 4

@implementation ServerListViewController

@synthesize tableTitles;
@synthesize getController;

//- (StatusListViewController *) init
//{
//	if (self = [super init]) self.title = @"Table Edits";
//self.title = @"My table";
//[self.tabBarItem initWithTitle:self.title image:createImage(((float) 75.5f / 10.0f)) tag:0];
//	return self;
//}

-(ServerListViewController *) initWithTitle: (NSString *)title
{
	self = [super init];
	self.title = title;
	//[self.tabBarItem initWithTitle:self.title image:createImage(((float) 75.5f / 10.0f)) tag:0];
	UIImage *tabIcon = [UIImage imageNamed:@"servers.png"];
	CGSize sz = CGSizeMake(32,32);
	UIImage *resized_icon = scaleImage(tabIcon, sz);
	self.tabBarItem.image = resized_icon;
	return self;
}

#pragma mark Data Source methods
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView 
{
	return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section 
{
	return [tableTitles count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
	NSInteger tVal = 0;
	NSInteger tItem =0;
	NSUInteger row = [indexPath row];
    NSString *rowValue = [tableTitles objectAtIndex:row];
	NSMutableArray *allNames = self.getController.statNames;
	NSMutableArray *allValues= self.getController.statValues;
	NSMutableArray *allIndexs = self.getController.statIndex;
	int nc = [allNames count];
	int ic = [allIndexs count];
	NSInteger tmp;
	for (int i = 0; i < nc && i < ic; i++) {
		tmp = [[allIndexs objectAtIndex:i] intValue];
		if (tmp == 0 && [[allNames objectAtIndex:i] isEqualToString:rowValue]) {
			tVal = 0; tItem = 0;
			for (int j = i+1; j < nc && j < ic && [[allIndexs objectAtIndex:j] intValue] == 2; j++) {
				tVal += [[allValues objectAtIndex:j] intValue];
				tItem ++;
			}
			break;
		}
	}
	
	UIImage *image_green = [UIImage imageNamed:@"green.png"];
	UIImage *image_red = [UIImage imageNamed:@"red.png"];
	UIImage *image_yellow = [UIImage imageNamed:@"yellow.png"];
	CGSize sz = CGSizeMake(32,32);
	UIImage *resized_green = scaleImage(image_green, sz);
	UIImage *resized_red = scaleImage(image_red, sz);
	UIImage *resized_yellow = scaleImage(image_yellow, sz);
	
	UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"any-cell"];
	if (!cell) cell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:@"any-cell"] autorelease];
	
	cell.text = [tableTitles objectAtIndex:[indexPath row]];
	cell.accessoryType = UITableViewCellAccessoryDetailDisclosureButton;
	cell.hidesAccessoryWhenEditing = YES;
	
	if (tItem > 0) {
		if (tVal > 0 && tVal == tItem) 
			cell.imageView.image = resized_green;
		else if(tVal > 0 && tVal < tItem)
			cell.imageView.image = resized_yellow;
		else if (tVal == 0)
			cell.imageView.image = resized_red;
	}
	
	return cell;
}

#pragma mark Delegate Methods

// Add a new item
- (void) add
{
	[tableTitles addObject:[NSString stringWithFormat:@"Server #%d", ++ithTitle]];	
	[self.tableView reloadData];
}

/*
 * Accessory Button Handler: This simply opens an image controller with the Hello World image.
 */

- (void)tableView:(UITableView *)tableView accessoryButtonTappedForRowWithIndexPath:(NSIndexPath *)indexPath
{
	NSUInteger row = [indexPath row];
    NSString *rowValue = [tableTitles objectAtIndex:row];
	NSMutableArray *allNames = self.getController.statNames;
	NSMutableArray *allValues= self.getController.statValues;
	NSMutableArray *allIndexs = self.getController.statIndex;
	NSMutableArray *serviceItems = [[NSMutableArray alloc] init];
	NSMutableArray *serviceValues = [[NSMutableArray alloc] init];
	int nc = [allNames count];
	int ic = [allIndexs count];
	NSInteger tmp;
	for (int i = 0; i < nc && i < ic; i++) {
		tmp = [[allIndexs objectAtIndex:i] intValue];
		if (tmp == 0 && [[allNames objectAtIndex:i] isEqualToString:rowValue]) {
			for (int j = i+1; j < nc && j < ic && [[allIndexs objectAtIndex:j] intValue] == 2; j++) {
				[serviceItems addObject:[[allNames objectAtIndex:j] copy]];
				[serviceValues addObject:[[allValues objectAtIndex:j] copy]];
			}
			break;
		}
	}
	[[self navigationController] pushViewController:[[StatusListViewController alloc] 
													 initWithTitleItems:@"Services" 
													 itemlist:serviceItems 
													 valuelist:serviceValues 
													 parent:rowValue
													 getHandler:self.getController
													 ]  animated:YES];
	//[[self navigationController] pushViewController:[[StatusListViewController alloc] initWithTitle:@"Services"]  animated:YES];
	
	[serviceItems release];
}

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

- (void) logout
{
	printf("User presses the logout button\n");
	[[[ZimbraAppDelegate sharedAppDelegate] getSecurityController] loginMain];
}

// Respond to deletion
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle 
forRowAtIndexPath:(NSIndexPath *)indexPath 
{
	printf("About to delete item %d\n", [indexPath row]);
	[tableTitles removeObjectAtIndex:[indexPath row]];
	[self.tableView reloadData];
}	 

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
	printf("User selected row %d\n", [indexPath row] + 1);
	NSUInteger row = [indexPath row];
    NSString *rowValue = [tableTitles objectAtIndex:row];
/*
	NSMutableArray *allNames = self.getController.statNames;
	NSMutableArray *allValues= self.getController.statValues;
	NSMutableArray *allIndexs = self.getController.statIndex;
	NSMutableArray *serviceItems = [[NSMutableArray alloc] init];
	NSMutableArray *serviceValues = [[NSMutableArray alloc] init];
	int nc = [allNames count];
	int ic = [allIndexs count];
	NSInteger tmp;
	for (int i = 0; i < nc && i < ic; i++) {
		tmp = [[allIndexs objectAtIndex:i] intValue];
		if (tmp == 0 && [[allNames objectAtIndex:i] isEqualToString:rowValue]) {
			for (int j = i+1; j < nc && j < ic && [[allIndexs objectAtIndex:j] intValue] == 2; j++) {
				[serviceItems addObject:[[allNames objectAtIndex:j] copy]];
				[serviceValues addObject:[[allValues objectAtIndex:j] copy]];
			}
			break;
		}
	}
	[[self navigationController] pushViewController:[[StatusListViewController alloc] 
													 initWithTitleItems:@"Statistics" 
													 itemlist:serviceItems 
													 valuelist:serviceValues 
													 parent:rowValue
													 getHandler:self.getController
													 ]  animated:YES];	
*/
	[[self navigationController] pushViewController:[[StatisticsController alloc] initWithTitle:@"Usage" serverId:rowValue]  animated:YES];
}

- (void)loadView
{
	[super loadView];
	
	//initate GetController
	NSString * kGetURLText = @"http://10.117.4.35:8080/mytest/zimbra-cluster-stats.txt";//@"http://localhost:8888/zimbra-cluster-stats.txt";//
	GetController *kgetController = [[GetController alloc] initWithGetURL:kGetURLText];
	self.getController = kgetController;
	self.getController.viewController = self;
	
	//self.navigationItem.rightBarButtonItem = self.editButtonItem;

	self.navigationItem.leftBarButtonItem = [[[UIBarButtonItem alloc]
											  initWithTitle:@"Logout" 
											  style:UIBarButtonItemStylePlain 
											  target:self 
											  action:@selector(logout)] autorelease];


	//get status info from remote server
	[self.getController startGetController];
	if (self.getController.statNames) {
		NSLog([self.getController.statNames description]);
	}else 
		printf("self.getController.statName == nil\n");
	
	// Initialize the titles
	tableTitles = [[NSMutableArray alloc] init];

	ithTitle = NCELLS;
	for (int i = 1; i <= NCELLS; i++) 
		[tableTitles addObject:[[NSString stringWithFormat:@"Server #%d", i] retain]];
	
	//[self addStatInServerListView:self.getController];
	[kgetController release];
	[kGetURLText release];
}

- (void) freshView
{
	if (getController == nil)
		return;
	[getController startGetController];
}

- (void) startTimer
{
	// Timer for auto-fresh table view
	freshTimer = [NSTimer scheduledTimerWithTimeInterval:30.0f
												  target:self
												selector:@selector(freshView)
												userInfo:nil
												 repeats:YES];	
}

-(void) viewWillAppear:(BOOL)animated {
	NSLog(@"viewWillAppear called");
	[super viewWillAppear:animated];
	if (freshTimer == nil) 
		[self startTimer];
	
	// remove the tabbar badge
	self.tabBarItem.badgeValue = nil;
}

-(void) viewWillDisappear:(BOOL)animated {
	NSLog(@"viewWillDisappear called");
	[super viewWillDisappear:animated];
	if (freshTimer) {
		[freshTimer invalidate];
		freshTimer = nil;
	}
}

-(void) dealloc
{
	[tableTitles release];
	[super dealloc];
}


@end

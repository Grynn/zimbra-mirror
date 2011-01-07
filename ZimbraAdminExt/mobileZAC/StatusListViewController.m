//
//  StatusListViewController.m
//  Zimbra
//
//  Created by Qin An on 12/11/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import "ZimbraAppDelegate.h"
#import "StatusListViewController.h"
#import "StatusViewController.h"
#import "GetController.h"
#import "PutController.h"
#import "BoxMaker.h"
#import "SecurityController.h"
#import "StatisticsController.h"

#define NCELLS 4

@implementation StatusListViewController

@synthesize statNames;
@synthesize statValues;
@synthesize parentItem;
@synthesize getController;
@synthesize putController;
@synthesize selectedService;



-(StatusListViewController *) initWithTitle: (NSString *)title
{
	self = [super init];
	self.title = title;
	[self.tabBarItem initWithTitle:self.title image:createImage(((float) 75.5f / 10.0f)) tag:0];
	return self;
}

-(StatusListViewController *) initWithTitleItems: (NSString *)title 
							itemlist:(NSMutableArray*) items
							valuelist:(NSMutableArray*) values
							parent:(NSString*) pItem
							getHandler: (GetController*)ghandler
{
	self = [super init];
	self.title = title;
	//self.statNames = [[NSMutableArray alloc] init];
	//[self.statNames addObjectsFromArray:items];
	self.statNames = items;
	self.statValues = values;
	self.parentItem = pItem;
	self.getController = ghandler;
	
	return self;
}

#pragma mark Data Source methods
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView 
{
	return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section 
{
	return [statNames count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
	UIImage *image_green = [UIImage imageNamed:@"green.png"];
	UIImage *image_red = [UIImage imageNamed:@"red.png"];
	CGSize sz = CGSizeMake(32,32);
	UIImage *resized_green = scaleImage(image_green, sz);
	UIImage *resized_red = scaleImage(image_red, sz);

	NSUInteger row = [indexPath row];
	
	UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"any-cell"];
	if (!cell) //cell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:@"any-cell"] autorelease];
		cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue1
                                       reuseIdentifier: @"any-cell"] autorelease];
	
	cell.text = [statNames objectAtIndex:[indexPath row]];
	if ([[self.statValues objectAtIndex:row] intValue] == 1) {
		cell.imageView.image = resized_green;
		cell.detailTextLabel.textColor = [UIColor grayColor];
		cell.detailTextLabel.text = @"Working";
	}
	else {
		cell.imageView.image = resized_red;
		cell.detailTextLabel.textColor = [UIColor redColor];
		cell.detailTextLabel.text = @"Shutdown";
	}
	//cell.accessoryType = UITableViewCellAccessoryDetailDisclosureButton;
	//cell.hidesAccessoryWhenEditing = YES;
	
	return cell;
}

- (void) CommitOperation
{
	NSString *cmd = [NSString stringWithFormat:@"%@\t%@",parentItem, self.selectedService];
	NSLog(@"Start Put controller and do sending: %@",cmd);
	[self.putController startPutController:cmd];
}

// operation on cell
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
	printf("User Pressed Button %d\n", buttonIndex + 1);
	if (buttonIndex == 1 && self.selectedService) {
		// check execute auth
		[[[ZimbraAppDelegate sharedAppDelegate] getSecurityController] setMyViewController:self];
		[[[ZimbraAppDelegate sharedAppDelegate] getSecurityController] executeConfirm];
	}
	[alertView release];
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    NSUInteger row = [indexPath row];
    NSString *rowValue = [statNames objectAtIndex:row];
	self.selectedService = rowValue;
	/*
    NSString *message = [[NSString alloc] initWithFormat:
                         @"Service '%@' is selected", rowValue];
    UIAlertView *alert = [[UIAlertView alloc] 
                          initWithTitle:@"Want to Restart?"
                          message:message 
                          delegate:self 
                          cancelButtonTitle:nil 
                          otherButtonTitles:@"No", @"Restart",nil];
    [alert show];
    
    [message release];
    //[alert release];
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
	*/
}



#pragma mark Delegate Methods

// Add a new item
- (void) add
{
	[statNames addObject:[NSString stringWithFormat:@"Service #%d", ++ithTitle]];	
	[self.tableView reloadData];
}

// Reload and fresh table view
- (void) freshView
{

	printf("User presses freshView button\n");
	if (self.getController == nil) 
		return;
	
	[self.getController startGetController];

}

/*
 * Accessory Button Handler: This simply opens an image controller with the Hello World image.
 */

- (void)tableView:(UITableView *)tableView accessoryButtonTappedForRowWithIndexPath:(NSIndexPath *)indexPath
{
	//[[self navigationController] pushViewController:[[StatusViewController alloc] init]  animated:YES];
	[[self navigationController] pushViewController:[[StatisticsController alloc] initWithTitle:@"Usage" serverId:parentItem]  animated:YES];
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

// Respond to deletion
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle 
forRowAtIndexPath:(NSIndexPath *)indexPath 
{
	printf("About to delete item %d\n", [indexPath row]);
	[statNames removeObjectAtIndex:[indexPath row]];
	[self.tableView reloadData];
}	 

- (void) startService
{
	NSLog(@"start service: %@", self.selectedService);
	[[[ZimbraAppDelegate sharedAppDelegate] getSecurityController] setMyViewController:self];
	[[[ZimbraAppDelegate sharedAppDelegate] getSecurityController] executeConfirm];
}

- (void) buildServiceDataTable
{
	
	
	NSMutableArray *allNames = self.getController.statNames;
	NSMutableArray *allValues= self.getController.statValues;
	NSMutableArray *allIndexs = self.getController.statIndex;
	NSMutableArray *serviceItems = [[NSMutableArray alloc] init];
	NSMutableArray *serviceValues = [[NSMutableArray alloc] init];
	int nc = [allNames count];
	int ic = [allIndexs count];
	
	if (nc == 0 || ic == 0)
		return;
	
	NSInteger tmp;
	for (int i = 0; i < nc && i < ic; i++) {
		tmp = [[allIndexs objectAtIndex:i] intValue];
		if (tmp == 0 && [[allNames objectAtIndex:i] isEqualToString:self.parentItem]) {
			for (int j = i+1; j < nc && j < ic && [[allIndexs objectAtIndex:j] intValue] == 2; j++) {
				[serviceItems addObject:[[allNames objectAtIndex:j] copy]];
				[serviceValues addObject:[[allValues objectAtIndex:j] copy]];
				if ([[allValues objectAtIndex:j] intValue] == 0)
					hasDown = YES;
			}
			break;
		}
	}
	
	// update status data
	if (statNames == nil)
		statNames = [[NSMutableArray alloc] init];
	else [self.statNames removeAllObjects];
	if (statValues == nil)
		statValues = [[NSMutableArray alloc] init];
	else [self.statValues removeAllObjects];
	[self.statNames addObjectsFromArray:serviceItems];	
	[self.statValues addObjectsFromArray:serviceValues];
	
	[serviceItems release];
	[serviceValues release];
	[self.tableView reloadData];
	
	if (hasDown) {
		self.navigationItem.rightBarButtonItem = [[[UIBarButtonItem alloc]
												   initWithTitle:@"Start" 
												   style:UIBarButtonItemStylePlain 
												   target:self 
												   action:@selector(startService)] autorelease];
	}
}

- (void)loadView
{
	[super loadView];
	
	//self.navigationItem.rightBarButtonItem = self.editButtonItem;
	
	self.navigationItem.rightBarButtonItem = [[[UIBarButtonItem alloc]
											  initWithTitle:@"Refresh" 
											  style:UIBarButtonItemStylePlain 
											  target:self 
											  action:@selector(freshView)] autorelease];
	
	// Initialize the titles
	//statNames = [[NSMutableArray alloc] init];
	//ithTitle = NCELLS;
	//for (int i = 1; i <= NCELLS; i++) 
	//	[statNames addObject:[[NSString stringWithFormat:@"Service #%d", i] retain]];
	
	// initiate PutController
	NSString * kPutURLText = @"http://10.117.4.35:8080/mytest/";
	NSString * kPutCmdFile = @"zimbra-cluster-cmds-req-restart.txt";
	PutController *pController = [[PutController alloc] initWithPutURL:kPutURLText cmdFile:kPutCmdFile];
	self.putController = pController;	
	[pController release];
	
	//if (self.getController == nil) {
		//initate GetController
		NSString * kGetURLText = @"http://10.117.4.35:8080/mytest/zimbra-cluster-stats.txt";//@"http://localhost:8888/zimbra-cluster-stats.txt";//
		GetController *kgetController = [[GetController alloc] initWithGetURL:kGetURLText];
		self.getController = kgetController;
		self.getController.viewController = self;
		[kgetController release];
		
		//start GetController
		[self freshView];
	//}
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

	[super viewWillAppear:animated];
	if (freshTimer == nil) 
		[self startTimer];
}

-(void) viewWillDisappear:(BOOL)animated {

	[super viewWillDisappear:animated];
	if (freshTimer) {
		[freshTimer invalidate];
		freshTimer = nil;
	}
}

-(void) dealloc
{
	[statNames release];
	[super dealloc];
}

@end

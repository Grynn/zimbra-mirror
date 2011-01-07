//
//  AccountListViewControllerExt.m
//  Zimbra
//
//  Created by Qin An on 12/14/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import "ZimbraAppDelegate.h"
#import "AccountListViewControllerExt.h"
#import "AccountViewControllerExt.h"
#import "GetController.h"
#import "PutController.h"
#import "BoxMaker.h"
#import "SecurityController.h"
#import "NewAccountController.h"

#import "QuartzCore/QuartzCore.h"

#define NCELLS 4

@implementation AccountListViewControllerExt

@synthesize tableTitles;
@synthesize filterTitles;
@synthesize deleteTitles;
@synthesize getController;
@synthesize putController;
@synthesize acctTable;
@synthesize nameList;
@synthesize fieldList;

-(AccountListViewControllerExt *) initWithTitle: (NSString *)title
{
	self = [super init];
	self.title = title;
	//[self.tabBarItem initWithTitle:self.title image:createImage(((float) 75.5f / 10.0f)) tag:0];
	UIImage *tabIcon = [UIImage imageNamed:@"account.png"];
	CGSize sz = CGSizeMake(32,32);
	UIImage *resized_icon = scaleImage(tabIcon, sz);
	self.tabBarItem.image = resized_icon;
	//[self.tabBarItem initWithTitle:self.title image:resized_icon tag:0];
	self.fieldList = [NSMutableArray arrayWithObjects:@"owner", @"mail", @"displayname", @"zimbraaccountstatus", @"zimbraisadminaccount",nil];

	return self;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView 
{
	return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section 
{
	return [filterTitles count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
	UIImage *img_account_blue = [UIImage imageNamed:@"account_blue.png"];
	UIImage *img_account_red = [UIImage imageNamed:@"account_red.png"];
	CGSize sz = CGSizeMake(32,32);
	UIImage *resized_account_blue = scaleImage(img_account_blue, sz);
	UIImage *resized_account_red = scaleImage(img_account_red, sz);
	
	UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"any-cell"];
	if (!cell) //cell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:@"any-cell"] autorelease];
		cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleSubtitle
                                       reuseIdentifier: @"any-cell"] autorelease];
	
	NSString *curAcct = [filterTitles objectAtIndex:[indexPath row]];
	NSInteger idx = [nameList indexOfObject:curAcct];
	NSMutableArray *acctInfo = nil;
	if (idx >= 0 && idx < [nameList count])
		acctInfo = [acctTable objectAtIndex:idx];
	
	if (acctInfo == nil || [acctInfo count] < 1) {
		cell.text = curAcct;
		cell.imageView.image = resized_account_blue;
	} else {
		NSString *displayname = [[acctInfo objectAtIndex:2] copy];
		if (displayname && [displayname length] > 0) {
			cell.text = displayname;
			cell.detailTextLabel.text = curAcct;
		} else {
			cell.text = [[curAcct componentsSeparatedByString:@"@"] objectAtIndex:0];
			cell.detailTextLabel.text = curAcct;
		}
		NSString *status = [acctInfo objectAtIndex:3];
		if (status != nil && [status isEqualToString:@" active"])
			cell.imageView.image = resized_account_blue;
		else cell.imageView.image = resized_account_red;
	}
	cell.accessoryType = UITableViewCellAccessoryDetailDisclosureButton;
	cell.hidesAccessoryWhenEditing = YES;
	
	return cell;
}

- (void)tableView:(UITableView *)tableView accessoryButtonTappedForRowWithIndexPath:(NSIndexPath *)indexPath
{
	NSString *curAcct = [filterTitles objectAtIndex:[indexPath row]];
	NSInteger idx = [nameList indexOfObject:curAcct];
	NSMutableArray *acctInfo = nil;
	if (idx >= 0 && idx < [nameList count])
		acctInfo = [acctTable objectAtIndex:idx];
	
	[[self navigationController] pushViewController:[[AccountViewControllerExt alloc] initWithAcctInfo:@"Details" info:acctInfo]  animated:YES];
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
	[deleteTitles addObject:[tableTitles objectAtIndex:[indexPath row]]];
	[tableTitles removeObjectAtIndex:[indexPath row]];
	[self.tableView reloadData];
}

// Perform the re-order
-(void) tableView: (UITableView *) tableView moveRowAtIndexPath: (NSIndexPath *) oldPath toIndexPath:(NSIndexPath *) newPath
{
	NSString *title = [tableTitles objectAtIndex:[oldPath row]];
	[tableTitles removeObjectAtIndex:[oldPath row]];
	[tableTitles insertObject:title atIndex:[newPath row]];
}

// create an array by applying the search string
- (void) buildSearchArrayFrom: (NSString *) matchString
{
	NSString *upString = [matchString uppercaseString];
	if (filterTitles)
		[filterTitles release];
	filterTitles = [[NSMutableArray alloc] init];
	
	for (NSString *word in tableTitles)
	{
		if ([matchString length] == 0)
		{
			[filterTitles addObject:word];
			continue;
		}
		
		NSRange range = [[word uppercaseString] rangeOfString:upString];
		if (range.location != NSNotFound) [filterTitles addObject:word];
	}
	
	[self.tableView reloadData];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{

	NSString *curAcct = [filterTitles objectAtIndex:[indexPath row]];
	NSLog(@"user presses on %@",curAcct);
	NSInteger idx = [nameList indexOfObject:curAcct];
	NSMutableArray *acctInfo = nil;
	if (idx >= 0 && idx < [nameList count])
		acctInfo = [acctTable objectAtIndex:idx];
	
	[[ZimbraAppDelegate sharedAppDelegate] setCurrentAccount:acctInfo];
	
}

// When the search text changes, update the array
- (void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText
{
	[self buildSearchArrayFrom:searchText];
	if ([searchText length] == 0) [searchBar resignFirstResponder];
	if (![searchBar isFirstResponder]) {
		// user type the 'clear' button
		NSLog(@"resignFirstResponder not work");
		[search becomeFirstResponder];
	}
}

// When the search button (i.e. "Done") is clicked, hide the keyboard
- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar
{
	[searchBar resignFirstResponder];
}

- (void) buildAccountDataTable
{
	NSMutableArray *allNames = self.getController.statNames;
	NSMutableArray *allValues= self.getController.statValues;
	NSMutableArray *allIndexs = self.getController.statIndex;

	int nc = [allNames count];
	int ic = [allIndexs count];
	
	if (nc == 0 || ic == 0)
		return;
	//NSLog([allValues description]);
	acctTable = [[NSMutableArray alloc] init];
	nameList = [[NSMutableArray alloc] init];
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
}

- (void)loadView
{
	[super loadView];
	
	//initate GetController
	NSString * kGetURLText = @"http://10.117.4.35:8080/mytest/zimbra-cluster-acctinfo.txt";//@"http://localhost:8888/zimbra-cluster-acctinfo.txt";//
	GetController *kgetController = [[GetController alloc] initWithGetURL:kGetURLText];
	self.getController = kgetController;
	self.getController.viewController = self;

	// initiate PutController
	NSString * kPutURLText = @"http://10.117.4.35:8080/mytest/";
	NSString * kPutCmdFile = @"zimbra-cluster-cmds-req-delacct.txt";
	PutController *pController = [[PutController alloc] initWithPutURL:kPutURLText cmdFile:kPutCmdFile];
	self.putController = pController;
	
	self.navigationItem.rightBarButtonItem = self.editButtonItem;
	self.navigationItem.rightBarButtonItem.title = @"Delete";
	
	self.navigationItem.leftBarButtonItem = [[[UIBarButtonItem alloc]
											  initWithTitle:@"Logout" 
											  style:UIBarButtonItemStylePlain 
											  target:self 
											  action:@selector(logout)] autorelease];
	
	// initiate search bar
	[self buildSearchArrayFrom:@""];
	search = [[UISearchBar alloc] initWithFrame:CGRectMake(0.0f, 0.0f, 280.0f, 44.0f)];
	search.delegate = self;
	search.placeholder = @"Search Account";
	search.autocorrectionType = UITextAutocorrectionTypeNo;
	search.autocapitalizationType = UITextAutocapitalizationTypeNone;
	//self.navigationController.navigationBar.tintColor = [UIColor grayColor];
	search.tintColor = [UIColor lightGrayColor];
	//self.navigationItem.titleView = search;
	self.tableView.tableHeaderView = search;

	
	// "Search" is the wrong key usage here. Replacing it with "Done"
	UITextField *searchField = [[search subviews] lastObject];
	[searchField setReturnKeyType:UIReturnKeyDone];

	// set foot button in table view
	// create a UIButton (Deconnect button)
/*
	UIButton *btnDeco = [UIButton buttonWithType:UIButtonTypeRoundedRect];
	btnDeco.frame = CGRectMake(20, 5, 280, 30);
	[btnDeco setTitle:@"Show More" forState:UIControlStateNormal];
	btnDeco.backgroundColor = [UIColor lightGrayColor];
	[btnDeco setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
	[btnDeco addTarget:self action:@selector(showMoreAccount) forControlEvents:UIControlEventTouchUpInside];
	//[btnDeco sizeToFit];
	
	UIView *footerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 40)];
	footerView.backgroundColor = [UIColor lightGrayColor];
	[footerView addSubview:btnDeco];

	self.tableView.tableFooterView = footerView; 
	[footerView release];
*/
	///////////////QuartzCore/////////////////
/*	
	UIButton *downButton = [[UIButton alloc] initWithFrame:CGRectMake(20, 5, 280, 30)];
	[downButton setTitle:@"Show more" forState:UIControlStateNormal];
	[downButton setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
	[downButton setTitleColor:[UIColor lightGrayColor] forState:UIControlEventTouchDown];
	[downButton.titleLabel setFont:[UIFont fontWithName:@"Georgia" size: 14]];
	
	[downButton addTarget:self action:@selector(showMoreAccount) forControlEvents:UIControlEventTouchUpInside];
	[downButton setBackgroundColor:[UIColor whiteColor]];
	[downButton setTag:89];
	
	CALayer * downButtonLayer = [downButton layer];
	[downButtonLayer setMasksToBounds:YES];
	[downButtonLayer setCornerRadius:10.0];
	[downButtonLayer setBorderWidth:2.0];
	[downButtonLayer setBorderColor:[[UIColor grayColor] CGColor]];
	[self.view insertSubview:downButton atIndex:0];
	[downButton release];
	
	UIView *footerView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 40)];
	footerView.backgroundColor = [UIColor lightGrayColor];
	[footerView addSubview:downButton];
	
	self.tableView.tableFooterView = footerView; 
*/
	///////////// tool bar on the navigator right//////////////////
/*
	// edit: UIBarButtonSystemItemCompose
    // delete: UIBarButtonSystemItemTrash & UIBarButtonSystemItemStop
   //  add UIBarButtonSystemItemAdd
 
	UIToolbar *tools = [[UIToolbar alloc]
						initWithFrame:CGRectMake(0.0f, 0.0f, 80.0f, 44.0f)]; // 44.01 shifts it up 1px for some reason
	tools.clearsContextBeforeDrawing = NO;
	tools.clipsToBounds = NO;
	tools.tintColor = [UIColor colorWithWhite:0.305f alpha:0.0f]; // closest I could get by eye to black, translucent style.
	// anyone know how to get it perfect?
	tools.barStyle = -1; // clear background
	NSMutableArray *buttons = [[NSMutableArray alloc] initWithCapacity:3];
	
	// Create a standard refresh button.
	UIBarButtonItem *bi = [[UIBarButtonItem alloc]
						   initWithBarButtonSystemItem:UIBarButtonSystemItemCompose target:self action:@selector(refresh:)];
	[buttons addObject:bi];
	[bi release];
	
	bi = [[UIBarButtonItem alloc]
		  initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(refresh:)];
	[buttons addObject:bi];
	[bi release];
	
	bi = [[UIBarButtonItem alloc]
		  initWithBarButtonSystemItem:UIBarButtonSystemItemTrash target:self action:@selector(refresh:)];
	[buttons addObject:bi];
	[bi release];
	
	// Add profile button.
	//bi = [[UIBarButtonItem alloc] initWithTitle:@"Add" style:UIBarButtonSystemItemAdd target:self action:@selector(goToProfile)];
	//bi.style = UIBarButtonItemStyleBordered;
	//[buttons addObject:bi];
	//[bi release];

	// Add profile button.
	//bi = [[UIBarButtonItem alloc] initWithTitle:@"Delete" style:UIBarButtonSystemItemStop target:self action:@selector(goToProfile)];
	//bi.style = UIBarButtonItemStyleBordered;
	//[buttons addObject:bi];
	//[bi release];
 
	// Add profile button.
	//bi = [[UIBarButtonItem alloc] initWithTitle:@"Profile" style:UIBarButtonItemStylePlain target:self action:@selector(goToProfile)];
	//bi.style = UIBarButtonItemStyleBordered;
	//[buttons addObject:bi];
	//[bi release];
	
	// Add buttons to toolbar and toolbar to nav bar.
	[tools setItems:buttons animated:NO];
	[buttons release];
	UIBarButtonItem *twoButtons = [[UIBarButtonItem alloc] initWithCustomView:tools];
	[tools release];
	self.navigationItem.rightBarButtonItem = twoButtons;
	[twoButtons release];
*/	

	NSArray *buttonNames = [NSArray arrayWithObjects:@"Add", @"Edit", nil];
	UISegmentedControl* segmentedControl = [[UISegmentedControl alloc] initWithItems:buttonNames];
	segmentedControl.momentary = YES;
	//self.navigationItem.title = @"";
	
	segmentedControl.autoresizingMask = UIViewAutoresizingFlexibleWidth;
	segmentedControl.segmentedControlStyle = UISegmentedControlStyleBar;
	segmentedControl.frame = CGRectMake(0, 0, 120, 30);
	segmentedControl.tintColor = [UIColor darkGrayColor];
	[segmentedControl addTarget:self action:@selector(segmentAction:) forControlEvents:UIControlEventValueChanged];
	
    //UIBarButtonItem * segmentBarItem = [[UIBarButtonItem alloc] initWithCustomView: segmentedControl];
    //self.navigationItem.leftBarButtonItem = segmentBarItem;
	self.navigationItem.titleView = segmentedControl;

	[segmentedControl release];	
	
	///////////////////////////////
	
	//get status info from remote server
	[self.getController startGetController];
	
	// Initialize the titles
	tableTitles = [[NSMutableArray alloc] init];
	deleteTitles = [[NSMutableArray alloc] init];
	
	ithTitle = NCELLS;
	for (int i = 1; i <= NCELLS; i++) 
		[tableTitles addObject:[[NSString stringWithFormat:@"Account #%d", i] retain]];
	
	[search release];	
	[kgetController release];
	[kGetURLText release];
	
	//start timer
	[self startTimer];
}

- (void) CommitOperation
{
	NSString *cmd = @"";
	for (int i = 0; i < [deleteTitles count]; i++) {
		cmd = [NSString stringWithFormat:@"%@zmprov da %@\n",cmd, [deleteTitles objectAtIndex:i]];
	}
	NSLog(@"cmd = %@",cmd);
	// send the command
	if ([cmd length] > 0)
		[self.putController startPutController:cmd];
	[deleteTitles removeAllObjects];
}

// overload the action for edititem on navigationbar
- (void)setEditing:(BOOL)editing animated:(BOOL)animate
{
    [super setEditing:editing animated:animate];
    if(editing) {
        NSLog(@"editMode on");
    } else {
        NSLog(@"Done leave editmode");
		// check execute auth
		[[[ZimbraAppDelegate sharedAppDelegate] getSecurityController] setMyViewController:self];
		[[[ZimbraAppDelegate sharedAppDelegate] getSecurityController] executeConfirm];
		self.navigationItem.rightBarButtonItem.title = @"Delete";
    }
	
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

// action handler for 'fresh' botton
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

- (void) showMoreAccount
{
	NSLog(@"user presses show more account");
}


- (BOOL) checkEditValid
{
	NSMutableArray *acctInfo = [[ZimbraAppDelegate sharedAppDelegate] getCurrentAccount];
	if (acctInfo != nil)
		return TRUE;
	
    NSString *message = [[NSString alloc] initWithFormat:
                         @"You must select an account for edition."];
    UIAlertView *alert = [[UIAlertView alloc] 
                          initWithTitle:@"Operation Error"
                          message:message 
                          delegate:nil 
                          cancelButtonTitle:@"OK" 
                          otherButtonTitles:nil];
    [alert show];
    
    [message release];
    [alert release];
	return FALSE;
}

-(void) segmentAction: (id) sender
{
	NewAccountController *nControl = nil;
	UINavigationController *nav = nil;
	
	switch([sender selectedSegmentIndex])
	{
		case 0: 
			nControl = [[NewAccountController alloc] initWithTitle:@"New Account" loadData:FALSE];
			nav = [[UINavigationController alloc] initWithRootViewController:nControl];
			nav.navigationBar.barStyle = UIBarStyleBlackTranslucent;
			[self.navigationController presentModalViewController:nav animated:YES];
			break;
		case 1: 
			NSLog(@"two");
			if (![self checkEditValid]) break;
			nControl = [[NewAccountController alloc] initWithTitle:@"Edit Account" loadData:TRUE];
			nav = [[UINavigationController alloc] initWithRootViewController:nControl];
			nav.navigationBar.barStyle = UIBarStyleBlackTranslucent;
			[self.navigationController presentModalViewController:nav animated:YES];
			break;
		default: break;
	}
}


-(void) dealloc
{
	[tableTitles release];
	[super dealloc];
}

@end

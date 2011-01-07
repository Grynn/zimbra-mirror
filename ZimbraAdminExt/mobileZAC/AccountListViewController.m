//
//  AccountListViewController.m
//  Zimbra
//
//  Created by Qin An on 12/14/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import "AccountListViewController.h"
#import "AccountViewController.h"
#import "BoxMaker.h"


@implementation AccountListViewController

@synthesize searchArray;
@synthesize colorArray;
@synthesize tableTitles;

-(AccountListViewController *) initWithTitle: (NSString *)title
{
	self = [super init];
	self.title = title;
	[self.tabBarItem initWithTitle:self.title image:createImage(((float) 75.5f / 10.0f)) tag:0];
	return self;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView 
{
	return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section 
{
	return [searchArray count];
}


// Convert a 6-character hex color to a UIColor object
- (UIColor *) getColor: (NSString *) hexColor
{
	unsigned int red, green, blue;
	NSRange range;
	range.length = 2;
	
	range.location = 0; 
	[[NSScanner scannerWithString:[hexColor substringWithRange:range]] scanHexInt:&red];
	range.location = 2; 
	[[NSScanner scannerWithString:[hexColor substringWithRange:range]] scanHexInt:&green];
	range.location = 4; 
	[[NSScanner scannerWithString:[hexColor substringWithRange:range]] scanHexInt:&blue];	
	
	return [UIColor colorWithRed:(float)(red/255.0f) green:(float)(green/255.0f) blue:(float)(blue/255.0f) alpha:1.0f];
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
	NSInteger row = [indexPath row];
	
	// Create a cell if one is not already available
	UITableViewCell *cell = [self.tableView dequeueReusableCellWithIdentifier:@"any-cell"];
	if (cell == nil) 
		cell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:@"any-cell"] autorelease];
	
	// Set up the cell by coloring its text
	NSArray *crayon = [[searchArray objectAtIndex:row] componentsSeparatedByString:@"#"];
	cell.text = [crayon objectAtIndex:0];
	cell.textColor = [self getColor:[crayon objectAtIndex:1]];
	
	cell.accessoryType = UITableViewCellAccessoryDetailDisclosureButton;
	cell.hidesAccessoryWhenEditing = YES;
	
	return cell;
}

// Remove the current table row selection
- (void) deselect
{	
	[self.tableView deselectRowAtIndexPath:[self.tableView indexPathForSelectedRow] animated:YES];
}

// Respond to user selection by coloring the navigation bar
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)newIndexPath
{
	// Retrieve named color
	int row = [newIndexPath row];
	NSArray *crayon = [[searchArray objectAtIndex:row] componentsSeparatedByString:@"#"];
	
	// Update the nav bar color
	UIColor *newColor = [self getColor:[crayon objectAtIndex:1]];
	//self.navigationController.navigationBar.tintColor = newColor;
	//search.tintColor = newColor;
	
	// Deselect
	//[self performSelector:@selector(deselect) withObject:NULL afterDelay:0.5];
}


- (void)tableView:(UITableView *)tableView accessoryButtonTappedForRowWithIndexPath:(NSIndexPath *)indexPath
{
	[[self navigationController] pushViewController:[[AccountViewController alloc] init]  animated:YES];
}


// create an array by applying the search string
- (void) buildSearchArrayFrom: (NSString *) matchString
{
	NSString *upString = [matchString uppercaseString];//[[matchString uppercaseString] autorelease];
	if (searchArray) [searchArray release];
	
	searchArray = [[NSMutableArray alloc] init];
	for (NSString *word in colorArray)
	{
		if ([matchString length] == 0)
		{
			[searchArray addObject:word];
			continue;
		}
		
		NSRange range = [[[[word componentsSeparatedByString:@" #"] objectAtIndex:0] uppercaseString] rangeOfString:upString];
		if (range.location != NSNotFound) [searchArray addObject:word];
	}
	
	[self.tableView reloadData];
}

// When the search text changes, update the array
- (void)searchBar:(UISearchBar *)searchBar textDidChange:(NSString *)searchText
{
	[self buildSearchArrayFrom:searchText];
	if ([searchText length] == 0) [searchBar resignFirstResponder];
}

// When the search button (i.e. "Done") is clicked, hide the keyboard
- (void)searchBarSearchButtonClicked:(UISearchBar *)searchBar
{
	[searchBar resignFirstResponder];
}

// Prepare the Table View
- (void)loadView
{
	[super loadView];
	
	// Retrieve the text and colors from file
	NSString *pathname = [[NSBundle mainBundle]  pathForResource:@"acctcache" ofType:@"txt" inDirectory:@"/"];
	NSString *wordstring = [NSString stringWithContentsOfFile:pathname];
    colorArray = [[wordstring componentsSeparatedByString:@"\n"] retain];
	[self buildSearchArrayFrom:@""];

/*	
	search = [[UISearchBar alloc] initWithFrame:CGRectMake(0.0f, 0.0f, 280.0f, 44.0f)];
	search.delegate = self;
	search.placeholder = @"Search Account";
	search.autocorrectionType = UITextAutocorrectionTypeNo;
	search.autocapitalizationType = UITextAutocapitalizationTypeNone;
	self.navigationController.navigationBar.tintColor = [UIColor blackColor];
	search.tintColor = [UIColor blackColor];
	self.navigationItem.titleView = search;
	[search release];
	
	// "Search" is the wrong key usage here. Replacing it with "Done"
	UITextField *searchField = [[search subviews] lastObject];
	[searchField setReturnKeyType:UIReturnKeyDone];
*/	
		self.navigationItem.leftBarButtonItem = self.editButtonItem;

}

// Test codes
/*
- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
	
	//Initialize the toolbar
	UIToolbar *toolbar = [[UIToolbar alloc] init];
	toolbar.barStyle = UIBarStyleDefault;
	
	//Set the toolbar to fit the width of the app.
	[toolbar sizeToFit];
	
	//Caclulate the height of the toolbar
	CGFloat toolbarHeight = [toolbar frame].size.height;
	
	//Get the bounds of the parent view
	CGRect rootViewBounds = self.parentViewController.view.bounds;
	
	//Get the height of the parent view.
	CGFloat rootViewHeight = CGRectGetHeight(rootViewBounds);
	
	//Get the width of the parent view,
	CGFloat rootViewWidth = CGRectGetWidth(rootViewBounds);
	
	//Create a rectangle for the toolbar
	CGRect rectArea = CGRectMake(0, rootViewHeight - toolbarHeight, rootViewWidth, toolbarHeight);
	
	//Reposition and resize the receiver
	[toolbar setFrame:rectArea];
	
	//Create a button
	//UIBarButtonItem *infoButton = [[UIBarButtonItem alloc] 
	//							   initWithTitle:@"Info" style:UIBarButtonItemStyleBordered target:self action:@selector(doAction)];
	
	NSMutableArray* allitems = [[NSMutableArray alloc] init];
	[allitems addObject:[[[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(doAction)] autorelease]];
	//[allitems addObject:[[[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemBookmarks target:self action:@selector(doAction:)] autorelease]];
	//[allitems addObject:[[[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCamera target:self action:@selector(doAction:)] autorelease]];
	//[allitems addObject:[[[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCompose target:self action:@selector(doAction:)] autorelease]];
	//[allitems addObject:[[[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemOrganize target:self action:@selector(doAction:)] autorelease]];
	[allitems addObject:[[[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemRefresh target:self action:@selector(doAction)] autorelease]];
	[allitems addObject:[[[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemReply target:self action:@selector(doAction)] autorelease]];
	[allitems addObject:[[[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemSearch target:self action:@selector(doAction)] autorelease]];
	//[allitems addObject:[[[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemStop target:self action:@selector(doAction:)] autorelease]];
	[allitems addObject:[[[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemTrash target:self action:@selector(doAction)] autorelease]];
	
	//[toolbar setItems:[NSArray arrayWithObjects:infoButton,nil]];
	[toolbar setItems:allitems];
	//Add the toolbar as a subview to the navigation controller.
	[self.navigationController.view addSubview:toolbar];
	//[self makeTabBarHidden:FALSE];
	//self.tabBarController.hidesBottomBarWhenPushed=YES;
	toolbar.tintColor = [UIColor blackColor];
	
	//Reload the table view
	[self.tableView reloadData];
	
}

- (void) doAction {
	printf("Toolbar button is clicked");
}

- (void) hideTabBar: (UIView*) toolbarView
{
	for(UIView *view in self.tabBarController.view.subviews)
	{
		if([view isKindOfClass:[UITabBar class]])
		{
			view.hidden = YES;
			view = toolbarView;
			break;
		}
	}
}
*/



// Respond to a user tap
- (void)tableView:(UITableView *)tableView selectionDidChangeToIndexPath:(NSIndexPath *)newIndexPath fromIndexPath:(NSIndexPath *)oldIndexPath 
{
	printf("User selected row %d\n", [newIndexPath row] + 1);
	[self performSelector:@selector(deselect) withObject:nil afterDelay:0.5f];
}

// end of Test codes

// Clean up
-(void) dealloc
{
	[colorArray release];
	[searchArray release];
	[super dealloc];
}

@end

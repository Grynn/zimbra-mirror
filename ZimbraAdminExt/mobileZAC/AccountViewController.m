//
//  AccountViewController.m
//  Zimbra
//
//  Created by Qin An on 12/14/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import "AccountViewController.h"

#define URLSTRING_TAG		999

@implementation AccountViewController


- (AccountViewController *) init
{
	if (self = [super init]) self.title = @"Discription";
	return self;
}

#pragma mark UITableViewDataSource Methods

// Only one section in this table
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView 
{
	return 2;
}

// Return how many rows in the table
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section 
{
	return 1;
}

- (void) modCell:(UITableViewCell *)aCell withTitle:(NSString *)title
			note: (NSString *) comment //url: (NSString *) url
{
	// Title
	CGRect tRect1 = CGRectMake(65.0f, 5.0f, 260.0f, 40.0f);
	UILabel *title1 = [[UILabel alloc] initWithFrame:tRect1];
	[title1 setText:title];
	[title1 setTextAlignment:UITextAlignmentCenter];
	[title1 setFont: [UIFont fontWithName:@"American Typewriter" size:22.0f]];
	[title1 setBackgroundColor:[UIColor clearColor]];
	
	// Image
	UIImage *image = [UIImage imageNamed:@"Icon.png"];
	CGRect tRect2 = CGRectMake(5.0f, 5.0f, 60.0f, 60.0f);
	UIImageView *imageView = [[UIImageView alloc] initWithImage:image];
	imageView.frame = tRect2;
	
	
	// Comment
	CGRect tRect3 = CGRectMake(70.0f, 40.0f, 260.0f, 20.0f);
	UILabel *title3 = [[UILabel alloc] initWithFrame:tRect3];
	[title3 setText:comment];
	[title3 setTextAlignment:UITextAlignmentLeft];
	[title3 setFont: [UIFont fontWithName:@"Helvetica" size:18.0f]];
	[title3 setBackgroundColor:[UIColor clearColor]];
	
	
	// URL
	//CGRect tRect2 = CGRectMake(60.0f, 45.0f, 260.0f, 20.0f);
	//UILabel *title2 = [[UILabel alloc] initWithFrame:tRect2];
	//title2.tag = URLSTRING_TAG;
	//[title2 setText:url];
	//[title2 setTextAlignment:UITextAlignmentCenter];
	//[title2 setFont: [UIFont fontWithName:@"Helvetica" size:18.0f]];
	//[title2 setBackgroundColor:[UIColor clearColor]];	
	
	// Add to cell
	[aCell addSubview:title1];
	//[aCell addSubview:title2];
	[aCell addSubview:title3];
	[aCell addSubview:imageView];
	
	[title1 release];
	//[title2 release];
	[title3 release];
	[imageView release];
}

// Return a cell for the ith row
- (UITableViewCell *)tableView:(UITableView *)tView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
	UITableViewCell *cell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:@"any-cell"] autorelease];
	
	// Set up the cell
	int row = [indexPath row];
	if (row == 0) {
		[self modCell:cell withTitle:@"Service #1" 
				 note: @"Server: "];
	}
	
	return cell;
}

// This recipe adds a title for each section
//- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
//{
//	return [NSString stringWithFormat:@"Crayon names starting with '%@'", 
//			@"handler"];
//}

/*
 - (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
 {
 // create the parent view that will hold header Label
 UIView* customView = [[[UIView alloc] initWithFrame:CGRectMake(10,0,320,80)] autorelease];
 
 // create image object
 UIImage *myImage = [UIImage imageNamed:@"helloworld.png"];;
 
 // create the label objects
 UILabel *headerLabel = [[[UILabel alloc] initWithFrame:CGRectZero] autorelease];
 headerLabel.backgroundColor = [UIColor clearColor];
 headerLabel.font = [UIFont boldSystemFontOfSize:18];
 headerLabel.frame = CGRectMake(70,18,200,20);
 headerLabel.text =  @"Some Text";
 headerLabel.textColor = [UIColor redColor];
 
 UILabel *detailLabel = [[[UILabel alloc] initWithFrame:CGRectZero] autorelease];
 detailLabel.backgroundColor = [UIColor clearColor];
 detailLabel.textColor = [UIColor darkGrayColor];
 detailLabel.text = @"Some detail text";
 detailLabel.font = [UIFont systemFontOfSize:12];
 detailLabel.frame = CGRectMake(70,33,230,25);
 
 // create the imageView with the image in it
 UIImageView *imageView = [[[UIImageView alloc] initWithImage:myImage] autorelease];
 imageView.frame = CGRectMake(10,10,50,50);
 
 [customView addSubview:imageView];
 [customView addSubview:headerLabel];
 [customView addSubview:detailLabel];
 
 return customView;
 
 }
 */


#pragma mark UITableViewDelegateMethods

// Respond to user selection
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)newIndexPath
{
	printf("User selected row %d\n", [newIndexPath row] + 1);
	[[UIApplication sharedApplication] openURL:[NSURL URLWithString:[(UILabel *)[[tableView cellForRowAtIndexPath:newIndexPath] viewWithTag:URLSTRING_TAG] text]]];
}


- (void)loadView
{
	[super loadView];
	self.tableView.rowHeight = 100.0f;
}


@end

//
//  SettingViewController.m
//  Zimbra
//
//  Created by Qin An on 1/5/11.
//  Copyright 2011 VMware. All rights reserved.
//

#import "SettingViewController.h"
#import "BoxMaker.h"

@implementation SettingViewController

static NSString * kDefaultLocalFile = @"zimbraMobleClientSetting.txt";

- (SettingViewController *) initWithTitle: (NSString *)title
{
	if (self = [super initWithStyle:UITableViewStyleGrouped]) self.title = title;
	UIImage *tabIcon = [UIImage imageNamed:@"configure.png"];
	CGSize sz = CGSizeMake(32,32);
	UIImage *resized_icon = scaleImage(tabIcon, sz);
	self.tabBarItem.image = resized_icon;
	return self;
}

// Number of groups
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView 
{
	return 2;
}

// Section Titles
- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
	switch (section) 
	{
		case 0:
			return @"General";
		case 1:
			return @"Network";
		default:
			return @"";
	}
}

// Number of rows per section
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section 
{
	switch (section) 
	{
		case 0:
			return 2;
		case 1:
			return 3;
		default:
			return 0;
	}
}

// Heights per row
- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
	int section = [indexPath section];
	int row = [indexPath row];
	
	switch (section) 
	{
		case 0:
			//if (row == 0) return 40.0f;  // custom specific row
			//return 40.0f;
			return 44.0f;
		case 1:
			return 44.0f;
		default:
			return 44.0f;
	}
}

// Produce cells
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
	NSInteger row = [indexPath row];
	NSInteger section = [indexPath section];
	UITableViewCell *cell;
	
	switch (section) 
	{
		case 0:
			if (row == 0) {
				// Add a text field to the cell
				cell = [tableView dequeueReusableCellWithIdentifier:@"textCell"];
				if (!cell) {
					cell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:@"textCell"] autorelease];
					
					UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(20.0f, 10.0f, 70.0f, 20.0f)];
					[label setText:@"Server:"];
					[cell addSubview:label];
					[label release];
					
					[cell addSubview:[[UITextField alloc] initWithFrame:CGRectMake(90.0f, 10.0f, 210.0f, 20.0f)]];
				}
				servertf = [[cell subviews] lastObject];
				servertf.placeholder = @"Enter ZCS server address";
				servertf.delegate = self;
				servertf.borderStyle = UITextBorderStyleNone;//UITextBorderStyleBezel;
				servertf.autocorrectionType = UITextAutocorrectionTypeNo;
				if (zcsServer && [zcsServer length] > 0)
					servertf.text = zcsServer;
				return cell;
			}
/*
			if (row == 1) {
				// Create a big word-wrapped UILabel
				cell = [tableView dequeueReusableCellWithIdentifier:@"libertyCell"];
				if (!cell) {
					cell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:@"libertyCell"] autorelease];
					[cell addSubview:[[UILabel alloc] initWithFrame:CGRectMake(20.0f, 0.0f, 280.0f, 60.0f)]];
				}
				UILabel *sv = [[cell subviews] lastObject];
				sv.text =  @"When in the Course of human events, it becomes necessary";
				sv.textAlignment = UITextAlignmentCenter;
				sv.lineBreakMode = UILineBreakModeWordWrap;
				sv.numberOfLines = 9;
				return cell;
			}
*/
			if (row == 1) {
				// Add a text field to the cell
				cell = [tableView dequeueReusableCellWithIdentifier:@"textCell"];
				if (!cell) {
					cell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:@"textCell"] autorelease];
					
					UILabel *label = [[UILabel alloc] initWithFrame:CGRectMake(20.0f, 10.0f, 70.0f, 20.0f)];
					[label setText:@"Port:"];
					[cell addSubview:label];
					[label release];
					
					[cell addSubview:[[UITextField alloc] initWithFrame:CGRectMake(90.0f, 10.0f, 210.0f, 20.0f)]];
				}
				porttf = [[cell subviews] lastObject];
				porttf.placeholder = @"Enter ZCS server port";
				porttf.delegate = self;
				porttf.borderStyle = UITextBorderStyleNone;//UITextBorderStyleBezel;
				porttf.autocorrectionType = UITextAutocorrectionTypeNo;
				if (zcsPort && [zcsPort length] > 0) {
					porttf.text = zcsPort;
				}
				return cell;
			}
			break;
		case 1:
			{// Create cells with accessory checking
				cell = [tableView dequeueReusableCellWithIdentifier:@"checkCell"];
				if (!cell) {
					cell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:@"checkCell"] autorelease];
					cell.accessoryType = UITableViewCellAccessoryNone;
				}
				BOOL ischeck = FALSE;
				if (row == 0) {
					cell.text = [NSString stringWithFormat:@"SSL support"];
					ischeck = ([sslSupport isEqualToString:@"checked"]);
				}
				else if (row == 1) {
					cell.text = [NSString stringWithFormat:@"X.509 Certificate support"];
					ischeck = [x509Support isEqualToString:@"checked"];
				} else if (row == 2) {
					cell.text = [NSString stringWithFormat:@"Auto-Lock"];
					ischeck = [autolockSupport isEqualToString:@"checked"];
				}
				cell.accessoryType = (ischeck)?UITableViewCellAccessoryCheckmark:UITableViewCellAccessoryNone;
				return cell;
			}
			break;
		default:
			break;
	}
	
	// Return a generic cell if all else fails
	cell = [tableView dequeueReusableCellWithIdentifier:@"any-cell"];
	if (cell == nil) {
		cell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:@"any-cell"] autorelease];
	}
	
	return cell;
}

// utility functions
- (void) deselect
{	
	[self.tableView deselectRowAtIndexPath:[self.tableView indexPathForSelectedRow] animated:YES];
}

- (void) notify: (NSString *) aMessage
{
	UIAlertView *baseAlert = [[UIAlertView alloc] 
							  initWithTitle:@"Alert" message:aMessage 
							  delegate:self cancelButtonTitle:nil
							  otherButtonTitles:@"Okay", nil];
	[baseAlert show];
}

// TextField delegate handles return events and dismisses keyboard
- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
	[textField resignFirstResponder];
	//[self notify:[NSString stringWithFormat:@"Hello %@", [textField text]]];
	return YES;
}

// Respond to user selection based on the cell type
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)newIndexPath
{
	
	int section = [newIndexPath section];
	int row = [newIndexPath row];
	UITableViewCell *cell = [self.tableView cellForRowAtIndexPath:newIndexPath];
	
	switch (section) 
	{
		case 0:
			if (row == 0)
			{
				//[self notify:[NSString stringWithFormat:@"Hello %@", [[[cell subviews] lastObject] text]]];
			}
			break;
		case 1: {
			NSString *isCheck = @"unchecked";
			if (cell.accessoryType == UITableViewCellAccessoryNone) {
				cell.accessoryType = UITableViewCellAccessoryCheckmark;
				isCheck = @"checked";
			} else {
				cell.accessoryType = UITableViewCellAccessoryNone;
				isCheck = @"unchecked";
			}
			if (row == 0) sslSupport = isCheck;
			else if(row == 1) x509Support = isCheck;
			else if(row == 2) autolockSupport = isCheck;
			}
			break;
		default:
			break;
	}
	
	[self performSelector:@selector(deselect) withObject:NULL afterDelay:0.5];
}

- (void) saveToLocalFile: (NSString*) msg toPath: (NSString*) path
{
	if (msg == nil || path == nil)
		return;
	
	NSData* data=[msg dataUsingEncoding:NSUTF8StringEncoding];
	// construct path within our documents directory
	NSString *applicationDocumentsDir = 
	[NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) lastObject];
	NSString *storePath = [applicationDocumentsDir stringByAppendingPathComponent:path];
	
	// write to file atomically (using temp file)
	[data writeToFile:storePath atomically:TRUE];
}

- (void) loadDataToView
{
	NSString *applicationDocumentsDir = 
	[NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) lastObject];
	NSString *storePath = [applicationDocumentsDir stringByAppendingPathComponent:kDefaultLocalFile];
	
	NSString *content = [self readFromLocalFile:storePath];
	if (content == nil) return;
	NSLog(@"read content = %@",content);
	
	NSArray *chunkitems = [content componentsSeparatedByString: @"|"];
	for (int i = 0; i < [chunkitems count]; i++) {
		NSArray *items = [[chunkitems objectAtIndex:i] componentsSeparatedByString: @":"];
		if ([items count] < 2) continue;
		else if ([[items objectAtIndex:0] isEqualToString:@"server"]) {
			zcsServer = [[items objectAtIndex:1] copy];
		} else if ([[items objectAtIndex:0] isEqualToString:@"port"]) {
			zcsPort = [[items objectAtIndex:1] copy];
		} else if ([[items objectAtIndex:0] isEqualToString:@"ssl"]) {
			sslSupport = [[items objectAtIndex:1] copy];
		} else if ([[items objectAtIndex:0] isEqualToString:@"x509"]) {
			x509Support = [[items objectAtIndex:1] copy];
		} else if ([[items objectAtIndex:0] isEqualToString:@"autocheck"]) {
			autolockSupport = [[items objectAtIndex:1] copy];
		}
	}
}

- (NSString*) readFromLocalFile: (NSString*)path
{
	if (path == nil) return nil;
	NSString *myText = [NSString stringWithContentsOfFile:path]; 
	return myText;
}

- (void)loadView
{
	[super loadView];
	self.navigationItem.rightBarButtonItem = [[[UIBarButtonItem alloc]
											  initWithTitle:@"Save" 
											  style:UIBarButtonItemStylePlain 
											  target:self 
											  action:@selector(saveSetting)] autorelease];
	self.navigationController.navigationBar.tintColor = [UIColor darkGrayColor];
	[self loadDataToView];
}

- (void) saveSetting
{
	NSLog(@"sever = %@, port = %@, ssl = %@, x509 = %@, autolock = %@",
		  servertf.text, porttf.text, sslSupport, x509Support, autolockSupport);
	NSString *msg = [NSString stringWithFormat:@"server:%@|port:%@|ssl:%@|x509:%@|autolock:%@",
					 servertf.text, porttf.text, sslSupport, x509Support, autolockSupport];	
	NSLog(@"msg = %@",msg);
	[self saveToLocalFile:msg toPath:kDefaultLocalFile];
}

@end

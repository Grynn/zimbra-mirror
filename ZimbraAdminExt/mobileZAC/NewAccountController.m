//
//  NewAccountController.m
//  Zimbra
//
//  Created by Qin An on 12/14/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import "ZimbraAppDelegate.h"
#import "NewAccountController.h"
#import "BoxMaker.h"
#import "UICustomSwitch.h"
//#import "UICustomSwitchExt.h"
#import "PutController.h"
#import "SecurityController.h"


@implementation NewAccountController

@synthesize isLoadData;
@synthesize indexOfTab;
@synthesize putController;

- (NewAccountController *) init
{
	if (self = [super init]) self.title = @"New Account";
	return self;
}
/*
-(NewAccountController *) initWithTitle: (NSString *)title
{
	self = [super init];
	self.title = title;
	self.indexOfTab = 0;
	[self.tabBarItem initWithTitle:self.title image:createImage(((float) 75.5f / 10.0f)) tag:0];
	return self;
}
*/

-(NewAccountController *) initWithTitle: (NSString *)title loadData: (BOOL) isLoad
{
	self = [super init];
	self.title = title;
	self.isLoadData = isLoad;
	//[self.tabBarItem initWithTitle:self.title image:createImage(((float) 75.5f / 10.0f)) tag:0];
	if (self.isLoadData) {
		UIImage *tabIcon = [UIImage imageNamed:@"edit.png"];
		CGSize sz = CGSizeMake(32,32);
		UIImage *resized_icon = scaleImage(tabIcon, sz);
		self.tabBarItem.image = resized_icon;
	} else {
		UIImage *tabIcon = [UIImage imageNamed:@"add.png"];
		CGSize sz = CGSizeMake(32,32);
		UIImage *resized_icon = scaleImage(tabIcon, sz);
		self.tabBarItem.image = resized_icon;
	}

	return self;
}


// This method catches the return action
- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
	[textField resignFirstResponder];
	return YES;
}


// Implement loadView to create a view hierarchy programmatically.
- (void)loadView {
	// Load an application image and set it as the primary view
	contentView = [[UIView alloc] initWithFrame:[[UIScreen mainScreen] applicationFrame]];
	contentView.backgroundColor = [UIColor whiteColor];
	self.view = contentView;
	[contentView release];
	
	UILabel *nameLabel = [[[UILabel alloc] initWithFrame:CGRectMake(0.0f, 10.0f, 80.0f, 40.0f)] retain];
	[nameLabel setText:@"Name: "];
	[nameLabel setCenter:CGPointMake(100.0f, 65.0f)];
	[contentView addSubview:nameLabel];
	[nameLabel release];

	UILabel *acctLabel = [[[UILabel alloc] initWithFrame:CGRectMake(0.0f, 10.0f, 80.0f, 40.0f)] retain];
	[acctLabel setText:@"Account: "];
	[acctLabel setCenter:CGPointMake(90.0f, 100.0f)];
	[contentView addSubview:acctLabel];
	[acctLabel release];
	
	UILabel *adminLabel = [[[UILabel alloc] initWithFrame:CGRectMake(0.0f, 10.0f, 80.0f, 40.0f)] retain];
	[adminLabel setText:@"Is admin: "];
	[adminLabel setCenter:CGPointMake(170.0f, 132.0f)];
	[contentView addSubview:adminLabel];
	[adminLabel release];
	
	
	UILabel *passLabel = [[[UILabel alloc] initWithFrame:CGRectMake(0.0f, 10.0f, 120.0f, 30.0f)] retain];
	[passLabel setText:@"Password: "];
	[passLabel setCenter:CGPointMake(95.0f, 167.0f)];
	[contentView addSubview:passLabel];
	[passLabel release];
	
	UILabel *rpassLabel = [[[UILabel alloc] initWithFrame:CGRectMake(0.0f, 10.0f, 160.0f, 40.0f)] retain];
	[rpassLabel setText:@"Re-Password: "];
	[rpassLabel setCenter:CGPointMake(90.0f, 203.0f)];
	[contentView addSubview:rpassLabel];
	[rpassLabel release];

	UILabel *chgPwdLabel = [[[UILabel alloc] initWithFrame:CGRectMake(0.0f, 10.0f, 160.0f, 40.0f)] retain];
	[chgPwdLabel setText:@"Must Changed: "];
	[chgPwdLabel setCenter:CGPointMake(160.0f, 235.0f)];
	[contentView addSubview:chgPwdLabel];
	[chgPwdLabel release];
	
	UILabel *statusLabel = [[[UILabel alloc] initWithFrame:CGRectMake(0.0f, 10.0f, 160.0f, 40.0f)] retain];
	[statusLabel setText:@"Locked: "];
	[statusLabel setCenter:CGPointMake(220.0f, 265.0f)];
	[contentView addSubview:statusLabel];
	[statusLabel release];
	
	// A normal text entry field
	namefield = [[[UITextField alloc] initWithFrame:CGRectMake(120.0f, 50.0f, 180.0f, 30.0f)] retain];
	[namefield setBorderStyle:UITextBorderStyleRoundedRect];
	
	namefield.placeholder = @"display name";
	namefield.autocorrectionType = UITextAutocorrectionTypeNo;
	namefield.autocapitalizationType = UITextAutocapitalizationTypeNone;
	namefield.returnKeyType = UIReturnKeyDone;
	namefield.clearButtonMode = UITextFieldViewModeWhileEditing;
	
	namefield.delegate = self;
	[contentView addSubview:namefield];
	[namefield release];

	acctfield = [[[UITextField alloc] initWithFrame:CGRectMake(120.0f, 85.0f, 180.0f, 30.0f)] retain];
	[acctfield setBorderStyle:UITextBorderStyleRoundedRect];
	acctfield.placeholder = @"account name";
	acctfield.autocorrectionType = UITextAutocorrectionTypeNo;
	acctfield.autocapitalizationType = UITextAutocapitalizationTypeNone;
	acctfield.returnKeyType = UIReturnKeyDone;
	acctfield.clearButtonMode = UITextFieldViewModeWhileEditing;
	
	acctfield.delegate = self;
	[contentView addSubview:acctfield];
	[acctfield release];
	
	// A secure password field
	passfield = [[[UITextField alloc] initWithFrame:CGRectMake(120.0f, 152.0f, 180.0f, 30.0f)] retain];
	[passfield setBorderStyle:UITextBorderStyleRoundedRect];
	
	passfield.placeholder = @"password";
	passfield.secureTextEntry = YES;
	passfield.autocorrectionType = UITextAutocorrectionTypeNo;
	passfield.autocapitalizationType = UITextAutocapitalizationTypeNone;
	passfield.returnKeyType = UIReturnKeyDone;
	passfield.clearButtonMode = UITextFieldViewModeWhileEditing;
	
	passfield.delegate = self;
	[contentView addSubview:passfield];
	[passfield release];
	
	// Confirm password field
	rpassfield = [[[UITextField alloc] initWithFrame:CGRectMake(120.0f, 187.0f, 180.0f, 30.0f)] retain];
	[rpassfield setBorderStyle:UITextBorderStyleRoundedRect];
	
	rpassfield.placeholder = @"Confrim password";
	rpassfield.secureTextEntry = YES;
	rpassfield.autocorrectionType = UITextAutocorrectionTypeNo;
	rpassfield.autocapitalizationType = UITextAutocapitalizationTypeNone;
	rpassfield.returnKeyType = UIReturnKeyDone;
	rpassfield.clearButtonMode = UITextFieldViewModeWhileEditing;
	
	rpassfield.delegate = self;
	[contentView addSubview:rpassfield];
	[rpassfield release];	
	
	
	// Yes-No Switch
/*
	UICustomSwitch *switchView = [[UICustomSwitch alloc] initWithFrame:CGRectZero];
	[switchView setCenter:CGPointMake(250.0f,235.0f)];
	[switchView setLeftLabelText: @"YES"];
	[switchView setRightLabelText: @"NO"];
	[contentView addSubview:switchView];
	chgPassSwitch = switchView;
	[switchView release];
	
	switchView = [[UICustomSwitch alloc] initWithFrame:CGRectZero];
	[switchView setCenter:CGPointMake(250.0f,133.0f)];
	[switchView setLeftLabelText: @"YES"];
	[switchView setRightLabelText: @"NO"];
	[contentView addSubview:switchView];
	adminSwitch = switchView;
	[switchView release];
	
	switchView = [[UICustomSwitch alloc] initWithFrame:CGRectZero];
	[switchView setCenter:CGPointMake(250.0f,265.0f)];
	[switchView setLeftLabelText: @"YES"];
	[switchView setRightLabelText: @"NO"];
	[contentView addSubview:switchView];
	statusSwitch = switchView;
	[switchView release];
*/
	UISwitch *switchView = [[UISwitch alloc] initWithFrame:CGRectZero];
	[switchView setCenter:CGPointMake(250.0f,235.0f)];
	//[switchView setLeftLabelText: @"YES"];
	//[switchView setRightLabelText: @"NO"];
	[contentView addSubview:switchView];
	chgPassSwitch = switchView;
	[switchView release];
	
	switchView = [[UISwitch alloc] initWithFrame:CGRectZero];
	[switchView setCenter:CGPointMake(250.0f,133.0f)];
	//[switchView setLeftLabelText: @"YES"];
	//[switchView setRightLabelText: @"NO"];
	[contentView addSubview:switchView];
	adminSwitch = switchView;
	[switchView release];
	
	switchView = [[UISwitch alloc] initWithFrame:CGRectZero];
	[switchView setCenter:CGPointMake(250.0f,265.0f)];
	//[switchView setLeftLabelText: @"YES"];
	//[switchView setRightLabelText: @"NO"];
	[contentView addSubview:switchView];
	statusSwitch = switchView;
	[switchView release];
	
	// initiate PutController
	NSString * kPutURLText = @"http://10.117.4.35:8080/mytest/";
	NSString * kPutCmdFile = @"zimbra-cluster-cmds-req-addeditacct.txt";
	PutController *pController = [[PutController alloc] initWithPutURL:kPutURLText cmdFile:kPutCmdFile];
	self.putController = pController;
}


- (void) loadDataToUI
{
	NSMutableArray *acctInfo = [[ZimbraAppDelegate sharedAppDelegate] getCurrentAccount];
	if (acctInfo) {
		NSString *acct = [acctInfo objectAtIndex:0];
		NSString *displayname = [acctInfo objectAtIndex:2];
		NSString *admin = [acctInfo objectAtIndex: 4];
		NSString *status = [acctInfo objectAtIndex:3];
		
		NSLog(@"acct = %@, displayname = %@, admin = %@, status = %@",acct,displayname,admin,status);
		
		// set to UI
		nameOld = namefield.text = displayname;
		acctfield.text = acct;
		passOld = passfield.text = @"123456";
		rpassfield.text = @"123456";
		
		if ([admin isEqualToString:@" TRUE"]) {
			[adminSwitch setOn:YES animated:YES];
			adminOld = 1;
		}
		
		if (![status isEqualToString:@" active"]) {
			[statusSwitch setOn:YES animated:YES];
			statusOld = 1;
		} 
		
		acctfield.userInteractionEnabled = NO; // disable edit
		acctfield.textColor = [UIColor grayColor];
	}
}

// Implement viewDidLoad to do additional setup after loading the view.
- (void)viewDidLoad {
	
		
	self.navigationItem.leftBarButtonItem = [[[UIBarButtonItem alloc] 
												  initWithBarButtonSystemItem:UIBarButtonSystemItemCancel 
												  target:self action:@selector(cancel_Clicked:)] autorelease];
	
	self.navigationItem.rightBarButtonItem = [[[UIBarButtonItem alloc]
											  initWithTitle:@"Save" 
											  style:UIBarButtonItemStylePlain 
											  target:self 
											  action:@selector(CommitAccount)] autorelease];

	//self.navigationController.navigationBar.tintColor = [UIColor blackColor];
	if (isLoadData)
		[self loadDataToUI];

}

// the method will be called at any time when view is shown
-(void) viewWillAppear:(BOOL)animated {
	
	[super viewWillAppear:animated];
	NSLog(@"EditController:viewWillAppear called");
	if (isLoadData)
		[self loadDataToUI];
	
}



-(void) cancel_Clicked:(id)sender {
	
	[self.navigationController dismissModalViewControllerAnimated:YES];
	self.tabBarController.selectedIndex = 1;
}

- (void) CommitAccount
{
	NSLog(@"displayname = %@, acct = %@, passwd = %@, rpasswd = %@",
		  namefield.text, acctfield.text, passfield.text, rpassfield.text);
	NSLog(@"nameOld = %@, passOld = %@, adminOld = %d, chkpass = %d, status = %d",
		  nameOld, passOld, adminOld, chgpassOld, statusOld);
	if (adminSwitch.on) 
		NSLog(@"admin is selected");
	else NSLog(@"admin is not selected");
	if (chgPassSwitch.on) 
		NSLog(@"chgpass is selected");
	else NSLog(@"chgpass is not selected");
	if (statusSwitch.on) 
		NSLog(@"status is selected");
	else NSLog(@"status is not selected");
	
	//check password consistency
	if (!([passfield.text length] > 0 && [rpassfield.text length] > 0 
		  && [passfield.text isEqualToString:rpassfield.text])) {
		UIAlertView *alert = [[UIAlertView alloc] 
							  initWithTitle:@"Input Error"
							  message:@"Password should be same and non-empty."
							  delegate:nil
							  cancelButtonTitle:@"OK" 
							  otherButtonTitles:nil];
		[alert show];
		[alert release];
		return;
	}
	
	// check account empty
	if ([acctfield.text length] <= 0) {
		UIAlertView *alert = [[UIAlertView alloc] 
							  initWithTitle:@"Input Error"
							  message:@"Account field should not be empty."
							  delegate:nil
							  cancelButtonTitle:@"OK" 
							  otherButtonTitles:nil];
		[alert show];
		[alert release];
		return;
	}
	
	// check new account password
	if (!isLoadData && [passfield.text length] <= 0) {
		UIAlertView *alert = [[UIAlertView alloc] 
							  initWithTitle:@"Input Error"
							  message:@"Password should be given."
							  delegate:nil
							  cancelButtonTitle:@"OK" 
							  otherButtonTitles:nil];
		[alert show];
		[alert release];
		return;
	}
	
	// check execute auth
	[[[ZimbraAppDelegate sharedAppDelegate] getSecurityController] setMyViewController:self];
	[[[ZimbraAppDelegate sharedAppDelegate] getSecurityController] executeConfirm];
}

- (void) CommitOperation
{
	// zmprov attributes:
	// * zimbraAccountStatus [active|locked]
	// * displayName  "my string"
	// * zimbraIsAdminAccount  [TRUE|FALSE]
	// * zimbraPasswordMustChange  [TRUE|FALSE]
	// * userPassword  "some string"
	NSString *cmd = nil;
	if (isLoadData) {
		cmd = [NSString stringWithFormat:@"zmprov ma %@ ",acctfield.text];
		if ([namefield.text length] != [nameOld length] 
			|| ([namefield.text length] > 0 && ![namefield.text isEqualToString:nameOld]) ) {
			cmd = [NSString stringWithFormat:@"%@ displayName \\\"%@\\\"",cmd, namefield.text];
		}
		if ([passfield.text length] != [passOld length] 
			|| ([passfield.text length] > 0 && ![passfield.text isEqualToString:passOld]) ) {
			cmd = [NSString stringWithFormat:@"%@ userPassword \\\"%@\\\"",cmd, passfield.text];
		}
		if (adminSwitch.on != (adminOld == 1)) {
			cmd = [NSString stringWithFormat:@"%@ zimbraIsAdminAccount %@",cmd, (adminSwitch.on)? @"TRUE":@"FALSE"];
		}
		if (chgPassSwitch.on != (chgpassOld == 1)) {
			cmd = [NSString stringWithFormat:@"%@ zimbraPasswordMustChange %@",cmd, (chgPassSwitch.on)? @"TRUE":@"FALSE"];
		}
		if (statusSwitch.on != (statusOld == 1)) {
			cmd = [NSString stringWithFormat:@"%@ zimbraAccountStatus %@",cmd, (statusSwitch.on)? @"locked":@"active"];
		}
	}else {
		cmd = [NSString stringWithFormat:@"zmprov ca %@ ",acctfield.text];
		cmd = [NSString stringWithFormat:@"%@ \\\"%@\\\"",cmd, passfield.text];
		if ([namefield.text length] > 0) {
			cmd = [NSString stringWithFormat:@"%@ displayName \\\"%@\\\"",cmd, namefield.text];
		}
		cmd = [NSString stringWithFormat:@"%@ zimbraIsAdminAccount %@",cmd, (adminSwitch.on)? @"TRUE":@"FALSE"];
		cmd = [NSString stringWithFormat:@"%@ zimbraPasswordMustChange %@",cmd, (chgPassSwitch.on)? @"TRUE":@"FALSE"];
		cmd = [NSString stringWithFormat:@"%@ zimbraAccountStatus %@",cmd, (statusSwitch.on)? @"locked":@"active"];
		
	}
	
	
	NSLog(@"cmd = %@",cmd);
	// send command
	[self.putController startPutController:cmd];
	self.tabBarController.selectedIndex = 1;
}

- (void)dealloc {
    [super dealloc];
}



@end

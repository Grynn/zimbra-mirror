//
//  ChangePasswordController.m
//  Zimbra
//
//  Created by Qin An on 12/15/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import "ZimbraAppDelegate.h"
#import "ChangePasswordController.h"
#import "BoxMaker.h"
#import "UICustomSwitch.h"
#import "PutController.h"
#import "SecurityController.h"

@implementation ChangePasswordController

@synthesize putController;

-(ChangePasswordController *) initWithTitle: (NSString *)title
{
	self = [super init];
	self.title = title;
	UIImage *tabIcon = [UIImage imageNamed:@"password.png"];
	CGSize sz = CGSizeMake(32,32);
	UIImage *resized_icon = scaleImage(tabIcon, sz);
	self.tabBarItem.image = resized_icon;
	return self;
}

- (void) loadDataToUI
{
	NSMutableArray *acctInfo = [[ZimbraAppDelegate sharedAppDelegate] getCurrentAccount];
	if (acctInfo) {
		NSString *acct = [acctInfo objectAtIndex:0];
		NSString *displayname = [acctInfo objectAtIndex:2];
		
		NSLog(@"acct = %@, displayname = %@",acct,displayname);
		
		if (acct != nil)
			emailView.text = acct;
		else emailView.text = @"user@domain.com";
		if (displayname != nil)
			nameView.text = displayname;
		else nameView.text = @"User Name";
	}else {
		emailView.text = @"user@domain.com";
		nameView.text = @"User Name";
	}
	
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

	nameView = [[UITextView alloc] initWithFrame:CGRectMake(0.0f, 0.0f, 240.0f, 60.0f)];
	[nameView setCenter:CGPointMake(160.0f, 40.0f)];
	//nameView.autoresizingMask = UIViewAutoresizingFlexibleHeight;
	//nameView.backgroundColor = [UIColor grayColor];
	nameView.font = [UIFont fontWithName:@"Verdana" size:24.0f];;
	nameView.text = @"";
	[nameView sizeToFit];
	nameView.userInteractionEnabled = NO; // disable keyboard input	
	nameView.delegate = self;
	[contentView addSubview:nameView];
	[nameView release];
	
	emailView = [[UITextView alloc] initWithFrame:CGRectMake(0.0f, 0.0f, 240.0f, 30.0f)];
	[emailView setCenter:CGPointMake(160.0f, 65.0f)];
	emailView.textColor = [UIColor grayColor];
	emailView.font = [UIFont fontWithName:@"Verdana" size:16.0f];;
	emailView.text = @"";
	[emailView sizeToFit];
	emailView.userInteractionEnabled = NO; 
	emailView.delegate = self;
	[contentView addSubview:emailView];
	[emailView release];
	
	UILabel *passLabel = [[[UILabel alloc] initWithFrame:CGRectMake(0.0f, 10.0f, 120.0f, 40.0f)] retain];
	[passLabel setText:@"Password: "];
	[passLabel setCenter:CGPointMake(92.0f, 105.0f)];
	[contentView addSubview:passLabel];
	[passLabel release];
	
	UILabel *rpassLabel = [[[UILabel alloc] initWithFrame:CGRectMake(0.0f, 10.0f, 160.0f, 40.0f)] retain];
	[rpassLabel setText:@"Re-Password: "];
	[rpassLabel setCenter:CGPointMake(90.0f, 145.0f)];
	[contentView addSubview:rpassLabel];
	[rpassLabel release];
	
	UILabel *chgPwdLabel = [[[UILabel alloc] initWithFrame:CGRectMake(0.0f, 10.0f, 160.0f, 40.0f)] retain];
	[chgPwdLabel setText:@"Must Changed: "];
	[chgPwdLabel setCenter:CGPointMake(160.0f, 180.0f)];
	[contentView addSubview:chgPwdLabel];
	[chgPwdLabel release];
	

	
	// A secure password field
	passfield = [[[UITextField alloc] initWithFrame:CGRectMake(120.0f, 90.0f, 180.0f, 30.0f)] retain];
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
	rpassfield = [[[UITextField alloc] initWithFrame:CGRectMake(120.0f, 130.0f, 180.0f, 30.0f)] retain];
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
	UICustomSwitch *switchView = [[UICustomSwitch alloc] initWithFrame:CGRectZero];
	[switchView setCenter:CGPointMake(250.0f,180.0f)];
	[switchView setLeftLabelText: @"YES"];
	[switchView setRightLabelText: @"NO"];
	[contentView addSubview:switchView];
	chgPassSwitch = switchView;
	[switchView release];
	

	self.navigationController.navigationBar.tintColor = [UIColor blackColor];
	
	// initiate PutController
	NSString * kPutURLText = @"http://10.117.4.35:8080/mytest/";
	NSString * kPutCmdFile = @"zimbra-cluster-cmds-req-setpass.txt";
	PutController *pController = [[PutController alloc] initWithPutURL:kPutURLText cmdFile:kPutCmdFile];
	self.putController = pController;
}



// Implement viewDidLoad to do additional setup after loading the view.
- (void)viewDidLoad {
	
	
	self.navigationItem.leftBarButtonItem = [[[UIBarButtonItem alloc] 
											  initWithBarButtonSystemItem:UIBarButtonSystemItemCancel 
											  target:self action:@selector(cancel_Clicked:)] autorelease];
	
	self.navigationItem.rightBarButtonItem = [[[UIBarButtonItem alloc]
											   initWithTitle:@"Commit" 
											   style:UIBarButtonItemStylePlain 
											   target:self 
											   action:@selector(CommitAccount)] autorelease];
	
	[self loadDataToUI];
}

-(void) viewWillAppear:(BOOL)animated {
	
	[super viewWillAppear:animated];
	
}


-(void) cancel_Clicked:(id)sender {
	
	[self.navigationController dismissModalViewControllerAnimated:YES];
	self.tabBarController.selectedIndex = 1;
}

- (void) CommitAccount
{

	NSLog(@"passwd = %@, re-passwd = %@\n",passfield.text, rpassfield.text);
	if (chgPassSwitch.on) 
		NSLog(@"must change password is selected");
	else NSLog(@"must change password is not selected");

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
	if ([emailView.text length] <= 0 || [emailView.text isEqualToString:@"user@domain.com"]) {
		UIAlertView *alert = [[UIAlertView alloc] 
							  initWithTitle:@"Operation Error"
							  message:@"Please choose an account before reset password."
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
	NSString *cmd = nil;
	if ([passfield.text length] > 0 ) {
		cmd = [NSString stringWithFormat:@"zmprov ma %@ ",emailView.text];
		cmd = [NSString stringWithFormat:@"%@ userPassword \"%@\"",cmd, passfield.text];
		cmd = [NSString stringWithFormat:@"%@ zimbraPasswordMustChange %@",cmd, (chgPassSwitch.on)? @"TRUE":@"FALSE"];
	}	
	
	NSLog(@"cmd = %@",cmd);
	// send command
	[self.putController startPutController:cmd];
	self.tabBarController.selectedIndex = 1;
}

- (void)dealloc 
{
    [super dealloc];
}

@end

//
//  ZmprovController.m
//  Zimbra
//
//  Created by Qin An on 12/15/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import "ZimbraAppDelegate.h"
#import "ZmprovController.h"
#import "BoxMaker.h"
#import "GetController.h"
#import "PutController.h"
#import "SecurityController.h"

#define INDICATOR_VIEW	999


@implementation ZmprovController

@synthesize getController;
@synthesize putController;
@synthesize preOutput;

-(ZmprovController *) initWithTitle: (NSString *)title
{
	self = [super init];
	self.title = title;
	UIImage *tabIcon = [UIImage imageNamed:@"zmprov.gif"];
	CGSize sz = CGSizeMake(32,32);
	UIImage *resized_icon = scaleImage(tabIcon, sz);
	self.tabBarItem.image = resized_icon;
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
	
	UILabel *cmmtLabel = [[[UILabel alloc] initWithFrame:CGRectMake(0.0f, 0.0f, 220.0f, 40.0f)] retain];
	[cmmtLabel setText:@"Zmprov Command Shell"];
	[cmmtLabel setCenter:CGPointMake(180.0f, 25.0f)];
	[contentView addSubview:cmmtLabel];
	[cmmtLabel release];
	
	
	UILabel *cmdLabel = [[[UILabel alloc] initWithFrame:CGRectMake(0.0f, 20.0f, 120.0f, 40.0f)] retain];
	[cmdLabel setText:@"zmprov: "];
	[cmdLabel setCenter:CGPointMake(75.0f, 70.0f)];
	[contentView addSubview:cmdLabel];
	[cmdLabel release];
	
		
	// A command field
	cmdfield = [[[UITextField alloc] initWithFrame:CGRectMake(80.0f, 55.0f, 220.0f, 30.0f)] retain];
	[cmdfield setBorderStyle:UITextBorderStyleRoundedRect];
	
	cmdfield.placeholder = @"e.g. 'gaa'";
	cmdfield.autocorrectionType = UITextAutocorrectionTypeNo;
	cmdfield.autocapitalizationType = UITextAutocapitalizationTypeNone;
	cmdfield.returnKeyType = UIReturnKeyDone;
	cmdfield.clearButtonMode = UITextFieldViewModeWhileEditing;
	
	cmdfield.delegate = self;
	[contentView addSubview:cmdfield];
	[cmdfield release];

	// Add the TextView to show zmprov result abstract
	outputView = [[UITextView alloc] initWithFrame:CGRectMake(0.0f, 100.0f, 320.0f, 250.0f)];
	//outputView.autoresizingMask = UIViewAutoresizingFlexibleHeight;
	//outputView.backgroundColor = [UIColor grayColor];
	outputView.text = @"";
	//[outputView sizeToFit];
	outputView.userInteractionEnabled = NO; // disable keyboard input
	
	outputView.delegate = self;
	[contentView addSubview:outputView];
	[outputView release];

	// Add the progress indicator but do not start it
	inProgressing = NO;
    UIActivityIndicatorView *activityIndicator = [[UIActivityIndicatorView alloc] initWithFrame:CGRectMake(0.0f, 0.0f, 32.0f, 32.0f)];
	[activityIndicator setCenter:CGPointMake(160.0f, 208.0f)];
    [activityIndicator setActivityIndicatorViewStyle:UIActivityIndicatorViewStyleGray];
	activityIndicator.tag = INDICATOR_VIEW;
    [contentView addSubview:activityIndicator];
	[activityIndicator release];
	
	self.navigationController.navigationBar.tintColor = [UIColor darkGrayColor];
	

	//initate GetController
	NSString * kGetURLText = @"http://10.117.4.35:8080/mytest/zimbra-cluster-cmds-resp-zmprov.txt";
	GetController *kgetController = [[GetController alloc] initWithGetURL:kGetURLText];
	self.getController = kgetController;
	self.getController.viewController = self;
	
	// initiate PutController
	NSString * kPutURLText = @"http://10.117.4.35:8080/mytest/";
	NSString * kPutCmdFile = @"zimbra-cluster-cmds-req-zmprov.txt";
	PutController *pController = [[PutController alloc] initWithPutURL:kPutURLText cmdFile:kPutCmdFile];
	self.putController = pController;
}



// Implement viewDidLoad to do additional setup after loading the view.
- (void)viewDidLoad {
	
	
	self.navigationItem.leftBarButtonItem = [[[UIBarButtonItem alloc] 
											  initWithBarButtonSystemItem:UIBarButtonSystemItemCancel 
											  target:self action:@selector(cancel_Clicked:)] autorelease];
	
	self.navigationItem.rightBarButtonItem = [[[UIBarButtonItem alloc]
											   initWithTitle:@"Run" 
											   style:UIBarButtonItemStylePlain 
											   target:self 
											   action:@selector(CommitAccount)] autorelease];
	
	// load the preOutput value
	[self.getController startGetController];

}

-(void) viewWillAppear:(BOOL)animated {
	
	[super viewWillAppear:animated];
	
}

- (void) loadDataToUI: (NSString*) text
{
	NSString *newOutput = [NSString stringWithFormat:@"$ zmprov %@\n%@",cmdfield.text, text];
	if (preOutput == nil) {
		preOutput = [text copy];
	} else if (![text isEqualToString:preOutput]) {
		inProgressing = NO;
		//outputView.text = [NSString stringWithFormat:@"$ zmprov %@\n%@",cmdfield.text, text];
		preOutput = [text copy];
		outputView.text = newOutput;
		[self performIndicator:inProgressing];
	}

}

-(void) cancel_Clicked:(id)sender {
	
	outputView.text = @"";
	[self.navigationController dismissModalViewControllerAnimated:YES];
	self.tabBarController.selectedIndex = 1;
}

- (void) performIndicator: (BOOL) isStart
{
	UIActivityIndicatorView *activityIndicator = (UIActivityIndicatorView *)[self.view viewWithTag:INDICATOR_VIEW];
	if (!isStart) [activityIndicator stopAnimating]; else [activityIndicator startAnimating];
	//inProgressing = !inProgressing;
}

- (void) CommitAccount
{
	
	NSLog(@"cmd = %@",cmdfield.text);	
	if ([cmdfield.text length] < 1)
		return;
	
	
	// check execute auth
	[[[ZimbraAppDelegate sharedAppDelegate] getSecurityController] setMyViewController:self];
	[[[ZimbraAppDelegate sharedAppDelegate] getSecurityController] executeConfirm];

/*	
	NSString *cmd = [NSString stringWithFormat:@"zmprov %@ ",cmdfield.text];
	[self.putController startPutController:cmd];
	inProgressing = YES;

	outputView.text = @"Waiting ...";
	
	//start timer with interval 30s
	[self performIndicator:inProgressing];
	receiveTimer = [NSTimer scheduledTimerWithTimeInterval:30.0f
										 target:self
									   selector:@selector(updateOutput)
									   userInfo:nil
										repeats:YES];

	//[self.getController startGetController];	
	//self.tabBarController.selectedIndex = 1;
*/
}

// called by SecurityController after confirm password
- (void) CommitOperation
{
	NSString *cmd = [NSString stringWithFormat:@"zmprov %@ ",cmdfield.text];
	[self.putController startPutController:cmd];
	inProgressing = YES;
	
	outputView.text = @"Waiting ...";
	
	//start timer with interval 30s
	[self performIndicator:inProgressing];
	receiveTimer = [NSTimer scheduledTimerWithTimeInterval:30.0f
													target:self
												  selector:@selector(updateOutput)
												  userInfo:nil
												   repeats:YES];
	
	//[self.getController startGetController];	
	//self.tabBarController.selectedIndex = 1;
}

- (void) updateOutput
{
	NSLog(@"timer is on");	
	if (!inProgressing && receiveTimer != nil) {
		[receiveTimer invalidate]; // invalidate is also containing release
		return;
	}
	[self.getController startGetController];	
}

- (void)dealloc 
{
    [super dealloc];
}


@end

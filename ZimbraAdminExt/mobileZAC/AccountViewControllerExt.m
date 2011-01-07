//
//  AccountViewControllerExt.m
//  Zimbra
//
//  Created by Qin An on 12/16/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import "AccountViewControllerExt.h"


@implementation AccountViewControllerExt

@synthesize statusValue;
@synthesize displayname;
@synthesize emailValue;
@synthesize cosValue;
@synthesize adminValue;

-(AccountViewControllerExt *) initWithAcctInfo: (NSString *)title info: (NSMutableArray*) acctinfo
{
	self = [super init];
	self.title = title;
	[self initData:acctinfo];
	return self;
}

- (void) initData: (NSMutableArray*) acctinfo
{
	NSString *tmp = nil;
	
	if (acctinfo == nil) 
		return;
	tmp = [acctinfo objectAtIndex:2];
	if ([tmp length] > 0)
		displayname = tmp;
	else displayname = @"User Name";
	tmp = [acctinfo objectAtIndex:0];
	if ([tmp length] > 0)
		emailValue = tmp;
	else emailValue = @"user@domain.com";
	tmp = [acctinfo objectAtIndex:4];
	if ([tmp length] > 0)
		adminValue = tmp;
	else adminValue = @"NO";
	tmp = [acctinfo objectAtIndex:3];
	if ([tmp length] > 0)
		statusValue= tmp;
	else statusValue = @"N/A";
	
	cosValue = @"N/A";
	
	if ([displayname isEqualToString:@"User Name"] && ![emailValue isEqualToString:@"user@domain.com"])
		displayname = [[emailValue componentsSeparatedByString:@"@"] objectAtIndex:0];

}

// Implement loadView to create a view hierarchy programmatically.
- (void)loadView {
	// Load an application image and set it as the primary view
	contentView = [[UIView alloc] initWithFrame:[[UIScreen mainScreen] applicationFrame]];
	contentView.backgroundColor = [UIColor whiteColor];
	self.view = contentView;
	[contentView release];

	UIImage *image = [UIImage imageNamed:@"account_black.png"];
	//CGRect tRect2 = CGRectMake(35.0f, 15.0f, 60.0f, 60.0f);
	CGRect tRect2 = CGRectMake(35.0f, 55.0f, 60.0f, 60.0f);
	UIImageView *imageView = [[UIImageView alloc] initWithImage:image];
	imageView.frame = tRect2;
	[contentView addSubview:imageView];
	[imageView release];
	
	UITextView *nameView = [[UITextView alloc] initWithFrame:CGRectMake(0.0f, 0.0f, 240.0f, 60.0f)];
	//[nameView setCenter:CGPointMake(215.0f, 50.0f)];
	[nameView setCenter:CGPointMake(215.0f, 90.0f)];
	//nameView.autoresizingMask = UIViewAutoresizingFlexibleHeight;
	//nameView.backgroundColor = [UIColor grayColor];
	nameView.font = [UIFont fontWithName:@"Verdana" size:24.0f];;
	nameView.text = displayname;
	[nameView sizeToFit];
	nameView.userInteractionEnabled = NO; // disable keyboard input	
	nameView.delegate = self;
	[contentView addSubview:nameView];
	[nameView release];
	
	UILabel *emailLabel = [[[UILabel alloc] initWithFrame:CGRectMake(0.0f, 10.0f, 80.0f, 40.0f)] retain];
	[emailLabel setText:@"Email: "];
	//[emailLabel setCenter:CGPointMake(80.0f, 105.0f)];
	[emailLabel setCenter:CGPointMake(80.0f, 145.0f)];
	[contentView addSubview:emailLabel];
	[emailLabel release];

	UITextView *emailView = [[UITextView alloc] initWithFrame:CGRectMake(0.0f, 0.0f, 220.0f, 40.0f)];
	//[emailView setCenter:CGPointMake(200.0f, 105.0f)];
	[emailView setCenter:CGPointMake(200.0f, 145.0f)];
	emailView.font = [UIFont fontWithName:@"Verdana" size:16.0f];
	emailView.backgroundColor = [UIColor grayColor];
	emailView.text = emailValue;
	[emailView sizeToFit];
	emailView.userInteractionEnabled = NO; 
	emailView.delegate = self;
	[contentView addSubview:emailView];
	[emailView release];
	
	UILabel *cosLabel = [[[UILabel alloc] initWithFrame:CGRectMake(0.0f, 10.0f, 160.0f, 40.0f)] retain];
	[cosLabel setText:@"Class of Service: "];
	//[cosLabel setCenter:CGPointMake(120.0f, 155.0f)];
	[cosLabel setCenter:CGPointMake(120.0f, 195.0f)];
	[contentView addSubview:cosLabel];
	[cosLabel release];
	
	UITextView *cosView = [[UITextView alloc] initWithFrame:CGRectMake(0.0f, 0.0f, 140.0f, 40.0f)];
	//[cosView setCenter:CGPointMake(240.0f, 155.0f)];
	[cosView setCenter:CGPointMake(240.0f, 195.0f)];
	cosView.font = [UIFont fontWithName:@"Verdana" size:16.0f];
	cosView.backgroundColor = [UIColor grayColor];
	cosView.text = cosValue;
	[cosView sizeToFit];
	cosView.userInteractionEnabled = NO; 
	cosView.delegate = self;
	[contentView addSubview:cosView];
	[cosView release];
	
	UILabel *adminLabel = [[[UILabel alloc] initWithFrame:CGRectMake(0.0f, 10.0f, 160.0f, 40.0f)] retain];
	[adminLabel setText:@"Administrator: "];
	//[adminLabel setCenter:CGPointMake(120.0f, 205.0f)];
	[adminLabel setCenter:CGPointMake(120.0f, 245.0f)];
	[contentView addSubview:adminLabel];
	[adminLabel release];
	
	UITextView *adminView = [[UITextView alloc] initWithFrame:CGRectMake(0.0f, 0.0f, 100.0f, 40.0f)];
	//[adminView setCenter:CGPointMake(200.0f, 205.0f)];
	[adminView setCenter:CGPointMake(200.0f, 245.0f)];
	adminView.font = [UIFont fontWithName:@"Verdana" size:16.0f];
	adminView.backgroundColor = [UIColor grayColor];
	adminView.text = adminValue;
	[adminView sizeToFit];
	adminView.userInteractionEnabled = NO; 
	adminView.delegate = self;
	[contentView addSubview:adminView];
	[adminView release];
	
	UILabel *statusLabel = [[[UILabel alloc] initWithFrame:CGRectMake(0.0f, 10.0f, 160.0f, 40.0f)] retain];
	[statusLabel setText:@"Status: "];
	//[statusLabel setCenter:CGPointMake(120.0f, 255.0f)];
	[statusLabel setCenter:CGPointMake(120.0f, 295.0f)];
	[contentView addSubview:statusLabel];
	[statusLabel release];

	UITextView *statusView = [[UITextView alloc] initWithFrame:CGRectMake(0.0f, 0.0f, 100.0f, 40.0f)];
	//[statusView setCenter:CGPointMake(200.0f, 255.0f)];
	[statusView setCenter:CGPointMake(200.0f, 295.0f)];
	statusView.font = [UIFont fontWithName:@"Verdana" size:16.0f];
	statusView.backgroundColor = [UIColor grayColor];
	statusView.text = statusValue;
	[statusView sizeToFit];
	statusView.userInteractionEnabled = NO; 
	statusView.delegate = self;
	[contentView addSubview:statusView];
	[statusView release];
}



// Implement viewDidLoad to do additional setup after loading the view.
- (void)viewDidLoad {
	
	self.navigationItem.rightBarButtonItem = [[[UIBarButtonItem alloc]
											   initWithTitle:@"Edit" 
											   style:UIBarButtonItemStylePlain 
											   target:self 
											   action:@selector(EditAccount)] autorelease];
	
	//self.navigationController.navigationBar.tintColor = [UIColor blackColor];
}

- (void) EditAccount
{
	self.tabBarController.selectedIndex = 3;
}

- (void)dealloc 
{
    [super dealloc];
}

@end

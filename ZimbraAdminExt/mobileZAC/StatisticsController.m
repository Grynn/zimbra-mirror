//
//  StatisticsController.m
//  Zimbra
//
//  Created by Qin An on 12/21/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import "StatisticsController.h"
#import "StatusListViewController.h"
#import "GetController.h"


@implementation StatisticsController

@synthesize graphView;
@synthesize serverId;
@synthesize statData;
@synthesize getController;


-(StatisticsController *) initWithTitle: (NSString *)title serverId: (NSString*) server
{
	self = [super init];
	self.title = title;
	self.serverId = server;
	return self;
}



// Implement loadView to create a view hierarchy programmatically, without using a nib.
- (void)loadView {
	UIImageView *contentView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 360, 420)];//[[UIScreen mainScreen] applicationFrame]];
	//contentView.backgroundColor = [UIColor whiteColor];
	self.view = contentView;
	[contentView release];
	
	[contentView setAutoresizingMask:(UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight)];
		
	graphView = [[ZimGraphView alloc] initWithFrame:CGRectMake(0, 70, 360, 320)];//;self.view.bounds];
	[graphView setAutoresizingMask:(UIViewAutoresizingFlexibleBottomMargin | UIViewAutoresizingFlexibleRightMargin)];
	[contentView addSubview:graphView];
	graphView.dataSource = self;
	
	UILabel *yLabel = [[[UILabel alloc] initWithFrame:CGRectMake(0.0f, 10.0f, 30.0f, 30.0f)] retain];
	[yLabel setText:@"(%)"];
	yLabel.textColor = [UIColor whiteColor];
	yLabel.backgroundColor = [UIColor blackColor];
	[yLabel setFont:[UIFont fontWithName:@"Courier" size:14]];
	[yLabel setCenter:CGPointMake(20.0f, 225.0f)];
	[contentView addSubview:yLabel];
	[yLabel release];

	UILabel *memLabel = [[[UILabel alloc] initWithFrame:CGRectMake(0.0f, 10.0f, 60.0f, 30.0f)] retain];
	[memLabel setText:@"Memory"];
	memLabel.textColor = RGB(80, 180, 50);//[UIColor greenColor];
	memLabel.backgroundColor = [UIColor blackColor];
	[memLabel setFont:[UIFont fontWithName:@"Courier" size:16]];
	[memLabel setCenter:CGPointMake(130.0f, 65.0f)];
	[contentView addSubview:memLabel];
	[memLabel release];

	UILabel *memLine = [[[UILabel alloc] initWithFrame:CGRectMake(0.0f, 10.0f, 30.0f, 2.0f)] retain];
	[memLine setText:@""];
	//memLabel.textColor = [UIColor greenColor];
	memLine.backgroundColor = RGB(80, 180, 50);//[UIColor greenColor];
	//[memLabel setFont:[UIFont fontWithName:@"Courier" size:16]];
	[memLine setCenter:CGPointMake(80.0f, 67.0f)];
	[contentView addSubview:memLine];
	[memLine release];

	UILabel *cpuLabel = [[[UILabel alloc] initWithFrame:CGRectMake(0.0f, 10.0f, 60.0f, 30.0f)] retain];
	[cpuLabel setText:@"CPU"];
	cpuLabel.textColor = RGB(5, 141, 191);
	cpuLabel.backgroundColor = [UIColor blackColor];
	[cpuLabel setFont:[UIFont fontWithName:@"Courier" size:16]];
	[cpuLabel setCenter:CGPointMake(280.0f, 65.0f)];
	[contentView addSubview:cpuLabel];
	[cpuLabel release];
	
	UILabel *cpuLine = [[[UILabel alloc] initWithFrame:CGRectMake(0.0f, 10.0f, 30.0f, 2.0f)] retain];
	[cpuLine setText:@""];
	//cpuLine.textColor = [UIColor greenColor];
	cpuLine.backgroundColor = RGB(5, 141, 191);
	//[cpuLine setFont:[UIFont fontWithName:@"Courier" size:16]];
	[cpuLine setCenter:CGPointMake(230.0f, 67.0f)];
	[contentView addSubview:cpuLine];
	[cpuLine release];
	
	//initate GetController
	NSString * kGetURLText = [NSString stringWithFormat:@"http://10.117.4.35:8080/mytest/%@-syslog.txt",serverId];
	GetController *kgetController = [[GetController alloc] initWithGetURL:kGetURLText];
	self.getController = kgetController;
	self.getController.viewController = self;
	//[self.getController startGetController];
}


- (void)viewDidLoad {
    [super viewDidLoad];
	
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
	
	NSNumberFormatter *numberFormatter = [NSNumberFormatter new];
	[numberFormatter setNumberStyle:NSNumberFormatterDecimalStyle];
	[numberFormatter setMinimumFractionDigits:0];
	[numberFormatter setMaximumFractionDigits:0];
	
	self.graphView.yValuesFormatter = numberFormatter;
	
	NSDateFormatter *dateFormatter = [NSDateFormatter new];
	[dateFormatter setTimeStyle:NSDateFormatterNoStyle];
	[dateFormatter setDateStyle:NSDateFormatterShortStyle];
	//qin
	[dateFormatter setDateFormat:@"hh:mm"];
	
	self.graphView.xValuesFormatter = dateFormatter;
	
	[dateFormatter release];        
	[numberFormatter release];
	
	self.graphView.backgroundColor = [UIColor blackColor];
	
	self.graphView.drawAxisX = YES;
	self.graphView.drawAxisY = YES;
	self.graphView.drawGridX = YES;
	self.graphView.drawGridY = YES;
	
	self.graphView.xValuesColor = [UIColor whiteColor];
	self.graphView.yValuesColor = [UIColor whiteColor];
	
	self.graphView.gridXColor = [UIColor whiteColor];
	self.graphView.gridYColor = [UIColor whiteColor];
	
	self.graphView.drawInfo = NO;
	self.graphView.info = @"Load";
	self.graphView.infoColor = [UIColor whiteColor];
	
	// update the plot data
	
	//When you need to update the data, make this call:
	
	//[self.graphView reloadData];
	
	self.navigationItem.rightBarButtonItem = [[[UIBarButtonItem alloc]
											   initWithTitle:@"Services" 
											   style:UIBarButtonItemStylePlain 
											   target:self 
											   action:@selector(showServices)] autorelease];
	[self.getController startGetController];
	
}

- (void) showServices
{
	// show service list
	[[self navigationController] pushViewController:[[StatusListViewController alloc] 
													 //initWithTitle:@"Services"
													 initWithTitleItems:@"Services" 
													 itemlist:nil
													 valuelist:nil 
													 parent:serverId
													 getHandler:nil
													 ]  animated:YES];
	// refresh 
	//[self.getController startGetController];
}

- (void) loadDataToUI: (NSString*) text
{
	statData = [text copy];
	[self.graphView reloadData];
}


/*
 - (void)viewWillAppear:(BOOL)animated {
 [super viewWillAppear:animated];
 }
 */
/*
 - (void)viewDidAppear:(BOOL)animated {
 [super viewDidAppear:animated];
 }
 */
/*
 - (void)viewWillDisappear:(BOOL)animated {
 [super viewWillDisappear:animated];
 }
 */
/*
 - (void)viewDidDisappear:(BOOL)animated {
 [super viewDidDisappear:animated];
 }
 */


 // Override to allow orientations other than the default portrait orientation.
 - (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
	 // Return YES for supported orientations.
	 //return (interfaceOrientation == UIInterfaceOrientationPortrait);
	 return YES;
 }


- (void)didReceiveMemoryWarning {
	// Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
	
	// Release any cached data, images, etc that aren't in use.
}

- (void)viewDidUnload {
	// Release anything that can be recreated in viewDidLoad or on demand.
	// e.g. self.myOutlet = nil;
}


- (void)dealloc {
	[graphView release];
	graphView = nil;
	
    [super dealloc];
}

#pragma mark protocol ZimGraphViewDataSource

- (NSUInteger)graphViewNumberOfPlots:(ZimGraphView *)graphView {
	/* Return the number of plots you are going to have in the view. 1+ */
	if (statData == nil || [statData length] < 1)
		return 0;
	
	NSArray *serverStats = [statData componentsSeparatedByString: @"\n"];
	NSString *oneStat = nil;
	//if ([serverStats count] > 0)
		oneStat = [serverStats objectAtIndex:0];
	//else oneStat = serverStats;
	
	NSInteger dataSep = [[oneStat componentsSeparatedByString: @","] count];
	return (dataSep - 1);
}

- (NSArray *)graphViewXValues:(ZimGraphView *)graphView {
	/* An array of objects that will be further formatted to be displayed on the X-axis.
	 The number of elements should be equal to the number of points you have for every plot. */
/*
	if (statData == nil || [statData length] < 1) {
		array = [[NSMutableArray alloc] initWithCapacity:100];
		for ( int i = 0 ; i < 100 ; i ++ ) {
			[array addObject:[NSNumber numberWithInt:i]];	
		}

	} else {
		
	}
*/
	NSMutableArray *array = [[NSMutableArray alloc] init];
	if (statData == nil || [statData length] < 1)
		return array;
	
	NSArray *serverStats = [statData componentsSeparatedByString: @"\n"];
	NSString *oneStat = nil;
	NSDateFormatter *dateFormat = [[NSDateFormatter alloc] init];
	[dateFormat setLocale:[[[NSLocale alloc] initWithLocaleIdentifier:@"en_US_POSIX"] autorelease]];
	[dateFormat setDateFormat:@"yyyy-MM-dd HH:mm:ss"];

	for (int i = 0; i < [serverStats count]; i++) {
		oneStat = [serverStats objectAtIndex:i];
		NSArray *items = [oneStat componentsSeparatedByString:@","];
		//NSArray *timestamp = [[items objectAtIndex:0] componentsSeparatedByString:@" "];
		//if ([timestamp count] < 2) continue;
		//NSDate *date = [dateFormat dateFromString:[timestamp objectAtIndex:1]]; 
		NSDate *date = [dateFormat dateFromString:[items objectAtIndex:0]];
		//[array addObject:[timestamp objectAtIndex:1]];	
		if (date == nil) continue;
		[array addObject:date];
	}

	//[array description];
	return array;
}

- (NSArray *)graphView:(ZimGraphView *)graphView yValuesForPlot:(NSUInteger)plotIndex {
	/* Return the values for a specific graph. Each plot is meant to have equal number of points.
	 And this amount should be equal to the amount of elements you return from graphViewXValues: method. */
	NSMutableArray *array = [[NSMutableArray alloc] init];
	//array = [[NSMutableArray alloc] initWithCapacity:101];
	if (statData == nil || [statData length] < 1)
		return array;
	
	NSArray *serverStats = [statData componentsSeparatedByString: @"\n"];
	NSString *oneStat = nil;
	
	for (int i = 0; i < [serverStats count]; i++) {
		oneStat = [serverStats objectAtIndex:i];
		NSArray *items = [oneStat componentsSeparatedByString:@","];
		if ([items count] < 3) continue;
		switch (plotIndex) {
			default:
				for ( int i = 0 ; i <= 50 ; i ++ ) {
					[array addObject:[NSNumber numberWithInt:i*i]];	// y = x*x		
				}
				break;
			case 0:
				//if ([items count] < 2) break;
				[array addObject:[[items objectAtIndex:1] copy]];
				break;
			case 1:
				//if ([items count] < 3) break;
				[array addObject:[[items objectAtIndex:2] copy]];
				break;
		}
		 
	}

	return array;
}



@end

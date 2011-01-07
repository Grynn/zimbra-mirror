//
//  ZimbraAppDelegate.m
//  Zimbra
//
//  Created by Qin An on 12/11/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import "ZimbraAppDelegate.h"
#import "math.h"
#import "ServerListViewController.h"
#import "BoxMaker.h"
#import "BrightnessController.h"
#import "AccountListViewController.h"
#import "NewAccountController.h"
#import "AccountListViewControllerExt.h"
#import "ChangePasswordController.h"
#import "ZmprovController.h"
#import "AccountStatusController.h"
#import "SecurityController.h"
#import "RotatingTabBarController.h"
#import "DomainListViewController.h"
#import "DLListViewController.h"
#import "CosListViewController.h"
#import "SettingViewController.h"


@implementation ZimbraAppDelegate

@synthesize securityController;
@synthesize apnBadge;
@synthesize controllers;

- (void)tabBarController:(UITabBarController *)theTabBarController didSelectViewController:(UIViewController *)viewController {
    NSUInteger indexOfTab = [theTabBarController.viewControllers indexOfObject:viewController];
    NSLog(@"Tab index = %u", indexOfTab);
	
	// store the tab sequence
	// begin from 1, zero is null
	previousTab = currentTab;
	currentTab = indexOfTab + 1;
	
	NSLog(@"previousTab = %u, currentTab = %u", previousTab, currentTab);
	
	if (indexOfTab == 0) {
		ServerListViewController *pListController = (ServerListViewController *) viewController;
		pListController.tabBarItem.badgeValue = 0;
		self.apnBadge = nil;
		[UIApplication sharedApplication].applicationIconBadgeNumber = 0;
	}
}

- (void)applicationDidFinishLaunching:(UIApplication *)application {	
	NSLog(@"applicationDidFinishLaunching enter");
	UIWindow *window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
	
	// Create the array of UIViewControllers
	controllers = [[NSMutableArray alloc] init];
	
	
	
	// Status List Controller
	ServerListViewController *sControl = [[ServerListViewController alloc] initWithTitle:@"Servers"];
	UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:sControl];
	nav.navigationBar.barStyle = UIBarStyleBlackTranslucent;
	sControl.tabBarItem.badgeValue = apnBadge;
	[controllers addObject:nav];
	[sControl release];
	[nav release];	
	
	
	// Account List Controller
	AccountListViewControllerExt *aControl = [[AccountListViewControllerExt alloc] initWithTitle:@"Accounts"];
	nav = [[UINavigationController alloc] initWithRootViewController:aControl];
	nav.navigationBar.barStyle = UIBarStyleBlackTranslucent;
	[controllers addObject:nav];
	[aControl release];
	[nav release];


	// Domain List Controller
	DomainListViewController *dControl = [[DomainListViewController alloc] initWithTitle:@"Domain"];
	nav = [[UINavigationController alloc] initWithRootViewController:dControl];
	nav.navigationBar.barStyle = UIBarStyleBlackTranslucent;
	[controllers addObject:nav];
	[dControl release];
	[nav release];

	// DL List Controller
	DLListViewController *dlControl = [[DLListViewController alloc] initWithTitle:@"DL"];
	nav = [[UINavigationController alloc] initWithRootViewController:dlControl];
	nav.navigationBar.barStyle = UIBarStyleBlackTranslucent;
	[controllers addObject:nav];
	[dlControl release];
	[nav release];
	
	// COS List Controller
	CosListViewController *cosControl = [[CosListViewController alloc] initWithTitle:@"COS"];
	nav = [[UINavigationController alloc] initWithRootViewController:cosControl];
	nav.navigationBar.barStyle = UIBarStyleBlackTranslucent;
	[controllers addObject:nav];
	[cosControl release];
	[nav release];
/*	
	// New Account Controller
	NewAccountController *nControl = [[NewAccountController alloc] initWithTitle:@"New Account" loadData:FALSE];
	nav = [[UINavigationController alloc] initWithRootViewController:nControl];
	nav.navigationBar.barStyle = UIBarStyleBlackTranslucent;
	[controllers addObject:nav];
	[nControl release];
	[nav release];
	
	// Edit Account Controller
	NewAccountController *eControl = [[NewAccountController alloc] initWithTitle:@"Edit Account" loadData:TRUE];
	nav = [[UINavigationController alloc] initWithRootViewController:eControl];
	nav.navigationBar.barStyle = UIBarStyleBlackTranslucent;
	[controllers addObject:nav];
	[eControl release];
	[nav release];
*/	
	
	// Reset Password Controller
	ChangePasswordController *cControl = [[ChangePasswordController alloc] initWithTitle:@"Reset Password"];
	nav = [[UINavigationController alloc] initWithRootViewController:cControl];
	nav.navigationBar.barStyle = UIBarStyleBlackTranslucent;
	[controllers addObject:nav];
	[cControl release];
	[nav release];
	
	// ZmProv Command Controller
	ZmprovController *zControl = [[ZmprovController alloc] initWithTitle:@"Zmprov"];
	nav = [[UINavigationController alloc] initWithRootViewController:zControl];
	nav.navigationBar.barStyle = UIBarStyleBlackTranslucent;
	[controllers addObject:nav];
	[zControl release];
	[nav release];
	
	/*
	// ZmProv Command Controller
	AccountStatusController *tControl = [[AccountStatusController alloc] initWithTitle:@"Account Status"];
	nav = [[UINavigationController alloc] initWithRootViewController:tControl];
	nav.navigationBar.barStyle = UIBarStyleBlackTranslucent;
	[controllers addObject:nav];
	[tControl release];
	[nav release];
	*/
	
	// ZmProv Command Controller
	SettingViewController *setControl = [[SettingViewController alloc] initWithTitle:@"Setting"];
	nav = [[UINavigationController alloc] initWithRootViewController:setControl];
	nav.navigationBar.barStyle = UIBarStyleBlackTranslucent;
	[controllers addObject:nav];
	[setControl release];
	[nav release];
	
	/*
	 for (int i = 0; i < 6; i++) {
	 BrightnessController *bControl = [[BrightnessController alloc] initWithBrightness:i];
	 UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:bControl];
	 nav.navigationBar.barStyle = UIBarStyleBlackTranslucent;
	 [controllers addObject:nav];
	 [bControl release];
	 [nav release];
	 }
	 */
	
	
	// Create the toolbar and add the view controllers
	//UITabBarController *tbarController = [[UITabBarController alloc] init];
	RotatingTabBarController *tbarController = [[UITabBarController alloc] init];
	tbarController.viewControllers = controllers;
	tbarController.customizableViewControllers = controllers;
	tbarController.delegate = self;
	tbarController.selectedIndex = 1;
	
	// Set up the window
	[window addSubview:tbarController.view];
	[window makeKeyAndVisible];
	
	[controllers release];

	// APN setting
	NSLog(@"Registering for push notifications...");    
    [[UIApplication sharedApplication] 
	 registerForRemoteNotificationTypes:
	 (UIRemoteNotificationTypeAlert | 
	  UIRemoteNotificationTypeBadge | 
	  UIRemoteNotificationTypeSound)];
	
	
	//login check
	securityController = [SecurityController alloc];
	[securityController loginMain];
}

// Apple Push Notification

- (void)application:(UIApplication *)app didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken { 
	
    NSString *str = [NSString 
					 stringWithFormat:@"Device Token=%@",deviceToken];
    NSLog(str);
	
}

- (void)application:(UIApplication *)app didFailToRegisterForRemoteNotificationsWithError:(NSError *)err { 
	
    NSString *str = [NSString stringWithFormat: @"Error: %@", err];
    NSLog(str);    
	
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo {
	
    for (id key in userInfo) {
        NSLog(@"key: %@, value: %@", key, [userInfo objectForKey:key]);
    }
	//UIViewController *viewController = [self.tabBarController.viewControllers objectAtIndex:0];
	//if (viewController && [viewController isKindOfClass:[ServerListViewController class]]) {
	//	ServerListViewController *pListController = (ServerListViewController *) viewController;
	//	pListController.tabBarItem.badgeValue = @"1";
	//}
	//if (self.controllers && [self.controllers count] > 1) {
	//	UIViewController *viewController = [self.controllers objectAtIndex:0];
	//	if (viewController && [viewController isKindOfClass:[ServerListViewController class]]) {
	//		ServerListViewController *pListController = (ServerListViewController *) viewController;
	//		pListController.tabBarItem.badgeValue = @"1";
	//	}
	//}
	
	self.apnBadge = @"1";
}

// set current account info
- (void) setCurrentAccount: (NSMutableArray*) info
{
	currentAcct = info;
}

// get current account info
- (NSMutableArray*) getCurrentAccount
{
	return currentAcct;
}

// set all account info
- (void) setAllAccount: (NSMutableArray*) info
{
	allAcct = info;
}

// get all account info
- (NSMutableArray*) getAllAccount
{
	return allAcct;
}

// get security controller
- (SecurityController*) getSecurityController
{
	return securityController;
}

// share delegate
+ (ZimbraAppDelegate *)sharedAppDelegate
{
    return (ZimbraAppDelegate *) [UIApplication sharedApplication].delegate;
}

@end

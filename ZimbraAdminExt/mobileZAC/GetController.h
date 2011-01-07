//
//  GetController.h
//  Zimbra
//
//  Created by Qin An on 12/13/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import <Foundation/Foundation.h>

@class NetworkController;
@class ServerListViewController;
@class StatusListViewController;

@interface GetController : NSObject {
	NSURLConnection *           connection;
    NSString *                  filePath;
    NSOutputStream *            fileStream;
	
	NSMutableArray *			statNames;
	NSMutableArray *			statValues;
	NSMutableArray *			statIndex;
	NSString *					respContent;
	
	NSString				*badgeStatus;
	NSString				*resetService;
	NSString *				getURL;
	
	NetworkController *		networkController;
	UIViewController *	viewController;
}

@property (nonatomic, readonly) BOOL              isReceiving;
@property (nonatomic, retain)   NSURLConnection * connection;
@property (nonatomic, copy)     NSString *        filePath;
@property (nonatomic, retain)   NSOutputStream *  fileStream;

@property (nonatomic, retain) NSMutableArray	*statNames;
@property (nonatomic, retain) NSMutableArray	*statValues;
@property (nonatomic, retain) NSMutableArray	*statIndex;
@property (nonatomic, retain) NSString			*respContent;

@property (nonatomic, retain) NSString				*badgeStatus;
@property (nonatomic, retain) NSString				*resetService;

@property (nonatomic, retain) NSString				*getURL;


@property (retain, nonatomic) NetworkController *		networkController;
@property (retain, nonatomic) UIViewController *	viewController;

- (void) startGetController;
- (void) _setAppBadge: (NSString *) nextStats;
-(GetController *) initWithGetURL: (NSString *)getUrl;

@end

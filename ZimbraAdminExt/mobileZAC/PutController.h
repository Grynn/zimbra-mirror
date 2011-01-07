//
//  PutController.h
//  Zimbra
//
//  Created by Qin An on 12/13/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import <Foundation/Foundation.h>

@class NetworkController;

@interface PutController : NSObject {
    NSURLConnection *           connection;
    NSInputStream *             fileStream;
	
	NetworkController *			networkController;
	
	NSString *					putURL;
	NSString *					cmdFile;
}

@property (nonatomic, readonly) BOOL              isSending;
@property (nonatomic, retain)   NSURLConnection * connection;
@property (nonatomic, retain)   NSInputStream *   fileStream;


@property (nonatomic, retain)   NSString *			putURL;
@property (nonatomic, retain)   NSString *			cmdFile;

@property (retain, nonatomic) NetworkController *		networkController;

-(PutController *) initWithPutURL: (NSString *)putUrl cmdFile: (NSString *) cmdFileName;
- (void) startPutController: (NSString *) text;

@end

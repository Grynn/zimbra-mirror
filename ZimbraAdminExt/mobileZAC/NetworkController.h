//
//  NetworkController.h
//  Zimbra
//
//  Created by Qin An on 12/13/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface NetworkController : NSObject {

	NSInteger               networkingCount;
	
}

@property (nonatomic, assign) NSInteger             networkingCount;

- (NSURL *)smartURLForString:(NSString *)str;
- (NSString *)pathForTemporaryFileWithPrefix:(NSString *)prefix;
- (void)didStartNetworking;
- (void)didStopNetworking;

@end

//
//  GetController.m
//  Zimbra
//
//  Created by Qin An on 12/13/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import "GetController.h"
#import "NetworkController.h"
#import "ServerListViewController.h"
#import "AccountListViewControllerExt.h"
#import "AccountStatusController.h"
#import "ZmprovController.h"
#import "StatisticsController.h"
#import "StatusListViewController.h"
#import "DomainListViewController.h"
#import "DLListViewController.h"
#import "CosListViewController.h"


@interface NSString (HTTPExtensions)

- (BOOL)isHTTPContentType:(NSString *)prefixStr;

@end

@implementation NSString (HTTPExtensions)

- (BOOL)isHTTPContentType:(NSString *)prefixStr
{
    BOOL    result;
    NSRange foundRange;
    
    result = NO;
    
    foundRange = [self rangeOfString:prefixStr options:NSAnchoredSearch | NSCaseInsensitiveSearch];
    if (foundRange.location != NSNotFound) {
        assert(foundRange.location == 0);            // because it's anchored
        if (foundRange.length == self.length) {
            result = YES;
        } else {
            unichar nextChar;
            
            nextChar = [self characterAtIndex:foundRange.length];
            result = nextChar <= 32 || nextChar >= 127 || (strchr("()<>@,;:\\<>/[]?={}", nextChar) != NULL);
        }
		/*
		 From RFC 2616:
		 
		 token          = 1*<any CHAR except CTLs or separators>
		 separators     = "(" | ")" | "<" | ">" | "@"
		 | "," | ";" | ":" | "\" | <">
		 | "/" | "[" | "]" | "?" | "="
		 | "{" | "}" | SP | HT
		 
		 media-type     = type "/" subtype *( ";" parameter )
		 type           = token
		 subtype        = token
		 */
    }
    return result;
}

@end


@implementation GetController

@synthesize connection;
@synthesize filePath;
@synthesize fileStream;

@synthesize statNames;
@synthesize statValues;
@synthesize statIndex;
@synthesize respContent;
@synthesize getURL;
@synthesize badgeStatus;
@synthesize resetService;

@synthesize networkController;
@synthesize viewController;

static NSString * kDefaultGetURLText = @"http://10.117.4.35:8080/mytest/zimbra-cluster-stats.txt";

-(GetController *) initWithGetURL: (NSString *)getUrl
{
	self = [super init];
	if (getUrl != nil) {
		self.getURL = getUrl;
	} else {
		self.getURL = kDefaultGetURLText;
	}
	
	return self;
}

- (void) startGetController
{
	if (self.networkController == nil) {
		NetworkController * netController = [NetworkController alloc];
		self.networkController = netController;
		[netController release];
	}
	if (self.isReceiving) {
        //[self _stopReceiveWithStatus:@"Cancelled"];
		return;
    } else {
        [self _startReceive];
    }
}


- (void)_receiveDidStart
{

	NSLog(@"GET: Receiving");
    //[NetworkController didStartNetworking];
	[self.networkController didStartNetworking];
}


- (void)_receiveDidStopWithStatus:(NSString *)statusString
{
    if (statusString == nil) {
        assert(self.filePath != nil);
		statusString = [NSString stringWithContentsOfFile:self.filePath];
        NSLog(@"GET: succeeded");
		
		// check and set badge
		//[self _setAppBadge: statusString];
    }
    NSLog(statusString);
    //[self.activityIndicator stopAnimating];
	// consume the input data from remote server
	if(statusString) {
		respContent = statusString;
		NSArray *chunks = [statusString componentsSeparatedByString: @"##"];
		self.statNames = [[NSMutableArray alloc] init];
		self.statValues = [[NSMutableArray alloc] init];
		self.statIndex = [[NSMutableArray alloc] init];
		NSNumber *indent_zero = [[NSNumber alloc] initWithInt:0];
		NSNumber *stat_one = [[NSNumber alloc] initWithInt:1];
		NSNumber *indent_two = [[NSNumber alloc] initWithInt:2];
		
		for (int i = 0; i < [chunks count]; i++) {
			NSString *serverStr = [chunks objectAtIndex:i];	
			
			NSArray *chunkitems = [serverStr componentsSeparatedByString: @"#"];
			if ([chunkitems count] > 1) {
				//NSString *tmp = [chunkitems objectAtIndex:0];
				//[self.statNames addObject:[NSString stringWithFormat:@"%s", tmp]];
				id obj = [[chunkitems objectAtIndex:0] copy];
				[self.statNames addObject:obj];
				[self.statValues addObject:stat_one];
				//[self.statIndex addObject:[NSNumber numberWithInt:0]];
				[self.statIndex addObject: indent_zero];
				//[self.statIndex insertObject: indent_zero atIndex:idx++];
				NSString *serverStatsStr = [chunkitems objectAtIndex:1];
				NSArray *serverStats = [serverStatsStr componentsSeparatedByString: @"|"];
				for (int j = 0; j < [serverStats count]; j++) {
					NSString *serverStatsItems = [serverStats objectAtIndex:j];
					NSArray *items = [serverStatsItems componentsSeparatedByString: @":"];
					//NSNumber *statstr = [items objectAtIndex:1];
					if ([items count] > 1) {
						//NSNumber *statstr = [items objectAtIndex:1];
						//NSString *sname = [items objectAtIndex:0];
						//if ([sname isEqualToString:server0name] || [sname isEqualToString:server1name]
						//	|| [sname isEqualToString:server2name]) {
						//	continue;
						//}
						//NSLog([items objectAtIndex:0]);
						id tmp1 = [[items objectAtIndex:0] copy];
						//[self.statNames addObject:[NSString stringWithFormat:@"%s", tmp1]];
						[self.statNames addObject:tmp1];
						id tmp2 = [[items objectAtIndex:1] copy];
						[self.statValues addObject:tmp2];
						//[self.statIndex addObject:[NSNumber numberWithInt:2]];
						[self.statIndex addObject: indent_two];
						//[self.statIndex insertObject: indent_two atIndex:idx++];
					}
				}
				
			}
			
		}
		//test the statNames
		
		//for (int i = 0; i < [self.statNames count]; i++) {
		//	NSLog([self.statNames objectAtIndex:i]);
		//}
		//NSLog([self.statNames description]);
		//NSLog([self.statNames description]);
		//for (int j = 0; j < [self.statIndex count]; j++) {
		//	NSLog([self.statIndex objectAtIndex:j]);
		//}
		//NSLog([self.statIndex description]);
		
		//self.listData = self.statNames;
		//[self loadView];
		[self _addDataInUIView];
	}
	
    //[NetworkController didStopNetworking];
	[self.networkController didStopNetworking];
}

- (void) _addDataInUIView
{
	if ([self.viewController isKindOfClass:[ZmprovController class]]) {
		ZmprovController *pViewController = (ZmprovController *) viewController;
		[pViewController loadDataToUI:respContent];
	} else 	if ([self.viewController isKindOfClass:[StatisticsController class]]) {
		StatisticsController *pViewController = (StatisticsController *) viewController;
		[pViewController loadDataToUI:respContent];
	}
	
	/* The following controllers need to check corrent parameters */
	
	int nc = [self.statNames count];
	int ic = [self.statIndex count];
	if (!nc || !ic) {
		return;
	}
	
	if ([self.viewController isKindOfClass:[ServerListViewController class]]) {
		ServerListViewController *pListController = (ServerListViewController *) viewController;

		if (pListController) {
			if ([pListController.tableTitles count]) {
				//[self.viewController.tableTitles release];
				//self.viewController.tableTitles = [[NSMutableArray alloc] init];
				[pListController.tableTitles removeAllObjects];
			}
			for (int i = 0; i < nc && i < ic; i++) {
				id name = [[self.statNames objectAtIndex:i] copy];
				NSInteger idx = [[self.statIndex objectAtIndex:i] intValue];
				if (idx == 0) 
					[pListController.tableTitles addObject:name];
			}
			[pListController.tableView reloadData];
		}
		
	} else 	if ([self.viewController isKindOfClass:[AccountListViewControllerExt class]]) {
		AccountListViewControllerExt *pViewController = (AccountListViewControllerExt *) viewController;
		
		if (pViewController) {
			if ([pViewController.tableTitles count]) {
				[pViewController.tableTitles removeAllObjects];
			}
			for (int i = 0; i < nc && i < ic; i++) {
				id name = [[self.statNames objectAtIndex:i] copy];
				NSInteger idx = [[self.statIndex objectAtIndex:i] intValue];
				if (idx == 0) 
					[pViewController.tableTitles addObject:name];
			}
			//NSLog([self.statNames description]);
			//NSLog([self.statValues description]);
			//NSLog([self.statIndex description]);
			[pViewController buildAccountDataTable];
			[pViewController buildSearchArrayFrom:@""];
		}
		
	} else 	if ([self.viewController isKindOfClass:[AccountStatusController class]]) {
		AccountStatusController *pViewController = (AccountStatusController *) viewController;
		
		if (pViewController) {
			if ([pViewController.tableTitles count]) {
				[pViewController.tableTitles removeAllObjects];
			}
			for (int i = 0; i < nc && i < ic; i++) {
				id name = [[self.statNames objectAtIndex:i] copy];
				NSInteger idx = [[self.statIndex objectAtIndex:i] intValue];
				if (idx == 0) 
					[pViewController.tableTitles addObject:name];
			}
			//NSLog([self.statNames description]);
			//NSLog([self.statValues description]);
			//NSLog([self.statIndex description]);
			[pViewController buildAccountDataTable];

		}
		
	} else 	if ([self.viewController isKindOfClass:[StatusListViewController class]]) {
		StatusListViewController *pViewController = (StatusListViewController *) viewController;
		
		if (pViewController) {
			[pViewController buildServiceDataTable];
			
		}
		
	} else 	if ([self.viewController isKindOfClass:[DomainListViewController class]]) {
		DomainListViewController *pViewController = (DomainListViewController *) viewController;
		
		if (pViewController) {
			if ([pViewController.tableTitles count]) {
				[pViewController.tableTitles removeAllObjects];
			}
			for (int i = 0; i < nc && i < ic; i++) {
				id name = [[self.statNames objectAtIndex:i] copy];
				NSInteger idx = [[self.statIndex objectAtIndex:i] intValue];
				if (idx == 0) 
					[pViewController.tableTitles addObject:name];
			}
			[pViewController buildAccountDataTable];
			[pViewController buildSearchArrayFrom:@""];
		}
		
	} else 	if ([self.viewController isKindOfClass:[DLListViewController class]]) {
		DLListViewController *pViewController = (DLListViewController *) viewController;
		
		if (pViewController) {
			if ([pViewController.tableTitles count]) {
				[pViewController.tableTitles removeAllObjects];
			}
			for (int i = 0; i < nc && i < ic; i++) {
				id name = [[self.statNames objectAtIndex:i] copy];
				NSInteger idx = [[self.statIndex objectAtIndex:i] intValue];
				if (idx == 0) 
					[pViewController.tableTitles addObject:name];
			}
			[pViewController buildAccountDataTable];
			[pViewController buildSearchArrayFrom:@""];
		}
		
	} else 	if ([self.viewController isKindOfClass:[CosListViewController class]]) {
		CosListViewController *pViewController = (CosListViewController *) viewController;
		
		if (pViewController) {
			if ([pViewController.tableTitles count]) {
				[pViewController.tableTitles removeAllObjects];
			}
			for (int i = 0; i < nc && i < ic; i++) {
				id name = [[self.statNames objectAtIndex:i] copy];
				NSInteger idx = [[self.statIndex objectAtIndex:i] intValue];
				if (idx == 0) 
					[pViewController.tableTitles addObject:name];
			}
			[pViewController buildAccountDataTable];
			[pViewController buildSearchArrayFrom:@""];
		}
		
	} 

	
}


- (BOOL)isReceiving
{
    return (self.connection != nil);
}

- (void)_startReceive
// Starts a connection to download the current URL.
{
    BOOL                success;
    NSURL *             url;
    NSURLRequest *      request;
    
    assert(self.connection == nil);   
    assert(self.fileStream == nil);  
    assert(self.filePath == nil);  
	
    // First get and check the URL.
    
    //url = [NetworkController smartURLForString:kDefaultGetURLText];
	url = [self.networkController smartURLForString:(self.getURL? self.getURL:kDefaultGetURLText)];
    
	success = (url != nil);
	
    // If the URL is bogus, let the user know.  Otherwise kick off the connection.
    
    if ( ! success) {
        NSLog(@"GET: Invalid URL");
    } else {
		
        // Open a stream for the file we're going to receive into.
		
        //self.filePath = [NetworkController pathForTemporaryFileWithPrefix:@"Get"];
		self.filePath = [self.networkController pathForTemporaryFileWithPrefix:@"Get"];
        assert(self.filePath != nil);
        
        self.fileStream = [NSOutputStream outputStreamToFileAtPath:self.filePath append:NO];
        assert(self.fileStream != nil);
        
        [self.fileStream open];
		
        // Open a connection for the URL.
		
        request = [NSURLRequest requestWithURL:url];
        assert(request != nil);
        
        self.connection = [NSURLConnection connectionWithRequest:request delegate:self];
        assert(self.connection != nil);
		
        // Tell the UI we're receiving.
        
        [self _receiveDidStart];
    }
}

- (void)_stopReceiveWithStatus:(NSString *)statusString
// Shuts down the connection and displays the result (statusString == nil) 
// or the error status (otherwise).
{
    if (self.connection != nil) {
        [self.connection cancel];
        self.connection = nil;
    }
    if (self.fileStream != nil) {
        [self.fileStream close];
        self.fileStream = nil;
    }
    [self _receiveDidStopWithStatus:statusString];
    self.filePath = nil;
}

- (void)connection:(NSURLConnection *)theConnection didReceiveResponse:(NSURLResponse *)response
// A delegate method called by the NSURLConnection when the request/response 
// exchange is complete.  We look at the response to check that the HTTP 
// status code is 2xx and that the Content-Type is acceptable.  If these checks 
// fail, we give up on the transfer.
{
#pragma unused(theConnection)
    NSHTTPURLResponse * httpResponse;
    NSString *          contentTypeHeader;
	
    assert(theConnection == self.connection);
    
    httpResponse = (NSHTTPURLResponse *) response;
    assert( [httpResponse isKindOfClass:[NSHTTPURLResponse class]] );
    
    if ((httpResponse.statusCode / 100) != 2) {
        [self _stopReceiveWithStatus:[NSString stringWithFormat:@"HTTP error %zd", (ssize_t) httpResponse.statusCode]];
    } else {
        contentTypeHeader = [httpResponse.allHeaderFields objectForKey:@"Content-Type"];
        if (contentTypeHeader == nil) {
            [self _stopReceiveWithStatus:@"No Content-Type!"];
        } else if ( ! [contentTypeHeader isHTTPContentType:@"text/html"]  
				   && ! [contentTypeHeader isHTTPContentType:@"text/plain"]
				   ) {
            [self _stopReceiveWithStatus:[NSString stringWithFormat:@"Unsupported Content-Type (%@)", contentTypeHeader]];
        } else {
            NSLog(@"GET: Response OK.");
        }
    }    
}

- (void)connection:(NSURLConnection *)theConnection didReceiveData:(NSData *)data
// A delegate method called by the NSURLConnection as data arrives.  We just 
// write the data to the file.
{
#pragma unused(theConnection)
    NSInteger       dataLength;
    const uint8_t * dataBytes;
    NSInteger       bytesWritten;
    NSInteger       bytesWrittenSoFar;
	
    assert(theConnection == self.connection);
    
    dataLength = [data length];
    dataBytes  = [data bytes];
	
    bytesWrittenSoFar = 0;
    do {
        bytesWritten = [self.fileStream write:&dataBytes[bytesWrittenSoFar] maxLength:dataLength - bytesWrittenSoFar];
        assert(bytesWritten != 0);
        if (bytesWritten == -1) {
            [self _stopReceiveWithStatus:@"File write error"];
            break;
        } else {
            bytesWrittenSoFar += bytesWritten;
        }
    } while (bytesWrittenSoFar != dataLength);
}

- (void)connection:(NSURLConnection *)theConnection didFailWithError:(NSError *)error
// A delegate method called by the NSURLConnection if the connection fails. 
// We shut down the connection and display the failure.  Production quality code 
// would either display or log the actual error.
{
#pragma unused(theConnection)
#pragma unused(error)
    assert(theConnection == self.connection);
    
    [self _stopReceiveWithStatus:@"Connection failed"];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)theConnection
// A delegate method called by the NSURLConnection when the connection has been 
// done successfully.  We shut down the connection with a nil status, which 
// causes the image to be displayed.
{
#pragma unused(theConnection)
    assert(theConnection == self.connection);
    
    [self _stopReceiveWithStatus:nil];
}

- (void) _setAppBadge: (NSString *) nextStats
{
	
	if (nextStats == nil) {
		return;
	}
	
	if (self.badgeStatus == nil || (self.badgeStatus != nil && ![self.badgeStatus isEqualToString:nextStats])) {
		self.badgeStatus = nextStats;
		[[UIApplication sharedApplication] setApplicationIconBadgeNumber:1];
		//self.isBadge = TRUE;
	} 
	//else if (self.badgeStatus != nil && [self.badgeStatus isEqualToString:nextStats]) {
	//	[[UIApplication sharedApplication] setApplicationIconBadgeNumber:0];
	//}
	
}


@end

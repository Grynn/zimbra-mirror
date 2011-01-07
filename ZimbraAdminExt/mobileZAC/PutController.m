//
//  PutController.m
//  Zimbra
//
//  Created by Qin An on 12/13/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import "PutController.h"
#import "NetworkController.h"


static NSString * kDefaultPutURLText = @"http://10.117.4.35:8080/mytest/";
static NSString * kDefaultPutFileName = @"zimbra-cluster-tmp.txt";

@implementation PutController

@synthesize connection;
@synthesize fileStream;
@synthesize putURL;
@synthesize cmdFile;
@synthesize networkController;

-(PutController *) initWithPutURL: (NSString *)putUrl cmdFile: (NSString *) cmdFileName
{
	self = [super init];
	if (putUrl != nil) {
		self.putURL = putUrl;
	} else {
		self.putURL = kDefaultPutURLText;
	}
	if (cmdFileName != nil)
		self.cmdFile = cmdFileName;
	else self.cmdFile = kDefaultPutFileName;
	
	return self;
}

- (void) startPutController: (NSString *) text
{
	if (self.networkController == nil) {
		NetworkController * netController = [NetworkController alloc];
		self.networkController = netController;
		[netController release];
	}
	
	// save and send the input string
	NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES); 
	NSString *documentsDirectory = [paths objectAtIndex:0]; // Get documents directory
	
	//NSLog(documentsDirectory);
	NSError *error;
	BOOL succeed = [text writeToFile:[documentsDirectory stringByAppendingPathComponent:cmdFile]
						  atomically:YES encoding:NSUTF8StringEncoding error:&error];
	if (!succeed){
		// Handle error here
		NSLog(@"PUT: can not save: %@",text);
		return;
	}
	
	NSString *localFilePath = [NSString stringWithFormat:@"%@/%@",documentsDirectory, cmdFile];
	NSLog(localFilePath);
	[self _startSend:localFilePath];
}

- (BOOL)isSending
{
    return (self.connection != nil);
}


- (void) _saveStringTofile: (NSString *) text
{
	NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES); 
	NSString *documentsDirectory = [paths objectAtIndex:0]; // Get documents directory
	
	//NSLog(documentsDirectory);
	NSError *error;
	BOOL succeed = [text writeToFile:[documentsDirectory stringByAppendingPathComponent:cmdFile]
						  atomically:YES encoding:NSUTF8StringEncoding error:&error];
	if (!succeed){
		// Handle error here
	}
	
	NSString *localFilePath = [NSString stringWithFormat:@"%@/%@",documentsDirectory, cmdFile];
	NSLog(localFilePath);
	[self _startSend:localFilePath];
	
}

- (void)_startSend:(NSString *)filePath
{
    BOOL                    success;
    NSURL *                 url;
    NSMutableURLRequest *   request;
    NSNumber *              contentLength;
    
    assert(filePath != nil);
    assert([[NSFileManager defaultManager] fileExistsAtPath:filePath]);
    assert( [filePath.pathExtension isEqual:@"txt"] || [filePath.pathExtension isEqual:@"html"] );
    
    assert(self.connection == nil);         // don't tap send twice in a row!
    assert(self.fileStream == nil);         // ditto
	
    // First get and check the URL.
    
    url = [self.networkController smartURLForString:kDefaultPutURLText];
    success = (url != nil);
    
    if (success) {
        // Add the last the file name to the end of the URL to form the final 
        // URL that we're going to PUT to.
        
        url = [NSMakeCollectable(
								 CFURLCreateCopyAppendingPathComponent(NULL, (CFURLRef) url, (CFStringRef) [filePath lastPathComponent], false)
								 ) autorelease];
        success = (url != nil);
    }
    
    // If the URL is bogus, let the user know.  Otherwise kick off the connection.
	
    if ( ! success) {
        //self.statusLabel.text = @"Invalid URL";
		NSLog(@"PUT: Invalid URL");
    } else {
		
        // Open a stream for the file we're going to send.  We do not open this stream; 
        // NSURLConnection will do it for us.
        
        self.fileStream = [NSInputStream inputStreamWithFileAtPath:filePath];
        assert(self.fileStream != nil);
        
        // Open a connection for the URL, configured to PUT the file.
		
        request = [NSMutableURLRequest requestWithURL:url];
        assert(request != nil);
        
        [request setHTTPMethod:@"PUT"];
        [request setHTTPBodyStream:self.fileStream];
        
        if ( [filePath.pathExtension isEqual:@"png"] ) {
            [request setValue:@"image/png" forHTTPHeaderField:@"Content-Type"];
        } else if ( [filePath.pathExtension isEqual:@"jpg"] ) {
            [request setValue:@"image/jpeg" forHTTPHeaderField:@"Content-Type"];
        } else if ( [filePath.pathExtension isEqual:@"gif"] ) {
            [request setValue:@"image/gif" forHTTPHeaderField:@"Content-Type"];
        } else if ( [filePath.pathExtension isEqual:@"txt"] ) {
            [request setValue:@"text/plain" forHTTPHeaderField:@"Content-Type"];
        } else if ( [filePath.pathExtension isEqual:@"html"] ) {
            [request setValue:@"text/html" forHTTPHeaderField:@"Content-Type"];
        } else {
            assert(NO);
        }
		
        contentLength = (NSNumber *) [[[NSFileManager defaultManager] attributesOfItemAtPath:filePath error:NULL] objectForKey:NSFileSize];
        assert( [contentLength isKindOfClass:[NSNumber class]] );
        [request setValue:[contentLength description] forHTTPHeaderField:@"Content-Length"];
        
        self.connection = [NSURLConnection connectionWithRequest:request delegate:self];
        assert(self.connection != nil);
        
        // Tell the UI we're sending.
        
        [self _sendDidStart];
    }
}

- (void)_stopSendWithStatus:(NSString *)statusString
{
    if (self.connection != nil) {
        [self.connection cancel];
        self.connection = nil;
    }
    if (self.fileStream != nil) {
        [self.fileStream close];
        self.fileStream = nil;
    }
    [self _sendDidStopWithStatus:statusString];
}

- (void)_sendDidStart
{
    //self.statusLabel.text = @"Sending";
	NSLog(@"PUT: Sending");
    //self.cancelButton.enabled = YES;
    //[self.activityIndicator startAnimating];
    [self.networkController didStartNetworking];
}

- (void)_sendDidStopWithStatus:(NSString *)statusString
{
    if (statusString == nil) {
        statusString = @"PUT: succeeded";
    }
    //self.statusLabel.text = statusString;
	NSLog(statusString);
    //self.cancelButton.enabled = NO;
    //[self.activityIndicator stopAnimating];
    [self.networkController didStopNetworking];
}

- (void)connection:(NSURLConnection *)theConnection didReceiveResponse:(NSURLResponse *)response
// A delegate method called by the NSURLConnection when the request/response 
// exchange is complete.  We look at the response to check that the HTTP 
// status code is 2xx.  If it isn't, we fail right now.
{
#pragma unused(theConnection)
	NSHTTPURLResponse * httpResponse;
	
	assert(theConnection == self.connection);
	
	httpResponse = (NSHTTPURLResponse *) response;
	assert( [httpResponse isKindOfClass:[NSHTTPURLResponse class]] );
	
	if ((httpResponse.statusCode / 100) != 2) {
		[self _stopSendWithStatus:[NSString stringWithFormat:@"HTTP error %zd", (ssize_t) httpResponse.statusCode]];
	} else {
		//self.statusLabel.text = @"Response OK.";
		NSLog(@"PUT: Response OK.");
	}    
}



- (void)connection:(NSURLConnection *)theConnection didReceiveData:(NSData *)data
// A delegate method called by the NSURLConnection as data arrives.  The 
// response data for a PUT is only for useful for debugging purposes, 
// so we just drop it on the floor.
{
#pragma unused(theConnection)
#pragma unused(data)
	
	assert(theConnection == self.connection);
	
	// do nothing
}



- (void)connection:(NSURLConnection *)theConnection didFailWithError:(NSError *)error
// A delegate method called by the NSURLConnection if the connection fails. 
// We shut down the connection and display the failure.  Production quality code 
// would either display or log the actual error.
{
#pragma unused(theConnection)
#pragma unused(error)
	assert(theConnection == self.connection);
	
	[self _stopSendWithStatus:@"Connection failed"];
}



- (void)connectionDidFinishLoading:(NSURLConnection *)theConnection
// A delegate method called by the NSURLConnection when the connection has been 
// done successfully.  We shut down the connection with a nil status, which 
// causes the image to be displayed.
{
#pragma unused(theConnection)
	assert(theConnection == self.connection);
	
	[self _stopSendWithStatus:nil];
}



@end

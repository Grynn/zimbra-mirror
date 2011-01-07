//
//  StatisticsController.h
//  Zimbra
//
//  Created by Qin An on 12/21/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import "ZimGraphView.h"

@class GetController;

@interface StatisticsController : UIViewController <ZimGraphViewDataSource> {
	ZimGraphView *graphView;
	NSString *serverId;
	NSString *statData;
	
	GetController *getController;

}

@property (nonatomic, retain) ZimGraphView *graphView;
@property (nonatomic, retain) NSString *serverId;
@property (nonatomic, retain) NSString *statData;

@property (nonatomic, retain) GetController *getController;


-(StatisticsController *) initWithTitle: (NSString *)title serverId: (NSString*) server;
- (void) loadDataToUI: (NSString*) text;

@end

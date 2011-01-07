//
//  ZimGraphView.h
//  Zimbra
//
//  Created by Qin An on 12/21/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import <UIKit/UIKit.h>

#ifndef RGB
#define RGB(R,G,B) [UIColor colorWithRed:R/255.0f green:G/255.0f blue:B/255.0f alpha:1.0f]
#endif


@class ZimGraphView;

@protocol ZimGraphViewDataSource<NSObject>

@required

/** 
 * Returns the number of plots your want to be rendered in the view.
 * 
 * @params
 * graphView Component that is asking for the number of plots.
 * 
 * @return Number of plots your want to be rendered in the view.
 */
- (NSUInteger)graphViewNumberOfPlots:(ZimGraphView *)graphView;

/** 
 * Returns an array with objects of any type you can provide a formatter for via xValueFormatter property
 * or stay happy with default formatting.
 * 
 * @params
 * graphView Component that is asking for the values.
 * 
 * @return Array with objects of any type you can provide a formatter for via xValueFormatter property
 * or stay happy with default formatting.
 */
- (NSArray *)graphViewXValues:(ZimGraphView *)graphView;

/** 
 * Returns an array with objects of type NSNumber.
 * 
 * @params
 * graphView Component that is asking for the values.
 * plotIndex Index of the plot that you should provide an array of values for.
 * 
 * @return Array with objects of type NSNumber.
 */
- (NSArray *)graphView:(ZimGraphView *)graphView yValuesForPlot:(NSUInteger)plotIndex;

@optional

/** 
 * Returns the value indicating whether the component should render the plot with the specified index as filled.
 *
 * @param
 * graphView Component that is asking whether the plot should be filled.
 * plotIndex Index of the plot that you should decide whether it is filled or not.
 * 
 * @return YES if the plot should be rendered as filled; otherwise, NO.
 */
- (BOOL)graphView:(ZimGraphView *)graphView shouldFillPlot:(NSUInteger)plotIndex;

@end

@interface ZimGraphView : UIView {
	
@private
	
	id<ZimGraphViewDataSource> _dataSource;
	
	NSFormatter *_xValuesFormatter;
	NSFormatter *_yValuesFormatter;
	
	BOOL _drawAxisX;
	BOOL _drawAxisY;
	BOOL _drawGridX;
	BOOL _drawGridY;
	
	UIColor *_xValuesColor;
	UIColor *_yValuesColor;
	
	UIColor *_gridXColor;
	UIColor *_gridYColor;
	
	BOOL _drawInfo;
	NSString *_info;
	UIColor *_infoColor;
}

/** Returns a different color for the first 10 plots. */
+ (UIColor *)colorByIndex:(NSInteger)index;

@property (nonatomic, assign) IBOutlet id<ZimGraphViewDataSource> dataSource;

@property (nonatomic, retain) IBOutlet NSFormatter *xValuesFormatter;
@property (nonatomic, retain) IBOutlet NSFormatter *yValuesFormatter;

@property (nonatomic, assign) BOOL drawAxisX;
@property (nonatomic, assign) BOOL drawAxisY;
@property (nonatomic, assign) BOOL drawGridX;
@property (nonatomic, assign) BOOL drawGridY;

@property (nonatomic, retain) UIColor *xValuesColor;
@property (nonatomic, retain) UIColor *yValuesColor;

@property (nonatomic, retain) UIColor *gridXColor;
@property (nonatomic, retain) UIColor *gridYColor;

@property (nonatomic, assign) BOOL drawInfo;
@property (nonatomic, copy) NSString *info;
@property (nonatomic, retain) UIColor *infoColor;

- (void)reloadData;



@end


//
//  UICustomSwitchExt.m
//  Zimbra
//
//  Created by Qin An on 12/30/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import "UICustomSwitchExt.h"


@implementation UICustomSwitchExt

@synthesize on;
@synthesize tintColor, clippingView, leftLabel, rightLabel;

+(UICustomSwitchExt *)switchWithLeftText:(NSString *)leftText andRight:(NSString *)rightText
{
	UICustomSwitchExt *switchView = [[UICustomSwitchExt alloc] initWithFrame:CGRectZero];
	
	switchView.leftLabel.text = leftText;
	switchView.rightLabel.text = rightText;
	
	return [switchView autorelease];
}

-(id)initWithFrame:(CGRect)rect
{
	if ((self=[super initWithFrame:CGRectMake(rect.origin.x,rect.origin.y,95,27)]))
	{
		//		self.clipsToBounds = YES;
		
		[self awakeFromNib];
	}
	return self;
}

-(void)awakeFromNib
{
	[super awakeFromNib];
	
	self.backgroundColor = [UIColor clearColor];
	
	[self setThumbImage:[UIImage imageNamed:@"switchThumb.png"] forState:UIControlStateNormal];
	[self setMinimumTrackImage:[UIImage imageNamed:@"switchBlueBg.png"] forState:UIControlStateNormal];
	[self setMaximumTrackImage:[UIImage imageNamed:@"switchOffPlain.png"] forState:UIControlStateNormal];
	
	self.minimumValue = 0;
	self.maximumValue = 1;
	self.continuous = NO;
	
	self.on = NO;
	self.value = 0.0;
	
	self.clippingView = [[UIView alloc] initWithFrame:CGRectMake(4,2,87,23)];
	self.clippingView.clipsToBounds = YES;
	self.clippingView.userInteractionEnabled = NO;
	self.clippingView.backgroundColor = [UIColor clearColor];
	[self addSubview:self.clippingView];
	[self.clippingView release];
	
	NSString *leftLabelText = NSLocalizedString(@"ON","Custom UISwitch ON label. If localized to empty string then I/O will be used");
	if ([leftLabelText length] == 0)	
	{
		leftLabelText = @"l";		// use helvetica lowercase L to be a 1. 
	}
	
	self.leftLabel = [[UILabel alloc] init];
	self.leftLabel.frame = CGRectMake(0, 0, 48, 23);
	self.leftLabel.text = leftLabelText;
	self.leftLabel.textAlignment = UITextAlignmentCenter;
	self.leftLabel.font = [UIFont boldSystemFontOfSize:17];
	self.leftLabel.textColor = [UIColor whiteColor];
	self.leftLabel.backgroundColor = [UIColor clearColor];
	//		self.leftLabel.shadowColor = [UIColor redColor];
	//		self.leftLabel.shadowOffset = CGSizeMake(0,0);
	[self.clippingView addSubview:self.leftLabel];
	[self.leftLabel release];
	
	
	NSString *rightLabelText = NSLocalizedString(@"OFF","Custom UISwitch OFF label. If localized to empty string then I/O will be used");
	if ([rightLabelText length] == 0)	
	{
		rightLabelText = @"O";	// use helvetica uppercase o to be a 0. 
	}
	
	self.rightLabel = [[UILabel alloc] init];
	self.rightLabel.frame = CGRectMake(95, 0, 48, 23);
	self.rightLabel.text = rightLabelText;
	self.rightLabel.textAlignment = UITextAlignmentCenter;
	self.rightLabel.font = [UIFont boldSystemFontOfSize:17];
	self.rightLabel.textColor = [UIColor grayColor];
	self.rightLabel.backgroundColor = [UIColor clearColor];
	//		self.rightLabel.shadowColor = [UIColor redColor];
	//		self.rightLabel.shadowOffset = CGSizeMake(0,0);
	[self.clippingView addSubview:self.rightLabel];
	[self.rightLabel release];
	
	
}

-(void)layoutSubviews
{
	[super layoutSubviews];
	
	//	NSLog(@"leftLabel=%@",NSStringFromCGRect(self.leftLabel.frame));
	
	// move the labels to the front
	[self.clippingView removeFromSuperview];
	[self addSubview:self.clippingView];
	
	CGFloat thumbWidth = self.currentThumbImage.size.width;
	CGFloat switchWidth = self.bounds.size.width;
	CGFloat labelWidth = switchWidth - thumbWidth;
	CGFloat inset = self.clippingView.frame.origin.x;
	
	//	NSInteger xPos = self.value * (self.bounds.size.width - thumbWidth) - (self.leftLabel.frame.size.width - thumbWidth/2); 
	NSInteger xPos = self.value * labelWidth - labelWidth - inset;
	self.leftLabel.frame = CGRectMake(xPos, 0, labelWidth, 23);
	
	//	xPos = self.value * (self.bounds.size.width - thumbWidth) + (self.rightLabel.frame.size.width - thumbWidth/2); 
	xPos = switchWidth + (self.value * labelWidth - labelWidth) - inset; 
	self.rightLabel.frame = CGRectMake(xPos, 0, labelWidth, 23);
	
	//	NSLog(@"value=%f    xPos=%i",self.value,xPos);
	//	NSLog(@"thumbWidth=%f    self.bounds.size.width=%f",thumbWidth,self.bounds.size.width);
}

- (void)scaleSwitch:(CGSize)newSize 
{
	self.transform = CGAffineTransformMakeScale(newSize.width,newSize.height);
}

- (UIImage *)image:(UIImage*)image tintedWithColor:(UIColor *)tint 
{	
	//	static NSInteger modeColor = 0;
	
    if (tint != nil) 
	{
		UIGraphicsBeginImageContext(image.size);
		
		//draw mask so the alpha is respected
		CGContextRef currentContext = UIGraphicsGetCurrentContext();
		CGImageRef maskImage = [image CGImage];
		CGContextClipToMask(currentContext, CGRectMake(0, 0, image.size.width, image.size.height), maskImage);
		CGContextDrawImage(currentContext, CGRectMake(0,0, image.size.width, image.size.height), image.CGImage);
		
		[image drawAtPoint:CGPointMake(0,0)];
		[tint setFill];
		UIRectFillUsingBlendMode(CGRectMake(0,0,image.size.width,image.size.height),kCGBlendModeColor);
		UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext();
		UIGraphicsEndImageContext();
		
		//		NSLog(@"modeColor=%i",modeColor);
		//
		//		modeColor += 1;
		
        return newImage;
    }
    else 
	{
        return image;
    }
}


-(void)setTintColor:(UIColor*)color
{
	if (color != tintColor)
	{
		[tintColor release];
		tintColor = [color retain];
		
		[self setMinimumTrackImage:[self image:[UIImage imageNamed:@"switchBlueBg.png"] tintedWithColor:tintColor] forState:UIControlStateNormal];
	}
	
}

- (void)setOn:(BOOL)turnOn animated:(BOOL)animated;
{
	on = turnOn;
	
	if (animated)
	{
		[UIView	 beginAnimations:@"UICustomSwitchExt" context:nil];
		[UIView setAnimationDuration:0.2];
	}
	
	if (on)
	{
		self.value = 1.0;
	}
	else 
	{
		self.value = 0.0;
	}
	
	if (animated)
	{
		[UIView	commitAnimations];	
	}
}

- (void)setOn:(BOOL)turnOn
{
	[self setOn:turnOn animated:NO];
}


- (void)endTrackingWithTouch:(UITouch *)touch withEvent:(UIEvent *)event
{
	//	NSLog(@"preendTrackingWithtouch");
	[super endTrackingWithTouch:touch withEvent:event];
	//	NSLog(@"postendTrackingWithtouch");
	m_touchedSelf = YES;
	
	[self setOn:on animated:YES];
}

- (void)touchesBegan:(NSSet*)touches withEvent:(UIEvent*)event
{
	[super touchesBegan:touches withEvent:event];
	//	NSLog(@"touchesBegan");
	m_touchedSelf = NO;
	on = !on;
}

- (void)touchesEnded:(NSSet*)touches withEvent:(UIEvent*)event
{
	[super touchesEnded:touches withEvent:event];
	//	NSLog(@"touchesEnded");
	
	if (!m_touchedSelf)
	{
		[self setOn:on animated:YES];
		[self sendActionsForControlEvents:UIControlEventValueChanged];
	}
}

-(void)dealloc
{
	[tintColor release];
	[clippingView release];
	[rightLabel release];
	[leftLabel release];
	
	[super dealloc];
}


@end

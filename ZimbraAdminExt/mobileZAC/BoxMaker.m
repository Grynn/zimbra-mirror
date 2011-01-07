//
//  BoxMaker.m
//  Zimbra
//
//  Created by Qin An on 12/11/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import "BoxMaker.h"
#import "math.h"

@implementation BoxMaker

CGContextRef MyCreateBitmapContext (int pixelsWide,
									int pixelsHigh)
{
    CGContextRef    context = NULL;
    CGColorSpaceRef colorSpace;
    void *          bitmapData;
    int             bitmapByteCount;
    int             bitmapBytesPerRow;
	
    bitmapBytesPerRow   = (pixelsWide * 4);
    bitmapByteCount     = (bitmapBytesPerRow * pixelsHigh);
	
    colorSpace = CGColorSpaceCreateDeviceRGB();
    bitmapData = malloc( bitmapByteCount );
    if (bitmapData == NULL)
    {
        fprintf (stderr, "Memory not allocated!");
		CGColorSpaceRelease( colorSpace );
        return NULL;
    }
    context = CGBitmapContextCreate (bitmapData,
									 pixelsWide,
									 pixelsHigh,
									 8,
									 bitmapBytesPerRow,
									 colorSpace,
									 kCGImageAlphaPremultipliedLast);
    if (context== NULL)
    {
        free (bitmapData);
        fprintf (stderr, "Context not created!");
        return NULL;
    }
    CGColorSpaceRelease( colorSpace );
	
    return context;
}

// addRoundedRectToPath: Source based on Apple Sample Code
static void addRoundedRectToPath(CGContextRef context, CGRect rect, float ovalWidth,
								 float ovalHeight)
{
	float fw, fh;
	if (ovalWidth == 0 || ovalHeight == 0) {
		CGContextAddRect(context, rect);
		return;
	}
	
	CGContextSaveGState(context);
	CGContextTranslateCTM(context, CGRectGetMinX(rect), CGRectGetMinY(rect));
	CGContextScaleCTM(context, ovalWidth, ovalHeight);
	fw = CGRectGetWidth(rect) / ovalWidth;
	fh = CGRectGetHeight(rect) / ovalHeight;
	
	CGContextMoveToPoint(context, fw, fh/2);  // Start at lower right corner
	CGContextAddArcToPoint(context, fw, fh, fw/2, fh, 1);  // Top right corner
	CGContextAddArcToPoint(context, 0, fh, 0, fh/2, 1); // Top left corner
	CGContextAddArcToPoint(context, 0, 0, fw/2, 0, 1); // Lower left corner
	CGContextAddArcToPoint(context, fw, 0, fw, fh/2, 1); // Back to lower right
	
	CGContextClosePath(context);
	CGContextRestoreGState(context);
}

// Build an image tinted at the given percentage
//- (id) createImage:(float) percentage
id createImage(float percentage)
{
	CGContextRef context  = MyCreateBitmapContext(30, 30);
	addRoundedRectToPath(context, CGRectMake(0.0f, 0.0f, 30.0f, 30.0f), 4.0f, 4.0f);
	CGFloat gray[4] = {percentage, percentage, percentage, 1.0f};
	CGContextSetFillColor(context, gray);
	CGContextFillPath(context);
	CGImageRef myRef = CGBitmapContextCreateImage (context);
	free(CGBitmapContextGetData(context));
	CGContextRelease(context);
	return [UIImage imageWithCGImage:myRef];
}

// Resize the image
// Usage:
// 	UIImage *image_red = [UIImage imageNamed:@"red.jpg"];
//  CGSize sz = CGSizeMake(16,16);
//  UIImage *resized_red = scaleImage(image_red, sz);
//
UIImage* scaleImage(UIImage *image, CGSize size)
{
    UIGraphicsBeginImageContext(size);
    [image drawInRect:CGRectMake(0, 0, size.width, size.height)];
    UIImage *scaledImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return scaledImage;
}


@end

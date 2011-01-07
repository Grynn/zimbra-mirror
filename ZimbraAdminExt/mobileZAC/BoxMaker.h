//
//  BoxMaker.h
//  Zimbra
//
//  Created by Qin An on 12/11/10.
//  Copyright 2010 VMware. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface BoxMaker : NSObject {

}

id createImage(float percentage);
UIImage *scaleImage(UIImage *image, CGSize size);

@end

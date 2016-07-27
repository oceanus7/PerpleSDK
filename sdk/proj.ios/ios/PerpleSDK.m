//
//  PerpleSDK.m
//  PerpleSDK
//
//  Created by PerpleLab on 2016. 7. 28..
//  Copyright © 2016년 PerpleLab. All rights reserved.
//

#import "PerpleSDK.h"

@implementation PerpleSDK

- (BOOL) initSDK {

    [FIRApp configure];

    return YES;
}

@end

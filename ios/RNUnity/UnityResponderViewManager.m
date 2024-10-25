#import <React/RCTViewManager.h>
#import <UIKit/UIKit.h>
#import "UnityResponderViewManager.h"

@implementation UnityResponderViewManager

RCT_EXPORT_MODULE(UnityResponderView)

- (UIView *)view {
    return [UnityResponderView new];
}

@end

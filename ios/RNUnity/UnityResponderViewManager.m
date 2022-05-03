#import <React/RCTViewManager.h>
#import <UIKit/UIKit.h>
#import "UnityResponderViewManager.h"

@implementation UnityResponderViewManager

RCT_EXPORT_MODULE(UnityResponderView)

- (UIView *)view
{
    UnityResponderView *view = [UnityResponderView new];
    UIWindow * main = [[[UIApplication sharedApplication] delegate] window];

    if(main != nil) {
        [main makeKeyAndVisible];
    }

    return view;
}

RCT_EXPORT_VIEW_PROPERTY(fullScreen, BOOL)

@end


#import <UIKit/UIKit.h>
#include <UnityFramework/UnityFramework.h>
#import "RNUnity.h"

@interface UnityResponderView : UIView

@property (nonatomic, strong) UIView* uView;
@property UnityFramework * ufw;
@property (nonatomic) bool fullScreen;

- (void)setUnityView:(UIView *)view;

@end

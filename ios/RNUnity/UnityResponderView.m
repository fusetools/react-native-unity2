#import "UnityResponderView.h"
#import "RNUnity.h"

UIView* _unityView;

@implementation UnityResponderView

-(id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (!_unityView) {
        _unityView = [[[RNUnity launchWithOptions:nil] appController] rootView];
    }
    return self;
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    [_unityView removeFromSuperview];
    _unityView.frame = self.bounds;
    [self insertSubview:_unityView atIndex:0];
}

@end

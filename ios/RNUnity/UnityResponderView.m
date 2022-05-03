#import "UnityResponderView.h"
#import "RNUnity.h"

NSDictionary* appLaunchOpts;
UIView* _unityView;

@implementation UnityResponderView

-(id)initWithFrame:(CGRect)frame
{
  self = [super initWithFrame:frame];
  if (self) {
      _unityView = [[[RNUnity launchWithOptions:appLaunchOpts] appController] rootView];
  }
  return self;
}

- (void)setFullScreen:(bool)fullScreen
{
  _fullScreen = fullScreen;
}

- (void)setUnityView:(UIView *)view
{
    self.uView = view;
    [self setNeedsLayout];
}

- (void)dealloc
{
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    if (!_fullScreen) {
        [_unityView removeFromSuperview];
        _unityView.frame = self.bounds;
        [self insertSubview:_unityView atIndex:0];
    } else {
        UIWindow* unityWindow = [[[RNUnity ufw] appController] window];
        CGRect viewRect = CGRectMake(0, 0, self.bounds.size.width, self.bounds.size.height);
        unityWindow.frame = viewRect;
    }
}

@end

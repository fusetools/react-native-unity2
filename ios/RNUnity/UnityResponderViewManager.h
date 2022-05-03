#import <React/RCTViewManager.h>
#import <React/RCTBridgeModule.h>
#include <UnityFramework/UnityFramework.h>
#import "UnityResponderViewManager.h"
#import "UnityResponderView.h"
#import "RNUnity.h"

@interface UnityResponderViewManager : RCTViewManager <RCTBridgeModule>

@property UnityFramework * ufw;

@end

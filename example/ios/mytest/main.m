#import <UIKit/UIKit.h>
#import <RNUnity/RNUnity.h>
#import "AppDelegate.h"

int main(int argc, char * argv[]) {
  @autoreleasepool {
    [RNUnity setArgc:argc];
    [RNUnity setArgv:argv];
    return UIApplicationMain(argc, argv, nil, NSStringFromClass([AppDelegate class]));
  }
}

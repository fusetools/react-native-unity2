#import <Foundation/Foundation.h>

#import <UIKit/UIKit.h>

#include <mach-o/ldsyms.h>

#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#include <UnityFramework/UnityAppController.h>

@protocol RNUnityAppController <UIApplicationDelegate>

- (UIWindow *)window;
- (UIView *)rootView;
- (UnityView *)unityView;

@end


@protocol RNUnityFramework <NSObject>

+ (id<RNUnityFramework>)getInstance;
- (id<RNUnityAppController>)appController;

- (void)setExecuteHeader:(const typeof(_mh_execute_header)*)header;
- (void)setDataBundleId:(const char*)bundleId;

- (void)runEmbeddedWithArgc:(int)argc argv:(char*[])argv appLaunchOpts:(NSDictionary*)appLaunchOpts;

- (void)unloadApplication;

- (void)showUnityWindow;

- (void)quitApplication:(int)exitCode;

- (void)pause:(bool)pause;

- (void)sendMessageToGOWithName:(const char*)goName functionName:(const char*)name message:(const char*)msg;

@end


@interface RNUnity : RCTEventEmitter <RCTBridgeModule>

@property (atomic, class) int argc;
@property (atomic, class) char** argv;

@property (atomic, class) id<RNUnityFramework> ufw;

+ (void)initFromSwift;

+ (void)applicationWillResignActive:(UIApplication*)application;
+ (void)applicationDidEnterBackground:(UIApplication*)application;
+ (void)applicationWillEnterForeground:(UIApplication*)application;
+ (void)applicationDidBecomeActive:(UIApplication*)application;
+ (void)applicationWillTerminate:(UIApplication*)application;

+ (id<RNUnityFramework>)launchWithOptions:(NSDictionary*)launchOptions;

+ (void)emitEvent:(const char*)name data:(const char*)data;

@end

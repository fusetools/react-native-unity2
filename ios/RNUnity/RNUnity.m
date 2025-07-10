#import "RNUnity.h"

#include <crt_externs.h>

// Use __dso_handle as a ‘universal’ Mach-O image start symbol.
// https://forums.developer.apple.com/forums/thread/760543
extern const struct mach_header __dso_handle;

@interface RNUnity ()

@property (nonatomic) BOOL hasListeners;

@end


@implementation RNUnity

RCT_EXPORT_MODULE(RNUnity)

static int _RNUnity_argc;
static char **_RNUnity_argv;
static id<RNUnityFramework> _RNUnity_ufw;
static RNUnity *_RNUnity_sharedInstance;

+ (char **)argv {
    return _RNUnity_argv;
}

+ (void)setArgv:(char **)argv {
    _RNUnity_argv = argv;
}

+ (int)argc {
    return _RNUnity_argc;
}

+ (void)setArgc:(int)argc {
    _RNUnity_argc = argc;
}

+ (id<RNUnityFramework>)ufw {
    return _RNUnity_ufw;
}

+ (void)setUfw:(id<RNUnityFramework>)ufw {
    _RNUnity_ufw = ufw;
}

+ (void)initFromSwift {
    _RNUnity_argc = *_NSGetArgc();
    _RNUnity_argv = *_NSGetArgv();
}

+ (bool)unityIsInitialized {
    return _RNUnity_sharedInstance && [[self ufw] appController];
}

+ (void)applicationWillResignActive:(UIApplication *)application {
    if ([RNUnity unityIsInitialized]) {
        [[[self ufw] appController] applicationWillResignActive: application];
    }
}

+ (void)applicationDidEnterBackground:(UIApplication *)application {
    if ([RNUnity unityIsInitialized]) {
        [[[self ufw] appController] applicationDidEnterBackground: application];
    }
}

+ (void)applicationWillEnterForeground:(UIApplication *)application {
    if ([RNUnity unityIsInitialized]) {
        [[[self ufw] appController] applicationWillEnterForeground: application];
    }
}

+ (void)applicationDidBecomeActive:(UIApplication *)application {
    if ([RNUnity unityIsInitialized]) {
        [[[self ufw] appController] applicationDidBecomeActive: application];
    }
}

+ (void)applicationWillTerminate:(UIApplication *)application {
    if ([RNUnity unityIsInitialized]) {
        [[[self ufw] appController] applicationWillTerminate: application];
    }
}

+ (id<RNUnityFramework>)launchWithOptions:(NSDictionary *)applaunchOptions {
    NSString *bundlePath = nil;
    bundlePath = [[NSBundle mainBundle] bundlePath];
    bundlePath = [bundlePath stringByAppendingString: @"/Frameworks/UnityFramework.framework"];

    NSBundle *bundle = [NSBundle bundleWithPath: bundlePath];
    if ([bundle isLoaded] == false) [bundle load];

    id<RNUnityFramework> framework = [bundle.principalClass getInstance];
    if (![framework appController]) {
        // Unity is not initialized
        [framework setExecuteHeader: &__dso_handle];
    }

    [framework setDataBundleId: [bundle.bundleIdentifier cStringUsingEncoding:NSUTF8StringEncoding]];
    [framework setRNUnityProxy: (id<RNUnityProxy>)self];

    [framework runEmbeddedWithArgc: self.argc argv: self.argv appLaunchOpts: applaunchOptions];
    [self setUfw:framework];

    // Notify the app that Unity is ready to receive messages
    [_RNUnity_sharedInstance emitEvent:@"ready" data:@""];

    return self.ufw;
}

RCT_EXPORT_METHOD(initialize) {
    _RNUnity_sharedInstance = self;
}

- (void)startObserving {
    self.hasListeners = YES;

    if (_RNUnity_ufw != nil) {
        // Notify the app that Unity is ready to receive messages
        [_RNUnity_sharedInstance emitEvent:@"ready" data:@""];
    }
}

- (void)stopObserving {
    self.hasListeners = NO;
}

- (NSArray<NSString *> *)supportedEvents {
    return @[@"ready", @"message", @"reject", @"resolve"];
}

- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}

RCT_EXPORT_METHOD(pause) {
    if ([RNUnity unityIsInitialized]) {
        [[RNUnity ufw] pause:true];
    }
}

RCT_EXPORT_METHOD(resume) {
    if ([RNUnity unityIsInitialized]) {
        [[RNUnity ufw] pause:false];
    }
}

RCT_EXPORT_METHOD(sendMessage:(NSString *)gameObject
                  functionName:(NSString *)functionName
                  message:(NSString *)message) {
    if ([RNUnity unityIsInitialized]) {
        [[RNUnity ufw] sendMessageToGOWithName:[gameObject UTF8String] functionName:[functionName UTF8String] message:[message UTF8String]];
    }
}

+ (void)emitEvent:(const char *)name data:(const char *)data {
    [_RNUnity_sharedInstance emitEvent:[NSString stringWithUTF8String:name] data:[NSString stringWithUTF8String:data]];
}

- (void)emitEvent:(NSString *)name data:(NSString *)data {
    if (self.hasListeners) {
        [self sendEventWithName:name body:data];
    }
}

RCT_EXPORT_METHOD(setKeepAwake:(BOOL)keepAwake) {
    dispatch_async(dispatch_get_main_queue(), ^{
        [[UIApplication sharedApplication] setIdleTimerDisabled:keepAwake];
    });
}

RCT_EXPORT_METHOD(unloadUnity) {
    if ([RNUnity unityIsInitialized]) {
        [[RNUnity ufw] unloadApplication];
        [RNUnity setUfw:nil];
    }
}

@end

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
static char ** _RNUnity_argv;
static id<RNUnityFramework> _RNUnity_ufw;
static RNUnity *_RNUnity_sharedInstance;

+ (char **)argv {
    @synchronized (self) {
        return _RNUnity_argv;
    }
}

+ (void)setArgv:(char **)argv {
    @synchronized (self) {
        _RNUnity_argv = argv;
    }
}

+ (int)argc {
    @synchronized (self) {
        return _RNUnity_argc;
    }
}

+ (void)setArgc:(int)argc {
    @synchronized (self) {
        _RNUnity_argc = argc;
    }
}

+ (id<RNUnityFramework>)ufw {
    @synchronized (self) {
        return _RNUnity_ufw;
    }
}

+ (void)setUfw:(id<RNUnityFramework>)ufw {
    @synchronized (self) {
        _RNUnity_ufw = ufw;
    }
}

+ (void)initFromSwift {
    NSLog(@"RNUnity.initFromSwift()");
    _RNUnity_argc = *_NSGetArgc();
    _RNUnity_argv = *_NSGetArgv();
}

+ (void)applicationWillResignActive:(UIApplication*)application {
    NSLog(@"RNUnity.applicationWillResignActive()");
    [[[self ufw] appController] applicationWillResignActive: application];
}

+ (void)applicationDidEnterBackground:(UIApplication*)application {
    NSLog(@"RNUnity.applicationDidEnterBackground()");
    [[[self ufw] appController] applicationDidEnterBackground: application];
}

+ (void)applicationWillEnterForeground:(UIApplication*)application {
    NSLog(@"RNUnity.applicationWillEnterForeground()");
    [[[self ufw] appController] applicationWillEnterForeground: application];
}

+ (void)applicationDidBecomeActive:(UIApplication*)application {
    NSLog(@"RNUnity.applicationDidBecomeActive()");
    [[[self ufw] appController] applicationDidBecomeActive: application];
}

+ (void)applicationWillTerminate:(UIApplication*)application {
    NSLog(@"RNUnity.applicationWillTerminate()");
    [[[self ufw] appController] applicationWillTerminate: application];
}

+ (id<RNUnityFramework>)launchWithOptions:(NSDictionary*)applaunchOptions {

    NSString* bundlePath = nil;
    bundlePath = [[NSBundle mainBundle] bundlePath];
    bundlePath = [bundlePath stringByAppendingString: @"/Frameworks/UnityFramework.framework"];

    NSBundle* bundle = [NSBundle bundleWithPath: bundlePath];
    if ([bundle isLoaded] == false) [bundle load];

    id<RNUnityFramework> framework = [bundle.principalClass getInstance];
    if (![framework appController]) {
        // Unity is not initialized
        [framework setExecuteHeader: &__dso_handle];
    }
    [framework setRNUnityProxy: (id<RNUnityProxy>)self];
    [framework setDataBundleId: [bundle.bundleIdentifier cStringUsingEncoding:NSUTF8StringEncoding]];
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
    if (_RNUnity_sharedInstance) {
        [[RNUnity ufw] pause:true];
    }
}

RCT_EXPORT_METHOD(resume) {
    if (_RNUnity_sharedInstance) {
        [[RNUnity ufw] pause:false];
    }
}

RCT_EXPORT_METHOD(sendMessage:(NSString *)gameObject
                  functionName:(NSString *)functionName
                  message:(NSString *)message) {

    if (_RNUnity_sharedInstance) {
        [[RNUnity ufw] sendMessageToGOWithName:[gameObject UTF8String] functionName:[functionName UTF8String] message:[message UTF8String]];
    }
}

+ (void)emitEvent:(const char*)name data:(const char*)data {
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

@end

#import <UnityFramework/UnityFramework.h>
#import "RNUProxy.h"

@protocol RNUnityProxy <NSObject>

- (void)emitEvent:(const char*)name data:(const char*)data;

@end

@interface UnityFramework (RNUnityProxy)

- (void)setRNUnityProxy:(id<RNUnityProxy>)proxy;

@end

static id<RNUnityProxy> _RNUnityProxy;

@implementation UnityFramework (RNUnityProxy)

- (void)setRNUnityProxy:(id<RNUnityProxy>)proxy {
    _RNUnityProxy = proxy;
}

@end

void RNUProxyEmitEvent(const char* name, const char* data) {
    if (_RNUnityProxy) {
        [_RNUnityProxy emitEvent:name data:data];
    } else {
        NSLog(@"ERROR: Make sure to invoke setRNUnityProxy before emitting events!");
    }
}

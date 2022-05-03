#import "RNUProxy.h"

@interface RNUnity : NSObject

+ (void)emitEvent:(const char*)name data:(const char*)data;

@end

void RNUProxyEmitEvent(const char* name, const char* data) {
    [RNUnity emitEvent:name data:data];
}

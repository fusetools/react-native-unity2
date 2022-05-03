#ifndef RNUProxy_h
#define RNUProxy_h

#if defined(__cplusplus)
#define RN_UNITY_EXTERN extern "C"
#else
#define RN_UNITY_EXTERN extern
#endif

RN_UNITY_EXTERN void RNUProxyEmitEvent(const char* name, const char* data);

#endif /* RNUProxy_h */

#include <stdexcept>

extern "C" void RNUProxyEmitEvent(const char* name, const char* data) {
    throw std::runtime_error("Not implemented on this platform");
}

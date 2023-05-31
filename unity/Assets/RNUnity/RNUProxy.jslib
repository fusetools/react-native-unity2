mergeInto(LibraryManager.library, {

    RNUProxyEmitEvent: function (name, data) {
        name = UTF8ToString(name)
        data = UTF8ToString(data)
        console.log("RNUProxyEmitEvent (not implemented for WebGL):", name, data)
    },

});

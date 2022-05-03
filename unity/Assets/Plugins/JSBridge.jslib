mergeInto(LibraryManager.library, {
    RNUProxyEmitEvent: function (name, data) {
        //name = UTF8ToString(name)
        //data = UTF8ToString(data)
        window.alert("Hello, world!");
    },
});

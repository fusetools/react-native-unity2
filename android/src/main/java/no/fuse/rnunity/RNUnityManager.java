package no.fuse.rnunity;

import static no.fuse.rnunity.RNUnityModule.*;
import no.fuse.rnunity.RNUnityModule.UnityPlayerCallback;

import android.os.Handler;
import android.view.View;
import android.util.Log;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ReactModule(name = RNUnityManager.REACT_CLASS)
public class RNUnityManager extends SimpleViewManager<RNUnityView> implements LifecycleEventListener, View.OnAttachStateChangeListener {
    public static final String REACT_CLASS = "UnityView";

    public static RNUnityView view;

    public RNUnityManager(ReactApplicationContext reactContext) {
        super();
        reactContext.addLifecycleEventListener(this);
    }

    @Nonnull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Nonnull
    @Override
    protected RNUnityView createViewInstance(@Nonnull ThemedReactContext reactContext) {
        Log.d("RNUnityManager", "createViewInstance");

        final RNUnityModule module = RNUnityModule.getInstance();
        view = new RNUnityView(reactContext);
        view.addOnAttachStateChangeListener(this);

        if (getPlayer() != null) {
            try {
                view.setUnityPlayer(getPlayer());
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {}
        } else {
            try {
                createPlayer(reactContext.getCurrentActivity(), new UnityPlayerCallback() {
                @Override
                public void onReady() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
                    view.setUnityPlayer(getPlayer());
                    module.emitEvent("ready", "");
                }
                
                @Override
                public void onUnload() {
                    module.emitEvent("onPlayerUnload", "");
                }
                
                @Override
                public void onQuit() {
                    module.emitEvent("onPlayerQuit", "");
                }
            });
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {}
    }
    
        return view;
    }

    @Override
    public void onDropViewInstance(RNUnityView view) {
        view.removeOnAttachStateChangeListener(this);
        super.onDropViewInstance(view);
    }

    @Override
    public void onHostResume() {
        if (isUnityReady()) {
        assert getPlayer() != null;
        getPlayer().resume();
        restoreUnityUserState();
        }
    }

    @Override
    public void onHostPause() {
        if (isUnityReady()) {
        assert getPlayer() != null;
        getPlayer().pause();
        }
    }

    @Override
    public void onHostDestroy() {
        if (isUnityReady()) {
        assert getPlayer() != null;
        getPlayer().destroy();
        }
    }

    @Override
    public void onViewAttachedToWindow(View v) {
        restoreUnityUserState();
    }

    @Override
    public void onViewDetachedFromWindow(View v) {}

    public void unloadUnity(RNUnityView view) {
        if (isUnityReady()) {
        getPlayer().unload();
        }
    }

    public void pauseUnity(RNUnityView view, boolean pause) {
        if (isUnityReady()) {
        assert getPlayer() != null;
        getPlayer().pause();
        }
    }

    public void resumeUnity(RNUnityView view) {
        if (isUnityReady()) {
        assert getPlayer() != null;
        getPlayer().resume();
        }
    }

    public void windowFocusChanged(RNUnityView view, boolean hasFocus) {
        if (isUnityReady()) {
        assert getPlayer() != null;
        getPlayer().windowFocusChanged(hasFocus);
        }
    }
    
    private void restoreUnityUserState() {
        // restore the unity player state
        if (isUnityPaused()) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            if (getPlayer() != null) {
                getPlayer().pause();
            }
            }
        }, 300);
        }
    }
}

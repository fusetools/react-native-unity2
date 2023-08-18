package no.fuse.rnunity;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.unity3d.player.IUnityPlayerLifecycleEvents;
import com.unity3d.player.UnityPlayer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nonnull;

public class RNUnityManager extends SimpleViewManager<UnityPlayer> implements LifecycleEventListener, View.OnAttachStateChangeListener, IUnityPlayerLifecycleEvents {
    public static final String REACT_CLASS = "UnityView";

    public static UnityPlayer player;

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
    protected UnityPlayer createViewInstance(@Nonnull ThemedReactContext reactContext) {
        Log.d("RNUnityManager", "createViewInstance");

        final RNUnityModule module = RNUnityModule.getInstance();

        final Activity activity = reactContext.getCurrentActivity();
        final Handler handler = new Handler(Looper.getMainLooper());

        final Window window = activity.getWindow();
        final int windowFlags = window.getAttributes().flags;

        if (player == null) {
            player = new UnityPlayer(activity, this);
        } else {
            // Force-remove parent view to avoid exceptions thrown
            resetPlayerParent();

            // Restart Unity after delay to workaround a glitch
            // where Unity sometimes seems to stop rendering
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("RNUnityManager", "Restarting Unity player");
                            player.pause();
                            player.resume();
                        }
                    });
                }
            }, 199);
        }

        // Restore original window flags after Unity has modified them
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int flags = windowFlags;
                boolean keepAwake = module.getKeepAwake();

                // Race condition: Keep awake might be enabled or disabled after
                // the original flags were saved
                if (keepAwake) {
                    flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                } else {
                    flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                }

                Log.d("RNUnityManager", "Resetting window flags; keepAwake=" + keepAwake);
                window.setFlags(flags, -1);
            }
        });

        // Race condition: Keep awake is sometimes turned off after a few
        // seconds. Check this every second for a couple of seconds and turn it
        // on again if necessary.
        for (int i = 2; i < 9; i++) {
            final int delay = i * 1000;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Window window = activity.getWindow();
                            final int windowFlags = window.getAttributes().flags;
                            final boolean existing = (windowFlags & WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) == WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                            final boolean keepAwake = module.getKeepAwake();

                            if (existing != keepAwake) {
                                Log.d("RNUnityManager", "Keep awake flag out of sync; delay=" + delay);
                                module.setKeepAwake(keepAwake);
                            }
                        }
                    });
                }
            }, delay);
        }

        player.addOnAttachStateChangeListener(this);
        player.windowFocusChanged(true);
        player.requestFocus();
        player.resume();

        // Notify the app that Unity is ready to receive messages
        module.emitEvent("ready", "");

        return player;
    }

    @Override
    public void onDropViewInstance(UnityPlayer view) {
        Log.d("RNUnityManager", "onDropViewInstance: " + view);

        view.removeOnAttachStateChangeListener(this);
        player.pause();
    }

    @Override
    public void onHostResume() {
        Log.d("RNUnityManager", "onHostResume");

        if (player != null)
            player.resume();
    }

    @Override
    public void onHostPause() {
        Log.d("RNUnityManager", "onHostPause");

        if (player != null)
            player.pause();
    }

    @Override
    public void onHostDestroy() {
        Log.d("RNUnityManager", "onHostDestroy");
    }

    @Override
    public void onViewAttachedToWindow(View view) {
        Log.d("RNUnityManager", "onViewAttachedToWindow: " + view);
    }

    @Override
    public void onViewDetachedFromWindow(View view) {
        Log.d("RNUnityManager", "onViewDetachedFromWindow: " + view);
    }

    @Override
    public void onUnityPlayerUnloaded() {
        Log.d("RNUnityManager", "onUnityPlayerUnloaded");
    }

    @Override
    public void onUnityPlayerQuitted() {
        Log.d("RNUnityManager", "onUnityPlayerQuitted");
    }

    static void resetPlayerParent() {
        if (player.getParent() == null)
            return;

        ((ViewGroup) player.getParent()).removeView(player);

        if (player.getParent() == null)
            return;

        Log.d("RNUnityManager", "Using reflection to reset parent!");

        try {
            Method method = View.class.getDeclaredMethod("assignParent", new Class<?>[]{ ViewParent.class });
            method.setAccessible(true);
            method.invoke(player, new Object[]{ null });
            method.setAccessible(false);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        if (player.getParent() == null)
            return;

        Log.e("RNUnityManager", "Unable to reset parent of player " + player);
    }
}

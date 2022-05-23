package no.fuse.rnunity;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.unity3d.player.IUnityPlayerLifecycleEvents;
import com.unity3d.player.UnityPlayer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nonnull;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class RNUnityManager extends SimpleViewManager<RNUnityView> implements LifecycleEventListener, View.OnAttachStateChangeListener, IUnityPlayerLifecycleEvents {
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
    protected RNUnityView createViewInstance(@Nonnull ThemedReactContext reactContext) {
        Log.d("RNUnityManager", "createViewInstance");

        final Activity activity = reactContext.getCurrentActivity();

        if (player == null) {
            player = new UnityPlayer(activity, this);
        } else {
            // Force-remove parent view to avoid exceptions thrown
            resetPlayerParent();

            // Restart Unity after delay to workaround a glitch
            // where Unity sometimes seem to stop rendering
            final Handler handler = new Handler(Looper.getMainLooper());
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

        RNUnityView view = new RNUnityView(activity, player);
        view.addOnAttachStateChangeListener(this);
        view.addView(player, 0, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));

        player.windowFocusChanged(true);
        player.requestFocus();
        player.resume();

        return view;
    }

    @Override
    public void onDropViewInstance(RNUnityView view) {
        Log.d("RNUnityManager", "onDropViewInstance: " + view);

        // Force-remove parent view to avoid exceptions thrown
        resetPlayerParent();

        // Move player to activity before pause
        Activity activity = (Activity) player.getContext();
        activity.addContentView(player, new ViewGroup.LayoutParams(1, 1));

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

class RNUnityView extends FrameLayout {
    private UnityPlayer player;

    public RNUnityView(Context context, UnityPlayer player) {
        super(context);
        this.player = player;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        Log.d("RNUnityView", "onWindowFocusChanged: " + hasWindowFocus);
        super.onWindowFocusChanged(hasWindowFocus);

        if (player != null) {
            player.windowFocusChanged(hasWindowFocus);
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        Log.d("RNUnityView", "onConfigurationChanged: " + newConfig);
        super.onConfigurationChanged(newConfig);

        if (player != null) {
            player.configurationChanged(newConfig);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.d("RNUnityView", "onDetachedFromWindow");
        super.onDetachedFromWindow();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        Log.d("RNUnityView", "onWindowVisibilityChanged: " + visibility);
        super.onWindowVisibilityChanged(visibility);
    }
}

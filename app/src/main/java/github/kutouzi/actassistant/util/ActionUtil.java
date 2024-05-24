package github.kutouzi.actassistant.util;

import android.accessibilityservice.GestureDescription;
import android.content.res.Resources;
import android.graphics.Path;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import github.kutouzi.actassistant.service.ACTFloatingWindowService;

public class ActionUtil {
    // 控制每一个上划间隔的参数
    private static final int _RANDOM_MAX_SWIPEUP_VALUE = 14000;
    private static final int _RANDOM_MIN_SWIPEUP_VALUE = 10000;

    // 控制上划操作所耗时间的参数
    private static final int _RANDOM_MAX_DELAY_VALUE = 400;
    private static final int _RANDOM_MIN_DELAY_VALUE = 200;

    // 可执行动作
    private static Runnable pendingAction = null;

    private static final Handler handler = new Handler();

    private static void performSwipeUp(String TAG,Resources resources,ACTFloatingWindowService actFloatingWindowService) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            float startX = getScreenWidth(resources) / 2f;
            float startY = getScreenHeight(resources) / 2f;
            float endX = startX;
            float endY = getScreenHeight(resources) / 4f;

            Path path = new Path();
            path.moveTo(startX, startY);
            path.lineTo(endX, endY);

            GestureDescription.StrokeDescription stroke = new GestureDescription.StrokeDescription(
                    path, 0, RandomtTimeUtil.getRandomDelayTillis(_RANDOM_MIN_DELAY_VALUE,_RANDOM_MAX_DELAY_VALUE));

            GestureDescription gesture = new GestureDescription.Builder()
                    .addStroke(stroke)
                    .build();
            actFloatingWindowService.dispatchGesture(gesture, null, null);
            Log.i(TAG,"上划被调用");
        }
    }

    private static int getScreenWidth(Resources resources) {
        return resources.getDisplayMetrics().widthPixels;
    }

    private static int getScreenHeight(Resources resources) {
        return resources.getDisplayMetrics().heightPixels;
    }

    public static void processSwipe(String TAG, boolean isInterrupted,Resources resources,ACTFloatingWindowService actFloatingWindowService) {
        if (!isInterrupted) {
            return;
        }
        if (pendingAction != null) {
            handler.removeCallbacks(pendingAction);
        }
        pendingAction = () -> {

            ActionUtil.performSwipeUp(TAG,resources,actFloatingWindowService);
            if (!isInterrupted) {
                handler.postDelayed(pendingAction, RandomtTimeUtil.getRandomDelayTillis(_RANDOM_MIN_SWIPEUP_VALUE,_RANDOM_MAX_SWIPEUP_VALUE));
            }
        };
        handler.post(pendingAction);
    }
    public static void removeSwipeAction(){
        handler.removeCallbacks(pendingAction);
    }
}

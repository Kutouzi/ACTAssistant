package github.kutouzi.actassistant.util;

import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import github.kutouzi.actassistant.enums.ApplicationDefinition;

public class MeituanUtil {
    public static int scanMeituanApplication(String TAG, CharSequence packageName){
        if(packageName.toString().contains("meituan")){
            Log.i(TAG,"美团正在运行于前台");
            return ApplicationDefinition.MEITUAN;
        }
        return ApplicationDefinition.NULLAPP;
    }
    public static void switchToVideo(String TAG,AccessibilityNodeInfo nodeInfo){
        if (nodeInfo != null){
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("视频");
            Log.i(TAG,"发现"+ list.size()+"个符合条件的节点");
            for (AccessibilityNodeInfo info:
                    list) {
                if(info.isClickable()){
                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.i(TAG,"已找到按钮并点击");
                }
            }
        }
    }
}

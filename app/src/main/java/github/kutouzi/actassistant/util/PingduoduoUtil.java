package github.kutouzi.actassistant.util;

import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import github.kutouzi.actassistant.enums.ApplicationDefinition;

public class PingduoduoUtil {
    private static final String _TAG = PingduoduoUtil.class.getName();
    public static int scanPingduoduoApplication(CharSequence packageName){
        if(packageName.toString().contains("pinduoduo")){
            Log.i(_TAG,"拼多多正在运行于前台");

            return ApplicationDefinition.PINGDUODUO;
        }
        return ApplicationDefinition.NULLAPP;
    }

    public static void switchToVideo(AccessibilityNodeInfo nodeInfo){
        if (nodeInfo != null){
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("多多视频");
            Log.i(_TAG,"发现"+ list.size()+"个符合条件的节点");
            for (AccessibilityNodeInfo info:
                    list) {
                if(info.isClickable()){
                    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Log.i(_TAG,"已找到按钮并点击");
                }
            }
        }
    }
}

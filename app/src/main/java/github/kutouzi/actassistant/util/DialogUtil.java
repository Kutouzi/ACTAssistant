package github.kutouzi.actassistant.util;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import github.kutouzi.actassistant.service.ACTFloatingWindowService;

public class DialogUtil {
    public static void clickDialog(String TAG, AccessibilityNodeInfo nodeInfo, ACTFloatingWindowService actFloatingWindowService, List<String> keyWordList) {
        for (String s: keyWordList) {
            if(nodeInfo.findAccessibilityNodeInfosByText(s) != null){
                List<AccessibilityNodeInfo> jiangliList = nodeInfo.findAccessibilityNodeInfosByText(s);
                for (AccessibilityNodeInfo info:
                        jiangliList) {
                    Log.i(TAG,"发现'"+ s + "'节点");
                    actFloatingWindowService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    AccessibilityNodeInfo i = TraverseNodeUtil.traverseParent(TAG,info);
                    if(i != null){
                        i.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
            }
        }
    }
    public static void cancelDialog(String TAG,AccessibilityNodeInfo nodeInfo,ACTFloatingWindowService actFloatingWindowService,List<String> keyWordList){
        for (String s:keyWordList) {
            if(!nodeInfo.findAccessibilityNodeInfosByText(s).isEmpty()){
                actFloatingWindowService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                Log.i(TAG,"找到'" + s + "'关闭了弹窗");
            }
        }
    }
}

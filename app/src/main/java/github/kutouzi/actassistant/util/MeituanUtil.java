package github.kutouzi.actassistant.util;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import github.kutouzi.actassistant.enums.ApplicationDefinition;
import github.kutouzi.actassistant.service.ACTFloatingWindowService;

public class MeituanUtil {
    private static final List<String> meituanKeyWordList = (ArrayList<String>) Stream.of("现金秒到账", "開")
            .collect(Collectors.toList());

    public static int meituanFunction(String TAG,CharSequence packageName,AccessibilityNodeInfo nodeInfo){
        if(packageName.toString().contains("meituan")){
            Log.i(TAG,"美团正在运行于前台");
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
            return ApplicationDefinition.MEITUAN;
        }
        return ApplicationDefinition.NULLAPP;

    }

    public static void cancelDialog(String TAG, AccessibilityNodeInfo nodeInfo, ACTFloatingWindowService actFloatingWindowService) {
        for (String s: meituanKeyWordList) {
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
}

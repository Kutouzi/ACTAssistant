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
import github.kutouzi.actassistant.util.inf.IApplicationUtil;

public class PingduoduoUtil implements IApplicationUtil {

    private static final List<String> pingduoduoKeyWordList = (ArrayList<String>) Stream.of("领取今日奖励","专属现金红包","立即提现","领取今日现金","明日继续来领","打款金额")
            .collect(Collectors.toList());

    public static int pingduoduoFunction(String TAG,CharSequence packageName,AccessibilityNodeInfo nodeInfo){
        if(packageName.toString().contains("pinduoduo")){
            Log.i(TAG,"拼多多正在运行于前台");
            if (nodeInfo != null){
                List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("多多视频");
                Log.i(TAG,"发现"+ list.size()+"个符合条件的节点");
                for (AccessibilityNodeInfo info:
                        list) {
                    if(info.isClickable()){
                        info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        Log.i(TAG,"已找到按钮并点击");
                    }
                }
            }
            return ApplicationDefinition.PINGDUODUO;
        }
        return ApplicationDefinition.NULLAPP;
    }

    @Override
    public void cancelDialog(String TAG, AccessibilityNodeInfo nodeInfo, ACTFloatingWindowService actFloatingWindowService) {
        for (String s: pingduoduoKeyWordList) {
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

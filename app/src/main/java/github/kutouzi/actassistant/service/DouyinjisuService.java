package github.kutouzi.actassistant.service;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import github.kutouzi.actassistant.exception.FailedTaskException;
import github.kutouzi.actassistant.util.ActionUtil;

public class DouyinjisuService extends ApplicationService{
    public static final int APPLICATION_INDEX = 3;
    public static final String PACKAGE_NAME = "com.ss.android.ugc.aweme.lite";
    public static final String NAME = "抖音极速版";
    private static final String _TAG = DouyinjisuService.class.getName();
    private static final DouyinjisuService INSTANCE = new DouyinjisuService();
    public static DouyinjisuService getInsatance(){
        return INSTANCE;
    }
    private DouyinjisuService() {
        super();
    }
    @Override
    public int scanApplication(CharSequence packageName) {
        if(packageName.toString().equals(PACKAGE_NAME)){
            Log.i(_TAG,NAME + "正在运行于前台");
            return APPLICATION_INDEX;
        }
        return NullService.APPLICATION_INDEX;
    }

    @Override
    public void switchToVideo(AccessibilityNodeInfo nodeInfo) {

    }
    public void autoCheckInTask(AccessibilityNodeInfo nodeInfo, AccessibilityService accessibilityService) throws FailedTaskException {
        int layers = 0;
        if(!ActionUtil.clickAction(nodeInfo,"赚钱")){
            ActionUtil.returnAction(accessibilityService,layers);
            throw new FailedTaskException("未能打开任务页");
        }
        layers++;
        if(!ActionUtil.findClickAction(nodeInfo,"签到领奖励","立即签到")){
            ActionUtil.returnAction(accessibilityService,layers);
            throw new FailedTaskException("未能点击到签到");
        }
        layers++;
        ActionUtil.returnAction(accessibilityService,layers);
    }
}

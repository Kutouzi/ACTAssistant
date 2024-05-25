package github.kutouzi.actassistant.service;

import static github.kutouzi.actassistant.MainActivity.CREATE_OR_DESTROY_ACT_FLOATING_WINGDOW_SERVICE;
import static github.kutouzi.actassistant.enums.ApplicationDefinition.MEITUAN;
import static github.kutouzi.actassistant.enums.ApplicationDefinition.PINGDUODUO;
import static github.kutouzi.actassistant.enums.ApplicationDefinition.PINGDUODUO_PAKAGENAME;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.NonNull;

import com.gsls.gt.GT;

import java.util.Objects;

import github.kutouzi.actassistant.MainActivity;
import github.kutouzi.actassistant.R;
import github.kutouzi.actassistant.exception.PakageNotFoundException;
import github.kutouzi.actassistant.util.ActionUtil;
import github.kutouzi.actassistant.util.DrawableUtil;
import github.kutouzi.actassistant.util.MeituanUtil;
import github.kutouzi.actassistant.util.PingduoduoUtil;
import github.kutouzi.actassistant.view.ToggleButtonLayout;


public class ACTFloatingWindowService extends AccessibilityService {
    private final String _TAG = getClass().getName();

    //////////////////////
    //悬浮窗窗体相关
    private WindowManager _windowManager;
    private View _windowView;
    //////////////////////////////////


    //////////////////////
    //悬浮窗内按钮相关
    private ToggleButtonLayout _returnMainActivityButton;

    private ToggleButtonLayout _listeningDialogButton;

    private ToggleButtonLayout _startApplicationButton;

    private ToggleButtonLayout _swipeUpButton;

    //////////////////////////////////


    //////////////////////
    //全局标记相关
    private static int _scanDialogFlag = 0;
    private boolean _isViewAdded = false;
    //////////////////////////////////

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (CREATE_OR_DESTROY_ACT_FLOATING_WINGDOW_SERVICE.equals(intent.getAction())) {
                if(Objects.equals(intent.getStringExtra("key"), "Create")){
                    if(_windowManager != null){
                        //如果这个窗口还存在，就是重复创建悬浮窗
                        if(!_isViewAdded){
                            _windowManager.addView(_windowView,getLayoutParams());
                        }
                    }else {
                        // 先创建悬浮窗
                        CreateFloatingWindow();
                        // 再创建悬浮窗里的开关
                        CreateListeningDialogSwitch();
                        CreateStartApplicationSwitch();
                        CreateReturnMainActivitySwitch();
                        CreateSwipeUpSwitch();
                        Log.i(_TAG,"悬浮窗已创建");
                    }
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter(CREATE_OR_DESTROY_ACT_FLOATING_WINGDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(broadcastReceiver, filter, Context.RECEIVER_EXPORTED);
        }else {
            registerReceiver(broadcastReceiver, filter);
        }
    }

    private void CreateFloatingWindow(){
        //创建悬浮窗
        if(_windowManager == null) {
            _windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        }

        Display display = _windowManager.getDefaultDisplay();
        display.getSize(new Point());

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        _windowView = inflater.inflate(R.layout.window_view, null);

        if (_windowView != null) {
            _windowManager.addView(_windowView, getLayoutParams());
            _isViewAdded = true;
        }
    }

    @NonNull
    private static WindowManager.LayoutParams getLayoutParams() {
        WindowManager.LayoutParams _layoutParams;
        // 设置悬浮窗参数
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            _layoutParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
            );
        }else {
            _layoutParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
            );
        }
        _layoutParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        return _layoutParams;
    }

    private void RemoveFloatingWindow(){
        if (_windowView != null) {
            _windowManager.removeView(_windowView);
            _isViewAdded = false;
            Log.i(_TAG, "悬浮窗被移除");
        }
    }

    private void CreateListeningDialogSwitch(){
        // 创建监听弹窗开关
        _listeningDialogButton = _windowView.findViewById(R.id.listeningDialogButton);
        _listeningDialogButton.setEnabled(true);
        // 根据按钮状态开启和禁用
        _listeningDialogButton.setOnClickListener(v->{
            if (_listeningDialogButton._isToggle){
                // 如果按钮已经被按过
                SwitchButtonColor(_listeningDialogButton);
                _listeningDialogButton._isToggle = false;
                SwitchOtherButtonStates();
                Log.i(_TAG,"服务开关已被禁用");
            }else {
                // 如果按钮没按过
                SwitchButtonColor(_listeningDialogButton);
                _listeningDialogButton._isToggle = true;
                SwitchOtherButtonStates();
                Log.i(_TAG,"服务开关已被禁用");
            }
        });
    }

    private void CreateStartApplicationSwitch(){
        // 创建开启引用开关
        _startApplicationButton = _windowView.findViewById(R.id.startApplicationButton);
        _startApplicationButton.setOnClickListener(v->{
            try{
                RequestStartApplication(PINGDUODUO_PAKAGENAME);
                _startApplicationButton._isToggle = true;
                _startApplicationButton.setEnabled(false);
            }catch (PakageNotFoundException e){
                Log.i(_TAG,e.getMessage());
                GT.toast_time("自动开启应用失败，请手动打开",8000);
            }
        });
    }

    private void RequestStartApplication(String pakageName) throws PakageNotFoundException {
        Intent intent = getPackageManager().getLaunchIntentForPackage(pakageName);
        if (intent != null){
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(intent);
        }else {
            throw new PakageNotFoundException(PINGDUODUO_PAKAGENAME);
        }

    }

    private void CreateReturnMainActivitySwitch(){
        //创建返回开关
        _returnMainActivityButton = _windowView.findViewById(R.id.returnMainActivityButton);
        _returnMainActivityButton.setOnClickListener(v->{
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            onInterrupt();
        });
    }

    private void CreateSwipeUpSwitch() {
        //创建上划开关
        _swipeUpButton = _windowView.findViewById(R.id.swipeUpButton);
        _swipeUpButton.setOnClickListener(v->{
            if(_swipeUpButton._isToggle){
                // 如果上划按钮被按过
                // 停止上划
                ActionUtil.removeSwipeAction();
                SwitchButtonColor(_swipeUpButton);
                _swipeUpButton._isToggle = false;
                SwitchOtherButtonStates();
                GT.toast_time("上划结束",1000);
            }else {
                // 如果上划按钮没被按过
                // 直接开始上划
                ActionUtil.processSwipe(_TAG,getResources(),this);
                SwitchButtonColor(_swipeUpButton);
                _swipeUpButton._isToggle = true;
                SwitchOtherButtonStates();
                GT.toast_time("上划开始",1000);
            }
        });
    }

    private void SwitchOtherButtonStates(){
        if(_swipeUpButton._isToggle || _listeningDialogButton._isToggle){
            // ft、tf 禁用
            // 如果这两个按钮任意一个被按下过
            _returnMainActivityButton.setClickable(false);
            _startApplicationButton.setClickable(false);
            DrawableUtil.setDrawableBackground(this, _returnMainActivityButton, 1, R.color.disable_button_rounded_color);
            DrawableUtil.setDrawableBackground(this, _startApplicationButton, 1, R.color.disable_button_rounded_color);
            Log.i(_TAG, "悬浮窗开启应用按钮和返回按钮已禁用");
        }else{
            // 如果没有被按下过
            _returnMainActivityButton.setClickable(true);
            _startApplicationButton.setClickable(true);
            DrawableUtil.setDrawableBackground(this, _returnMainActivityButton, 1, R.color.button_color);
            DrawableUtil.setDrawableBackground(this, _startApplicationButton, 1, R.color.button_color);
            Log.i(_TAG, "悬浮窗开启应用按钮和返回按钮已启用");

        }
    }

    private void SwitchButtonColor(ToggleButtonLayout toggleButtonLayout){
        if (toggleButtonLayout._isToggle){
            // 如果已经被按下，就变按下去的颜色
            DrawableUtil.setDrawableBackground(this,toggleButtonLayout,1,R.color.button_color);
        }else {
            // 如果没被按下过，就变没按下去的颜色
            DrawableUtil.setDrawableBackground(this,toggleButtonLayout,1,R.color.pressed_button_color);
        }

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(_windowView != null){
            // 需要启动应用按钮被按过才触发检测
            if(_startApplicationButton._isToggle){
                // 还需要窗口变化才能检测
                if(event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
                    if(getRootInActiveWindow().getPackageName() != null){
                        //执行检查拼多多、美团等软件是否启动的逻辑
                        _scanDialogFlag = PingduoduoUtil.pingduoduoFunction(_TAG,event.getPackageName(),getRootInActiveWindow())
                                + MeituanUtil.meituanFunction(_TAG,event.getPackageName(),getRootInActiveWindow());
                        if(_scanDialogFlag != 0){
                            if(!_swipeUpButton._isToggle){
                                // 如果没有被按下过
                                _swipeUpButton.callOnClick();
                            }
                            _startApplicationButton._isToggle = false;
                            _startApplicationButton.setEnabled(true);
                        }
                    }
                }
            }
            if(_listeningDialogButton._isToggle) {
                //检查拼多多、美团等软件是否启动后才会进入搜索dialog逻辑
                switch (_scanDialogFlag) {
                    case PINGDUODUO:
                        PingduoduoUtil.cancelDialog(_TAG, getRootInActiveWindow(), this);
                        break;
                    case MEITUAN:
                        MeituanUtil.cancelDialog(_TAG, getRootInActiveWindow(), this);
                        break;
                    default:
                        break;
                }

            }
        }
    }

    @Override
    public void onInterrupt() {
        RemoveFloatingWindow();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        Log.i(_TAG, "服务被释放");
    }
}

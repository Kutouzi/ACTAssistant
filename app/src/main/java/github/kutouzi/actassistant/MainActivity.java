package github.kutouzi.actassistant;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import github.kutouzi.actassistant.adapter.ClientViewAdapter;
import github.kutouzi.actassistant.databinding.ActivityMainBinding;
import github.kutouzi.actassistant.entity.ClientViewData;
import github.kutouzi.actassistant.service.ACTFloatingWindowService;

public class MainActivity extends AppCompatActivity  {

    static {
        System.loadLibrary("actassistant");
    }

    public static final String CREATE_OR_DESTROY_ACT_FLOATING_WINGDOW_SERVICE = "github.kutouzi.actassistant.CREATE_OR_DESTROY_ACT_FLOATING_WINGDOW_SERVICE";

    private final String _TAG = getClass().getName();
    private ActivityMainBinding _binding;

    // 除服务开关外，其他所有按钮的状态，false表示全部禁用
    private boolean _allButtonInWindowStartedState = true;


    private ImageButton _settingButton;
    private ImageButton _addClientButton;
    private ImageButton _startACTFloatingWindowServiceButton;
    private RecyclerView _clientRecyclerView;

    private final int _recyclerViewSpanCount = 3;
    private boolean _isStartACTFloatingWindowServiceButtonPressed = false;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        _binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(_binding.getRoot());

        createClientView();

        createSettingSwitch();
        createAddClientSwitch();
        createStartFloatingServiceWindowSwitch();

    }

    private void createClientView(){
        // 寻找clientRecyclerView的xml资源
        _clientRecyclerView = findViewById(R.id.clientView);

        // 设置布局管理器
        GridLayoutManager layoutManager = new GridLayoutManager(this, _recyclerViewSpanCount, GridLayoutManager.VERTICAL, false);
        _clientRecyclerView.setLayoutManager(layoutManager);

        // 创建数据集
        List<ClientViewData> clientViewData = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            clientViewData.add(new ClientViewData("192.168.1." + i, R.drawable.connection_lost));
        }

        // 创建adapter，准备为视图提供数据
        ClientViewAdapter adapter = new ClientViewAdapter(clientViewData);

        // 通过adapter把数据集绑定到clientRecyclerView以显示
        _clientRecyclerView.setAdapter(adapter);
    }

    private void createStartFloatingServiceWindowSwitch(){
        // 创建开启ACT悬浮窗的开关按钮
        _startACTFloatingWindowServiceButton = findViewById(R.id.serviceWindowButton);

        // 根据按钮状态打开或移除悬浮窗
        _startACTFloatingWindowServiceButton.setOnClickListener(v -> {
            // 判断用户有无悬浮窗权限
            if (!Settings.canDrawOverlays(v.getContext())) {
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
                Log.i(_TAG,"用户没有悬浮窗权限");
            }else {
                Log.i(_TAG, "用户已获取悬浮窗权限");
                // 判断此应用有没有在设置里获得无障碍模式权限
                AccessibilityManager accessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
                List<AccessibilityServiceInfo> enabledAccessibilityServiceList = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_SPOKEN);
                boolean isAccessibilityEnabled = isAccessibilityServiceEnabled(getPackageName(), enabledAccessibilityServiceList);
                if (!isAccessibilityEnabled) {
                    // 权限未开启，引导用户去开启
                    startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                    Log.i(_TAG, "用户没有无障碍权限");
                } else {
                    Log.i(_TAG, "用户已获取无障碍权限");
                    if (_isStartACTFloatingWindowServiceButtonPressed) {
                        // 向服务请求销毁悬浮窗
                        _isStartACTFloatingWindowServiceButtonPressed = false;
                    } else {
                        // 向服务请求开启悬浮窗
                        startService(new Intent(this, ACTFloatingWindowService.class));
                        requestCreateACTFloatingWindow();
                        // 隐藏此activity
                        moveTaskToBack(true);
                        _isStartACTFloatingWindowServiceButtonPressed = true;
                    }
                    switchOtherButtonStates();
                }
            }
        });
    }

    private void switchOtherButtonStates(){
        if(!_allButtonInWindowStartedState){
            _settingButton.setEnabled(true);
            _addClientButton.setEnabled(true);
            _allButtonInWindowStartedState = true;
            Log.i(_TAG,"主菜单其他按钮已启用");
        }else {
            _settingButton.setEnabled(false);
            _addClientButton.setEnabled(false);
            _allButtonInWindowStartedState = false;
            Log.i(_TAG,"主菜单其他按钮已禁用");
        }

    }
    private void createSettingSwitch(){
        _settingButton = findViewById(R.id.settingButton);
    }

    private void createAddClientSwitch(){
        _addClientButton = findViewById(R.id.addClientButton);
    }
    private void requestCreateACTFloatingWindow() {
        sendBroadcast(new Intent(CREATE_OR_DESTROY_ACT_FLOATING_WINGDOW_SERVICE).putExtra("key","Create"));
    }

    private boolean isAccessibilityServiceEnabled(String serviceName, List<AccessibilityServiceInfo> enabledAccessibilityServiceList) {
        for (AccessibilityServiceInfo info : enabledAccessibilityServiceList) {
            if (info.getResolveInfo().serviceInfo.packageName.equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

}
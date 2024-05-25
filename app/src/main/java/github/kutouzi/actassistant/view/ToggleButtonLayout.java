package github.kutouzi.actassistant.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;


public class ToggleButtonLayout extends LinearLayout {
    // 是否已触发
    public boolean _isToggle = false;


    public ToggleButtonLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}

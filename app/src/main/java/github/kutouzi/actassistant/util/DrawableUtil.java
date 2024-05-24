package github.kutouzi.actassistant.util;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;

import androidx.core.content.ContextCompat;

public class DrawableUtil {
    public static void setDrawableBackground(Context context, View view,int layerIndex,int newColor){
        LayerDrawable layerDrawable = (LayerDrawable) view.getBackground();
        GradientDrawable backgroundLayer = (GradientDrawable) layerDrawable.getDrawable(layerIndex);
        backgroundLayer.setColor(ContextCompat.getColor(context, newColor));
    }
}

package com.hermann.memorypics;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

/**
 * Created by OkanO on 11/14/2016.
 */

public class ResizeAnimation extends Animation {
    public View mView;
    public float mToHeight;
    public float mFromHeight;

    public float startTextSize[];
    public float endTextSize[];

    public float mToWidth;
    public float mFromWidth;

    public ResizeAnimation(View v, float fromWidth, float fromHeight, float toWidth, float toHeight) {
        mToHeight = toHeight;
        mToWidth = toWidth;
        mFromHeight = fromHeight;
        mFromWidth = fromWidth;
        mView = v;
//        setDuration(300);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float height = (mToHeight - mFromHeight) * interpolatedTime + mFromHeight;
        float width = (mToWidth - mFromWidth) * interpolatedTime + mFromWidth;

        float ratio = mToWidth/mFromWidth*1f;
        ViewGroup viewGroup = (ViewGroup)mView;
        if (startTextSize == null){
            startTextSize = new float[viewGroup.getChildCount()];
            endTextSize = new float[viewGroup.getChildCount()];
        }

        for (int i = 0; i < viewGroup.getChildCount(); i++){
            Object object = viewGroup.getChildAt(i);
            if (object instanceof TextView){
                TextView temp = (TextView) object;
                if (startTextSize[i] == 0){
                    startTextSize[i] = temp.getTextSize();
                    endTextSize[i] = startTextSize[i]*ratio;
                }

                float textSize = (endTextSize[i] - startTextSize[i]) * interpolatedTime + startTextSize[i];

                temp.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
//                temp.requestLayout();
            }
        }

//        ViewGroup.LayoutParams p = mView.getLayoutParams();
//        p.height = (int) height;
//        p.width = (int) width;
//        mView.requestLayout();
    }
}

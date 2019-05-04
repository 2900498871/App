package com.sc.main;

import com.bravin.btoast.BToast;

import org.litepal.LitePalApplication;

public class WeatherApplication extends LitePalApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        BToast.Config.getInstance()
//                .setAnimate() // Whether to startAnimation. default is fasle;
//                .setAnimationDuration()// Animation duration. default is 800 millisecond
//                .setAnimationGravity()// Animation entering position. default is BToast.ANIMATION_GRAVITY_TOP
//                .setDuration()// toast duration  is Either BToast.DURATION_SHORT or BToast.DURATION_LONG
//                .setTextColor()// textcolor. default is white
//                .setErrorColor()// error style background Color default is red
//                .setInfoColor()// info style background Color default is blue
//                .setSuccessColor()// success style background Color default is green
//                .setWarningColor()// waring style background Color default is orange
//                .setLayoutGravity()// whan show an toast with target, coder can assgin position relative to target. default is BToast.LAYOUT_GRAVITY_BOTTOM
//                .setLongDurationMillis()// long duration. default is 4500 millisecond
//                .setRadius()// radius. default is half of view's height. coder can assgin a positive value
//                .setRelativeGravity()// whan show an toast with target, coder can assgin position relative to toastself(like relativeLayout start end center), default is BToast.RELATIVE_GRAVITY_CENTER
//                .setSameLength()// sameLength.  whan layoutGravity is BToast.LAYOUT_GRAVITY_TOP or BToast.LAYOUT_GRAVITY_BOTTOM,sameLength mean toast's width is as same as target,otherwise is same height
//                .setShortDurationMillis()// short duration. default is 3000 millisecond
//                .setShowIcon()// show or hide icon
//                .setTextSize()// text size. sp unit
                .apply(this);// must call
    }
}

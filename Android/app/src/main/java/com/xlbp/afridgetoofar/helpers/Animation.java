package com.xlbp.afridgetoofar.helpers;

import android.animation.TimeInterpolator;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

public class Animation
{
    public Animation(View view)
    {
        _view = view;
        _alpha = _view.getAlpha();
        _timeInterpolator = new DecelerateInterpolator(f_animationFactor);
        _translationY = _view.getTranslationY();
        _startDelay = 0;

        _endAction = () ->
        {
        };
    }

    public Animation alpha(float alpha)
    {
        _alpha = alpha;
        return this;
    }

    public Animation translationY(float translationY)
    {
        _translationY = translationY;
        return this;
    }

    public Animation easeIn()
    {
        _timeInterpolator = new AccelerateInterpolator(f_animationFactor);
        return this;
    }

    public Animation easeOut()
    {
        _timeInterpolator = new DecelerateInterpolator(f_animationFactor);
        return this;
    }

    public Animation easeInOut()
    {
        _timeInterpolator = new AccelerateDecelerateInterpolator();
        return this;
    }

    public Animation startDelay(long startDelay)
    {
        _startDelay = startDelay;
        return this;
    }

    public Animation withEndAction(Runnable endAction)
    {
        _endAction = endAction;
        return this;
    }

    public void start()
    {
        // this is the equivalent of runOnUiThread
        new Handler(Looper.getMainLooper()).post(() ->
        {
            _view.animate()
                    .alpha(_alpha)
                    .translationY(_translationY)
                    .setDuration(f_animationTime)
                    .setInterpolator(_timeInterpolator)
                    .setStartDelay(_startDelay)
                    .withEndAction(_endAction);
        });

    }


    private View _view;
    private float _alpha;
    private TimeInterpolator _timeInterpolator;
    private float _translationY;
    private long _startDelay;
    private Runnable _endAction;

    private final int f_animationTime = 600;
    private final int f_animationFactor = 2;
}

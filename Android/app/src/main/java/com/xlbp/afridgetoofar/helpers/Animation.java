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
        _translationX = _view.getTranslationX();
        _translationY = _view.getTranslationY();
        _scaleX = 1;
        _scaleY = 1;
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

    public Animation translationX(float translationX)
    {
        _translationX = translationX;
        return this;
    }

    public Animation translationY(float translationY)
    {
        _translationY = translationY;
        return this;
    }

    public Animation scaleX(float scaleX)
    {
        _scaleX = scaleX;
        return this;
    }

    public Animation scaleY(float scaleY)
    {
        _scaleY = scaleY;
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
                    .translationX(_translationX)
                    .translationY(_translationY)
                    .scaleX(_scaleX)
                    .scaleY(_scaleY)
                    .setDuration(f_animationTime)
                    .setInterpolator(_timeInterpolator)
                    .setStartDelay(_startDelay)
                    .withEndAction(_endAction);
        });

    }


    private View _view;
    private float _alpha;
    private TimeInterpolator _timeInterpolator;
    private float _translationX;
    private float _translationY;
    private float _scaleX;
    private float _scaleY;
    private long _startDelay;
    private Runnable _endAction;

    private final int f_animationTime = 600;
    private final int f_animationFactor = 2;
}

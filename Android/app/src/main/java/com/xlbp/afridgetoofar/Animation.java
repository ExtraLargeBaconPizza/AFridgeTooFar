package com.xlbp.afridgetoofar;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class Animation
{
    public Animation(View view)
    {
        _view = view;
        _alpha = _view.getAlpha();
        _translationX = _view.getTranslationX();
        _translationY = _view.getTranslationY();
        _startDelay = 0;

        _runnable = () ->
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

    public Animation startDelay(long startDelay)
    {
        _startDelay = startDelay;
        return this;
    }

    public Animation withEndAction(Runnable runnable)
    {
        _runnable = runnable;
        return this;
    }

    public void start()
    {
        // this is the equivalent of runOnUiThread
        new Handler(Looper.getMainLooper()).post(() ->
        {
            _view.animate()
                    .alpha(_alpha)
                    .setDuration(f_animationTime)
                    .setInterpolator(new DecelerateInterpolator(f_animationFactor))
                    .setStartDelay(_startDelay)
                    .translationX(_translationX)
                    .translationY(_translationY)
                    .withEndAction(_runnable);
        });

    }


    private View _view;
    private float _alpha;
    private float _translationX;
    private float _translationY;
    private long _startDelay;
    private Runnable _runnable;

    private final int f_animationTime = 600;
    private final int f_animationFactor = 2;
}

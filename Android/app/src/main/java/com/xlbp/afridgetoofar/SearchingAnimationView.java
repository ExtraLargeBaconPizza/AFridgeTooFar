package com.xlbp.afridgetoofar;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.xlbp.afridgetoofar.helpers.Animation;
import com.xlbp.afridgetoofar.helpers.Helpers;

import java.util.ArrayList;

public class SearchingAnimationView extends FrameLayout
{
    public SearchingAnimationView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        init();
    }

    public void startAnimation()
    {
        _loopAnimation = true;

        runAnimation();
    }

    public void stopAnimation()
    {
        _loopAnimation = false;
    }

    private void init()
    {
        initViews();
    }

    private void initViews()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.searching_animation, this, true);

        _loadingBars = new ArrayList<>();

        LinearLayout loadingBarsLinearLayout = findViewById(R.id.loadingBarsLinearLayout);

        for (int i = 0; i < loadingBarsLinearLayout.getChildCount(); i++)
        {
            _loadingBars.add(loadingBarsLinearLayout.getChildAt(i));
        }

        _bottonPosition = Helpers.dpToPixels(150);
        _topPoisiton = -_bottonPosition;

        _delayTime = 25;
    }

    private void runAnimation()
    {
        for (View loadingBar : _loadingBars)
        {
            loadingBar.setAlpha(0);
            loadingBar.setTranslationY(_bottonPosition);
        }

        animateToMiddle();
    }

    private void animateToMiddle()
    {
        for (int i = 0; i < _loadingBars.size(); i++)
        {
            View loadingBar = _loadingBars.get(i);

            new Animation(loadingBar)
                    .alpha(1)
                    .translationY(0)
                    .startDelay(i * _delayTime)
                    .start();
        }

        int fullAnimationTime = (_loadingBars.size() * _delayTime) + 800;

        new Handler().postDelayed(() ->
        {
            if (_loopAnimation)
            {
                animateToTop();
            }
        }, fullAnimationTime);
    }

    private void animateToTop()
    {
        for (int i = 0; i < _loadingBars.size(); i++)
        {
            View loadingBar = _loadingBars.get(i);

            new Animation(loadingBar)
                    .alpha(0)
                    .translationY(_topPoisiton)
                    .easeIn()
                    .startDelay(i * _delayTime)
                    .start();
        }

        int fullAnimationTime = (_loadingBars.size() * _delayTime) + 800;

        new Handler().postDelayed(() ->
        {
            if (_loopAnimation)
            {
                runAnimation();
            }
        }, fullAnimationTime);
    }


    private ArrayList<View> _loadingBars;

    private int _topPoisiton;
    private int _bottonPosition;

    private int _delayTime;

    private boolean _loopAnimation;
}

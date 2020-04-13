package com.xlbp.afridgetoofar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

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

        startAnimation();
    }

    private void initViews()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.searching_animation4, this, true);

        _loadingBarTopView = findViewById(R.id.loadingBarTopView);
        _loadingBarBottomView = findViewById(R.id.loadingBarBottomView);

        _topPosition = -Helpers.dpToPixels(250);
        _bottomPosition = Helpers.dpToPixels(250);
    }

    private void runAnimation()
    {
        _loadingBarTopView.setAlpha(0);
        _loadingBarBottomView.setAlpha(0);

//        _loadingBarTopView.setTranslationY(_topPosition);
        _loadingBarBottomView.setTranslationY(_topPosition);

//        _loadingBarTopView.setScaleX(0);
        _loadingBarBottomView.setScaleX(1);

        animateToMiddle();
    }

    private void animateToMiddle()
    {
        if (_loopAnimation)
        {
//            new Animation(_loadingBarTopView)
//                    .alpha(1)
//                    .translationY(0)
//                    .start();

            new Animation(_loadingBarBottomView)
                    .alpha(1)
                    .translationY(_bottomPosition)
                    .withEndAction(() -> animateToTop())
                    .start();
        }
    }

    private void animateToCenter()
    {
        if (_loopAnimation)
        {
            new Animation(_loadingBarTopView)
                    .scaleX(0)
                    .start();

            new Animation(_loadingBarBottomView)
                    .scaleX(0)
                    .withEndAction(() -> animateFromCenter())
                    .start();
        }
    }

    private void animateFromCenter()
    {
        if (_loopAnimation)
        {
            new Animation(_loadingBarTopView)
                    .scaleX(1)
                    .start();

            new Animation(_loadingBarBottomView)
                    .scaleX(1)
                    .withEndAction(() -> animateFromMiddle())
                    .start();
        }
    }

    private void animateFromMiddle()
    {
        if (_loopAnimation)
        {
            new Animation(_loadingBarTopView)
                    .alpha(0)
                    .translationY(_topPosition)
                    .start();

            new Animation(_loadingBarBottomView)
                    .alpha(0)
                    .translationY(_bottomPosition)
                    .withEndAction(() -> runAnimation())
                    .start();
        }
    }

    private void animateToTop()
    {
        if (_loopAnimation)
        {
            new Animation(_loadingBarBottomView)
                    .scaleX(0)
                    .withEndAction(() -> runAnimation())
                    .start();
        }
    }


    private View _loadingBarTopView;
    private View _loadingBarBottomView;

    private int _topPosition;
    private int _bottomPosition;

    private boolean _loopAnimation;
}

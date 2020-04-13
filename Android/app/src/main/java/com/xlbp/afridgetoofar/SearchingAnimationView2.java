package com.xlbp.afridgetoofar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.xlbp.afridgetoofar.helpers.Animation;
import com.xlbp.afridgetoofar.helpers.Helpers;

import java.util.ArrayList;

public class SearchingAnimationView2 extends FrameLayout
{
    public SearchingAnimationView2(Context context, AttributeSet attrs)
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

        initAnimationPositions();

        startAnimation();
    }

    private void initViews()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.searching_animation, this, true);

        _loadingBars = new ArrayList<>();

        _loadingBars.add(findViewById(R.id.loadingBar1View));
        _loadingBars.add(findViewById(R.id.loadingBar2View));
        _loadingBars.add(findViewById(R.id.loadingBar3View));
        _loadingBars.add(findViewById(R.id.loadingBar4View));
        _loadingBars.add(findViewById(R.id.loadingBar5View));
        _loadingBars.add(findViewById(R.id.loadingBar6View));
        _loadingBars.add(findViewById(R.id.loadingBar7View));
        _loadingBars.add(findViewById(R.id.loadingBar8View));
        _loadingBars.add(findViewById(R.id.loadingBar9View));
        _loadingBars.add(findViewById(R.id.loadingBar10View));
        _loadingBars.add(findViewById(R.id.loadingBar11View));
        _loadingBars.add(findViewById(R.id.loadingBar12View));
        _loadingBars.add(findViewById(R.id.loadingBar13View));
        _loadingBars.add(findViewById(R.id.loadingBar14View));
        _loadingBars.add(findViewById(R.id.loadingBar15View));
    }

    private void initAnimationPositions()
    {
        _topPoisiton = -(Helpers.getScreenHeight() / 2) - Helpers.dpToPixels(10) - Helpers.getSafeInsetTop(getContext());
        _bottonPosition = (Helpers.getScreenHeight() / 2) + Helpers.dpToPixels(10) + Helpers.dpToPixels(48);
        _startPosition = -Helpers.getScreenWidth() / 2;
        _endPosition = Helpers.getScreenWidth() / 2;

        _topPoisiton = -Helpers.dpToPixels(150);
        _bottonPosition = Helpers.dpToPixels(150);
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
            int finalI = i;

            View loadingBar = _loadingBars.get(i);

            new Animation(loadingBar)
                    .alpha(1)
                    .translationY(0)
                    .startDelay(i * 100)
                    .withEndAction(() ->
                    {
                        if (finalI == _loadingBars.size() - 1 && _loopAnimation)
                        {
                            animateToTop();
                        }
                    })
                    .start();
        }
    }

    private void animateToTop()
    {
        for (int i = 0; i < _loadingBars.size(); i++)
        {
            View loadingBar = _loadingBars.get(i);

            int finalI = i;

            new Animation(loadingBar)
                    .alpha(0)
                    .translationY(_topPoisiton)
                    .easeIn()
                    .startDelay(i * 100)
                    .withEndAction(() ->
                    {
                        if (finalI == _loadingBars.size() - 1 && _loopAnimation)
                        {
                            runAnimation();
                        }
                    })
                    .start();
        }
    }


    private ArrayList<View> _loadingBars;
    private int _topPoisiton;
    private int _bottonPosition;
    private int _startPosition;
    private int _endPosition;

    private boolean _loopAnimation;
}

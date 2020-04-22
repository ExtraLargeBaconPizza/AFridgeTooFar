package com.xlbp.afridgetoofar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.xlbp.afridgetoofar.helpers.Animation;
import com.xlbp.afridgetoofar.helpers.Helpers;

import androidx.constraintlayout.widget.ConstraintLayout;

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

        initViewPositions();
    }

    private void initViews()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.searching_animation, this, true);

        _searchAnimationView = findViewById(R.id.searchBarView);
    }

    private void initViewPositions()
    {
        setStartingPosition();

        ConstraintLayout layout = findViewById(R.id.layout);

        // this is needed so we only get positions/heights after onLayout has happened
        layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                _searchAnimationViewWidth = _searchAnimationView.getWidth();

                // we need to remove the listener so positions are only set once
                layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void runAnimation()
    {
        setStartingPosition();

        animateToMiddle();
    }

    private void animateToMiddle()
    {
        if (_loopAnimation)
        {
            new Animation(_searchAnimationView)
                    .translationX(0)
                    .startDelay(600)
                    .withEndAction(this::animateToEnd)
                    .start();
        }
    }

    private void animateToEnd()
    {
        if (_loopAnimation)
        {
            new Animation(_searchAnimationView)
                    .translationX(_searchAnimationView.getWidth() + 10)
                    .startDelay(600)
                    .withEndAction(() ->
                    {
                        runAnimation();
                    })
                    .start();
        }
        else
        {
            setStartingPosition();
        }
    }

    private void setStartingPosition()
    {
        int translationX = _searchAnimationViewWidth != 0 ? _searchAnimationViewWidth : Helpers.dpToPixels(180);

        _searchAnimationView.setTranslationX(-translationX);
    }


    private View _searchAnimationView;
    private int _searchAnimationViewWidth;

    private boolean _loopAnimation;
}

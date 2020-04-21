package com.xlbp.afridgetoofar;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.xlbp.afridgetoofar.helpers.Animation;

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
        _searchAnimationView.setTranslationX(-_searchAnimationView.getWidth());

        ConstraintLayout layout = findViewById(R.id.layout);

        // this is needed so we only get positions/heights after onLayout has happened
        layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                _searchAnimationView.setTranslationX(-_searchAnimationView.getWidth());

                // we need to remove the listener so positions are only set once
                layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void runAnimation()
    {
        _searchAnimationView.setTranslationX(-_searchAnimationView.getWidth());

        animateToMiddle();
    }

    private void animateToMiddle()
    {
        new Animation(_searchAnimationView)
                .translationX(0)
                .start();

        new Handler().postDelayed(() ->
        {
            animateToEnd();
        }, 1800);
    }

    private void animateToEnd()
    {
        new Animation(_searchAnimationView)
                .translationX(_searchAnimationView.getWidth())
                .start();

        new Handler().postDelayed(() ->
        {
            if (_loopAnimation)
            {
                runAnimation();
            }
        }, 1200);
    }


    private View _searchAnimationView;

    private boolean _loopAnimation;
}

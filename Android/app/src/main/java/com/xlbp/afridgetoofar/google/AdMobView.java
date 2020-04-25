package com.xlbp.afridgetoofar.google;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.xlbp.afridgetoofar.R;

public class AdMobView extends FrameLayout
{
    // Money me. Money me now. Me need a lot of money.
    // this is just cut/paste from googles NativeAdvancedExample
    public AdMobView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        MobileAds.initialize(context, initializationStatus ->
        {
        });

        refreshAd();
    }

    public void setAdMobViewReadyRunnable(Runnable adMobViewReadyRunnable)
    {
        _adMobViewReadyRunnable = adMobViewReadyRunnable;
    }

    public void destroyNativeAd()
    {
        if (_nativeAd != null)
        {
            _nativeAd.destroy();
        }
    }

    public void refreshAd()
    {
        AdLoader.Builder builder = new AdLoader.Builder(getContext(), "ca-app-pub-9366961778606842/3078225067");

        // OnUnifiedNativeAdLoadedListener implementation.
        builder.forUnifiedNativeAd(unifiedNativeAd ->
        {
            // You must call destroy on old ads when you are done with them,
            // otherwise you will have a memory leak.
            if (_nativeAd != null)
            {
                _nativeAd.destroy();
            }
            _nativeAd = unifiedNativeAd;

            UnifiedNativeAdView adView = (UnifiedNativeAdView) LayoutInflater.from(getContext()).inflate(R.layout.ad_unified, null);

            populateUnifiedNativeAdView(adView);

            this.removeAllViews();
            this.addView(adView);
        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(true)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener()
        {
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void populateUnifiedNativeAdView(UnifiedNativeAdView adView)
    {
        // Set the media view.
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(_nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(_nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (_nativeAd.getBody() == null)
        {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        }
        else
        {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(_nativeAd.getBody());
        }

        if (_nativeAd.getCallToAction() == null)
        {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        }
        else
        {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(_nativeAd.getCallToAction());
        }

        if (_nativeAd.getIcon() == null)
        {
            adView.getIconView().setVisibility(View.GONE);
        }
        else
        {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    _nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (_nativeAd.getPrice() == null)
        {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        }
        else
        {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(_nativeAd.getPrice());
        }

        if (_nativeAd.getStore() == null)
        {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        }
        else
        {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(_nativeAd.getStore());
        }

        if (_nativeAd.getStarRating() == null)
        {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        }
        else
        {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(_nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (_nativeAd.getAdvertiser() == null)
        {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        }
        else
        {
            ((TextView) adView.getAdvertiserView()).setText(_nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(_nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = _nativeAd.getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent())
        {
            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks()
            {
                @Override
                public void onVideoEnd()
                {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    super.onVideoEnd();
                }
            });
        }

        _adMobViewReadyRunnable.run();
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        // View is now detached, and about to be destroyed

        destroyNativeAd();
    }


    private UnifiedNativeAd _nativeAd;
    private Runnable _adMobViewReadyRunnable;
}

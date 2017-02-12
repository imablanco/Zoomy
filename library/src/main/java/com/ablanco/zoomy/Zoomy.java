package com.ablanco.zoomy;

import android.app.Activity;
import android.view.View;

/**
 * Created by Álvaro Blanco Cabrero on 12/02/2017.
 * Zoomy.
 */

public class Zoomy {

    private static ZoomyConfig mDefaultConfig = new ZoomyConfig();

    private Zoomy() {
    }

    public static void setDefaultConfig(ZoomyConfig config) {
        mDefaultConfig = config;
    }

    public static class Builder {

        private boolean mDisposed = false;

        private ZoomyConfig mConfig;
        private Activity mActivity;
        private View mTargetView;

        public Builder(Activity activity) {
            this.mActivity = activity;
        }

        public Builder target(View target) {
            this.mTargetView = target;
            return this;
        }

        public Builder animateZooming(boolean animate) {
            if (mConfig == null) mConfig = new ZoomyConfig();
            this.mConfig.setZoomAnimationEnabled(animate);
            return this;
        }

        public Builder enableImmersiveMode(boolean enable) {
            if (mConfig == null) mConfig = new ZoomyConfig();
            this.mConfig.setImmersiveModeEnabled(enable);
            return this;
        }

        public void register() {
            if (mConfig == null) mConfig = mDefaultConfig;
            if (mActivity == null) throw new IllegalArgumentException("Activity must not be null");
            if (mTargetView == null)
                throw new IllegalArgumentException("Target view must not be null");
            mTargetView.setOnTouchListener(new ZoomableTouchListener(mActivity, mTargetView, mConfig));
            mDisposed = true;
        }

        private void checkNotDisposed(){
            if(mDisposed) throw new IllegalStateException("Builder already disposed");
        }

    }
}

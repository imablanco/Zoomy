package com.ablanco.zoomy;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.view.View;
import android.view.animation.Interpolator;

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

    public static void unregister(View view) {
        view.setOnTouchListener(null);
    }

    public static class Builder {

        private boolean mDisposed = false;

        private ZoomyConfig mConfig;
        private TargetContainer mTargetContainer;
        private View mTargetView;
        private ZoomListener mZoomListener;
        private Interpolator mZoomInterpolator;
        private TapListener mTapListener;
        private LongPressListener mLongPressListener;
        private DoubleTapListener mdDoubleTapListener;

        public Builder(Activity activity) {
            this.mTargetContainer = new ActivityContainer(activity);
        }

        public Builder(Dialog dialog) {
            this.mTargetContainer = new DialogContainer(dialog);
        }

        public Builder(DialogFragment dialogFragment) {
            this.mTargetContainer = new DialogFragmentContainer(dialogFragment);
        }

        public Builder target(View target) {
            this.mTargetView = target;
            return this;
        }

        public Builder animateZooming(boolean animate) {
            checkNotDisposed();
            if (mConfig == null) mConfig = new ZoomyConfig();
            this.mConfig.setZoomAnimationEnabled(animate);
            return this;
        }

        public Builder enableImmersiveMode(boolean enable) {
            checkNotDisposed();
            if (mConfig == null) mConfig = new ZoomyConfig();
            this.mConfig.setImmersiveModeEnabled(enable);
            return this;
        }

        public Builder interpolator(Interpolator interpolator) {
            checkNotDisposed();
            this.mZoomInterpolator = interpolator;
            return this;
        }

        public Builder zoomListener(ZoomListener listener) {
            checkNotDisposed();
            this.mZoomListener = listener;
            return this;
        }

        public Builder tapListener(TapListener listener) {
            checkNotDisposed();
            this.mTapListener = listener;
            return this;
        }

        public Builder longPressListener(LongPressListener listener) {
            checkNotDisposed();
            this.mLongPressListener = listener;
            return this;
        }


        public Builder doubleTapListener(DoubleTapListener listener) {
            checkNotDisposed();
            this.mdDoubleTapListener = listener;
            return this;
        }

        public void register() {
            checkNotDisposed();
            if (mConfig == null) mConfig = mDefaultConfig;
            if (mTargetContainer == null)
                throw new IllegalArgumentException("Target container must not be null");
            if (mTargetView == null)
                throw new IllegalArgumentException("Target view must not be null");
            mTargetView.setOnTouchListener(new ZoomableTouchListener(mTargetContainer, mTargetView,
                    mConfig, mZoomInterpolator, mZoomListener, mTapListener, mLongPressListener,
                    mdDoubleTapListener));
            mDisposed = true;
        }

        private void checkNotDisposed() {
            if (mDisposed) throw new IllegalStateException("Builder already disposed");
        }

    }
}

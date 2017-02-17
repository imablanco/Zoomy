package com.ablanco.zoomy;

import android.app.Activity;
import android.view.View;
import android.view.animation.Interpolator;

/**
 * Created by Álvaro Blanco Cabrero on 12/02/2017.
 * Zoomy.
 */

public class Zoomy {

    private static ZoomyConfig defaultConfig = new ZoomyConfig();

    private Zoomy() {
    }

    public static void setDefaultConfig(ZoomyConfig config) {
        defaultConfig = config;
    }

    public static class Builder {

        private boolean disposed = false;

        private ZoomyConfig config;
        private Activity activity;
        private View targetView;
        private ZoomListener zoomListener;
        private Interpolator zoomInterpolator;
        private TapListener tapListener;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder target(View target) {
            this.targetView = target;
            return this;
        }

        public Builder animateZooming(boolean animate) {
            checkNotDisposed();
            if ( config == null) config = new ZoomyConfig();
            this.config.setZoomAnimationEnabled(animate);
            return this;
        }

        public Builder enableImmersiveMode(boolean enable) {
            checkNotDisposed();
            if ( config == null) config = new ZoomyConfig();
            this.config.setImmersiveModeEnabled(enable);
            return this;
        }

        public Builder interpolator(Interpolator interpolator){
            checkNotDisposed();
            this.zoomInterpolator = interpolator;
            return this;
        }

        public Builder zoomListener(ZoomListener listener) {
            checkNotDisposed();
            this.zoomListener = listener;
            return this;
        }

        public Builder tapListener(TapListener listener) {
            checkNotDisposed();
            this.tapListener = listener;
            return this;
        }

        public void register() {
            checkNotDisposed();
            if ( config == null) config = defaultConfig;
            if ( activity == null) throw new IllegalArgumentException("Activity must not be null");
            if ( targetView == null)
                throw new IllegalArgumentException("Target view must not be null");
            targetView.setOnTouchListener(new ZoomableTouchListener( activity, targetView,
                    config, zoomInterpolator, zoomListener, tapListener ));
            disposed = true;
        }

        private void checkNotDisposed() {
            if ( disposed ) throw new IllegalStateException("Builder already disposed");
        }

    }
}

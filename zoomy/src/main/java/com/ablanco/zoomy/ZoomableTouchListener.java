package com.ablanco.zoomy;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

/**
 * Created by Álvaro Blanco Cabrero on 12/02/2017.
 * Zoomy.
 */

class ZoomableTouchListener implements View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {

    private static final int STATE_IDLE = 0;
    private static final int STATE_POINTER_DOWN = 1;
    private static final int STATE_ZOOMING = 2;

    private static final float MIN_SCALE_FACTOR = 1f;
    private static final float MAX_SCALE_FACTOR = 5f;

    private int state = STATE_IDLE;

    private Activity activity;
    private View target;
    private ImageView zoomableView;
    private View shadow;

    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private SimpleGestureListener gestureListener = new SimpleGestureListener(){
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if( tapListener != null) tapListener.onTap( target );
            return true;
        }
    };

    private float scaleFactor = 1f;

    private PointF currentMovementMidPoint = new PointF();
    private PointF initialPinchMidPoint = new PointF();
    private Point targetViewCords = new Point();

    private boolean animatingZoomEnding = false;

    private Interpolator endZoomingInterpolator;

    private ZoomyConfig config;
    private ZoomListener zoomListener;
    private final TapListener tapListener;

    private Runnable endingZoomAction = new Runnable() {
        @Override
        public void run() {
            removeFromDecorView( shadow );
            removeFromDecorView( zoomableView );
            target.setVisibility(View.VISIBLE);
            zoomableView = null;
            currentMovementMidPoint = new PointF();
            initialPinchMidPoint = new PointF();
            animatingZoomEnding = false;
            state = STATE_IDLE;

            if ( zoomListener != null) zoomListener.onViewEndedZooming( target );

            if ( config.isImmersiveModeEnabled()) showSystemUI();
        }
    };


    ZoomableTouchListener(Activity activity, View view, ZoomyConfig config, Interpolator interpolator,
                          ZoomListener zoomListener, TapListener tapListener) {
        this.activity = activity;
        this.target = view;
        this.config = config;
        this.endZoomingInterpolator = interpolator != null
                ? interpolator : new AccelerateDecelerateInterpolator();
        this.scaleGestureDetector = new ScaleGestureDetector(activity, this);
        this.gestureDetector = new GestureDetector(activity, gestureListener );
        this.zoomListener = zoomListener;
        this.tapListener = tapListener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent ev) {

        if ( animatingZoomEnding || ev.getPointerCount() > 2) return true;

        scaleGestureDetector.onTouchEvent(ev);
        gestureDetector.onTouchEvent(ev);

        int action = ev.getAction() & MotionEvent.ACTION_MASK;

        switch (action) {
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
                switch ( state ) {
                    case STATE_IDLE:
                        state = STATE_POINTER_DOWN;
                        break;
                    case STATE_POINTER_DOWN:
                        state = STATE_ZOOMING;
                        MotionUtils.midPointOfEvent( initialPinchMidPoint, ev);
                        startZoomingView( target );
                        break;
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if ( state == STATE_ZOOMING) {
                    MotionUtils.midPointOfEvent( currentMovementMidPoint, ev);
                    //because our initial pinch could be performed in any of the view edges,
                    //we need to substract this difference and add system bars height
                    //as an offset to avoid an initial transition jump
                    currentMovementMidPoint.x -= initialPinchMidPoint.x;
                    currentMovementMidPoint.y -= initialPinchMidPoint.y;
                    //because previous function returns the midpoint for relative X,Y coords,
                    //we need to add absolute view coords in order to ensure the correct position
                    currentMovementMidPoint.x += targetViewCords.x;
                    currentMovementMidPoint.y += targetViewCords.y;
                    float x = currentMovementMidPoint.x;
                    float y = currentMovementMidPoint.y;
                    zoomableView.setX(x);
                    zoomableView.setY(y);
                }

                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                switch ( state ) {
                    case STATE_ZOOMING:
                        endZoomingView();
                        break;
                    case STATE_POINTER_DOWN:
                        state = STATE_IDLE;
                        break;
                }


                break;

        }

        return true;
    }


    private void endZoomingView() {
        if ( config.isZoomAnimationEnabled()) {
            animatingZoomEnding = true;
            zoomableView.animate()
                    .x( targetViewCords.x)
                    .y( targetViewCords.y)
                    .scaleX(1)
                    .scaleY(1)
                    .setInterpolator( endZoomingInterpolator )
                    .withEndAction( endingZoomAction ).start();
        } else endingZoomAction.run();

    }


    private void startZoomingView(View view) {
        zoomableView = new ImageView( activity );
        zoomableView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        zoomableView.setImageBitmap(ViewUtils.getBitmapFromView(view));

        //show the view in the same coords
        targetViewCords = ViewUtils.getViewAbsoluteCords(view);

        zoomableView.setX( targetViewCords.x);
        zoomableView.setY( targetViewCords.y);

        if ( shadow == null) shadow = new View( activity );
        shadow.setBackgroundResource(0);

        addToDecorView( shadow );
        addToDecorView( zoomableView );

        //trick for simulating the view is getting out of his parent
        target.getParent().requestDisallowInterceptTouchEvent(true);
        target.setVisibility(View.INVISIBLE);

        if ( config.isImmersiveModeEnabled()) hideSystemUI();
        if ( zoomListener != null) zoomListener.onViewStartedZooming( target );


    }


    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        if ( zoomableView == null) return false;

        scaleFactor *= detector.getScaleFactor();

        // Don't let the object get too large.
        scaleFactor = Math.max(MIN_SCALE_FACTOR, Math.min( scaleFactor, MAX_SCALE_FACTOR));

        zoomableView.setScaleX( scaleFactor );
        zoomableView.setScaleY( scaleFactor );
        obscureDecorView( scaleFactor );

        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return zoomableView != null;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        scaleFactor = 1f;
    }

    private void addToDecorView(View v) {
        ((ViewGroup) activity.getWindow().getDecorView()).addView(v);
    }

    private void removeFromDecorView(View v) {
        ((ViewGroup) activity.getWindow().getDecorView()).removeView(v);
    }

    private void obscureDecorView(float factor) {
        //normalize value between 0 and 1
        float normalizedValue = (factor - MIN_SCALE_FACTOR) / (MAX_SCALE_FACTOR - MIN_SCALE_FACTOR);
        normalizedValue = Math.min(0.75f, normalizedValue * 2);
        int obscure = Color.argb((int) (normalizedValue * 255), 0, 0, 0);
        shadow.setBackgroundColor(obscure);
    }

    private void hideSystemUI() {
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN); // hide status ba;
    }

    private void showSystemUI() {
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }
}

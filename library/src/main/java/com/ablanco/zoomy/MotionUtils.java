package com.ablanco.zoomy;

import android.graphics.PointF;
import android.view.MotionEvent;

/**
 * Created by Álvaro Blanco Cabrero on 11/02/2017.
 * Zoomy.
 */
class MotionUtils {

    static void midPointOfEvent(PointF point, MotionEvent event){
        if (event.getPointerCount() == 2) {
            float x = event.getX(0) + event.getX(1);
            float y = event.getY(0) + event.getY(1);
            point.set(x / 2, y / 2);
        }
    }

}

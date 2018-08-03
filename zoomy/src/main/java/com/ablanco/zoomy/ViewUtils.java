package com.ablanco.zoomy;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.View;

/**
 * Created by Álvaro Blanco Cabrero on 11/02/2017.
 * Zoomy.
 */
class ViewUtils {

    static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    static Point getViewAbsoluteCords(View v) {
        int[] location = new int[2];
        v.getLocationInWindow(location);
        int x = location[0];
        int y = location[1];

        return new Point(x, y);
    }

    static void viewMidPoint(PointF point, View v) {
        float x = v.getWidth();
        float y = v.getHeight();
        point.set(x / 2, y / 2);
    }
}

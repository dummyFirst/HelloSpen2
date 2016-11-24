package com.example.pie.hellospen;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.samsung.android.sdk.pen.engine.SpenSimpleSurfaceView;
import com.samsung.android.sdk.pen.engine.SpenTouchListener;

/**
 * Created by monster on 2016-11-25.
 */

class UserCanvasView extends SpenSimpleSurfaceView implements SpenTouchListener {
    private Context _context ;
    public UserCanvasView(Context context){
        super(context) ;
        _context = context ;
    }

    @Override
    protected void onDraw( Canvas canvas ) {
        super.onDraw ( canvas );
        Toast.makeText (_context, "onDraw event", Toast.LENGTH_SHORT).show( ) ;
    }

    @Override
    public boolean onTouch( View view, MotionEvent motionEvent ) {

            Toast.makeText (_context, "onDraw event", Toast.LENGTH_SHORT).show( ) ;

        return false;
    }
}

package com.example.pie.hellospen;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;


/**
 * Created by pie on 16. 12. 9.
 */


class IncomingHandler extends Handler {
    private final WeakReference<FullscreenActivity> mActivityWeakReference;
    /**
     * for posting authentication attempts back to UI thread
     */



    IncomingHandler( FullscreenActivity activity ) {
        mActivityWeakReference = new WeakReference<FullscreenActivity>( activity );
    }

    @Override
    public void handleMessage( Message msg ) {
        FullscreenActivity activity = mActivityWeakReference.get( );
        if( activity != null ) {
            activity._canvasView.processMessage( msg );
        }
    }


}

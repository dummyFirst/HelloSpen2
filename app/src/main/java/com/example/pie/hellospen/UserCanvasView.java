package com.example.pie.hellospen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pen.Spen;
import com.samsung.android.sdk.pen.SpenSettingPenInfo;
import com.samsung.android.sdk.pen.document.SpenNoteDoc;
import com.samsung.android.sdk.pen.document.SpenPageDoc;
import com.samsung.android.sdk.pen.engine.SpenLongPressListener;
import com.samsung.android.sdk.pen.engine.SpenSimpleSurfaceView;
import com.samsung.android.sdk.pen.engine.SpenTouchListener;

import java.io.IOException;

/**
 * Created by monster on 2016-11-25.
 */

class UserCanvasView extends SpenSimpleSurfaceView {
    private Context _context;
    private SpenNoteDoc _noteDoc ;
    private SpenPageDoc _notePage ;
    private Rect _screenRect ;
    private boolean _isSpenFeatureEnabled ;
    private int _tooltype ;

    public UserCanvasView( Context context ) {
        super( context );
        _context = context ;

    }

    public void initialize( ) {
        final FullscreenActivity activity = (FullscreenActivity)_context ;

        _isSpenFeatureEnabled = activity.isSpenFeatureEnabled() ;

        _screenRect = new Rect( ) ;
        activity.getWindowManager().getDefaultDisplay().getRectSize( _screenRect );

        // Create SpenNoteDoc
        try {
            _noteDoc =
                    new SpenNoteDoc( _context, _screenRect.width( ), _screenRect.height( ) );
        } catch( IOException e ) {
            Toast.makeText( _context, "Cannot create new NoteDoc.",
                    Toast.LENGTH_SHORT ).show( );
            e.printStackTrace( );
            activity.finish( );
        } catch( Exception e ) {
            e.printStackTrace( );
            activity.finish( );
        }

        // Add a Page to NoteDoc, get an instance, and set it to the member variable.
        _notePage = _noteDoc.appendPage( );

        //**Change the background color to white.
        //mSpenPageDoc.setBackgroundColor(0xFFD6E6F5);
        _notePage.setBackgroundColor( Color.WHITE );
        _notePage.clearHistory( );
        setPageDoc( _notePage, true ) ;

        if( _isSpenFeatureEnabled == false ) {
            //**
            _tooltype = SpenSimpleSurfaceView.TOOL_FINGER;
            setToolTypeAction( _tooltype, SpenSimpleSurfaceView.ACTION_STROKE );
            Toast.makeText( _context,
                    "Device does not support Spen. \n You can draw stroke by finger.",
                    Toast.LENGTH_SHORT ).show( );
        }

        setZoomable( false );
        //**Change the pen info.
        SpenSettingPenInfo penInfo = new SpenSettingPenInfo( );
        penInfo.color = Color.BLACK;
        penInfo.size = 10;
        setPenSettingInfo( penInfo );

        //**Full Screen
        setLongPressListener( new SpenLongPressListener( ) {
            @Override
            public void onLongPressed( MotionEvent motionEvent ) {
                if( motionEvent.getToolType( 0 ) == MotionEvent.TOOL_TYPE_FINGER )
                    activity.toggle( );
            }
        } );
    }


}

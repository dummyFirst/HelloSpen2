package com.example.pie.hellospen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pen.Spen;
import com.samsung.android.sdk.pen.SpenSettingPenInfo;
import com.samsung.android.sdk.pen.document.SpenInvalidPasswordException;
import com.samsung.android.sdk.pen.document.SpenNoteDoc;
import com.samsung.android.sdk.pen.document.SpenPageDoc;
import com.samsung.android.sdk.pen.document.SpenUnsupportedTypeException;
import com.samsung.android.sdk.pen.document.SpenUnsupportedVersionException;
import com.samsung.android.sdk.pen.engine.SpenLongPressListener;
import com.samsung.android.sdk.pen.engine.SpenSimpleSurfaceView;
import com.samsung.android.sdk.pen.engine.SpenTouchListener;

import java.io.File;
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
    private String _curFileName ;
    private File _dir ;


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

        // Set the save directory for the file.
        _dir = new File ( Environment.getExternalStorageDirectory ().getAbsolutePath () + "/SPen/" );
        if ( !_dir.exists () ) {
            if ( !_dir.mkdirs () ) {
                Toast.makeText ( _context, "Save Path Creation Error", Toast.LENGTH_SHORT ).show ();
                return;
            }
        }

    }

    public void saveNoteFile( ) {
        final String fileName = _dir.getPath () + "/" + Utils.getTimeFileName () ;
        try {
            // Save NoteDoc
            _noteDoc.save ( fileName, false );
            Toast.makeText ( _context, "Save success to " + fileName, Toast.LENGTH_SHORT ).show ();
        } catch ( IOException e ) {
            Toast.makeText ( _context, "Cannot save NoteDoc file : " + fileName + ".",
                    Toast.LENGTH_SHORT ).show ();
            e.printStackTrace ();
            return;
            //return false;
        } catch ( Exception e ) {
            e.printStackTrace ();
            return;
            //return false;
        }

        /*
        //**Clear the view.
        _notePage.removeAllObject ();
        update ();
        */
    }

    public void loadNoteFile() {
        // Load the file list.
        final String[] fileList = Utils.setFileList ( _dir, _context );
        if ( fileList == null ) {
            return;
        }

        // Prompt Load File dialog.
        new AlertDialog.Builder ( _context ).setTitle ( "Select file" )
                .setItems ( fileList, new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick( DialogInterface dialog, int which ) {
                        String strFilePath = _dir.getPath () + '/' + fileList[ which ];
                        setSpdFile ( strFilePath );

                    }
                } ).show ();
    }

    private void setSpdFile( String fileName ) {
        _curFileName = fileName ;
        try {
            // Create NoteDoc with the selected file.
            SpenNoteDoc tmpSpenNoteDoc = new SpenNoteDoc ( _context,
                    fileName, _screenRect.width (),
                    SpenNoteDoc.MODE_WRITABLE, true );
            _noteDoc.close ();
            _noteDoc = tmpSpenNoteDoc;
            if ( _noteDoc.getPageCount () == 0 ) {
                _notePage = _noteDoc.appendPage ();
            } else {
                _notePage = _noteDoc.getPage ( _noteDoc.getLastEditedPageIndex () );
            }
            setPageDoc ( _notePage, true );
            update ();
            setZoomable ( false );

            Toast.makeText ( _context,
                    "Successfully loaded noteFile.",
                    Toast.LENGTH_SHORT ).show ();
        } catch ( IOException e ) {
            Toast.makeText ( _context, "Cannot open this file.", Toast.LENGTH_LONG ).show ();
        } catch ( SpenUnsupportedTypeException e ) {
            Toast.makeText ( _context, "This file is not supported.", Toast.LENGTH_LONG ).show ();
        } catch ( SpenInvalidPasswordException e ) {
            Toast.makeText ( _context, "This file is locked by a password.", Toast.LENGTH_LONG ).show ();
        } catch ( SpenUnsupportedVersionException e ) {
            Toast.makeText ( _context, "This file is the version that does not support.",
                    Toast.LENGTH_LONG ).show ();
        } catch ( Exception e ) {
            Toast.makeText ( _context, "Failed to load noteDoc.", Toast.LENGTH_LONG ).show ();
        }

    }

}

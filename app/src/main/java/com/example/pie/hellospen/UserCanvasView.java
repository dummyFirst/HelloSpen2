package com.example.pie.hellospen;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.backup.FullBackupDataOutput;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.view.MotionEventCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by monster on 2016-11-25.
 */

class UserCanvasView extends SpenSimpleSurfaceView {
    private Context _context;
    private FullscreenActivity _activity ;
    private SpenNoteDoc _noteDoc;
    private SpenPageDoc _notePage;
    private Rect _screenRect;
    private boolean _isSpenFeatureEnabled;
    private int _tooltype;

    private File _dir;
    private File _tempDir;
    private String _curFilePath;
    private String _saveFileName ;

    private TaskMode _taskMode;
    private boolean _ret ;

    boolean _isTouched ;
    Debug _d ;
    Debug _i ;
    public UserCanvasView( Context context ) {
        super( context );
        _context = context;
        _activity = (FullscreenActivity)_context ;
        _tooltype = SpenSimpleSurfaceView.TOOL_SPEN;
        _curFilePath = "";
        _ret = false ;
        _isTouched = false ;
        _d = new Debug( context, Debug.SHOW ) ;
        _i = new Debug( "dummy1", Debug.SHOW ) ;
    }


    public void initialize( ) {

        final FullscreenActivity activity = ( FullscreenActivity ) _context;
        _taskMode = activity._taskMode;

        _isSpenFeatureEnabled = activity.isSpenFeatureEnabled( );

        _screenRect = new Rect( );
        activity.getWindowManager( ).getDefaultDisplay( ).getRectSize( _screenRect );

        // Create SpenNoteDoc
        try {
            _noteDoc = new SpenNoteDoc( _context, _screenRect.width( ), _screenRect.height( ) );
        } catch( IOException e ) {
            Toast.makeText( _context, "Cannot create new NoteDoc.", Toast.LENGTH_SHORT ).show( );
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
        setPageDoc( _notePage, true );

        if( _isSpenFeatureEnabled == false ) {
            //**
            _tooltype = SpenSimpleSurfaceView.TOOL_FINGER;
            setToolTypeAction( _tooltype, SpenSimpleSurfaceView.ACTION_STROKE );
            Toast.makeText( _context, "Device does not support Spen. \n You can draw stroke by finger.", Toast.LENGTH_SHORT ).show( );
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
                if( motionEvent.getToolType( 0 ) == SpenSimpleSurfaceView.TOOL_FINGER )
                    activity.toggle( );
            }
        } );

        setTouchListener( new SpenTouchListener( ) {
            @Override
            public boolean onTouch( View view, MotionEvent motionEvent ) {
                if( motionEvent.getToolType( 0 ) == SpenSimpleSurfaceView.TOOL_SPEN &&
                        motionEvent.getAction( ) == MotionEvent.ACTION_UP ) {
                    _isTouched = true ;

                    if( _taskMode.isEdit() ) {
                        _taskMode.setTouched() ;
                        activity.enableMenuOnMode( _taskMode.get() );
                        _d.i( "UserCanvasView.onTouch : TaskMode = " + _taskMode.getString() ) ;
                    }
                }
                return false;
            }
        } );

        // Set the save directory for the file.
        _dir = new File( Environment.getExternalStorageDirectory( ).getAbsolutePath( ) + "/SPen/" );
        if( ! _dir.exists( ) ) {
            if( ! _dir.mkdirs( ) ) {
                Toast.makeText( _context, "Save Path Creation Error", Toast.LENGTH_SHORT ).show( );
                return;
            }
        }

        // Set the save temporary directory for the file.
        _tempDir = new File( Environment.getExternalStorageDirectory( ).getAbsolutePath( ) + "/SPen/.temp/" );
        if( ! _tempDir.exists( ) ) {
            if( ! _tempDir.mkdirs( ) ) {
                Toast.makeText( _context, "Save Temp Path Creation Error", Toast.LENGTH_SHORT ).show( );
                return;
            }
        }

    }

    boolean newNoteFile( ) {
        FullscreenActivity activity = ( FullscreenActivity ) _context;
        if( _isTouched ) {
            if( !saveNoteFile( ) ) {
                return false ;
            } else {

            }
        }
        _i.i( "newNoteFile() start" ) ;

        // Clear _noteDoc
        if( _noteDoc != null ) {
            try {
                _noteDoc.close( );
            } catch( Exception e ) {
                e.printStackTrace( );
            }
            _noteDoc = null;
        }

        // Create SpenNoteDoc
        try {
            _noteDoc = new SpenNoteDoc( _context, _screenRect.width( ), _screenRect.height( ) );
        } catch( IOException e ) {
            Toast.makeText( _context, "Cannot create new NoteDoc.", Toast.LENGTH_SHORT ).show( );
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
        setPageDoc( _notePage, true );
        _isTouched = false ;

        _d.i( "newNoteFile( ) end" ) ;
        return true ;
    }

    boolean saveNoteFile( ) {
        _i.i( "saveNoteFile() start : TaskMode = " + _taskMode.get() ) ;
        int taskMode = _taskMode.get( );
        if( taskMode == TaskMode.CREATE_TOUCHED || _curFilePath.length() == 0 ) {
            _saveFileName = Utils.getTimeFileName( );
        } else if( taskMode == TaskMode.LOAD_TOUCHED ) {
            _saveFileName = _curFilePath.substring( _curFilePath.lastIndexOf( "/" ) + 1,
                    _curFilePath.lastIndexOf( "." ) - 1 );
        } else if( taskMode == TaskMode.SAVED_TOUCHED ) {
            _saveFileName = _curFilePath.substring( _curFilePath.lastIndexOf( "/" ) + 1,
                    _curFilePath.lastIndexOf( "." ) - 1 );
        }
        _i.i( "_curFilePath : " + _curFilePath + "\n" +
            "_saveFileName : " + _saveFileName + "\n" +
                "TaskMode : " + _taskMode.getString( ) ) ;


        final FullscreenActivity activity = (FullscreenActivity)_context;
        _ret = false ;

        // Prompt Save File dialog to get the file name
        // and get its save format option (note file or image).
        LayoutInflater inflater = ( LayoutInflater ) _context.getSystemService( LAYOUT_INFLATER_SERVICE );
        final View layout = inflater.inflate( R.layout.save_file_dialog,
                ( ViewGroup ) findViewById( R.id.layout_root ) );

        AlertDialog.Builder dlg = new AlertDialog.Builder( activity );
        dlg.setView( layout );
        dlg.setIcon(  activity.getResources( ).getDrawable( android.R.drawable.ic_dialog_alert ) );

        final String fileName = _saveFileName;

        final EditText inputPath = ( EditText ) layout.findViewById( R.id.input_path );
        inputPath.setText( fileName );

        dlg.setTitle( "Enter the file name to be saved" )
                //   .setMessage( "Do you want to save the file :\n" + _saveFileName + "?" )
                .setPositiveButton( android.R.string.yes, new DialogInterface.OnClickListener( ) {
                    @Override
                    public void onClick( DialogInterface dialog, int which ) {
                        try {
                            // Save NoteDoc
                            _noteDoc.save( _dir.getAbsolutePath( ) + "/" +
                                    inputPath.getText( ) + ".spd", false );

                            _curFilePath = _dir.getAbsolutePath( ) + inputPath.getText( ) + ".spd";
                            Toast.makeText( _context, "Save success to " + inputPath.getText() + ".spd",
                                    Toast.LENGTH_SHORT ).show( );
                            _isTouched = false ;
                            activity.enableEdit( false );
                            _taskMode.set( TaskMode.SAVED );
                            activity.enableMenuOnMode( TaskMode.SAVED );
                            _i.i("save_item : TaskMode = " + _taskMode.getString()) ;
                            _ret = true ;
                        } catch( IOException e ) {
                            Toast.makeText( _context, "Cannot save NoteDoc file : " +
                                    _saveFileName + ".spd.", Toast.LENGTH_SHORT ).show( );
                            e.printStackTrace( );
                        } catch( Exception e ) {
                            e.printStackTrace( );
                        }
                    }
                } )
            .setNegativeButton( android.R.string.no, new DialogInterface.OnClickListener( ) {
                @Override
                public void onClick( DialogInterface dialog, int which ) {
                    dialog.dismiss( );
                }
        } ).show( );

        _i.i( "saveNoteFile() end");
        return _ret ;
    }

    boolean openFileDialog( ) {
        if( _taskMode.isTouched() ) {
            if( !saveNoteFile( ) ) {
                return false ;
            } else {

            }
        }

        _i.i("openFileDialog start") ;
        // Load the file list.
        final String[] fileList = Utils.setFileList( _dir, _context );
        if( fileList == null ) {
            return false ;
        }

        // Prompt Load File dialog.
        AlertDialog.Builder dialog = new AlertDialog.Builder( _context )
            .setTitle( "Select file" )
            .setItems( fileList, new DialogInterface.OnClickListener( ) {
                @Override
                public void onClick( DialogInterface dialog, int which ) {
                    String strFilePath = _dir.getPath( ) + '/' + fileList[ which ];
                    loadSpdFile( strFilePath );
                    _curFilePath = strFilePath;
                    _isTouched = false ;
                    _i.i("openFileDialog -> dialog : " + strFilePath) ;
                    _activity.enableEdit( false );
                    _taskMode.set( TaskMode.LOAD );
                    _activity.enableMenuOnMode( TaskMode.LOAD );
                    _i.i( "load_item : TaskMode = " + _taskMode.getString( ) );
                }
        } );

        dialog.show( );

        _i.i("openFileDialog end") ;
        return true ;
    }

    private void loadSpdFile( String filePath ) {
        try {
            // Create NoteDoc with the selected file.
            SpenNoteDoc tmpSpenNoteDoc = new SpenNoteDoc( _context, filePath,
                    _screenRect.width( ), SpenNoteDoc.MODE_WRITABLE, true );
            _noteDoc.close( );
            _noteDoc = tmpSpenNoteDoc;
            if( _noteDoc.getPageCount( ) == 0 ) {
                _notePage = _noteDoc.appendPage( );
            } else {
                _notePage = _noteDoc.getPage( _noteDoc.getLastEditedPageIndex( ) );
            }
            setPageDoc( _notePage, true );
            update( );
            setZoomable( false );


            String fileName = filePath.substring( filePath.lastIndexOf( "/" ) + 1,
                    filePath.length() - 1 ) ;
            Toast.makeText( _context, "Successfully loaded noteFile : " + fileName,
                    Toast.LENGTH_SHORT ).show( );


        } catch( IOException e ) {
            Toast.makeText( _context, "Cannot open this file.", Toast.LENGTH_LONG ).show( );
        } catch( SpenUnsupportedTypeException e ) {
            Toast.makeText( _context, "This file is not supported.", Toast.LENGTH_LONG ).show( );
        } catch( SpenInvalidPasswordException e ) {
            Toast.makeText( _context, "This file is locked by a password.", Toast.LENGTH_LONG ).show( );
        } catch( SpenUnsupportedVersionException e ) {
            Toast.makeText( _context, "This file is the version that does not support.", Toast.LENGTH_LONG ).show( );
        } catch( Exception e ) {
            Toast.makeText( _context, "Failed to load noteDoc.", Toast.LENGTH_LONG ).show( );
        }

    }

}

package com.example.pie.hellospen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
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
import java.io.FilenameFilter;
import java.io.IOException;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 2000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    //private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
        /*  mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);*/
            mSpenSimpleSurfaceView.setSystemUiVisibility ( View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION );
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };

    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.save_exit_item:
                String fileName = _dir.getPath () + "/" + Utils.getTimeFileName () ;
                saveNoteFile( fileName );
                return true;
            
            case R.id.load_item:
                loadNoteFile() ;
                return true;
                
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Context mContext;
    private SpenNoteDoc mSpenNoteDoc;
    private SpenPageDoc mSpenPageDoc;
    //**private SpenSimpleSurfaceView mSpenSimpleSurfaceView;
    private UserCanvasView mSpenSimpleSurfaceView;

    private int _tooltype ;
    //**private Button saveButton ;

    private File _dir ;
    private File _tempDir ;
    private Rect _screenRect ;

    private int _mode ;
    private final int MODE_FIRST_EDIT = 1;
    private final int MODE_READ = 2;
    private final int MODE_EDIT = 3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        //**mContentView = findViewById(R.id.fullscreen_content);


        // Set up the user interaction to manually show or hide the system UI.
        /*
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        */


        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.edit_button).setOnTouchListener(mDelayHideTouchListener);
        //**
        findViewById(R.id.edit_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hide() ;
                        mSpenSimpleSurfaceView.setToolTypeAction(_tooltype,
                                SpenSimpleSurfaceView.ACTION_STROKE);
                    }
                });

        mContext = this;

        // Initialize Spen
        boolean isSpenFeatureEnabled = false;
        Spen spenPackage = new Spen ();
        try {
            spenPackage.initialize ( this );
            isSpenFeatureEnabled = spenPackage.isFeatureEnabled ( Spen.DEVICE_PEN );
            //**
            _tooltype = SpenSimpleSurfaceView.TOOL_SPEN ;
        } catch ( SsdkUnsupportedException e ) {
            if ( Utils.processUnsupportedException ( this, e ) == true ) {
                return;
            }
        } catch ( Exception e1 ) {
            Toast.makeText ( mContext, "Cannot initialize Spen.",
                    Toast.LENGTH_SHORT ).show ();
            e1.printStackTrace ();
            finish ();
        }

        // Create Spen View
        RelativeLayout spenViewLayout =
                ( RelativeLayout ) findViewById ( R.id.spenViewLayout );
        //**
        mSpenSimpleSurfaceView = new UserCanvasView ( mContext );
        if ( mSpenSimpleSurfaceView == null ) {
            Toast.makeText ( mContext, "Cannot create new SpenView.",
                    Toast.LENGTH_SHORT ).show ();
            finish ();
        }
        spenViewLayout.addView ( mSpenSimpleSurfaceView );

        // Get the dimension of the device screen.
        Display display = getWindowManager ().getDefaultDisplay ();
        _screenRect = new Rect ();
        display.getRectSize ( _screenRect );

        // Create SpenNoteDoc
        try {
            mSpenNoteDoc =
                    new SpenNoteDoc ( mContext, _screenRect.width (), _screenRect.height () );
        } catch ( IOException e ) {
            Toast.makeText ( mContext, "Cannot create new NoteDoc.",
                    Toast.LENGTH_SHORT ).show ();
            e.printStackTrace ();
            finish ();
        } catch ( Exception e ) {
            e.printStackTrace ();
            finish ();
        }
        // Add a Page to NoteDoc, get an instance, and set it to the member variable.
        mSpenPageDoc = mSpenNoteDoc.appendPage ();

        //**Change the background color to white.
        //mSpenPageDoc.setBackgroundColor(0xFFD6E6F5);
        mSpenPageDoc.setBackgroundColor ( Color.WHITE );

        mSpenPageDoc.clearHistory ();
        // Set PageDoc to View.
        mSpenSimpleSurfaceView.setPageDoc ( mSpenPageDoc, true );

        if ( isSpenFeatureEnabled == false ) {
            //**
            _tooltype = SpenSimpleSurfaceView.TOOL_FINGER ;
            mSpenSimpleSurfaceView.setToolTypeAction ( _tooltype, SpenSimpleSurfaceView.ACTION_STROKE );
            Toast.makeText ( mContext,
                    "Device does not support Spen. \n You can draw stroke by finger.",
                    Toast.LENGTH_SHORT ).show ();
        }

        mSpenSimpleSurfaceView.setZoomable(false);
        //**Change the pen info.
        SpenSettingPenInfo penInfo = new SpenSettingPenInfo ();
        penInfo.color = Color.BLACK;
        penInfo.size = 10 ;
        mSpenSimpleSurfaceView.setPenSettingInfo ( penInfo );

        //**Full Screen
        mSpenSimpleSurfaceView.setLongPressListener ( new SpenLongPressListener () {
            @Override
            public void onLongPressed ( MotionEvent motionEvent ) {
                if( motionEvent.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER )
                    toggle( ) ;
            }
        } );

        // Set the save directory for the file.
        _dir = new File ( Environment.getExternalStorageDirectory ().getAbsolutePath () + "/SPen/" );
        if (!_dir.exists()) {
            if (!_dir.mkdirs()) {
                Toast.makeText(mContext, "Save Path Creation Error", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Set the save temporary directory for the file.
        _tempDir = new File ( Environment.getExternalStorageDirectory ().getAbsolutePath () + "/SPen/.temp/" );
        if (!_tempDir.exists()) {
            if (!_tempDir.mkdirs()) {
                Toast.makeText(mContext, "Save Temp Path Creation Error",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }

    }

    private void saveNoteFile(final String fileName) {
        try {
            // Save NoteDoc
            mSpenNoteDoc.save(fileName, false);
            Toast.makeText(mContext, "Save success to " + fileName, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(mContext, "Cannot save NoteDoc file : " + fileName + ".",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return ;
            //return false;
        } catch (Exception e) {
            e.printStackTrace();
            return ;
            //return false;
        }

        //**Clear the view.
        mSpenPageDoc.removeAllObject();
        mSpenSimpleSurfaceView.update();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mSpenSimpleSurfaceView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy ();

        if ( mSpenSimpleSurfaceView != null ) {
            mSpenSimpleSurfaceView.close ();
            mSpenSimpleSurfaceView = null;
        }

        if ( mSpenNoteDoc != null ) {
            try {
                mSpenNoteDoc.close ();
            } catch ( Exception e ) {
                e.printStackTrace ();
            }
            mSpenNoteDoc = null;
        }
    }

    private void loadNoteFile( ) {
        // Load the file list.
        final String[ ] fileList = Utils.setFileList(_dir, mContext);
        if (fileList == null) {
            return;
        }

        // Prompt Load File dialog.
        new AlertDialog.Builder(mContext).setTitle("Select file")
                .setItems(fileList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strFilePath = _dir.getPath() + '/' + fileList[which];

                        try {
                            // Create NoteDoc with the selected file.
                            SpenNoteDoc tmpSpenNoteDoc = new SpenNoteDoc(mContext,
                                    strFilePath, _screenRect.width(),
                                    SpenNoteDoc.MODE_WRITABLE, true);
                            mSpenNoteDoc.close();
                            mSpenNoteDoc = tmpSpenNoteDoc;
                            if (mSpenNoteDoc.getPageCount() == 0) {
                                mSpenPageDoc = mSpenNoteDoc.appendPage();
                            } else {
                                mSpenPageDoc = mSpenNoteDoc.getPage(mSpenNoteDoc.getLastEditedPageIndex());
                            }
                            mSpenSimpleSurfaceView.setPageDoc(mSpenPageDoc, true);
                            mSpenSimpleSurfaceView.update();
                            //**Disable Spen action.
                            mSpenSimpleSurfaceView.setToolTypeAction( _tooltype,
                                    SpenSimpleSurfaceView.ACTION_NONE );
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                            mSpenSimpleSurfaceView.setZoomable(false);
                            Toast.makeText(mContext,
                                    "Successfully loaded noteFile.", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            Toast.makeText(mContext, "Cannot open this file.", Toast.LENGTH_LONG).show();
                        } catch (SpenUnsupportedTypeException e) {
                            Toast.makeText(mContext, "This file is not supported.", Toast.LENGTH_LONG).show();
                        } catch (SpenInvalidPasswordException e) {
                            Toast.makeText(mContext, "This file is locked by a password.", Toast.LENGTH_LONG).show();
                        } catch (SpenUnsupportedVersionException e) {
                            Toast.makeText(mContext, "This file is the version that does not support.",
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(mContext, "Failed to load noteDoc.", Toast.LENGTH_LONG).show();
                        }
                    }
                }).show();
    }


}//**End FullscreenActivity

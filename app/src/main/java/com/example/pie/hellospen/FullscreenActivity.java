package com.example.pie.hellospen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
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
import com.samsung.android.sdk.pen.document.SpenNoteDoc;
import com.samsung.android.sdk.pen.document.SpenPageDoc;
import com.samsung.android.sdk.pen.engine.SpenSimpleSurfaceView;

import java.io.File;


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
    private final Handler mHideHandler = new Handler( );
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener( ) {
        @Override
        public boolean onTouch( View view, MotionEvent motionEvent ) {
            if( AUTO_HIDE ) {
                delayedHide( AUTO_HIDE_DELAY_MILLIS );
            }
            return false;
        }
    };
    MenuItem _new_item;
    MenuItem _load_item;
    MenuItem _save_item;
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable( ) {
        @Override
        public void run( ) {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar( );
            if( actionBar != null ) {
                actionBar.show( );
            }
            mControlsView.setVisibility( View.VISIBLE );
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable( ) {
        @Override
        public void run( ) {
            hide( );
        }
    };
    private Context _context;
    private SpenNoteDoc _noteDoc;
    private SpenPageDoc _notePage;
    //**private SpenSimpleSurfaceView _canvasView;
    private UserCanvasView _canvasView;
    //**private Button saveButton ;
    //private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable( ) {
        @SuppressLint("InlinedApi")
        @Override
        public void run( ) {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.

            _canvasView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION );
        }
    };
    private int _tooltype;
    private Button _editButton;
    private boolean _isSpenFeatureEnabled;
    private TaskMode _taskMode;

    public boolean isSpenFeatureEnabled( ) {
        return _isSpenFeatureEnabled;
    }

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_fullscreen );

        mVisible = true;
        mControlsView = findViewById( R.id.fullscreen_content_controls );

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.edit_button).setOnTouchListener(mDelayHideTouchListener);
        //**
        _editButton = (Button) findViewById( R.id.edit_button );
        _editButton.setOnClickListener(
                new View.OnClickListener( ) {
                    @Override
                    public void onClick( View v ) {
                        enableEdit( true );
                        hide( );
                    }
                } );

        _context = this;

        _taskMode = new TaskMode( );

        // Initialize Spen
        _isSpenFeatureEnabled = false;
        Spen spenPackage = new Spen( );
        try {
            spenPackage.initialize( this );
            _isSpenFeatureEnabled = spenPackage.isFeatureEnabled( Spen.DEVICE_PEN );
            //**
            _tooltype = SpenSimpleSurfaceView.TOOL_SPEN;
        } catch( SsdkUnsupportedException e ) {
            if( Utils.processUnsupportedException( this, e ) == true ) {
                return;
            }
        } catch( Exception e1 ) {
            Toast.makeText( _context, "Cannot initialize Spen.",
                    Toast.LENGTH_SHORT ).show( );
            e1.printStackTrace( );
            finish( );
        }

        // Create Spen View
        RelativeLayout spenViewLayout =
                (RelativeLayout) findViewById( R.id.spenViewLayout );
        //**
        _canvasView = new UserCanvasView( _context );
        if( _canvasView == null ) {
            Toast.makeText( _context, "Cannot create new SpenView.",
                    Toast.LENGTH_SHORT ).show( );
            finish( );
        }
        spenViewLayout.addView( _canvasView );
        _canvasView.initialize( );

        _taskMode.setTaskMode( TaskMode.TASK_CREATE );

        Toast.makeText( _context,
                "TaskMode : " + _taskMode.getTaskMode(),
                Toast.LENGTH_SHORT ).show( ) ;

    }

    @Override
    protected void onPostCreate( Bundle savedInstanceState ) {
        super.onPostCreate( savedInstanceState );

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide( 100 );
    }

    public void toggle( ) {
        if( mVisible ) {
            hide( );
        } else {
            show( );
        }
    }

    private void hide( ) {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar( );
        if( actionBar != null ) {
            actionBar.hide( );
        }
        mControlsView.setVisibility( View.GONE );
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks( mShowPart2Runnable );
        mHideHandler.postDelayed( mHidePart2Runnable, UI_ANIMATION_DELAY );
    }

    @SuppressLint("InlinedApi")
    private void show( ) {
        // Show the system bar
        _canvasView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION );
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks( mHidePart2Runnable );
        mHideHandler.postDelayed( mShowPart2Runnable, UI_ANIMATION_DELAY );
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide( int delayMillis ) {
        mHideHandler.removeCallbacks( mHideRunnable );
        mHideHandler.postDelayed( mHideRunnable, delayMillis );
    }


    @Override
    protected void onDestroy( ) {
        super.onDestroy( );

        if( _canvasView != null ) {
            _canvasView.close( );
            _canvasView = null;
        }

        if( _noteDoc != null ) {
            try {
                _noteDoc.close( );
            } catch( Exception e ) {
                e.printStackTrace( );
            }
            _noteDoc = null;
        }
    }

    public boolean onCreateOptionsMenu( Menu menu ) {
        MenuInflater inflater = getMenuInflater( );
        inflater.inflate( R.menu.menu_layout, menu );
        _new_item = menu.getItem( 0 );
        _load_item = menu.getItem( 1 );
        _save_item = menu.getItem( 2 );
        enableEdit( true );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Handle item selection
        switch( item.getItemId( ) ) {
            case R.id.save_item:
                _canvasView.saveNoteFile( );
                enableEdit( false );
                _taskMode.setTaskMode( TaskMode.TASK_SAVED );
                delayedHide( AUTO_HIDE_DELAY_MILLIS );
                Toast.makeText( _context,
                        "TaskMode : " + _taskMode.getTaskMode(),
                        Toast.LENGTH_SHORT ).show( ) ;
                return true;

            case R.id.load_item:
                _canvasView.openFileDialog( );
                enableEdit( false );
                _taskMode.setTaskMode( TaskMode.TASK_LOAD );

                delayedHide( AUTO_HIDE_DELAY_MILLIS );
                Toast.makeText( _context,
                        "TaskMode : " + _taskMode.getTaskMode(),
                        Toast.LENGTH_SHORT ).show( ) ;
                return true;

            default:
                return super.onOptionsItemSelected( item );
        }
    }

    public TaskMode getTaskMode( ) {
        return _taskMode;
    }

    public void enableMenuItem( int id, boolean isEnable ) {
        ( (MenuItem) findViewById( id ) ).setEnabled( isEnable );
    }

    private void enableEdit( boolean b ) {
        if( b == true ) {
            _canvasView.setToolTypeAction( _tooltype,
                    SpenSimpleSurfaceView.ACTION_STROKE );
            _editButton.setEnabled( false );
            _save_item.setEnabled( true );
            if( _taskMode.getTaskMode( ) == TaskMode.TASK_CREATE ) {
                _taskMode.setTaskMode( TaskMode.TASK_CREATE_EDIT );
            } else if( _taskMode.getTaskMode( ) == TaskMode.TASK_LOAD ) {
                _taskMode.setTaskMode( TaskMode.TASK_LOAD_EDIT );
            } else if( _taskMode.getTaskMode( ) == TaskMode.TASK_SAVED ) {
                _taskMode.setTaskMode( TaskMode.TASK_SAVED_EDIT );
            }
        } else {
            _canvasView.setToolTypeAction( _tooltype,
                    SpenSimpleSurfaceView.ACTION_NONE );
            _editButton.setEnabled( true );
            _save_item.setEnabled( false );
        }
        Toast.makeText( _context,
                "TaskMode : " + _taskMode.getTaskMode(),
                Toast.LENGTH_SHORT ).show( ) ;
    }

}//**End FullscreenActivity

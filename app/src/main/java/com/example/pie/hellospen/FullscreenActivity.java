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

import static com.example.pie.hellospen.TaskMode.CREATE_EDIT;


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

    TaskMode _taskMode;
    boolean _isEditable ;

    public boolean isSpenFeatureEnabled( ) {
        return _isSpenFeatureEnabled;
    }

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_fullscreen );

        _isEditable = false ;
        mVisible = true;
        mControlsView = findViewById( R.id.fullscreen_content_controls );

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.edit_button).se tOnTouchListener(mDelayHideTouchListener);
        //**
        _editButton = ( Button ) findViewById( R.id.edit_button );
        _editButton.setOnClickListener(
                new View.OnClickListener( ) {
                    @Override
                    public void onClick( View v ) {
                        if( !_isEditable ) {
                            enableEdit( true );
                            enableMenuOnMode( _taskMode.get( ) );
                        } else {
                            if( _taskMode.isTouched() ) {
                                if( _canvasView.saveNoteFile() ) {
                                    _taskMode.set( TaskMode.SAVED ) ;
                                    enableMenuOnMode( _taskMode.get( ) );
                                }
                            }
                            enableEdit( false );
                        }
                        hide( );
                    }
                } );

        _context = this;

        _taskMode = new TaskMode( this );

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
                ( RelativeLayout ) findViewById( R.id.spenViewLayout );
        //**
        _canvasView = new UserCanvasView( _context );
        if( _canvasView == null ) {
            Toast.makeText( _context, "Cannot create new SpenView.",
                    Toast.LENGTH_SHORT ).show( );
            finish( );
        }
        spenViewLayout.addView( _canvasView );
        _canvasView.initialize( );

        _taskMode.set( TaskMode.CREATE );

    }


    public boolean onCreateOptionsMenu( Menu menu ) {
        MenuInflater inflater = getMenuInflater( );
        inflater.inflate( R.menu.menu_layout, menu );

        _new_item = menu.getItem( 0 );
        _load_item = menu.getItem( 1 );
        _save_item = menu.getItem( 2 );
        enableMenuOnMode( _taskMode.get( ) );
        enableEdit( true ) ;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        boolean b = false ;
        // Handle item selection
        switch( item.getItemId( ) ) {
        case R.id.new_item:
            b = _canvasView.newNoteFile( );
            if( !b ) return false ;
            _taskMode.set( TaskMode.CREATE );
            enableMenuOnMode( TaskMode.CREATE );
            enableEdit( true );
            delayedHide( AUTO_HIDE_DELAY_MILLIS );
            return true;

        case R.id.save_item:
            b = _canvasView.saveNoteFile( );
            if( !b ) return false ;
            enableEdit( false );
            _taskMode.set( TaskMode.SAVED );
            enableMenuOnMode( TaskMode.SAVED );
            delayedHide( AUTO_HIDE_DELAY_MILLIS );
            return true;

        case R.id.load_item:
            b = _canvasView.openFileDialog( );
            if( !b ) return false ;
            enableEdit( false );
            _taskMode.set( TaskMode.LOAD );
            enableMenuOnMode( TaskMode.LOAD );
            delayedHide( AUTO_HIDE_DELAY_MILLIS );
            return true;

        default:
            return super.onOptionsItemSelected( item );
        }
    }
    
    void enableMenuOnMode( final int mode ) {
        if( mode == TaskMode.CREATE ) {
            _new_item.setEnabled( false ) ;
            _load_item.setEnabled( true ) ;
            _save_item.setEnabled( false ) ;
        } else if( mode == TaskMode.CREATE_EDIT ) {
            _new_item.setEnabled( false ) ;
            _load_item.setEnabled( true ) ;
            _save_item.setEnabled( false ) ;
        } else if( mode == TaskMode.CREATE_TOUCHED ) {
            _new_item.setEnabled( true ) ;
            _load_item.setEnabled( true ) ;
            _save_item.setEnabled( true ) ;
        } else if( mode == TaskMode.LOAD ) {
            _new_item.setEnabled( true ) ;
            _load_item.setEnabled( true ) ;
            _save_item.setEnabled( false ) ;
        } else if( mode == TaskMode.LOAD_EDIT ) {
            _new_item.setEnabled( true ) ;
            _load_item.setEnabled( true ) ;
            _save_item.setEnabled( false ) ;
        } else if( mode == TaskMode.LOAD_TOUCHED ) {
            _new_item.setEnabled( true ) ;
            _load_item.setEnabled( true ) ;
            _save_item.setEnabled( true ) ;
        } else if( mode == TaskMode.SAVED ) {
            _new_item.setEnabled( true ) ;
            _load_item.setEnabled( true ) ;
            _save_item.setEnabled( false ) ;
        } else if( mode == TaskMode.SAVED_EDIT ) {
            _new_item.setEnabled( true ) ;
            _load_item.setEnabled( true ) ;
            _save_item.setEnabled( false ) ;
        } else if( mode == TaskMode.SAVED_TOUCHED ) {
            _new_item.setEnabled( true ) ;
            _load_item.setEnabled( true ) ;
            _save_item.setEnabled( true ) ;
        }
    }
    
    void enableEdit( boolean b ) {
        if( b == true ) {
            _isEditable = true ;
            _canvasView.setToolTypeAction( _tooltype,
                    SpenSimpleSurfaceView.ACTION_STROKE );
            _editButton.setText("Read");
            _taskMode.setEdit( ) ;
            //_editButton.setEnabled( false );
        } else {
            _isEditable = false ;
            _canvasView.setToolTypeAction( _tooltype,
                    SpenSimpleSurfaceView.ACTION_NONE );
            _editButton.setText("Edit");
            //_editButton.setEnabled( true );
        }
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

    }


}//**End FullscreenActivity

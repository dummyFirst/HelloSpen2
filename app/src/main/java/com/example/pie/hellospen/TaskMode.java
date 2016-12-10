package com.example.pie.hellospen;

import android.app.Activity;
import android.content.Context;

import com.samsung.android.sdk.pen.SpenSettingViewInterface;
import com.samsung.android.sdk.pen.engine.SpenSimpleSurfaceView;

/**
 * Created by young on 2016-11-26.
 */

class TaskMode {
    
    static final int NONE = -1;
    static final int CREATE = 10;
    static final int CREATE_EDIT = 11;
    static final int CREATE_TOUCHED = 12;
    static final int CREATE_ERASER = 13;


    static final int LOAD = 20;
    static final int LOAD_EDIT = 21;
    static final int LOAD_TOUCHED = 22;
    static final int LOAD_ERASER = 23;

    static final int SAVED = 30;
    static final int SAVED_EDIT = 31;
    static final int SAVED_TOUCHED = 32;
    static final int SAVED_ERASER = 33;

    private int _mode;
    private int _oldMode ;
    
    Context _context ;
    Debug _i ;

    TaskMode( final Context context ) {
        _mode = NONE;
        _oldMode = NONE ;
        _context = context ;
        _i = new Debug( "dummy1", Debug.SHOW ) ;
    }

    int get( ) {
        return _mode;
    }

    void set( final int taskMode ) {
        _mode = taskMode;

        final SpenSimpleSurfaceView canvasView = ( ( FullscreenActivity ) _context )._canvasView;
        if( isTouched() ) {
            if( canvasView.getToolTypeAction(
                    SpenSettingViewInterface.TOOL_SPEN ) == SpenSimpleSurfaceView.ACTION_STROKE )
                canvasView.setToolTipEnabled( false );
        } else {
            canvasView.setToolTipEnabled( true );
        }

        _i.i( "TaskMode : " +  getString() ) ;
    }

    /*
        edit, touched
     */
    void setTouched( ) {
        if( _mode == CREATE_EDIT ) {
            set( CREATE_TOUCHED );
        } else if( _mode == LOAD_EDIT ) {
            set( LOAD_TOUCHED );
        } else if( _mode == SAVED_EDIT ) {
            set( SAVED_TOUCHED );
        } else if( isEraserOn () ){
            switch ( _mode ) {
            case CREATE_ERASER:
                _oldMode = CREATE_TOUCHED;
                break;
            case LOAD_ERASER:
                _oldMode = LOAD_TOUCHED;
                break;
            case SAVED_ERASER:
                _oldMode = SAVED_TOUCHED;
                break;
            }
        } else {
            _i.i ( "setTouched : _mode is not ~_EDIT" );
        }

    }

    void setEdit( ) {
        if( _mode == CREATE ) {
            set( CREATE_EDIT ) ;
        } else if( _mode == LOAD ) {
            set( LOAD_EDIT ) ;
        } else if( _mode == SAVED ) {
            set( SAVED_EDIT ) ;
        } else {
            _i.i( "setTouched : _mode is NOT first" ) ;
        }
    }
    
    String getString( ) {
        String str = null ;
        switch( _mode ) {
        case CREATE :
            str = "CREATE" ;
            break ;
        case CREATE_EDIT :
            str = "CREATE_EDIT" ;
            break ;
        case CREATE_TOUCHED :
            str = "CREATE_TOUCHED" ;
            break ;
        case CREATE_ERASER :
            str = "CREATE_ERASER" ;
            break ;
        
        case LOAD :
            str = "LOAD" ;
            break ;
        case LOAD_EDIT :
            str = "LOAD_EDIT" ;
            break ;
        case LOAD_TOUCHED :
            str = "LOAD_TOUCHED" ;
            break ;
        case LOAD_ERASER :
            str = "LOAD_ERASER" ;
            break ;

        case SAVED :
            str = "SAVED" ;
            break ;
        case SAVED_EDIT :
            str = "SAVED_EDIT" ;
            break ;
        case SAVED_TOUCHED :
            str = "SAVED_TOUCHED" ;
            break ;
        case SAVED_ERASER :
            str = "SAVED_ERASER" ;
            break ;

        }
        return str ;
    }

    boolean isTouched( ) {
        boolean b = false ;

        switch( _mode ) {
        case CREATE_TOUCHED :
        case LOAD_TOUCHED :
        case SAVED_TOUCHED :
            b = true ;
            break ;
        }

        return b ;
    }

    boolean isEdit( ) {
        boolean b = false ;

        switch( _mode ) {
            case CREATE_EDIT :
            case LOAD_EDIT :
            case SAVED_EDIT :
                b = true ;
                break ;
        }

        return b ;
    }

    void diableEdit( ) {
        if( _mode == CREATE_EDIT ) {
            set( CREATE );
        } else if( _mode == LOAD_EDIT ) {
            set( LOAD );
        } else if( _mode == SAVED_EDIT ) {
            set( SAVED );
        }
    }
    
    boolean isEraserOn( ) {
        boolean b = false ;
        switch( _mode ) {
        case CREATE_ERASER :
        case LOAD_ERASER :
        case SAVED_ERASER :
            b = true ;
            break ;
        }
        return b ;
    }

    void enableEraser( ) {
        _oldMode = _mode ;
        if( _mode == CREATE_EDIT || _mode == CREATE_TOUCHED ) {
            set( CREATE_ERASER ) ;
        } else if( _mode == LOAD_EDIT || _mode == LOAD_TOUCHED ) {
            set( LOAD_ERASER ) ;
        } else if( _mode == SAVED_EDIT || _mode == SAVED_TOUCHED ) {
            set( SAVED_ERASER ) ;
        } else {
            _i.i( "setTouched : _mode is NOT first" ) ;
        }
    }

    void diableEraser( ) {
        set( _oldMode ) ;
    }

    int getOldMode( ) {
        return _oldMode ;
    }

}

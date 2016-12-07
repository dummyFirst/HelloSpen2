package com.example.pie.hellospen;

import android.app.Activity;
import android.content.Context;

/**
 * Created by young on 2016-11-26.
 */

class TaskMode {

    static final int NONE = - 1;
    static final int CREATE = 10;
    static final int CREATE_EDIT = 11;
    static final int CREATE_TOUCHED = 12;
    static final int LOAD = 20;
    static final int LOAD_EDIT = 21;
    static final int LOAD_TOUCHED = 22;
    static final int SAVED = 30;
    static final int SAVED_EDIT = 31;
    static final int SAVED_TOUCHED = 32;

    private int _mode;
    Context _context ;
    Debug _debug ;

    TaskMode( final Context context ) {
        _mode = NONE;
        _context = context ;
        _debug = new Debug( context, Debug.SHOW ) ;
    }

    int get( ) {
        return _mode;
    }

    void set( final int taskMode ) {
        _mode = taskMode;
        _debug.p( "TaskMode : " +  _mode ) ;
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
        } else {
            _debug.p( "setTouched : _mode is not ~_EDIT" ) ;
        }
        _debug.p( "TaskMode : " +  _mode ) ;
    }

    void setEdit( ) {
        if( _mode == CREATE ) {
            set( CREATE_EDIT ) ;
        } else if( _mode == LOAD ) {
            set( LOAD_EDIT ) ;
        } else if( _mode == SAVED ) {
            set( SAVED_EDIT ) ;
        } else {
            _debug.p( "setTouched : _mode is NOT first" ) ;
        }
        _debug.p( "TaskMode : " +  _mode ) ;
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
        case LOAD :
            str = "LOAD" ;
            break ;
        case LOAD_EDIT :
            str = "LOAD_EDIT" ;
            break ;
        case LOAD_TOUCHED :
            str = "LOAD_TOUCHED" ;
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

}

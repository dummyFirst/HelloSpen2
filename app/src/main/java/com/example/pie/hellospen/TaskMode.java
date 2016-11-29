package com.example.pie.hellospen;

import android.app.Activity;
import android.content.Context;

/**
 * Created by young on 2016-11-26.
 */

class TaskMode {

    public static final int TASK_NONE = 0 ;
    public static final int TASK_CREATE = 10 ;
    public static final int TASK_CREATE_EDIT = 11 ;
    public static final int TASK_CREATE_TOUCHED = 12 ;
    public static final int TASK_LOAD = 20 ;
    public static final int TASK_LOAD_EDIT = 21 ;
    public static final int TASK_LOAD_TOUCHED = 22 ;
    public static final int TASK_SAVED = 30 ;
    public static final int TASK_SAVED_EDIT = 31;
    public static final int TASK_SAVED_TOUCHED = 32 ;

    private int _taskMode ;

    public TaskMode( ) {
        _taskMode = TASK_NONE ;
    }

    public int get( ) {
        return _taskMode ;
    }

    public void set( final int taskMode ) {
        _taskMode = taskMode;
    }

    /*
        edit, touched
     */
    public void set(String label) {
        if( _taskMode % 10 == 1 && label.equalsIgnoreCase( "touched" ) ) {
            _taskMode += 1;
        }
    }

}

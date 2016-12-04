package com.example.pie.hellospen;

import android.app.Activity;
import android.content.Context;

/**
 * Created by young on 2016-11-26.
 */

class TaskMode {

    static final int TASK_NONE = - 1;
    static final int TASK_CREATE = 10;
    static final int TASK_CREATE_EDIT = 11;
    static final int TASK_CREATE_TOUCHED = 12;
    static final int TASK_LOAD = 20;
    static final int TASK_LOAD_EDIT = 21;
    static final int TASK_LOAD_TOUCHED = 22;
    static final int TASK_SAVED = 30;
    static final int TASK_SAVED_EDIT = 31;
    static final int TASK_SAVED_TOUCHED = 32;

    private int _mode;

    TaskMode( ) {
        _mode = TASK_NONE;
    }

    int get( ) {
        return _mode;
    }

    void set( final int taskMode ) {
        _mode = taskMode;
    }

    /*
        edit, touched
     */
    void setTouched( ) {
        if( _mode % 10 == 1 ) {
            _mode += 1;
        }
    }

}

package com.example.pie.hellospen;

/**
 * Created by young on 2016-11-26.
 */

class TaskMode {

    public static final int FILE_NONE = -1 ;
    public static final int FILE_CREATE = 1 ;
    public static final int FILE_CREATE_EDIT = 2 ;
    public static final int FILE_CREATE_TOUCHED = 10 ;
    public static final int FILE_LOAD = 3 ;
    public static final int FILE_LOAD_EDIT = 4 ;
    public static final int FILE_LOAD_TOUCHED = 11 ;
    public static final int FILE_SAVED = 5 ;
    public static final int FILE_SAVED_EDIT = 6 ;
    public static final int FILE_SAVED_TOUCHED = 12 ;
    private int _fileMode ;

    public TaskMode( ) {

        _fileMode = FILE_NONE ;

    }

    public int getFileMode( ) {
        return _fileMode ;
    }

    public void setFileMode( int fileMode ) {
        _fileMode = fileMode ;
    }

}

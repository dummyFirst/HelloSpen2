package com.example.pie.hellospen;

/**
 * Created by young on 2016-11-26.
 */

class TaskMode {
    private int _editMode;
    public static final int MODE_NONE = -1 ;
    public static final int MODE_READ = 1;
    public static final int MODE_EDIT = 2;

    private int _fileMode ;
    public static final int FILE_NONE = -1 ;
    public static final int FILE_CREATE = 1 ;
    public static final int FILE_CREATE_EDIT = 2 ;
    public static final int FILE_OPEN = 3 ;
    public static final int FILE_OPEN_EDIT = 4 ;

    public TaskMode( ) {
        _editMode = MODE_NONE ;
        _fileMode = FILE_NONE ;
    }

    public int getEditMode( ) {
        return _editMode ;
    }

    public void setEditMode( int editMode ) {
        _editMode = editMode;
    }

    public int getFileMode( ) {
        return _fileMode ;
    }

    public void setFileMode( int fileMode ) {
        _fileMode = fileMode ;
    }
}

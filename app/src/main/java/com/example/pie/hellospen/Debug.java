package com.example.pie.hellospen;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by pie on 16. 11. 29.
 */

class Debug {
    int _mode;
    Context _context;
    String _prefix;

    static final int NONE = 0;
    static final int SHOW = 1;

    Debug( Context context ) {
        _context = context;
        _mode = SHOW;
    }

    Debug( Context context, int mode ) {
        _context = context;
        _mode = mode;
    }

    void p( final String msg ) {
        if( _mode == NONE ) return;

        Toast.makeText( _context, msg, Toast.LENGTH_SHORT ).show( );
    }

    void p( final String prefix, final String msg ) {
        p( prefix + msg );
    }

    void set( int mode ) {
        _mode = mode;
    }

    void setPrefix( String prefix ) {
        _prefix = prefix;
    }

}

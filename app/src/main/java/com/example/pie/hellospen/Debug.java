package com.example.pie.hellospen;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by pie on 16. 11. 29.
 */

class Debug {
    int _mode;
    Context _context;
    String _prefix;
    String _tag ;

    static final int NONE = 0;
    static final int SHOW = 1;

    Debug( final Context context ) {
        _context = context;
        _mode = SHOW;
        _prefix = null ;
    }

    Debug( final Context context, final int mode ) {
        _context = context;
        _mode = mode;
    }

    Debug( final String tag, final int mode ) {
        _tag = tag ;
        _mode = mode ;
    }

    void p( final String msg ) {
        if( _mode == NONE ) return;
        String str = "" ;

        Toast.makeText( _context, msg, Toast.LENGTH_SHORT ).show( );
    }

    void p( final String prefix, final String msg ) {
        p( prefix + msg );
    }

    void set( final int mode ) {
        _mode = mode;
    }

    void setPrefix( final String prefix ) {
        _prefix = prefix;
    }

    void i( final String msg ) {
        String str = "" ;
        if( _mode == NONE ) return;
        if( _prefix != null )  str += _prefix ;
        str += msg ;
        Log.i( _tag, str ) ;
    }
}

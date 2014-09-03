//
//  init.java
//  Chartboost Plugin
//
/*
The MIT License (MIT)

Copyright (c) 2014 Gremlin Interactive Limited

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
// ----------------------------------------------------------------------------

// Package name
package plugin.chartboost;

// Android Imports
import android.content.Context;

// JNLua imports
import com.naef.jnlua.LuaState;
import com.naef.jnlua.LuaType;

// Corona Imports
import com.ansca.corona.CoronaActivity;
import com.ansca.corona.CoronaEnvironment;
import com.ansca.corona.CoronaLua;
import com.ansca.corona.CoronaRuntime;
import com.ansca.corona.CoronaRuntimeTask;
import com.ansca.corona.CoronaRuntimeTaskDispatcher;
import com.ansca.corona.storage.FileContentProvider;

// Java/Misc Imports
import java.math.BigDecimal;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.io.File;

// Android Imports
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.R;
import android.R.drawable;

// Chartboost Imports
import com.chartboost.sdk.*;

/**
 * Implements the init() function in Lua.
 * <p>
 * Used for initializing the Chartboost Plugin.
 */
public class init implements com.naef.jnlua.NamedJavaFunction 
{
    /**
     * Gets the name of the Lua function as it would appear in the Lua script.
     * @return Returns the name of the custom Lua function.
     */
    @Override
    public String getName()
    {
        return "init";
    }

    // Our lua callback listener
    private int listenerRef;


    // Event task
    private class LuaCallBackListenerTask implements CoronaRuntimeTask 
    {
        private int fLuaListenerRegistryId;
        private String fStatus = null;

        public LuaCallBackListenerTask( int luaListenerRegistryId, String status ) 
        {
            fLuaListenerRegistryId = luaListenerRegistryId;
            fStatus = status;
        }

        @Override
        public void executeUsing( CoronaRuntime runtime )
        {
            try 
            {
                // Fetch the Corona runtime's Lua state.
                final LuaState L = runtime.getLuaState();

                // Dispatch the lua callback
                if ( CoronaLua.REFNIL != fLuaListenerRegistryId ) 
                {
                    // Setup the event
                    CoronaLua.newEvent( L, "license" );

                    // Event type
                    L.pushString( "check" );
                    L.setField( -2, "type" );

                    // Status
                    L.pushString( "valid" );
                    L.setField( -2, "status" );

                    // Dispatch the event
                    CoronaLua.dispatchEvent( L, fLuaListenerRegistryId, 0 );
                }
            }
            catch ( Exception ex ) 
            {
                ex.printStackTrace();
            }
        }
    }

    /**
     * This method is called when the Lua function is called.
     * <p>
     * Warning! This method is not called on the main UI thread.
     * @param luaState Reference to the Lua state.
     *                 Needed to retrieve the Lua function's parameters and to return values back to Lua.
     * @return Returns the number of values to be returned by the Lua function.
     */
    @Override
    public int invoke( LuaState luaState ) 
    {
        try
        {
            // This requires an options table with the following params
            /*
                appID
                appSignature
                listener
            */

            // Get the corona application context
            Context coronaApplication = CoronaEnvironment.getApplicationContext();

            // Parameters
            String appID = null;
            String appSignature = null;

            // If an options table has been passed
            if ( luaState.isTable( -1 ) )
            {
                //System.out.println( "options table exists" );
                // Get the listener field
                luaState.getField( -1, "listener" );
                if ( CoronaLua.isListener( luaState, -1, "chartboost" ) ) 
                {
                    // Assign the callback listener to a new lua ref
                    listenerRef = CoronaLua.newRef( luaState, -1 );
                }
                else
                {
                    // Assign the listener to a nil ref
                    listenerRef = CoronaLua.REFNIL;
                }
                luaState.pop( 1 );

                // Get the app key
                luaState.getField( -1, "appID" );
                if ( luaState.isString( -1 ) )
                {
                    appID = luaState.checkString( -1 );
                }
                else
                {
                    System.out.println( "Error: appID expected, got " + luaState.typeName( -1 ) );
                }
                luaState.pop( 1 );

                // Get the app secret
                luaState.getField( -1, "appSignature" );
                if ( luaState.isString( -1 ) )
                {
                    appSignature = luaState.checkString( -1 );
                }
                else
                {
                    System.out.println( "Error: appSignature expected, got " + luaState.typeName( -1 ) );
                }
                luaState.pop( 1 );

                // Pop the options table
                luaState.pop( 1 );
            }
            // No options table passed in
            else
            {
                System.out.println( "Error: chartboost.init(), options table expected, got " + luaState.typeName( -1 ) );
            }

            // Set helper values
            chartboostHelper.luaState = luaState;
            chartboostHelper.listenerRef = listenerRef;


            // Corona Activity
            CoronaActivity coronaActivity = null;
            if ( CoronaEnvironment.getCoronaActivity() != null )
            {
                coronaActivity = CoronaEnvironment.getCoronaActivity();
            }

            // Corona runtime task dispatcher
            final CoronaRuntimeTaskDispatcher dispatcher = new CoronaRuntimeTaskDispatcher( luaState );

            // Set variables to pass to chartboost (need to be final as they are accesed from within an inner class)
            final String cbAppID = appID;
            final String cbAppSignature = appSignature;
            final CoronaActivity activity = coronaActivity;

            // Create a new runnable object to invoke our activity
            Runnable runnableActivity = new Runnable()
            {
                public void run()
                {
                    // If the chartboost instance hasn't already being created
                    if ( chartboostHelper.chartboostInstance == null )
                    {
                        // Init Chartboost
                        chartboostHelper.chartboostInstance = Chartboost.sharedChartboost();

                        // Create the Chartboost delegate
                        ChartboostDelegate chartboostDelegate = new chartboostDelegate( activity, "CBT" );
                        chartboostHelper.chartboostInstance.onCreate( activity, cbAppID, cbAppSignature, chartboostDelegate );
                        chartboostHelper.chartboostInstance.onStart( activity );
                        // For OpenGL
                        CBPreferences.getInstance().setImpressionsUseActivities( true );

                        // Create the task
                        LuaCallBackListenerTask task = new LuaCallBackListenerTask( listenerRef, "" );

                        // Send the task to the Corona runtime asynchronously.
                        dispatcher.send( task ); 
                    }                 
                }
            };

            // Run the activity on the uiThread
            if ( coronaActivity != null )
            {
                coronaActivity.runOnUiThread( runnableActivity );
            }
        }
        catch( Exception ex )
        {
            // An exception will occur if given an invalid argument or no argument. Print the error.
            ex.printStackTrace();
        }
        
        return 0;
    }
}

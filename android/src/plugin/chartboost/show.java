//
//  show.java
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
 * Implements the show() function in Lua.
 * <p>
 * Used for showing an ad with the Chartboost Plugin.
 */
public class show implements com.naef.jnlua.NamedJavaFunction 
{
    /**
     * Gets the name of the Lua function as it would appear in the Lua script.
     * @return Returns the name of the custom Lua function.
     */
    @Override
    public String getName()
    {
        return "show";
    }

    // Pointer to the lua state
    LuaState theLuaState = null;

    // Save Moment Event task
    private static class showLuaCallBackListenerTask implements CoronaRuntimeTask 
    {
        private int fLuaListenerRegistryId;
        private String fPhase = null;

        public showLuaCallBackListenerTask( int luaListenerRegistryId, String phase ) 
        {
            fLuaListenerRegistryId = luaListenerRegistryId;
            fPhase = phase;
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
                    CoronaLua.newEvent( L, "chartboost" );

                    // Status
                    L.pushString( fPhase );
                    L.setField( -2, "phase" );

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

    
    // Our lua callback listener
    private int listenerRef;

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
            // Get the ad type
            String adType = luaState.checkString( 1 );
            // The interstitial named location
            String namedLocation = null;
            
            // Get the interstitial named location
            if ( luaState.isString( 2 ) )
            {
                namedLocation = luaState.checkString( 2 );
            }
                
            // Corona Activity
            CoronaActivity coronaActivity = null;
            if ( CoronaEnvironment.getCoronaActivity() != null )
            {
                coronaActivity = CoronaEnvironment.getCoronaActivity();
            }

            // The ad type
            final String theAdType = adType;
            // The location
            final String theNamedLocation = namedLocation;

            // Corona runtime task dispatcher
            final CoronaRuntimeTaskDispatcher dispatcher = new CoronaRuntimeTaskDispatcher( luaState );

            // Create a new runnable object to invoke our activity
            Runnable runnableActivity = new Runnable()
            {
                public void run()
                {
                    // If the chartboost instance is valid - could be invalid by calling this method before init invokes
                    if ( chartboostHelper.chartboostInstance != null )
                    {
    				    // If the ad type isn't null
                        if ( theAdType != null )
                        {
                            // If we want to display an interstitial
                            if ( theAdType.equalsIgnoreCase( "interstitial" ) )
                            {
                                // If we have already requested an ad, and are waiting for it to show/fail, lets not execute this block of code
                                if ( chartboostHelper.cbHasRequestedAd == false )
                                {
                                    //System.out.println( "CHARTBOOST: Showing interstitial\n");

                                    // If the user wants to show a cached nameded location
                                    if ( theNamedLocation != null )
                                    {
                                        if ( chartboostHelper.chartboostInstance.hasCachedInterstitial( theNamedLocation ) )
                                        {
                                            chartboostHelper.chartboostInstance.showInterstitial( theNamedLocation );
                                        }
                                        else
                                        {
                                            chartboostHelper.chartboostInstance.showInterstitial( "DefaultInterstitial" );
                                        }
                                    }
                                    // User just wants to show a default interstitial
                                    else
                                    {
                                        chartboostHelper.chartboostInstance.showInterstitial( "DefaultInterstitial" );
                                    }
                                    chartboostHelper.cbHasRequestedAd = true;
                                }
                            }
                            // If we want to display the more apps page
                            else if ( theAdType.equalsIgnoreCase( "moreApps" ) )
                            {
                                // If we have already requested a more apps page, and are waiting for it to show/fail, lets not execute this block of code
                                if ( chartboostHelper.cbHasRequestedMoreApps == false )
                                {
                                    //printf( "CHARTBOOST: Showing more apps\n");
                                    chartboostHelper.chartboostInstance.showMoreApps();
                                }
                            }
                        }
                        else
                        {
                            // Show error
                        }
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

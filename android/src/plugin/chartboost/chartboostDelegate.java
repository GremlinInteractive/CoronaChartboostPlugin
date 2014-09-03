//
//  chartboostDelegate.java
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
import com.chartboost.sdk.Chartboost.CBAgeGateConfirmation;
import com.chartboost.sdk.ChartboostDelegate;
import com.chartboost.sdk.Model.CBError.CBClickError;
import com.chartboost.sdk.Model.CBError.CBImpressionError;

// Chartboost class
public class chartboostDelegate implements ChartboostDelegate
{
	// Event task
	private static class LuaCallBackListenerTask implements CoronaRuntimeTask 
	{
	    private int fLuaListenerRegistryId;
	    private String fType = null;
	    private String fPhase = null;
	    private String fResult = null;
	    private String fLocation = null;

	    public LuaCallBackListenerTask( int luaListenerRegistryId, String type, String phase ) 
	    {
	        fLuaListenerRegistryId = luaListenerRegistryId;
	        fType = type;
	        fPhase = phase;
	    }

	    public LuaCallBackListenerTask( int luaListenerRegistryId, String type, String phase, String result ) 
	    {
	        fLuaListenerRegistryId = luaListenerRegistryId;
	        fType = type;
	        fPhase = phase;
	        fResult = result;
	    }

	    public LuaCallBackListenerTask( int luaListenerRegistryId, String type, String phase, String result, String location ) 
	    {
	        fLuaListenerRegistryId = luaListenerRegistryId;
	        fType = type;
	        fPhase = phase;
	        fResult = result;
	        fLocation = location;
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

	                // Type
	                L.pushString( fType );
	                L.setField( -2, "type" );

	                // Phase
	                L.pushString( fPhase );
	                L.setField( -2, "phase" );

	                // Result
	                if ( fResult != null && ! fResult.isEmpty() )
	                {
	                	L.pushString( fResult );
	                	L.setField( -2, "result" );
	                }

	                // Location
	                if ( fLocation != null )
	                {
	                	L.pushString( fLocation );
	                	L.setField( -2, "location" );
	                }

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

	// Vars
	private String TAG;
	private Context ctx;
	
	// Delegate
	public chartboostDelegate( Context cx, String tag )
	{
		TAG = tag;
		ctx = cx;
	}

	// Interstitals
	@Override
	public boolean shouldRequestInterstitial( String location )
	{
		//Log.i( TAG, "SHOULD REQUEST INSTERSTITIAL '"+ location + "'?" );
		return true;
	}

	@Override
	public boolean shouldDisplayInterstitial( String location )
	{
		// Corona runtime task dispatcher
		final CoronaRuntimeTaskDispatcher dispatcher = new CoronaRuntimeTaskDispatcher( chartboostHelper.luaState );

		// Create the task
		LuaCallBackListenerTask task = new LuaCallBackListenerTask( chartboostHelper.listenerRef, "interstitial", "willDisplay" );

		// Send the task to the Corona runtime asynchronously.
		dispatcher.send( task );

		return true;
	}

	@Override
	public void didCacheInterstitial( String location )
	{
		// Corona runtime task dispatcher
		final CoronaRuntimeTaskDispatcher dispatcher = new CoronaRuntimeTaskDispatcher( chartboostHelper.luaState );

		// Create the task
		LuaCallBackListenerTask task = new LuaCallBackListenerTask( chartboostHelper.listenerRef, "interstitial", "cached", "", location );

		// Send the task to the Corona runtime asynchronously.
		dispatcher.send( task );

		// We haven't requested a cache now
		chartboostHelper.cbHasRequestedCache = false;
	}

	@Override
	public void didFailToLoadInterstitial( String location, CBImpressionError error )
	{
		if ( chartboostHelper.cbHasRequestedAd == true )
		{
			// Corona runtime task dispatcher
			final CoronaRuntimeTaskDispatcher dispatcher = new CoronaRuntimeTaskDispatcher( chartboostHelper.luaState );

			// Create the task
			LuaCallBackListenerTask task = new LuaCallBackListenerTask( chartboostHelper.listenerRef, "interstitial", "load", "failed" );

			// Send the task to the Corona runtime asynchronously.
			dispatcher.send( task );
		}
		
		// We are no longer requesting an ad
		chartboostHelper.cbHasRequestedAd = false;
	}

	@Override
	public void didDismissInterstitial( String location )
	{
		// We are no longer requesting an ad
		chartboostHelper.cbHasRequestedAd = false;
	}

	@Override
	public void didCloseInterstitial( String location )
	{
		// Corona runtime task dispatcher
		final CoronaRuntimeTaskDispatcher dispatcher = new CoronaRuntimeTaskDispatcher( chartboostHelper.luaState );

		// Create the task
		LuaCallBackListenerTask task = new LuaCallBackListenerTask( chartboostHelper.listenerRef, "interstitial", "closed" );

		// Send the task to the Corona runtime asynchronously.
		dispatcher.send( task );

		// We are no longer requesting an ad
		chartboostHelper.cbHasRequestedAd = false;
	}

	@Override
	public void didClickInterstitial( String location )
	{
		// Corona runtime task dispatcher
		final CoronaRuntimeTaskDispatcher dispatcher = new CoronaRuntimeTaskDispatcher( chartboostHelper.luaState );

		// Create the task
		LuaCallBackListenerTask task = new LuaCallBackListenerTask( chartboostHelper.listenerRef, "interstitial", "clicked" );

		// Send the task to the Corona runtime asynchronously.
		dispatcher.send( task );

		// We are no longer requesting an ad
		chartboostHelper.cbHasRequestedAd = false;
	}

	@Override
	public void didShowInterstitial( String location )
	{
		// Corona runtime task dispatcher
		final CoronaRuntimeTaskDispatcher dispatcher = new CoronaRuntimeTaskDispatcher( chartboostHelper.luaState );

		// Create the task
		LuaCallBackListenerTask task = new LuaCallBackListenerTask( chartboostHelper.listenerRef, "interstitial", "didDisplay" );

		// Send the task to the Corona runtime asynchronously.
		dispatcher.send( task );

		// We are no longer requesting an ad
		chartboostHelper.cbHasRequestedAd = false;
	}

	@Override
	public boolean shouldDisplayLoadingViewForMoreApps()
	{
		return true;
	}

	@Override
	public boolean shouldRequestMoreApps()
	{
		return true;
	}

	@Override
	public boolean shouldDisplayMoreApps()
	{
		// We have requested a more apps page
		chartboostHelper.cbHasRequestedMoreApps = true;

		// Corona runtime task dispatcher
		final CoronaRuntimeTaskDispatcher dispatcher = new CoronaRuntimeTaskDispatcher( chartboostHelper.luaState );

		// Create the task
		LuaCallBackListenerTask task = new LuaCallBackListenerTask( chartboostHelper.listenerRef, "moreApps", "willDisplay" );

		// Send the task to the Corona runtime asynchronously.
		dispatcher.send( task );

		return true;
	}

	@Override
	public void didFailToLoadMoreApps( CBImpressionError error )
	{
		// Corona runtime task dispatcher
		final CoronaRuntimeTaskDispatcher dispatcher = new CoronaRuntimeTaskDispatcher( chartboostHelper.luaState );

		// Create the task
		LuaCallBackListenerTask task = new LuaCallBackListenerTask( chartboostHelper.listenerRef, "moreApps", "load", "failed" );

		// Send the task to the Corona runtime asynchronously.
		dispatcher.send( task );		

		// We are no longer requesting a more apps page
		chartboostHelper.cbHasRequestedMoreApps = false;
	}

	@Override
	public void didCacheMoreApps()
	{
		if ( chartboostHelper.cbHasRequestedCache == true )
		{
			// Corona runtime task dispatcher
			final CoronaRuntimeTaskDispatcher dispatcher = new CoronaRuntimeTaskDispatcher( chartboostHelper.luaState );

			// Create the task
			LuaCallBackListenerTask task = new LuaCallBackListenerTask( chartboostHelper.listenerRef, "moreApps", "cached" );

			// Send the task to the Corona runtime asynchronously.
			dispatcher.send( task );
		}
		chartboostHelper.cbHasRequestedCache = false;
	}

	@Override
	public void didDismissMoreApps()
	{
		// We are no longer requesting a more apps page
		chartboostHelper.cbHasRequestedMoreApps = false;
	}

	@Override
	public void didCloseMoreApps()
	{
		// Corona runtime task dispatcher
		final CoronaRuntimeTaskDispatcher dispatcher = new CoronaRuntimeTaskDispatcher( chartboostHelper.luaState );

		// Create the task
		LuaCallBackListenerTask task = new LuaCallBackListenerTask( chartboostHelper.listenerRef, "moreApps", "closed" );

		// Send the task to the Corona runtime asynchronously.
		dispatcher.send( task );

		// We are no longer requesting a more apps page
		chartboostHelper.cbHasRequestedMoreApps = false;
	}

	@Override
	public void didClickMoreApps()
	{
		// Corona runtime task dispatcher
		final CoronaRuntimeTaskDispatcher dispatcher = new CoronaRuntimeTaskDispatcher( chartboostHelper.luaState );

		// Create the task
		LuaCallBackListenerTask task = new LuaCallBackListenerTask( chartboostHelper.listenerRef, "moreApps", "willDisplay" );

		// Send the task to the Corona runtime asynchronously.
		dispatcher.send( task );

		// We are not longer requesting a more apps page
		chartboostHelper.cbHasRequestedMoreApps = false;
	}

	@Override
	public void didShowMoreApps()
	{
		// Corona runtime task dispatcher
		final CoronaRuntimeTaskDispatcher dispatcher = new CoronaRuntimeTaskDispatcher( chartboostHelper.luaState );

		// Create the task
		LuaCallBackListenerTask task = new LuaCallBackListenerTask( chartboostHelper.listenerRef, "moreApps", "didDisplay" );

		// Send the task to the Corona runtime asynchronously.
		dispatcher.send( task );
	}

	@Override
	public boolean shouldRequestInterstitialsInFirstSession()
	{
		return true;
	}

	@Override
	public void didFailToRecordClick( String uri, CBClickError error ) 
	{
		//Log.i( TAG, "FAILED TO RECORD CLICK " + (uri != null ? uri : "null") + ", error: " + error.name() );
	}

	@Override
	public boolean shouldPauseClickForConfirmation( final CBAgeGateConfirmation callback)
	{
		return false;
	}
}
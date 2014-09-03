//
//  LuaLoader.java
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
 
// This corresponds to the name of the Lua library,
// e.g. [Lua] require "plugin.library"
 
// Package name
package plugin.chartboost;
 
// Java Imports
import java.util.*;
import java.lang.*;

// Android Imports
import android.content.Context;
 
// JNLua imports
import com.naef.jnlua.LuaState;
import com.naef.jnlua.JavaFunction;
import com.naef.jnlua.NamedJavaFunction;
 
// Corona Imports
import com.ansca.corona.CoronaActivity;
import com.ansca.corona.CoronaEnvironment;
import com.ansca.corona.CoronaLua;
import com.ansca.corona.CoronaRuntime;
import com.ansca.corona.CoronaRuntimeListener;
import com.ansca.corona.CoronaRuntimeTask;

// Chartboost imports
import com.chartboost.sdk.*;

/**
 * Implements the Lua interface for the Chartboost plugin.
 * <p>
 * Only one instance of this class will be created by Corona for the lifetime of the application.
 * This instance will be re-used for every new Corona activity that gets created.
 */
public class LuaLoader implements JavaFunction, CoronaRuntimeListener
{
    /**
     * <p>
     * Note that a new LuaLoader instance will not be created for every CoronaActivity instance.
     * That is, only one instance of this class will be created for the lifetime of the application process.
     * This gives a plugin the option to do operations in the background while the CoronaActivity is destroyed.
     */
    public LuaLoader()
    {
        // Set up this plugin to listen for Corona runtime events to be received by methods
        // onLoaded(), onStarted(), onSuspended(), onResumed(), and onExiting().
        CoronaEnvironment.addRuntimeListener( this );
    }
 
    /**
     * Called when this plugin is being loaded via the Lua require() function.
     * <p>
     * Note that this method will be called everytime a new CoronaActivity has been launched.
     * This means that you'll need to re-initialize this plugin here.
     * <p>
     * Warning! This method is not called on the main UI thread.
     * @param L Reference to the Lua state that the require() function was called from.
     * @return Returns the number of values that the require() function will return.
     *         <p>
     *         Expected to return 1, the library that the require() function is loading.
     */
    @Override
    public int invoke( LuaState L )
    {
        // Register this plugin into Lua with the following functions.
        NamedJavaFunction[] luaFunctions = new NamedJavaFunction[]
        {
            new getPluginVersion(),
            new init(),
            new cache(),
            new config(),
            new show(),
            new startSession(),
            new hasCachedInterstitial(),
            new hasCachedMoreApps(),
        };
        String libName = L.toString( 1 );
        L.register( libName, luaFunctions );
 
        // Returning 1 indicates that the Lua require() function will return the above Lua library.
        return 1;
    }
    
    /**
     * Called after the Corona runtime has been created and just before executing the "main.lua" file.
     * <p>
     * Warning! This method is not called on the main thread.
     * @param runtime Reference to the CoronaRuntime object that has just been loaded/initialized.
     *                Provides a LuaState object that allows the application to extend the Lua API.
     */
    @Override
    public void onLoaded( CoronaRuntime runtime )
    {
        // Note that this method will not be called the first time a Corona activity has been launched.
        // This is because this listener cannot be added to the CoronaEnvironment until after
        // this plugin has been required-in by Lua, which occurs after the onLoaded() event.
        // However, this method will be called when a 2nd Corona activity has been created.
    }
 
    /**
     * Called just after the Corona runtime has executed the "main.lua" file.
     * <p>
     * Warning! This method is not called on the main thread.
     * @param runtime Reference to the CoronaRuntime object that has just been started.
     */
    @Override
    public void onStarted( CoronaRuntime runtime )
    {
    }
 
    /**
     * Called just after the Corona runtime has been suspended which pauses all rendering, audio, timers,
     * and other Corona related operations. This can happen when another Android activity (ie: window) has
     * been displayed, when the screen has been powered off, or when the screen lock is shown.
     * <p>
     * Warning! This method is not called on the main thread.
     * @param runtime Reference to the CoronaRuntime object that has just been suspended.
     */
    @Override
    public void onSuspended( CoronaRuntime runtime )
    {
    }
 
    /**
     * Called just after the Corona runtime has been resumed after a suspend.
     * <p>
     * Warning! This method is not called on the main thread.
     * @param runtime Reference to the CoronaRuntime object that has just been resumed.
     */
    @Override
    public void onResumed( CoronaRuntime runtime )
    {
    }
 
    /**
     * Called just before the Corona runtime terminates.
     * <p>
     * This happens when the Corona activity is being destroyed which happens when the user presses the Back button
     * on the activity, when the native.requestExit() method is called in Lua, or when the activity's finish()
     * method is called. This does not mean that the application is exiting.
     * <p>
     * Warning! This method is not called on the main thread.
     * @param runtime Reference to the CoronaRuntime object that is being terminated.
     */
    @Override
    public void onExiting( CoronaRuntime runtime )
    {
    }
}

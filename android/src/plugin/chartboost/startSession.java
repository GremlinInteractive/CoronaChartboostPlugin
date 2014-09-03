//
//  startSession().java
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
 * Implements the startSession() function in Lua.
 * <p>
 * Used for starting a Chartboost session.
 */
public class startSession implements com.naef.jnlua.NamedJavaFunction 
{
    /**
     * Gets the name of the Lua function as it would appear in the Lua script.
     * @return Returns the name of the custom Lua function.
     */
    @Override
    public String getName()
    {
        return "startSession";
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
            // Not used on android
        }
        catch( Exception ex )
        {
            // An exception will occur if given an invalid argument or no argument. Print the error.
            ex.printStackTrace();
        }
        
        return 0;
    }
}

#### Overview

Starts a Chartboost session.

Notes: Chartboost recommend that you call this method on every application resume event.

## Syntax

`````
chartboost.startSession( appId, appSignature )
`````

This function takes two arguments:

##### appID - (required)

__[String]__ Your Chartboost app ID. You can get your app ID from the [](https://www.chartboost.com)Chartboost website.

##### appSignature - (required)

__[String]__ Your Chartboost app signature. You can get your app signature from the [](https://www.chartboost.com)Chartboost website.

#### Example

    -- Require the Chartboost library
    local chartboost = require( "plugin.chartboost" )

    local yourAppID = "your_CB_app_id_here"
    local yourAppSignature = "your_CB_app_signature_here"

    local function systemEvent( event )
  	    local phase = event.phase;

  	    if event.type == 'applicationResume' then
  	        -- Start a ChartBoost session
  	        chartboost.startSession( yourAppID, yourAppSignature );
  	    end

  	    return true
    end

  -- Add the system listener
  Runtime:addEventListener( 'system', systemEvent );
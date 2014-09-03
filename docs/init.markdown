#### Overview

    Initializes the Chartboost library. This function is required and must be executed before making other Chartboost calls such as `chartboost.show()`.

## Syntax

`````
chartboost.init( options )
`````

    This function takes a single argument, `options`, which is a table that accepts the following parameters:

##### appID - (required)

__[String]__ Your Chartboost app ID. You can get your app ID from the [](https://www.chartboost.com)Chartboost website.

##### appSignature - (required)

__[String]__ Your Chartboost app signature. You can get your app signature from the [](https://www.chartboost.com)Chartboost website.

##### listener - (optional)

__[Listener]__ This function receives Chartboost events. With `event.type` of `"chartboost"`. The events returned for Chartboost are as follows:

__`"interstitial"__

Phases of: `"willDisplay"`, `"didDisplay"`, `"closed"`, `"clicked"`, `"cached"`, `"load"`.

__`"moreApps"__

Phases of: `"willDisplay"`, `"didDisplay"`, `"closed"`, `"clicked"`, `"cached"`, `"load"`.

#### Example

    -- Require the Chartboost library
    local chartboost = require( "plugin.chartboost" )

    -- Initialize the Chartboost library
    chartboost.init(
    {
        appID = "app_ID_generated_from_chartboost_here",
        appSignature = "app_signature_generated_from_chartboost_here",  
        listener = function( event )
            -- Print the events key/pair values
            for k,v in pairs( event ) do
                print( k, ":", v )
            end
        end
    })
#### Overview

Caches a Chartboost interstitial or more apps screen for instant loading for future display.

## Syntax

`````
chartboost.cache( namedLocation )
`````

This function takes a single argument:

##### namedLocation - (required)

__[String]__ The name of the cached location. For caching of the more apps screen, set this value to `"moreApps"`, for caching a specific interstitial, you may specify any string (that gets passed to chartboost.show()). For instance `"scene1AdLocation"` or `"gameAdLocation"`.

#### Example

    -- Require the Chartboost library
    local chartboost = require( "plugin.chartboost" )
    
    -- Cache an interstitial
    chartboost.cache( 'myCachedInterstitial' )

    -- Cache the more apps screen
    chartboost.cache( 'moreApps' )

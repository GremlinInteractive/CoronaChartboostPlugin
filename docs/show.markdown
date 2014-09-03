#### Overview

Shows a Chartboost interstitial or more apps screen.

## Syntax

`````
chartboost.show( adType, namedLocation )
`````

This function takes two arguments:

##### adType - (required)

__[String]__ The type of advertisement to show. Valid vales are `"interstitial"` and `"moreApps"`

##### namedLocation - (optional)

__[String]__ The name of the cached advertisement location. See chartboost.cache() for more information.

#### Example

	-- Require the Chartboost library
	local chartboost = require( "plugin.chartboost" )

	-- Show an interstitial
	chartboost.show( 'interstitial' )

	-- Show a more apps screen
	chartboost.show( 'moreApps' )
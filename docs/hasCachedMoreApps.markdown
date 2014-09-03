#### Overview

Returns whether a more apps page is cached or not.

## Syntax

`````
chartboost.hasCachedMoreApps()
`````

#### Example

	-- Require the Chartboost library
	local chartboost = require( "plugin.chartboost" )

	-- Is the more apps page cached?
	print( "Has cached interstitial: " .. chartboost.hasCachedMoreApps() );
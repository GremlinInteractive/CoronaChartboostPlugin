--[[
//
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
// ----------------------------------------------------------------------------
--]]

local chartboost = require( "plugin.chartboost" );
local widget = require( "widget" );
widget.setTheme( "widget_theme_ios" );

-- The ChartBoost listener function
local function chartBoostListener( event )
	for k, v in pairs( event ) do
		print( k, ":", v )
	end
end

-- Your ChartBoost app id
local yourAppID = "";
-- Your ChartBoost app signature
local yourAppSignature = "";

-- Change the appid/sig for android (required by Chartboost)
if system.getInfo( "platformName" ) == "Android" then
	yourAppID = "";
	yourAppSignature = "";
end

print( "plugin version is: ", chartboost.getPluginVersion() );

-- Initialise ChartBoost
chartboost.init(
	{
		--licenseKey = "PAS0H-STWVI-WXVUK-2RP4P",
		appID = yourAppID,
		appSignature = yourAppSignature, 
		listener = chartBoostListener,
		--betaMode = true,
	}
);

--chartboost.show( "interstitial" )
--chartboost.cache( "bah" );

-- Show Ad button
local showAdButton = widget.newButton
{
	label = "Show Ad";
	onRelease = function( event )
		print( "has cached Ad?:", chartboost.hasCachedInterstitial( "myLoc" ) );
		print( "has cached more apps?:", chartboost.hasCachedMoreApps() );
	
		if chartboost.hasCachedInterstitial( "myLoc" ) then
			chartboost.show( "interstitial", "myLoc" )
		else
			chartboost.show( "interstitial" );
		end
	end,
}
showAdButton.x = display.contentCenterX;
showAdButton.y = 150;

-- Cache Default Ad
local cacheDefaultAd = widget.newButton
{
	label = "Cache Default Ad",
	onRelease = function( event )
		chartboost.cache( 'myLoc' );
	end,
}
cacheDefaultAd.x = display.contentCenterX;
cacheDefaultAd.y = showAdButton.y + showAdButton.contentHeight + cacheDefaultAd.contentHeight * 0.5;

-- Cache More Apps
local cacheMoreAppsButton = widget.newButton
{
	label = "Cache More Apps",
	onRelease = function( event )
		chartboost.cache( "moreApps" );
	end,
}
cacheMoreAppsButton.x = display.contentCenterX;
cacheMoreAppsButton.y = cacheDefaultAd.y + cacheDefaultAd.contentHeight + cacheMoreAppsButton.contentHeight * 0.5;

-- Show More Apps button
local showMoreAppsButton = widget.newButton
{
	label = "Show More Apps";
	onRelease = function( event )
		chartboost.show( "moreApps" );
	end,
}
showMoreAppsButton.x = display.contentCenterX;
showMoreAppsButton.y = cacheMoreAppsButton.y + cacheMoreAppsButton.contentHeight + showMoreAppsButton.contentHeight * 0.5;


local function systemEvent( event )
	local phase = event.phase;

	if event.type == 'applicationResume' then
	    -- Start a ChartBoost session
		chartboost.startSession( yourAppID, yourAppSignature );
	end
	
	return true
end

Runtime:addEventListener( 'system', systemEvent );

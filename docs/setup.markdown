To use this plugin, add an entry into the plugins table of build.settings. When added, the build server will integrate the plugin during the build phase.

    settings =
    {
        plugins =
        {
            ["plugin.chartboost"] =
            {
                publisherId = "com.gremlininteractive"
            },
        },      
    }

For Android, the following permissions/features should be set when using this plugin:

    android =
    {
        usesPermissions =
        {
            "android.permission.INTERNET",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.ACCESS_WIFI_STATE", -- optional
            "android.permission.WRITE_EXTERNAL_STORAGE", -- optional
        },
    },
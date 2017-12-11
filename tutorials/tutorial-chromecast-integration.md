# Chromecast Integration

Chromecast UI should include the following components:
- [x] Chromecast button in the action bar (top right)
- [x] Overlayed Chromecast mini controls that should show up when there is an ongoing cast session
- [x] Expanded controls activity


## Chromecast Button

In order to include the chromecast button in your ActionBar, do the following:

### Bind you chromecast app ID within EMP registry
```java
EMPRegistry.bindChromecastAppId("<your_chromecast_app_id>");

// Optional: bind a locale so that the libraries know the default language for cast metadata
EMPRegistry.bindLocale("en");
```

### Add an item in your ActionBar Menu that has a **MediaRouteActionProvider**  
```xml
	<item android:id="@+id/media_route_menu_item"
        android:title="@string/media_route_menu_title"
        app:actionProviderClass="android.support.v7.app.MediaRouteActionProvider"
        app:showAsAction="always"/>
```

### Override **onCreateOptionsMenu** in the desired Activity and add the following code:
```java
	if (EMPRegistry.chromecastAppId() != null) {
		CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu, R.id.media_route_menu_item);
	}
```

## Mini Controls

The mini controls show up in the bottom of the screen when there is an ongoing session. 
This allows the user to browser for content while casting.
To add a default Mini Controller, just add the following Fragment to your activity layout xml:
```xml
	<fragment
        android:id="@+id/castMiniController"
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:visibility="gone"
        class="com.google.android.gms.cast.framework.media.widget.MiniControllerFragment"/>
```

## Expanded controls activity

By default, by including EMP Cast library, you will get default expanded controls.
But if you want your own, you can achieve that doing the following:

### Create your own CastOptionsProvider
Example:
```java
public class CastOptionsProvider implements OptionsProvider {
    @Override
    public CastOptions getCastOptions(Context context) {
        NotificationOptions notificationOptions = new NotificationOptions.Builder()
                .setTargetActivityClassName(ExpandedControlsActivity.class.getName())
                .build();
        CastMediaOptions mediaOptions = new CastMediaOptions.Builder()
                .setNotificationOptions(notificationOptions)
                .setExpandedControllerActivityClassName(ExpandedControlsActivity.class.getName())
                .build();

        return new CastOptions.Builder()
                .setReceiverApplicationId(EMPRegistry.chromecastAppId())
                .setCastMediaOptions(mediaOptions)
                .build();
    }

    @Override
    public List<SessionProvider> getAdditionalSessionProviders(Context context) {
        return null;
    }
}
```

### Register your CastOptionsProvider in the manifest
```xml
	<application>
		<!-- ... -->
        <meta-data
            android:name="com.google.android.gms.cast.framework.OPTIONS_PROVIDER_CLASS_NAME"
            android:value="net.ericsson.emovs.cast.CastOptionsProvider" />
    </application>
```


# StartCasting Code Flow

Once you have all the components in place, you can trigger a new cast session by calling the following code:

```java
if (EMPCastProvider.getInstance().getCurrentCastSession() != null) {
	EmpAsset castAsset = new EmpAsset();
	castAsset.assetId = "my_asset_id";
	// optional: add some titles, subtitles and image to EmpAsset

	EmpCustomData castProperties = new EmpCustomData();
	
	// Example of how to set startTime to 60 seconds in the future
	castProperties.startTime = 60; 
	
	EMPCastProvider.getInstance().startCasting(playable, castProperties, onCastSuccessCb, onCastFailCb);
}
```

# Event Listening

When casting, you can listen to 2 different types of events: chromecast-related or emp-related:

- [x] Method **getReceiverChannel** will return a communication channel where you can listen to EMP-related events (check **IEmpCastListener** interface)
- [x] Method **getCastContext** will return the cast context that you can use to listen for specific Chromecast events (Check **SessionManagerListener<Session>** in CC API).

# cordova-plugin-jitsi-meet-updated
Cordova plugin for Jitsi Meet React Native SDK. Works with both iOS and Android, and fixes the 64-bit binary dependency issue with Android found in previous versions of this plugin.

# Summary 
The original repo is here: https://github.com/sumeetchhetri/cordova-plugin-jitsi

# Requirements
Need to set MinSdk to 21 for Android only in config.xml

# Installation
`cordova plugin add https://github.com/ronny28/cordova-plugin-jitsi-meet-updated.git`

## iOS Installation
On iOS/Xcode you will need to manually specify the WebRTC and JitsiMeet frameworks manually to be embedded.

Example of how to select them here: https://github.com/ronny28/cordova-plugin-jitsi-meet-updated/blob/master/xcode-ios-framework-embed-example.png

# Add Activity to AndroidManifest.xml manually or using 
```
<activity
    android:name="com.cordova.plugin.jitsi.JitsiMeetPluginActivity"
    android:configChanges="keyboard|keyboardHidden|orientation|screenLayout|screenSize|smallestScreenSize"
    android:launchMode="singleTask"
    android:resizeableActivity="true"
    android:supportsPictureInPicture="true"
    android:windowSoftInputMode="adjustResize">
</activity>
```
```
<config-file target="AndroidManifest.xml" parent="/manifest/application">
    <activity
        android:name="com.cordova.plugin.jitsi.JitsiMeetPluginActivity"
        android:configChanges="keyboard|keyboardHidden|orientation|screenLayout|screenSize|smallestScreenSize"
        android:launchMode="singleTask"
        android:resizeableActivity="true"
        android:supportsPictureInPicture="true"
        android:windowSoftInputMode="adjustResize">
    </activity>
</config-file>

```

# Usage
```
const options = {
    domain: 'https://meet.jit.si/',
    subject: 'room-subject',
    roomId: 'your-custom-room-id',
    roomPwd: 'your-custom-room-password',
    audioOnly: false,
    audioMuted: false,
    videoMuted: false,
    userInfo: {
        displayName: 'user-display-name',
        email: 'user-email-id',
        avatar: 'user-avatar-url'
    },
    chatEnable: true,
    inviteEnable: false,
    calenderEnable: false,
    pipEnable: false,
    callIntegrationEnable: false,
    closeCaptionsEnable: false,
    iosRecordingEnable: false,
    liveStreamingEnable: false,
    meetingNameEnable: false,
    meetingPasswordEnable: false,
    raiseHandEnable: true,
    recordingEnable: false,
    tileViewEnable: true,
    toolboxEnable: true,
    welcomePageEnable: false,
    token: ''
};

jitsiplugin.join(options.domain, options.roomId, options, function (data) {
    //CONFERENCE_WILL_JOIN
    //CONFERENCE_JOINED
    //CONFERENCE_TERMINATED
    //CONFERENCE_FINISHED
    //CONFERENCE_DESTROYED
    if (data === "CONFERENCE_TERMINATED") {
        // call finished
        jitsiplugin.destroy(function (data) {
            // plugin destroyed
        }, function (err) {
            console.log(err);
        });
    }
}, function (err) {
    console.log(err);
});

jitsiplugin.finish( (callback) => {
    // call finished
    jitsiplugin.destroy(function (data) {
        // plugin destroyed
    }, function (err) {
        console.log(err);
    });
}, (err) => {
    console.log(err);
});
```
# cordova-plugin-jitsi-meet-updated

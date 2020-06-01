package com.cordova.plugin.jitsi;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PermissionHelper;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;
import android.content.Context;

import java.net.MalformedURLException;
import java.net.URL;

import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import android.view.View;

import org.apache.cordova.CordovaWebView;
import org.json.JSONObject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import com.facebook.react.modules.core.PermissionListener;

import android.view.ViewParent;
import android.view.ViewGroup;

public class JitsiPlugin extends CordovaPlugin
      implements JitsiMeetActivityInterface, JitsiPluginModel.OnJitsiPluginStateListener {

  private CallbackContext _callback;
  private static final String TAG = "cordova-plugin-jitsi";


  final static String[] permissions = { Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };
  public static final int TAKE_PIC_SEC = 0;
  public static final int REC_MIC_SEC = 1;

  private String _conferenceState = "";

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    // your init code here
    JitsiPluginModel.getInstance().setListener(this);
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    // CB-10120: The CAMERA permission does not need to be requested unless it is
    // declared
    // in AndroidManifest.xml. This plugin does not declare it, but others may and
    // so we must
    // check the package info to determine if the permission is present.
    _conferenceState = JitsiPluginModel.getInstance().getState();
    checkPermission();

    _callback = callbackContext;

    if (action.equals("join")) {
      String serverUrl = args.getString(0);
      String roomId = args.getString(1);
//      Boolean audioOnly = args.getBoolean(2);
      JSONObject options  = args.getJSONObject(2);
      this.join(serverUrl, roomId, options);
      return true;
    } else if (action.equals("destroy")) {
      this.destroy(callbackContext);
      return true;
    } else if (action.equals("backButtonPressed")) {
      this.finishMeeting(callbackContext);
      return true;
    }
    return false;
  }

  private void checkPermission() {
    boolean takePicturePermission = PermissionHelper.hasPermission(this, Manifest.permission.CAMERA);
    boolean micPermission = PermissionHelper.hasPermission(this, Manifest.permission.RECORD_AUDIO);

    // CB-10120: The CAMERA permission does not need to be requested unless it is
    // declared
    // in AndroidManifest.xml. This plugin does not declare it, but others may and
    // so we must
    // check the package info to determine if the permission is present.

    Log.e(TAG, "tp : " + takePicturePermission);
    Log.e(TAG, "mp : " + micPermission);

    if (!takePicturePermission) {
      takePicturePermission = true;

      try {
        PackageManager packageManager = this.cordova.getActivity().getPackageManager();
        String[] permissionsInPackage = packageManager.getPackageInfo(this.cordova.getActivity().getPackageName(),
            PackageManager.GET_PERMISSIONS).requestedPermissions;

        if (permissionsInPackage != null) {
          for (String permission : permissionsInPackage) {
            if (permission.equals(Manifest.permission.CAMERA)) {
              takePicturePermission = false;
              break;
            }
          }
        }
        Log.e(TAG, "10 : ");
      } catch (NameNotFoundException e) {
        // We are requesting the info for our package, so this should
        // never be caught
        Log.e(TAG, e.getMessage());
      }
    }

    if (!takePicturePermission) {
      PermissionHelper.requestPermissions(this, TAKE_PIC_SEC,
          new String[] { Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO });
    }
  }

//  private void join(final String serverUrl, final String roomId, final Boolean audioOnly) {
    private void join(final String serverUrl, final String roomId, final JSONObject options) throws JSONException {

    Log.e(TAG, "join called! Server: " + serverUrl + ", room : " + roomId);

      JSONObject tempUserInfo = options.getJSONObject("userInfo");
      JitsiMeetUserInfo userInfo = new JitsiMeetUserInfo();
      URL avatarURL = null;
      String displayName = "";
      String email = "";
      try {
        displayName = tempUserInfo.getString("displayName");
        email = tempUserInfo.getString("email");
        avatarURL = new URL(tempUserInfo.getString("avatar"));
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
      userInfo.setDisplayName(displayName);
      userInfo.setEmail(email);
      userInfo.setAvatar(avatarURL);

      String subject = options.getString("subject");
      Boolean audioOnly = options.getBoolean("audioOnly");
      Boolean audioMuted = options.getBoolean("audioMuted");
      Boolean videoMuted = options.getBoolean("videoMuted");
      Boolean chatEnable = options.getBoolean("chatEnable");
      Boolean inviteEnable = options.getBoolean("inviteEnable");
      Boolean calenderEnable = options.getBoolean("calenderEnable");
      Boolean pipEnable = options.getBoolean("pipEnable");
      Boolean callIntegrationEnable = options.getBoolean("callIntegrationEnable");
      Boolean closeCaptionsEnable = options.getBoolean("closeCaptionsEnable");
      Boolean iosRecordingEnable = options.getBoolean("iosRecordingEnable");
      Boolean liveStreamingEnable = options.getBoolean("liveStreamingEnable");
      Boolean meetingNameEnable = options.getBoolean("meetingNameEnable");
      Boolean meetingPasswordEnable = options.getBoolean("meetingPasswordEnable");
      Boolean raiseHandEnable = options.getBoolean("raiseHandEnable");
      Boolean recordingEnable = options.getBoolean("recordingEnable");
      Boolean tileViewEnable = options.getBoolean("tileViewEnable");
      Boolean toolboxEnable = options.getBoolean("toolboxEnable");
      Boolean welcomePageEnable = options.getBoolean("welcomePageEnable");
      String token = options.getString("token");

    cordova.getActivity().runOnUiThread(new Runnable() {
      public void run() {
        Context context = cordova.getActivity();
        URL serverUrlObject;
        try {
          serverUrlObject = new URL(serverUrl);
        } catch (MalformedURLException e) {
          e.printStackTrace();
          throw new RuntimeException("Invalid server URL!");
        }

        JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
            .setRoom(serverUrlObject.getProtocol() + "://" + serverUrlObject.getHost() + "/" +roomId)
            .setToken(token)
            .setSubject(subject)
            .setAudioOnly(audioOnly)
            .setVideoMuted(audioMuted)
            .setAudioMuted(videoMuted)
            .setUserInfo(userInfo)
            .setFeatureFlag("chat.enabled", chatEnable)
            .setFeatureFlag("invite.enabled", inviteEnable)
            .setFeatureFlag("calendar.enabled", calenderEnable)
            .setFeatureFlag("pip.enabled", pipEnable)
            .setFeatureFlag("call-integration.enabled", callIntegrationEnable)
            .setFeatureFlag("close-captions.enabled", closeCaptionsEnable)
            .setFeatureFlag("ios.recording.enabled", iosRecordingEnable)
            .setFeatureFlag("live-streaming.enabled", liveStreamingEnable)
            .setFeatureFlag("meeting-name.enabled", meetingNameEnable)
            .setFeatureFlag("meeting-password.enabled", meetingPasswordEnable)
            .setFeatureFlag("raise-hand.enabled", raiseHandEnable)
            .setFeatureFlag("recording.enabled", recordingEnable)
            .setFeatureFlag("tile-view.enabled", tileViewEnable)
            .setFeatureFlag("toolbox.enabled", toolboxEnable)
//            .setFeatureFlag("welcomepage.enabled", welcomePageEnable)
            .setWelcomePageEnabled(false).build();

        JitsiMeetPluginActivity.launchActivity(cordova.getActivity(), options);
      }
    });
  }

  private void destroy(final CallbackContext callbackContext) {
    cordova.getActivity().runOnUiThread(new Runnable() {
      public void run() {
        JitsiMeetActivityDelegate.onHostDestroy(cordova.getActivity());
//        getView();
//        cordova.getActivity().setContentView(getView());
        removeView();
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "DESTROYED");
        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
      }
    });
  }

  private void finishMeeting(final CallbackContext callbackContext) {
    cordova.getActivity().runOnUiThread(new Runnable() {
      public void run() {
        JitsiMeetActivityDelegate.onBackPressed();
//        getView();
//        cordova.getActivity().setContentView(getView());
        removeView();
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, "TERMINATED");
        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);
      }
    });
  }

  private View getView() {
    try {
      return (View) webView.getClass().getMethod("getView").invoke(webView);
    } catch (Exception e) {
      return (View) webView;
    }
  }

  private void removeView() {
    ViewParent viewParent = getView().getParent();
    if (viewParent != null && viewParent instanceof ViewGroup) {
      ViewGroup viewGroup = (ViewGroup) viewParent;
      viewGroup.removeView(getView());
    }
    cordova.getActivity().setContentView(getView());
  }

  @Override
  public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
    JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  @Override
  public void requestPermissions(String[] permissions, int requestCode, PermissionListener listener) {
    JitsiMeetActivityDelegate.requestPermissions(cordova.getActivity(), permissions, requestCode, listener);
  }

  @Override
  public boolean shouldShowRequestPermissionRationale(String permissions) {
    return true;
  }

  @Override
  public int checkSelfPermission(String permission) {
    return 0;
  }

  @Override
  public int checkPermission(String permission, int pid, int uid) {
    return 0;
  }

  @Override
  public void stateChanged() {
    _conferenceState = JitsiPluginModel.getInstance().getState();
    Log.d(TAG, "MainActivity says: Model state changed: " + _conferenceState);

//    getView();
//    cordova.getActivity().setContentView(getView());
    removeView();

    String m = "";

    switch (_conferenceState){
      case "onConferenceJoined":
        m = "CONFERENCE_JOINED";
        break;
      case "onConferenceWillJoin":
        m = "CONFERENCE_WILL_JOIN";
        break;
      case "onConferenceTerminated":
        m = "CONFERENCE_TERMINATED";
        break;
      case "onConferenceFinished":
        m = "CONFERENCE_FINISHED";
        break;
      case "onConferenceDestroyed":
        m = "CONFERENCE_DESTROYED";
        break;
    }

    if(m != "") {
      PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, m);
      pluginResult.setKeepCallback(true);
      _callback.sendPluginResult(pluginResult);
    }
  }
}

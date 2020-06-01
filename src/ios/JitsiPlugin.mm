#import "JitsiPlugin.h"
#import "JitsiMeet.framework/Headers/JitsiMeetConferenceOptions.h"


@implementation JitsiPlugin

CDVPluginResult *pluginResult = nil;

//- (void)join:(CDVInvokedUrlCommand *)command {
//    NSString* serverUrl = [command.arguments objectAtIndex:0];
//    NSString* room = [command.arguments objectAtIndex:1];
//    Boolean isAudioOnly = [[command.arguments objectAtIndex:2] boolValue];
//    commandBack = command;
//    jitsiMeetView = [[JitsiMeetView alloc] initWithFrame:self.viewController.view.frame];
//    jitsiMeetView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
//    jitsiMeetView.delegate = self;
//
//    JitsiMeetConferenceOptions *options = [JitsiMeetConferenceOptions fromBuilder:^(JitsiMeetConferenceOptionsBuilder *builder) {
//        builder.serverURL = [NSURL URLWithString: serverUrl];
//        builder.room = room;
//        builder.subject = @" ";
//        builder.welcomePageEnabled = NO;
//        builder.audioOnly = isAudioOnly;
//    }];
//
//    [jitsiMeetView join: options];
//    [self.viewController.view addSubview:jitsiMeetView];
//}

- (void)join:(CDVInvokedUrlCommand *)command {
    NSString* serverUrl = [command.arguments objectAtIndex:0];
    NSString* room = [command.arguments objectAtIndex:1];
    NSDictionary* tempOptions = [command.arguments objectAtIndex:2];
//    NSString* isAudioOnlyStr = tempOptions[@"audioOnly"];
    BOOL isAudioOnly = [tempOptions[@"audioOnly"] boolValue];
//    NSString* isAudioMutedStr = tempOptions[@"audioMuted"];
    BOOL audioMuted = [tempOptions[@"audioMuted"] boolValue];
//    NSString* isVidoMutedStr = tempOptions[@"videoMuted"];
    BOOL videoMuted = [tempOptions[@"videoMuted"] boolValue];
    NSString* subjectStr = tempOptions[@"subject"];
    NSString* tokenStr = tempOptions[@"token"];
    NSDictionary* tempUserInfo = tempOptions[@"userInfo"];
    NSString* displayName = tempUserInfo[@"displayName"];
    NSString* avatarUrl = tempUserInfo[@"avatar"];
    NSURL* avatar;
    if (avatarUrl == (id)[NSNull null] || avatarUrl.length == 0 ) {
        avatar = nil;
    } else {
        avatar = [[NSURL alloc] initWithString:avatarUrl];
    }
    NSString* email = tempUserInfo[@"email"];
    
    JitsiMeetUserInfo *tempUserInfo1 = [[JitsiMeetUserInfo alloc] init];
    tempUserInfo1.displayName = displayName;
    tempUserInfo1.avatar = avatar;
    tempUserInfo1.email = email;
    
    BOOL inviteEnable = [tempOptions[@"inviteEnable"] boolValue];
    BOOL chatEnable = [tempOptions[@"chatEnable"] boolValue];
    BOOL calenderEnable = [tempOptions[@"calenderEnable"] boolValue];
    BOOL pipEnable = [tempOptions[@"pipEnable"] boolValue];
    BOOL callIntegrationEnable = [tempOptions[@"callIntegrationEnable"] boolValue];
    BOOL closeCaptionsEnable = [tempOptions[@"closeCaptionsEnable"] boolValue];
    BOOL iosRecordingEnable = [tempOptions[@"iosRecordingEnable"] boolValue];
    BOOL liveStreamingEnable = [tempOptions[@"liveStreamingEnable"] boolValue];
    BOOL meetingNameEnable = [tempOptions[@"meetingNameEnable"] boolValue];
    BOOL meetingPasswordEnable = [tempOptions[@"meetingPasswordEnable"] boolValue];
    BOOL raiseHandEnable = [tempOptions[@"raiseHandEnable"] boolValue];
    BOOL recordingEnable = [tempOptions[@"recordingEnable"] boolValue];
    BOOL tileViewEnable = [tempOptions[@"tileViewEnable"] boolValue];
    BOOL toolboxEnable = [tempOptions[@"toolboxEnable"] boolValue];
    
    commandBack = command;
    
    jitsiMeetView = [[JitsiMeetView alloc] initWithFrame:self.viewController.view.frame];
    jitsiMeetView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    jitsiMeetView.delegate = self;
    
    JitsiMeetConferenceOptions *options = [JitsiMeetConferenceOptions fromBuilder:^(JitsiMeetConferenceOptionsBuilder *builder) {
        builder.serverURL = [NSURL URLWithString: serverUrl];
        builder.room = room;
        builder.subject = subjectStr;
        builder.welcomePageEnabled = NO;
        builder.audioOnly = isAudioOnly;
        builder.token = tokenStr;
        builder.audioMuted = audioMuted;
        builder.videoMuted = videoMuted;
        builder.userInfo = tempUserInfo1;
        [builder setFeatureFlag:@"invite.enabled" withBoolean:inviteEnable];
        [builder setFeatureFlag:@"chat.enabled" withBoolean:chatEnable];
        [builder setFeatureFlag:@"calendar.enabled" withBoolean:calenderEnable];
        [builder setFeatureFlag:@"pip.enabled" withBoolean:pipEnable];
        [builder setFeatureFlag:@"call-integration.enabled" withBoolean:callIntegrationEnable];
        [builder setFeatureFlag:@"close-captions.enabled" withBoolean:closeCaptionsEnable];
        [builder setFeatureFlag:@"ios.recording.enabled" withBoolean:iosRecordingEnable];
        [builder setFeatureFlag:@"live-streaming.enabled" withBoolean:liveStreamingEnable];
        [builder setFeatureFlag:@"meeting-name.enabled" withBoolean:meetingNameEnable];
        [builder setFeatureFlag:@"meeting-password.enabled" withBoolean:meetingPasswordEnable];
        [builder setFeatureFlag:@"raise-hand.enabled" withBoolean:raiseHandEnable];
        [builder setFeatureFlag:@"recording.enabled" withBoolean:recordingEnable];
        [builder setFeatureFlag:@"tile-view.enabled" withBoolean:tileViewEnable];
        [builder setFeatureFlag:@"toolbox.enabled" withBoolean:toolboxEnable];
    }];
    
    [jitsiMeetView join: options];
    [self.viewController.view addSubview:jitsiMeetView];
}

- (void)backButtonPressed:(CDVInvokedUrlCommand *)command {
    if(jitsiMeetView) {
        [jitsiMeetView removeFromSuperview];
        jitsiMeetView = nil;
    }
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"finishMeeting"];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}


- (void)destroy:(CDVInvokedUrlCommand *)command {
    if(jitsiMeetView){
        [jitsiMeetView removeFromSuperview];
        jitsiMeetView = nil;
    }
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"DESTROYED"];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

void _onJitsiMeetViewDelegateEvent(NSString *name, NSDictionary *data) {
    NSLog(
        @"[%s:%d] JitsiMeetViewDelegate %@ %@",
        __FILE__, __LINE__, name, data);

}

- (void)conferenceFailed:(NSDictionary *)data {
    _onJitsiMeetViewDelegateEvent(@"CONFERENCE_FAILED", data);
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"CONFERENCE_FAILED"];
    [pluginResult setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:commandBack.callbackId];
}

- (void)conferenceJoined:(NSDictionary *)data {
    _onJitsiMeetViewDelegateEvent(@"CONFERENCE_JOINED", data);
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"CONFERENCE_JOINED"];
    [pluginResult setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:commandBack.callbackId];
}

- (void)conferenceLeft:(NSDictionary *)data {
    _onJitsiMeetViewDelegateEvent(@"CONFERENCE_LEFT", data);
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"CONFERENCE_LEFT"];
    [pluginResult setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:commandBack.callbackId];

}

- (void)conferenceWillJoin:(NSDictionary *)data {
    _onJitsiMeetViewDelegateEvent(@"CONFERENCE_WILL_JOIN", data);
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"CONFERENCE_WILL_JOIN"];
    [pluginResult setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:commandBack.callbackId];
}

- (void)conferenceTerminated:(NSDictionary *)data {
    _onJitsiMeetViewDelegateEvent(@"CONFERENCE_TERMINATED", data);
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"CONFERENCE_TERMINATED"];
    [pluginResult setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:commandBack.callbackId];
}

- (void)loadConfigError:(NSDictionary *)data {
    _onJitsiMeetViewDelegateEvent(@"LOAD_CONFIG_ERROR", data);
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"LOAD_CONFIG_ERROR"];
    [pluginResult setKeepCallbackAsBool:YES];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:commandBack.callbackId];
}


@end

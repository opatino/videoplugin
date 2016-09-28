/*global cordova, module*/

module.exports = {
    playIP: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "playIP", []);
    },
    setToken: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "setToken", []);
    }
    setLicense: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "setLicense", []);
    }
    getAudios: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "getAudios", []);
    }
    changeAudio: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "changeAudio", []);
    }
    getSubtitles: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "getSubtitles", []);
    }
    changeSubtitle: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "changeSubtitle", []);
    }
    getSubtitles: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "getSubtitles", []);
    }
    supportMulticast: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "supportMulticast", []);
    }
    muteVideo: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "muteVideo", []);
    }
    setVideoSize: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "setVideoSize", []);
    }
    playDrm: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "playDrm", []);
    }
    playAudio: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "playAudio", []);
    }
    pauseAudio: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "pauseAudio", []);
    }
    resumeAudio: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "resumeAudio", []);
    }
    stopAudio: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "stopAudio", []);
    }
    seekAudio: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "seekAudio", []);
    }
    playPositionAudio: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "playPositionAudio", []);
    }
    playTimeAudio: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "playTimeAudio", []);
    }
    playVOD: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "playVOD", []);
    }
    pauseVOD: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "pauseVOD", []);
    }
    resumeVOD: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "resumeVOD", []);
    }
    playPositionVOD: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "playPositionVOD", []);
    }
    seekVOD: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "seekVOD", []);
    }
    playRtsp: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "playRtsp", []);
    }
    ffRtsp: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "ffRtsp", []);
    }
    rewindRtsp: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "rewindRtsp", []);
    }
    seekRtsp: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "seekRtsp", []);
    }
    release: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "VideoPlugin", "release", []);
    }
};

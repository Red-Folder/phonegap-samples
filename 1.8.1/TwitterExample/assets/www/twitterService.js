// ///////////////////
(function(){
// ///////////////////

// get local ref to global PhoneGap/Cordova/cordova object for exec function
var cordovaRef = window.PhoneGap || window.Cordova || window.cordova; // old to new fallbacks


cordovaRef.addConstructor(function() {
	cordovaRef.addPlugin("twitterService", new BackgroundService('com.red_folder.sample.TwitterService'));               
});

// ///////////////////
})();
// ///////////////////
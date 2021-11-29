# Onfido Android Authentication SDK

## Overview

The Onfido Android Authentication SDK provides a set of screens for Android applications to capture 3D face scans for the purpose of identity authentication.

//todo Add screenshots



## Getting Started

The SDK supports Android API level 21 and above. Our configuration is currently set to the following:

- `minSdkVersion = 21`
- `targetSdkVersion = 28`
- `android.useAndroidX=true`
- `Kotlin = 1.3+`


### 1. Add the SDK dependency

```gradle
repositories {
  mavenCentral()
}

dependencies {
  implementation 'com.onfido.sdk:authentication:x.y.z'
}
```

#### SDK Size & Minification

The Authentication SDK ships with a real-time image processing, computer vision, and machine learning pipeline, as well as machine learning models that perform quality, security, and pre-Liveness checks. In order to minimize the size of your app, the C++ code needs to be compiled for each of the CPU architectures (known as "ABIs") present in the Android environment. Currently, the SDK supports the following ABIs:

* `armeabi-v7a`: Version 7 or higher of the ARM processor. Most recent Android phones use this
* `arm64-v8a`: 64-bit ARM processors. Found on new generation devices
* `x86`: Most tablets and emulators
* `x86_64`: Used by 64-bit tablets

You can considerably reduce the size of your `.apk` by applying APK split by ABI, editing your `build.gradle` to the following:
```gradle
android {

  splits {
    abi {
        enable true
        reset()
        include 'x86', 'x86_64', 'arm64-v8a', 'armeabi-v7a'
        universalApk false
    }
  }
}
```
Read the [Android documentation](http://tools.android.com/tech-docs/new-build-system/user-guide/apk-splits) for more information.

Average size (with Proguard enabled):

| ABI          |  Size  |
| :----------- | :----- |
| armeabi-v7a  | 5.3 Mb |
| arm64-v8a    | 5.7 Mb |
| universal    | 6.8 Mb |

### 2. Build a configuration object

```kotlin
val config = OnfidoAuthenticationConfig.builder(this)
   .withSdkToken(sdkToken)
   .withRetryCount(2)
   .build()
```

Use `.withRetryCount()` to set the number of repeat attempts a user can do after the first unsuccessful try.

### 3. Instantiating and starting the flow

```kotlin
val OnfidoAuthentication onfidoAuthentication = OnfidoAuthenticationImpl(this)

onfidoAuthentication.startActivityForResult(
 this,    // must be an Activity or Fragment (support library)
 AUTH_REQUEST_CODE,   // this request code will be important for you on onActivityResult() to identity the onfido callback
 config   // pass previously created configuration object
)

```

You have now successfully started the flow. Read the next sections to learn how to handle callbacks and customize the SDK.


## Handling callbacks

To receive the result from the flow, you should override the method `onActivityResult` on your `Activity`/`Fragment`. The following code is provided as an example:

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
   super.onActivityResult(requestCode, resultCode, data)

   if (requestCode == AUTH_REQUEST_CODE) {
       onfidoAuthentication.handleActivityResult(resultCode, data, object : OnfidoAuthentication.ResultListener {
               override fun onUserCompleted(authResult: OnfidoAuthentication.AuthenticationResult) {
                   // User completed
               }

               override fun onUserExited(exitCode: OnfidoAuthentication.ExitCode) {
                   // User exited
               }

               override fun onError(exception: OnfidoAuthentication.AuthException) {
                   // An error occurred during the flow
               }
           })
   }
}

```

When the user has successfully completed the flow and the captured 3D face scan has been uploaded, the `onUserCompleted` method will be invoked. The `AuthenticationResult` object contains information on whether the Authentication Session is verified and a JWT token which can be used to validate the response.

```kotlin
data class AuthenticationResult(
   val token: String?,
   val verified: Boolean,
   val uuid: String
)
```
If the user exits the flow without completing it, the `onUserExited` method will be invoked with an exit code.

| `exitCode`                            |
| ------------------------------------- |
| USER_CANCELLED                        |
| USER_CONSENT_DENIED                   |
| CAMERA_PERMISSION_DENIED              |

In case of an unexpected error, `onError` method will be invoked with a relevant error message in the `AuthException` object. Error messages are not in a presentable format to the end user and are not localised.

## Customizing the SDK

### UI customization

In order to enhance the user experience on the transition between your application and the SDK, you can provide some customization by defining certain colors inside your `colors.xml` file:


| Color     |    Description    |
| -----|-------|
| `onfidoColorPrimaryDark`     |    Color of the status bar    |
| `onfidoColorAccent`     |    Default color of certain UI elements such as dual spinner around selfie preview, oval around images in retry screen, and upload progress color. These elements can also be customised individually    |
| `onfidoTextColorPrimary`     |    Primary color of the texts used throughout the screens    |
| `onfidoPrimaryButtonTextColor`     |    Color of the text inside the primary action buttons    |
| `onfidoPrimaryButtonDisabledTextColor`     |    Color of the text inside the primary action buttons when disabled    |
| `onfidoPrimaryButtonColor`     |    Background color of the primary action buttons    |
| `onfidoPrimaryButtonColorPressed`     |    Background color of the primary action buttons when pressed    |
| `onfidoPrimaryButtonColorDisabled`     |    Background color of the primary action buttons when disabled    |
| `onfidoSecondaryButtonColor`     |    Background color of the secondary action buttons    |
| `onfidoSecondaryButtonColorPressed`     |    Background color of the secondary action buttons when pressed    |
| `onfidoAuthDualSpinnerColor`     |    Color of dual spinner rotating around selfie preview. This will override the default color provided by `onfidoColorAccent`    |
| `onfidoAuthRetryScreenOvalStrokeColor`     |    Stroke color of oval on ideal selfie image in retry screen. This will override the default color provided by `onfidoColorAccent`    |
| `onfidoUploadProgressFillColor`     |    Fill color of the uploading progress indicator bar. This will override the default color provided by `onfidoColorAccent`    |
| `onfidoPrimaryButtonColorDynamicDimmingMode`     |    Background color of the primary action buttons in dark mode    |
| `onfidoPrimaryButtonColorPressedDynamicDimmingMode`     |     Background color of the primary action buttons when pressed in dark mode   |
| `onfidoPrimaryButtonColorDisabledDynamicDimmingMode`     |    Background color of the primary action buttons when disabledin dark mode    |


You can customize the corner radius of all buttons by overriding `onfidoButtonCornerRadius` in your `dimens.xml` resource file.

## Localization

The Android SDK supports and maintains translations for the following locales:

- English    (en) :uk:
- Spanish    (es) :es:
- French     (fr) :fr:
- German     (de) :de:

The Android SDK also allows for the selection of a specific custom language for locales that Onfido does not currently support. You can have an additional XML strings file inside your resources folder for the desired locale (for example, `res/values-it/onfido_strings.xml` for :it: translation), with the content of our `strings.xml` file, translated for that locale.

By default, we infer the language to use from the device settings. However, you can also use the `withLocale(Locale.ITALIAN)` method of the `OnfidoAuthenticationConfig.Builder` to select a specific language.

**Note**: If the strings translations change it will result in a minor version change. If you have custom translations you're responsible for testing your translated layout.

If you want a locale translated you can get in touch with us at [android-sdk@onfido.com](mailto:android-sdk@onfido.com).

## User Consent Screen

This step contains a screen to collect US end users' privacy consent for Onfido. It contains the consent language required when you offer your service to US users as well as links to Onfido's policies and terms of use. This is an optional screen.

The user must click "Accept" to move past this step and continue with the flow. The content is available in English only, and is not translatable.

```kotlin
val config = OnfidoAuthenticationConfig.builder(this)
   ...
   .withUserConsentScreen()
   ...
   .build()

```

//todo Add screenshots

:warning: This step doesn't automatically inform Onfido that the user has given their consent.

If you choose to disable this step, you must incorporate the required consent language and links to Onfido's policies and terms of use into your own application's flow before your end user starts interacting with the Onfido SDK.

For more information about this step, and how to collect user consent, please visit [Onfido Privacy Notices and Consent](http://developers.onfido.com/guide/onfido-privacy-notices-and-consent).

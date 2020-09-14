# Docspell Android Client

This is an android app for [docspell](https://docspell.org), a digital
document organizer and archive. It can be used to conveniently upload
files from your android device.

It works as follows:

- Create a list of docspell urls and store them inside the app: Open
  Docspell in your browser and go to *Collective Settings ‣ Sources*.
  Create a new source or click *Show* on an existing one. Then add the
  *Public API Upload URL* to this app by scanning the QR code.
- Use whatever app, for example a document scanner app, like the free
  [OpenNoteScanner](https://github.com/ctodobom/OpenNoteScanner) and
  share the result with the docspell app. It will be uploaded to the
  configured url and docspell starts processing it immediatly.

A (slightly :)) longer version from the above is
[available](https://docspell.org/docs/tools/android/), too.

## Installing

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
    alt="Get it on F-Droid"
    height="80">](https://f-droid.org/packages/org.docspell.docspellshare)

Or

Download from the [release page](https://github.com/docspell/android-client/releases/latest).

## Building

Using gradle:

``` shell
gradle assembleRelease
```

Settings for signing must be made available.


## Android 7.0 + TLS

If you run in issues regarding your docspell server and tls, check
whether you use android 7.0. It might be
[this](https://github.com/nextcloud/news-android/issues/567#issuecomment-309700308).

Summary: The `ssl_ecdh_curve secp384r1;` definition in nginx/apache is
the problem. Replacing this with `ssl_ecdh_curve prime256v1;` solved
it. Or, if possible, update android to 7.1+.


## Screenhots

![screenshot-1](https://raw.githubusercontent.com/docspell/android-client/master/fastlane/metadata/android/en-US/images/phoneScreenshots/1.jpg)
![screenshot-2](https://raw.githubusercontent.com/docspell/android-client/master/fastlane/metadata/android/en-US/images/phoneScreenshots/2.jpg)
![screenshot-3](https://raw.githubusercontent.com/docspell/android-client/master/fastlane/metadata/android/en-US/images/phoneScreenshots/3.jpg)

# Docspell Android Client

This is a very basic android app that can be used to upload files from
your android device to [docspell](https://docspell.org).

It works as follows:

- Create a list of docspell urls and store them inside the app: go to
  *Collective Settings ‣ Sources* and create a new source or click
  *Show* on an existing one. Then add the *Public API Upload URL* to
  this app.
- Use whatever app, for example a document scanner app, like the free
  [OpenNoteScanner](https://github.com/ctodobom/OpenNoteScanner) and
  share the result with the docspell android app. It will be uploaded
  it to the configured url and docspell starts processing it.

A (slightly :)) longer version from the above is
[available](https://docspell.org/docs/tools/android/), too.

*Note: I'm [trying to get it into the f-droid
store](https://gitlab.com/fdroid/fdroiddata/-/merge_requests/7230),
until then you'd need to download from the [release
page](https://github.com/docspell/android-client/releases/latest) and
install manually.*

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

![screenshot-1](https://raw.githubusercontent.com/docspell/android-client/master/fastlane/metadata/android/en-US/phoneScreenshots/1.jpg)
![screenshot-2](https://raw.githubusercontent.com/docspell/android-client/master/fastlane/metadata/android/en-US/phoneScreenshots/2.jpg)
![screenshot-3](https://raw.githubusercontent.com/docspell/android-client/master/fastlane/metadata/android/en-US/phoneScreenshots/3.jpg)

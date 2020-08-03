# Docspell Android Client

This is a very basic android app that can be used to upload files from
your android device to [docspell](https://docspell.org).

It works as follows:

- create a list of docspell urls and store them inside the app
- the app hooks into the "share with" android menu
- Use whatever app, for example a document scanner app and share the
  result with the docspell android app. It will upload it to the
  configured url.

*Note, this is in very early state. I'm trying to get it into the
f-droid store, until then you'd need to download from the release page
and install manually.*

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

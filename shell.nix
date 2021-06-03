let
  nixpkgsUnstable = builtins.fetchTarball {
    ##this is nixpkgs-21.05 @ 03.06.2021
    url = "https://github.com/NixOS/nixpkgs/archive/eaba7870ffc3400eca4407baa24184b7fe337ec1.tar.gz";
  };
  pkgsUnstable = import nixpkgsUnstable {
    config = {
      android_sdk = {
        accept_license = true;
      };
      allowUnfree = true;
    };
  };
in
{ pkgs ? pkgsUnstable }:

(pkgs.buildFHSUserEnv {
  name = "android-sdk-env";
  targetPkgs = pkgs: (with pkgs;
    [
      androidenv.androidPkgs_9_0.androidsdk
      glibc
      gradle
      android-studio
    ]);
  runScript = ''
    bash
  '';
}).env


# let
#   nixpkgsUnstable = builtins.fetchTarball {
#     url = "https://github.com/NixOS/nixpkgs-channels/archive/nixos-unstable.tar.gz";
#   };
#   pkgsUnstable = import nixpkgsUnstable {
#     config = {
#       android_sdk = {
#         accept_license = true;
#       };
#     };
#   };

#   androidSdk = pkgsUnstable.androidenv.androidPkgs_9_0.androidsdk;
#   platformTools = pkgsUnstable.androidenv.androidPkgs_9_0.platform-tools;
# in
# with pkgsUnstable;

# mkShell {
#   buildInputs = [
#     platformTools
#     androidSdk
#     gradle
#     glibc
#   ];

#   GRADLE_OPTS = "-Dorg.gradle.project.android.aapt2FromMavenOverride=${androidSdk}/libexec/android-sdk/build-tools/28.0.3/aapt2";
# }

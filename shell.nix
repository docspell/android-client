let
  nixpkgs = builtins.fetchTarball {
    ##this is nixpkgs-22.05 @ 27.05.2022
    url = "https://github.com/NixOS/nixpkgs/archive/6efc186e6079ff3f328a2497ff3d36741ac60f6e.tar.gz";
  };
  defaultPkgs = import nixpkgs {
    config = {
      android_sdk = {
        accept_license = true;
      };
      allowUnfree = true;
    };
  };
in
{ pkgs ? defaultPkgs }:

(pkgs.buildFHSUserEnv {
  name = "android-sdk-env";
  targetPkgs = pkgs: (with pkgs;
    [
      androidenv.androidPkgs_9_0.androidsdk
      glibc
      (gradle.override {
        java = openjdk11;
      })
      android-studio
      openjdk11
      git
    ]);

  profile = ''
    export JAVA_HOME="${pkgs.openjdk11}/lib/openjdk"
  '';

  runScript = ''
    bash
  '';
}).env

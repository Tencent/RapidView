# Rapid Studio Visual Studio Code Extension

This extension can help you quickly develop the RapidView project.

## Useage

### Install from extension market

Search rapidtudio in vscode extension market and install it:

![Install From Extension Market](https://raw.githubusercontent.com/YongdongHe/RapidView/master/rapidstudio_vscode_extension/rapidstudio/resource/install_from_market.png)


### Set up Android Debug Bridge(adb)

To use rapid stuido extension, you need to configure adb install path in .vscode/settings.json.

For example:
```json
{
    "rapidstudio.adbPath": "/usr/local/bin/adb"
}
```

Check Extension Settings for more information about this.

If you have added adb to path, skip this step.

How to add path:

+ [MacoS](https://stackoverflow.com/questions/17901692/set-up-adb-on-mac-os-x)
+ [WIndows](https://stackoverflow.com/questions/23400030/windows-7-add-path)

### More Settings

Check [Extension Settings](#extension-settings) for more information about this.

## Requirements

Visual Studio Code Version: 1.17.0+

Android Debug Bridge(adb)

## Features
### Create new rapid workspace and view

Items in explorer right-click menu:

* `New Rapid Workspace ` : Initial a rapid workspace in current root directory. This operation creates a file named rapid_workspace.json.
* `New Rapid View ` : Create a view, and bind a main file for it.

### Quick debug

Buttions in the navigation bar in the upper right corner of the window:

* `Sync File ` : Quickly sync single file to sdcard
* `Sync Project ` : Quickly sync all files under the project to sdcard

### Automatic completion 

XML attributes and Lua function auto-completion support for rapid view. You can also install other lua extension for vscode extension market.

### Log Output Colorizer

Used the vscode-log-output-colorizer project from IBM-Bluemix:

<https://github.com/IBM-Bluemix/vscode-log-output-colorizer>

Language extension for VSCode that adds syntax colorization for both the output/debug/extensions panel and *.log files.

Note: If you are using other extensions that colorize the output panel, it could override and disable this extension.


## Extension Settings

Configure your project in .vscode/settings.json:

* `rapidstudio.folder` : The mobile device sdcard directory which rapid files will be placed on.
* `rapidstudio.viewMappingFile` : The name of file that stores the mapping between views and files.
* `rapidstudio.adbPath`: Android Debug Bridge(adb) install path.For example: `"/usr/local/bin/adb"`

For example:
```json
{
    "rapidstudio.folder": "/sdcard/rapid_debug/",
    "rapidstudio.viewMappingFile": "rapid_debug_config.json",
    "rapidstudio.adbPath": "/usr/local/bin/adb"
}
```


## Known Issues

This version does not currently support custom automatic completions for xml attributes and lua functions.

## Release Notes

### 1.0.0

The version released for testing.

### 1.0.1

Add explorer menu to add new rapid view. 

### 1.0.2

Improve log output.

### 1.0.3

Add adb path configuration that can be modified.

## For more information

### For Contributors
If you want to help us out, you are more than welcome to. You can send a pull request or leave your question in the issue.

You can also send me an email: <heyongdonghe@qq.com>

**Enjoy!**
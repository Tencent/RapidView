# Rapid Studio Visual Studio Code Extension

This extension can help you quickly develop the RapidView project.

[中文文档请点击这里](https://github.com/YongdongHe/RapidView/blob/master/rapidstudio_vscode_extension/rapidstudio/resource/README_CHINESE.md)

## Useage

### Install from extension market

Search rapidtudio in vscode extension market and install it:

![Install From Extension Market](https://raw.githubusercontent.com/YongdongHe/RapidView/master/rapidstudio_vscode_extension/rapidstudio/resource/install_from_market.png)


### Set up Android Debug Bridge(adb)

To use rapid stuido extension, you need to configure adb install path in .vscode/settings.json (File->Preferences->Settings).

Windows Examlple：
```json
{
    "rapidstudio.adbPath": "C:\\Users\\username\\AppData\\Local\\Android\\sdk\\platform-tools\\adb"
}
```


MacOS Examlple(Install adb with homebrew)：
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

### Set the debug path in sdcard

All files under workspace will be push to a folder you set when click ``Sync File`` or ``Sync Project``.

You can set the folder by configuring settings.json, for example:
```json
{
    "rapidstudio.folder": "/sdcard/rapid_debug/"
}
```


### More Settings

Check [Extension Settings](#extension-settings) for more information about this.

## Requirements

Visual Studio Code Version: 1.17.0+

Android Debug Bridge(adb)

## Features
### Create new rapid workspace and view

Items in explorer right-click menu:

![Explorer Menu](https://raw.githubusercontent.com/YongdongHe/RapidView/master/rapidstudio_vscode_extension/rapidstudio/resource/explorer_menu.png)

* `New Rapid Workspace ` : Initial a rapid workspace in current root directory. This operation creates a file named rapid_workspace.json.
* `New Rapid View ` : Create a view, and bind a main file for it.

### Quick debug

Buttions in the navigation bar in the upper right corner of the window:

![Title Menu](https://raw.githubusercontent.com/YongdongHe/RapidView/master/rapidstudio_vscode_extension/rapidstudio/resource/title_menu.png)

* `Sync File ` : Quickly sync single file to sdcard
* `Sync Project ` : Quickly sync all files under the project to sdcard

### Automatic completion 

XML attributes and Lua function auto-completion support for rapid view. You can also install other lua extension for vscode extension market.

You can also custom automatic completion for xml\lua through modify the rapid_workspace.json:

```json
{
    "completion" : {
        "xml_tags" : ["MyXMLTag"],
        "xml_attrs" : ["my_attr"],
        "lua_funcs" : ["my_function"]
    }
}
```

Every time you add new rapid workspace, rapid studio will copy a file from template file ``rapid_workspace.json`` under template folder.
You can also modify the template of rapid_workspace.json under the ./vscode/extension/rapidstudio/template.

### Log Output Colorizer

Reference to the realization of the previous project from IBM-Bluemix:

<https://github.com/IBM-Bluemix/vscode-log-output-colorizer>

Language extension for VSCode that adds syntax colorization for both the output/debug/extensions panel and *.log files.

Note: If you are using other extensions that colorize the output panel, it could override and disable this extension.


## Extension Settings

Configure your project in .vscode/settings.json:

![VScode Settings](https://raw.githubusercontent.com/YongdongHe/RapidView/master/rapidstudio_vscode_extension/rapidstudio/resource/settings.png)

* `rapidstudio.folder` : The mobile device sdcard directory which rapid files will be placed on.
* `rapidstudio.viewMappingFile` : The name of file that stores the mapping between views and files.
* `rapidstudio.adbPath`: Android Debug Bridge(adb) install path.For example: `"/usr/local/bin/adb"`
* `rapidstudio.autoSync` : When saving, the file is automatically synchronized to SD card. Default set to true.

For example:
```json
{
    "rapidstudio.folder": "/sdcard/rapid_debug/",
    "rapidstudio.viewMappingFile": "rapid_debug_config.json",
    "rapidstudio.adbPath": "/usr/local/bin/adb",
    "rapidstudio.autoSync": true,
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

### 1.0.4

Add autoSync configuration.When saving, the file is automatically synchronized to SD card.

### 1.0.5

+ Fix some bugs in synchronizing file and project.
+ Add custom auto completions config.
+ Support to custom workspace template.

### 1.0.6

+ Fix a bugs that console and output will be recognized as a file when syncing file.

### 1.0.7

+ Optimize the xml snipptes.
+ Optimize colorized log output.


### 1.0.8

+ Speed up project synchronization.

### 1.0.9

+ Fix a bug some device cannot sycn project.

## For more information

### For Contributors
If you want to help us out, you are more than welcome to. You can send a pull request or leave your question in the issue.

You can also send me an email: <heyongdonghe@qq.com>

**Enjoy!**
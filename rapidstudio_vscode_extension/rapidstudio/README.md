# Rapid Studio Visual Studio Code Extension

This extension can help you quickly develop the RapidView project.

## Features

### Quick debug

* `Sync File `: Quickly sync single file to sdcard
* `Sync Project `: Quickly sync all files under the project to sdcard

### Automatic completion 

xml and lua auto-completion

### Log Output Colorizer

Used the vscode-log-output-colorizer project from IBM-Bluemix:

<https://github.com/IBM-Bluemix/vscode-log-output-colorizer>

Language extension for VSCode that adds syntax colorization for both the output/debug/extensions panel and *.log files.

Note: If you are using other extensions that colorize the output panel, it could override and disable this extension.


## Requirements

Visual Studio Code Version: 1.17.0+

## Extension Settings

Configure your project in .vscode/settings.json:

* `rapidstudio.folder` : The mobile device sdcard directory which rapid files will be placed on.

For example:
```
{
    "rapidstudio.folder": "/sdcard/rapid_debug/"
}
```


## Known Issues

This version does not currently support custom views and completions.

## Release Notes

### 0.0.1

The version released for testing.



## For more information

### For Contributors
If you want to help us out, you are more than welcome to. You can send a pull request or leave your question in the issue.

You can also send me an email: <heyongdonghe@qq.com>

**Enjoy!**
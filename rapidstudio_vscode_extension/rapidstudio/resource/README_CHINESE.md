# Rapid Studio Visual Studio Code Extension

这是为RapidStudio定制的Visual Studio Code拓展。可以帮助你迅速新建和调试RapidView工程。

RapidView是一个Android动态控件加载库，可以从SD Card加载XML布局，利用Lua来实现控件逻辑。以视图为单位，每个视图绑定一个XML主文件，则在视图初始化时，会去寻找主XML进行加载。

## 用法

### 从VSCode拓展商店Rapid Studio安装拓展

点击VScode左侧拓展菜单，在最上方搜索栏搜索rapid studio即可搜索到并安装。

![Install From Extension Market](https://raw.githubusercontent.com/YongdongHe/RapidView/master/rapidstudio_vscode_extension/rapidstudio/resource/install_from_market.png)


### 配置安卓调试工具ADB

ADB在.vscode/settins.json里配置adb的路径（可以通过文件->首选项->设置来进行更改）。

通常windows下，adb被放置在``C:\Users\username\AppData\Local\Android\sdk\platform-tools``下，故可以如下设置：
```json
{
    "rapidstudio.adbPath": "C:\\Users\\username\\AppData\\Local\\Android\\sdk\\platform-tools\\adb"
}
```

MacOS下，取决于安装方式，所在目录会有一定差异，通过homebrew安装时，一般在``/usr/local/bin/adb``下，可按照如下方式配置：
```json
{
    "rapidstudio.adbPath": "/usr/local/bin/adb"
}
```

更多信息请查看[拓展设置](#拓展设置)

如果你已经将ADB添加到了Path中，那么你可以忽略此步骤。

如何将ADB添加到Path？快捷链接：

+ [MacoS](https://stackoverflow.com/questions/17901692/set-up-adb-on-mac-os-x)
+ [WIndows](https://stackoverflow.com/questions/23400030/windows-7-add-path)\

### 配置Rapid调试目录

当你点击Sync File或者Sync Project时，工作空间下的所有文件夹都会被同步到sdcard的指定目录。

你可以通过设置.vscode/settins.json来指定该目录（可以通过文件->首选项->设置 配置setting.json文件），示例：
```json
{
    "rapidstudio.folder": "/sdcard/rapid_debug/"
}



### 更多设置

更多信息请查看[拓展设置](#拓展设置)

## 环境要求

+ Visual Studio Code Version: 1.17.0版本以上

+ 已安装Android Debug Bridge(adb)

## 特性
### 创建Rapid工作空间和Rapid视图

在左侧文件浏览区域右键菜单：

![Explorer Menu](https://raw.githubusercontent.com/YongdongHe/RapidView/master/rapidstudio_vscode_extension/rapidstudio/resource/explorer_menu.png)


* `New Rapid Workspace ` : 在当前根目录下创建Rapid工作空间. 这个操作将在根目录下生成一个rapid_workspace.json文件
* `New Rapid View ` : 新建视图，并且给视图绑定一个XML主文件。

### 快速调试

导航栏右上角的按钮可以帮助快速调试，原理为使用adb将当前文件同步到手机sdcard的Rapid调试文件夹下。该目录可以通过配置rapidstudio.folder来指定。

![Title Menu](https://raw.githubusercontent.com/YongdongHe/RapidView/master/rapidstudio_vscode_extension/rapidstudio/resource/title_menu.png)


* `Sync File ` : 快速同步当前文件到Rapid调试文件夹
* `Sync Project ` : 快速同步当前工程所有文件到Rapid调试文件夹

### 自动补全 

本拓展加入了RapidView自带的控件的XML标签、属性支持，以及Lua关键字、函数补全支持。

你还可以选择安装其他的拓展（这里我们推荐再安装XML和Lua拓展）:

[XML Tools](https://marketplace.visualstudio.com/items?itemName=DotJoshJohnson.xml)

[vscode Lua](https://marketplace.visualstudio.com/items?itemName=trixnz.vscode-lua)

同时你也可以通过修改rapid_workspace.json文件（使用 New Rapid Workspace创建工作空间）自定义一些自动补全：
```json
{
    "completion" : {
        "xml_tags" : ["MyXMLTag"],
        "xml_attrs" : ["my_attr"],
        "lua_funcs" : ["my_function"]
    }
}
```
使用New Rapid Project时，实际上是把rapid_workspace.json的模版文件拷贝了一份，所以你也可以直接修改模版文件，windows平台下，模版被放置在vscode的extension目录下：``C:\Users\username\.vscode\extensions\rapidstudio\tempalte``

这样每次使用New Rapid Project时，会直接使用修改后的模版


## 拓展设置

编辑该文件进行拓展的设置 .vscode/settings.json:
![VScode Settings](https://raw.githubusercontent.com/YongdongHe/RapidView/master/rapidstudio_vscode_extension/rapidstudio/resource/settings.png)

* `rapidstudio.folder` : Rapid调试文件夹在手机sdcard的位置。RapidView加载时所需的XML\Lua和其他资源文件将被放置在该目录下。默认为``/sdcard/``。
* `rapidstudio.viewMappingFile` : 保存Rapid视图跟主文件映射关系的文件。默认为``photon_debug_config.json``
* `rapidstudio.adbPath`: Android Debug Bridge(adb) 完整路径
* `rapidstudio.autoSync`: 是否开启自动同步文件，如果开启的话，保存时（Ctrl + S）将自动同步当前文件
For example:
```json
{
    "rapidstudio.folder": "/sdcard/rapid_debug/",
    "rapidstudio.viewMappingFile": "rapid_debug_config.json",
    "rapidstudio.adbPath": "/usr/local/bin/adb",
    "rapidstudio.autoSync": true,
}
```


## 已知问题

在某些情况下，output面板的log没有颜色。

## 更多

### 给参与者的话

如果您有更好的建议，可以发送Pull Request，或者在Issues中留下您的使用困惑。

您也可以点击链接发送邮件给我: <heyongdonghe@qq.com>

**大吉大利，今晚无BUG!**
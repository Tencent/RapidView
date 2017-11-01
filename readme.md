# RapidView

>RapidView是一套用于开发Android客户端界面、逻辑以及功能的开发组件。布局文件(XML)及逻辑文件(Lua)可以运行时执行，主要用以解决Android客户端界面、逻辑快速更新以及快速开发的诉求。RapidView的XML语法规则与Android原生XML类似，而写逻辑的Lua部分除语言语法规则外，可以直接使用我们提供的Java API以及Android原生API，因此熟悉Android客户端开发的开发者上手成本会非常小。
>
>除了解决动态更新问题外，RapidView希望Android开发者能够以更快的速度开发产品功能需求，因此我们在语法和开发方式上做了一些改变，期望开发者能够实现：小功能极速开发、大功能极速上线。
>
>RapidView希望为开发者带来更小的安装包增量以及更加简单、易于维护和修改的组件库，RapidView的代码组件约180KB(30KB组件+150KB luaj)。

## 组件特性
>* 运行时加载，布局、逻辑可动态刷新
>
>* 无需编译，所见即所得，开发效率更高
>
>* 极小的安装包增量
>
>* Android开发者低上手成本
>
>* 与NATIVE开发体验相同

## 快速上手

>我们为开发者提供了一个简单的DEMO，以及一个简易调试工具Rapid Studio。Rapid Studio除了支持简单的XML语法校验，Lua语法高亮以及自动补全外，还可以实现实时调试，这将极大缩短开发者的调试成本。

### 工程引入RapidView

#### 初始化RapidView
将源码添加到工程目录下，在Application类的onCreate方法中添加如下代码进行初始化：

`RapidPool.getInstance().initialize(this, null); `

如果编译时需要混淆代码，请将*.rapidview.deobfuscated目录保持非混淆状态

#### 加载RapidView

写一张简单的XML布局文件，保存在assets/rapidview目录下。打开RapidConfig.java，在VIEW{}的枚举中分配一个视图名，在Map中与XML关联。

XML布局文件
```
<relativelayout width="match_parent" height="match_parent" backgroundcolor="ffabcdef">
</relativelayout>
```

RapidConfig.java
```
    /**VIEW列表，此处配置仅为防止重名，便于索引。**/
    public enum VIEW{
        native_demo_view, //Demo视图
    }

    /** VIEW和NaitveXML的映射关系，当View不存在服务端下发的XML时，寻找本地XML作为默认布局 **/
    static{
        try{
            msMapViewNaitve.put(RapidConfig.VIEW.native_demo_view.toString(), "demo_view.xml");
        }
        catch (Exception e){
            e.printStackTrace();
        }

```

在需要加载RapidView的地方插入如下代码，即完成了简单的RapidView加载

```
        mRapidView = RapidLoader.load(
                RapidConfig.VIEW.native_demo_view.toString(),
                HandlerUtils.getMainHandler(),
                this,
                RelativeLayoutParams.class,
                map,
                null);

        setContentView(mRapidView.getView(), mRapidView.getParser().getParams().getLayoutParams());
        
```

#### 开发与调试

打开RapidConfig.java，将一个名为DEBUG_MODE的静态boolean成员的值改为true并在本地重新编译并安装到手机。打开Rapid Studio调试小工具，点击文件->打开，选择工程下assets/rapidview目录，打开。

```
    /**DEBUG_MODE
     * 调试模式是否开启，一般发布时需要关闭。开启调试模式后，可以在rapiddebug目录中配置调试文件。调试文件
     * 实时，并以最高优先级生效。强烈推荐使用RapdiView皮肤引擎专用调试工具RapidStudio Studio调试界面，调
     * 试时需要安装开启调试模式的包。**/
    public final static boolean DEBUG_MODE = true;
```

**工具目录下有一个rapid_config.ini的配置，工程目录中utils目录下有一个FileUitl的文件，这两个地方配置的目录要对应起来，否则调试会失败。**


![](http://cms.gtimg.com/android_cms/gzskin/6352e38da357e28ee27cdfc3aad4cdcb.png)

修改XML中的任意可见参数，如backgroundcolor，按ctrl+s保存，观察到log中提示“[100%]xxx”，表明文件被push到手机当中，重新加载当前activity（返回重新进入），查看效果。

### Rapid Studio
点击[下载链接](http://cms.gtimg.com/android_cms/gzopen/8302bfa6dff45c03d0f5ba57ffdde469.zip)下载Rapid Studio工具包

## 许可协议（License）

>RapidView使用 MIT 许可协议， 详见[License](https://github.com/Tencent/RapidView/blob/master/License.txt)文件。

## 其它

>详细开发指南请参阅[开发文档](https://github.com/Tencent/RapidView/blob/master/document.md)，如果有其它问题，可以扫描下方二维码，加入QQ群咨询

![](http://cms.gtimg.com/android_cms/gzskin/c89fb14e3ae91058b2098072dd32965f.png)
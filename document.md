# RapidView开发文档

## 关于RapidView

>RapidView是一套用于开发Android客户端界面、逻辑以及功能的开发组件。布局文件(XML)及逻辑文件(Lua)可以运行时执行，主要用以解决Android客户端界面、逻辑快速更新以及快速开发的诉求。RapidView的XML语法规则与Android原生XML类似，而写逻辑的Lua部分除语言语法规则外，可以直接使用我们提供的Java API以及Android原生API，因此熟悉Android客户端开发的开发者上手成本会非常小。
>
>除了解决动态更新问题外，RapidView希望Android开发者能够以更快的速度开发产品功能需求，因此我们在语法和开发方式上做了一些改变，期望开发者能够实现：小功能极速开发、大功能极速上线。
>
>RapidView希望为开发者带来更小的安装包增量以及更加简单、易于维护和修改的组件库，RapidView的代码组件约180KB(30KB组件+150KB luaj)。

## 快速上手

>我们为开发者提供了一个简单的DEMO，以及一个简易调试工具Rapid Studio。Rapid Studio除了支持简单的XML语法校验，Lua语法高亮以及自动补全外，最重要的是可以实现实时调试，这将极大缩短开发者的调试成本。

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

修改XML中的任意可见参数，如backgroundcolor，重新加载当前页面，查看效果。

## 概念介绍

>RapidView除了能加载本地的布局逻辑文件，还可以加载服务器动态更新的文件布局。虽然后台部分需要团队自行实现，但是RapidView已经提供一套完整的更新方案，因此也衍生出一些与版本、文件相关的概念。

### RapidView版本(RapidVersion)
类似于安卓的APILevel，当客户端迭代对功能进行更新时，需要提高RapidVersion中的版本号。服务器应根据客户端的RapidVersion支持的版本文件。例如客户端在beta版本开发了全新的控件以及接口，在alpha版本不存在这些接口。当服务器下发了包含了新控件的布局文件到alpha版本时，alpha版本的View展示后将会出现残缺。因此RapidVersion用于指示后台当前版本支持哪些文件的加载。

### 视图名(ViewName)
RapidView在加载界面时并不直接操作XML文件，而是先获取一个叫视图名的唯一串，通过视图名关联的主XML，去加载关联XML的名字。服务器在下发文件时还会下发一些json文件，这些文件记录了服务器下发的XML与视图名的关联关系、必要文件列表、文件版本等信息。本地的XML与视图名关联关系在RapidConfig.java中配置。RapidView加载的文件实际包括了assets目录、服务器下发文件保存目录、DEBUG目录。在读取一个文件时，优先级是DEBUG目录（调试模式开启时）>后台下发目录>assets目录。这层包装的逻辑整体处于RapidView外层，对于RapidView来说并不是非常重要，也可以通过源码修改删除这部分逻辑。

### rapidID
RapidView除了以视图名的方式进行加载外，还可以通过实时加载的方式进行，这个过程类似于H5的加载，会根据rapidID找到一个本地的zip包，如果这个zip不存在那么到服务器上以这个id请求这个zip，拉取到本地后解压，加载其中指定的文件。rapidID就是这种实时加载过程中的唯一标识，它还被用于区分不同zip中的同名文件。

## 关键类、接口介绍

>RapidView是由加载模块、解析模块、数据模块、缓存模块、更新模块、task模块、Lua模块等组成的，想灵活的使用，需要先对这些模块有初步的认识，加粗的方法为常用或重要方法。

### RapidLoader
>RapidView的加载有两种方式，一种是优先加载服务器下发的文件，如果文件不存在，那么加载本地默认的文件。这种方式加载的View的优点是能立即加载完成，缺点是后台的文件如果没有下拉成功，则不能实时展示。另一种方式是把需要的文件打成一个zip包，如果本地的包不是最新的那么实时进行请求并展示，这种方式的优点是实时性较强，缺点是有一定的等待时间。一般来说前者用于主要页面的开发，后者用于替代H5，进行全新灵活页面的开发。

RapidLoader封装两种加载方式为两个加载方法。


```
    public static IRapidView load(String  viewName,
                                  Handler UIHandler,
                                  Context parent,
                                  Class   objClazz,
                                  Map<String, Var> dataMap,
                                  IRapidActionListener actionListener )

```
该方法用于加载一个已经缓存好的，或者在assets目录下的文件，立即返回一个IRapidView对象。

```
    public static boolean load(final String    rapidID,
                               final String    xmlName,
                               final boolean   limitLevel,
                               final Handler   UIHandler,
                               final Context   context,
                               final Class     objClazz,
                               final Globals   globals,
                               final RapidDataBinder binder,
                               final Map<String, Var> contextMap,
                               final IListener listener )

```

该方法用于加载一个实时的View，rapidID表示zip包的唯一key。xmlName用于指示加载其中的哪个xml文件。


### RapidObject
>IRapidView对象实际是由RapidObject生产出来的，这个类有两个关键方法initialize()和load()，initialize必须在load之前调用，initialize可以在任意线程调用，load必须在界面线程调用。这两个方法被用于界面的加载和预加载。除了使用RapidLoader外，也可以直接操作这个对象加载界面。

### IRapidView
>这是RapidView的界面类，通过实例化的对象可以操作整个界面。

`int getID();`

这个方法用于获取View的ID，等同于getView().getId()，与getParser().getID()不同的在于，getParser().getID()用于获取XML中定义的（虚拟）id，而这个方法则是获取View的ID。

`String getTag()`

获取标签，一般不用关心

`void setTag(String tag)`

设置一个标签，一般不用关心

**`View getView()`**

重要：获取RapidView生成的View对象

**`RapidParserObject getParser()`**

重要：获取RapidParserObject对象，对RapidView界面的管理基本是获取到Parser后完成的。

`boolean load(Context context, ParamsObject param, IRapidActionListener listener)`

这个方法用于加载一个界面，不需要手动调用，无需关注

`boolean initialize(..)`

这个方法用于加载时初始化，不需要手动调用，无需关注

### IRapidParser
>如果是在Lua中调用，那么只有IRapidParser定义的接口供调用，如果是在java中调用，那么RapidParserObject中的public接口也可以调用，一般来说，IRapidParser中的方法已经足够了。

**`void notify(EVENT event, StringBuilder ret, Object... args)`**

有的控件需要关注外部消息，如onResume/onPause等，这个方法用于将这些消息通知到内部，通知后关注的控件、接口可以接收到这些消息。控件可以在Parser类中重写onNotify方法接受外部消息。LuaJavaBridge可以通过notify方法接收到这些消息。Lua文件可以在onResume/onPause/onDestroy/onKeyBack四个方法中接收消息。

`void setParentView(IRapidViewGroup parentView)`

设置父View，无需关注

`IRapidViewGroup getParentView()`

获取父层View，不常使用

`void setIndexInParent(int index)`

记录一个父类中控件序号，无需关注

`int getIndexInParent()`

获取当前View在父类中的序号，无需关注

`Handler getUIHandler()`

取得一个界面线程的Handler

`void update(String attrKey, Object attrValue)`

更新当前节点中某个XML标签，如调用update("visibility", "gone") 那么view将会隐藏

**`ParamsObject getParams()`**

重要：获取当前RapidView视图对应的ParamsObject对象，addView的时候一般会调用这个对象中的getLayoutParams()方法

`String getID()`

获取在xml中对当前控件设置的id，如：`<imageview id="xxx" />`

**`IRapidView getChildView(String id)`**

重要：获取一个View中的子控件。需传入子控件id

**`RapidDataBinder getBinder()`**

重要：获取数据Binder，在RapidView中，数据Binder同时也承担了数据池的作用，因此进行数据绑定通常需要调用这个方法。

`IRapidActionListener getListener()`

获取IRapidActionListener接口，这个接口是load时传入的，关于这个接口将专门介绍。

`RapidTaskCenter getTaskCenter()`

获取TaskCenter，TaskCenter是当前类中的任务池

`RapidAnimationCenter getAnimationCenter()`

获取AnimationCenter，AnimationCenter是用于补间动画和帧动画的定义。这两种动画一般在XML中定义和使用。而如果项目希望使用更为强大、好用的属性动画，可以直接通过Lua调用，因此RapidView组件未做处理。

`LuaTable getEnv()`

主要是给Lua使用的，获取环境变量。环境变量是在include标签出现时，出现的一个概念。主要是用来给include的文件做标记区分。关于环境变量的使用，请参考include相关的介绍。

`Globals getGlobals()`

获取Luaj的Globals对象，不太需要关心

`RapidLuaJavaBridge getJavaInterface()`

获取提供给Lua使用的Java层接口

`boolean isLimitLevel()`

是否是受限等级的View，受限等级是用于限制一部分开发者使用某些接口、控件、action等的能力，如果项目不需要用到这类能力，加载时，统一填0，这里返回值也将是false。

`Context getContext()`

获取Context

`String getRapidID()`

如果界面是实时加载形式的，将会在加载是提供RapidID，这个方法用于获取RapidID

`int getScreenHeight()`
获取屏幕宽度

`int getScreenWidth()`
获取屏幕高度

`void setNotifyListener(IRapidNotifyListener listener)`
设置通知监听

`IRapidNotifyListener getNotifyListener()`
获取通知监听

### IDataBinder
>RapidView的数据池、数据绑定器，主要承担存储数据以及保存数据绑定关系的功能。如果是Lua调用，能够使用IDataBinder中的方法，如果是java层调用，则能够使用RapidDataBinder中的方法。RapidView中的数据都是以Map形式保存，key是一个String类型的ID，Value为任意类型，在java层最终将被保存成万能类型(Var.java)。

`Handler getUiHandler()`

获取界面线程的Handler

`void addView(IRapidView view)`

通知DataBinder有View需要添加，不需要关注

`void removeView(IRapidView view)`

通知DataBinder有View需要删除，不需要关注

`void update(String key, Object object)`

更新一个数据到数据池中

`void update(String key, String value)`

更新一个数据到数据池中

`void update(LuaTable table)`

更新一个table的数据到数据池中，主要提供给Lua使用

`LuaValue bind(String dataKey, String id, String attrKey)`

将XML某个节点与某个数据绑定起来

`boolean unbind(String dataKey, String id, String attrKey)`

解除XML某个节点与数据的绑定关系

`LuaValue get(String key)`

获取一个数据

`void removeData(String key)`

删除一个数据

### IRapidParams
>LayoutParams的虚拟类，一般只需要调用getLayoutParams方法获取LayoutParams对象

### IRapidTask
>task模块的入口，主要用来执行task、action、filter等，关于task的用法，参见XML语法的相关介绍。

`void setRapidView(IRapidView arrayView)`

设置一个RapidView，用以执行界面操作，无需关心

`void setEnvironment(Map<String, String> mapEnv)`

设置环境变量，无需关心

`LuaTable getEnv()`

获取环境变量，不常用

`void add(Element element)`

添加一个task，一般由加载时自动使用，无需关心

`void run(List<String> listKey)`

执行一系列task

`void run(String key)`

执行一个task

**`void notify(HOOK_TYPE type, String value)`**

重要：事件通知，一般数据填充需要主动告知开始和结束

**`IActionRunner getActionRunner()`**

获取一个action执行器，一般用于lua直接执行action

**`IFilterRunner getFilterRunner()`**

获取一个filter执行器，一般用于lua直接执行filter

### IRapidActionListener
>这是唯一一个加载时可选传入的Listener，设计这个Listener时主要考虑RapidView可能与外部进行一系列交互动作。这些交互的对外输出就要靠这个回调函数，XML通过使用outeraction来调用这个Listener。

### ILuaJavaInterface
>使用Lua时，如果希望调用java，除了通过luajavalib来调用外，还可以通过这个接口来调用。action和filter是XML用于执行功能的特性，而LuaJavaInterface则是Lua执行功能的接口。

`LuaValue create(String objName, args0~5)`

简易的方式创建对象，一些不得不混淆的类可以通过这种方式声明创建，源码中保留了json系列对象，用于示例。

`boolean request(String url, String/IBytes/LuaTable data, LuaTable header, String method, LuaFunction succeedListener, LuaFunction failedListener)`

发起http请求


`boolean isNetworkActive()`

判断网络是否存在

`boolean isWap()`

判断wap网络

`boolean isWifi()`

判断是否处于wifi环境

`boolean is2G()`

判断是否是2G网络

`boolean is3G()`

判断是否是3G网络

`boolean is4G()`

判断是否是4G网络

`String urlDecode(String url)`

对url进行decode

`String urlEncode(String url)`

对url进行encode

`void Log(String tag, String value)`

打印Log

`IBytes decode(String str, String flags)`

base64解压

`String encode(IBytes bytes, String flags)`

base64压缩

`LuaValue addView(String name, String parentID, String above, RapidDataBinder binder, LuaTable data, IRapidActionListener listener)`

加载一个view并添加到指定的位置

`LuaValue loadView(String name, String params, LuaTable data, IRapidActionListener listener)`

load一个View

`void finish()`

结束当前activity

`void startActivity(String xml, LuaTable params)`

启动一个activity，并传入需要加载的xml，只能以实时加载方式调用，因此只能传入xml。

`void delayRun(long milliSec, LuaFunction function)`

延迟在界面线程执行一段逻辑

`void postRun(LuaFunction function)`

post一个函数在界面线程执行

`int dip2px(int dip)`

dip转px

`int px2dip(int px)`

px转dip

`void takePicture(LuaTable params, LuaFunction succeedListener, LuaFunction failedListener)`

拍照接口

`void choosePicture(LuaTable params, LuaFunction succeedListener, LuaFunction failedListener)`

选择一张图片

`Bitmap getBitmapFromBytes(IBytes bytes)`

从将字节流流转换成bmp

`IBytes getBytesFromBitmap(Bitmap bitmap)`

将bmp转换成字节流

`void savePicture(Bitmap bitmap)`

保存图片

`IBytes toMD5Bytes(String source)`

转md5字节流

`IBytes toMD5Bytes(IBytes source)`

转md5字节流

`String toMD5(String source)`

转md5

## XML语法规则说明

>RapidView的XML语法规则整体仿写了Android布局文件的语法规则。简化了部分内容，如：不区分大小写、统一布局单位为dp、砍掉了命名空间等。同时新增了一些全新特性，如新增了通过百分比控制控件大小、include标签新增环境变量、通过task来执行逻辑、通过action、filter来执行功能等，这部分将对XML语法规则做以简单说明。

### 大小写说明
>RapidView的XML语法整体不区分大小写。这里对这个设计多做一下解释：最开始首字母大写或多单词通过下划线分隔，主要用于阅读时单词迅速辨识。但是在实践中出现了很多缩写单词，有的人习惯全部大写有的人习惯首字母大写，如Id(ID)、Tx(TX)，由于辨识分歧，这类特殊标签多了一层大小写的记忆。因此不限制大小写事实上可以降低一个维度的学习成本，实践中发现即使全部小写，对阅读的影响也不是非常严重，主要是因为XML中常用标签都是固定组合，如re..ot;te..ew;li..ot;看过来很快就能识别出relativelayout;textview;linearlayout，因此也推荐全部采用小写的写法。
>
>一个特例是在XML中对数据进行绑定，关联到的数据是区分大小写的。例如某标签backgroundcolor="data@bg_name$ffaabbcc"，这里的bg_name需要区分大小写。

### 特殊标签include和环境变量
>include标签是指某些样式具有一定复用性，将这些样式剥离出独立的XML，使用include标签将它们包含进来。为了解决区分反复include标签中控件等的分辨问题。在include的标签中可以指定一个environment参数，例如：
>
>`<include layout="a.xml" environment="i:1,count:10" />`
>
>这个标签中我们声明了两个环境变量i和count，在被include的XML中，参数值可以通过[]来使用环境变量。如：
>
>`<textview id="content[i]"/>`
>
>环境变量在XML中最终会以纯文本替换的方式替换成相应的内容。因此这个控件的id实际上content1。include的的实现过程是在原XML中释放被include的XML的内容。如果被include的XML希望数据具有隔离性，可以使用binder="new"标签。

### 特殊标签viewstub
>viewstub标签是指在加载时不显示、不加载当前布局，显示时再加载的特殊标签，一个viewstub的例子如：
>
>`<viewstub id="demo_viewstub" layout="demo_viewstub.xml"/>`
>
>当id为demo_viewstub的控件，被操作visibility属性，填充visible时，这个控件将被替换成demo_viewstub.xml中的控件。这个位置也将出现被替换的控件id，demo_viewstub控件id将被删除。


### 特殊标签merge
>在RapidView中，merge并没有实际作用，碰到merge标签将直接跳过当前节点解析下一级节点。merge标签主要用于格式化XML可读性。另外根节点不能使用merge标签。

### 分辨率适配相关
>RapidView中控件宽高的数值写法统一为dp单位，文字大小统一是COMPLEX_UNIT_PX单位。控件的宽度、高度除了wrap_content/match_parent外还支持10%/10%x/10%y三种写法。10%x和10%y表示控件是屏幕宽的10%和屏幕高度的10%。省略了后面的x或y则width参数表示屏幕宽度的10%，height参数表示屏幕高度的10%。

### 数据绑定与默认值
>XML标签的某个内容，如果需要使用外部数据，即binder中的数据。有多种方法，其中最为直接的一种是将当前标签与某个数据绑定起来。格式是[data@数据名$默认值]参考如下写法：
>
>`<textview width="wrap_content" height="wrap_content" text="data@content$默认文字"/>`
>
>这行配置中，数据池中有名为content的数据时，会将text的内容填充为该数据的内容。如果没有，那么该字段的内容是“默认文字”。如果没有这个字段，但过一会外部数据刷新进来，填充了这个字段，那么本来text是“默认文字”，被填充时，这个字段将自动变为被填充的数据。

### 任务（task）标签、动作（action）标签和过滤器（filter）标签
>RapidView希望对于简单的需求以一种快捷的方式进行开发。为了解决这个问题，我们开发了任务、动作、过滤器语法，语法规则如下：
>```
><task id="[TASKID]" hook="[触发时机]" value="[参数]">
>    [filter列表：每一个filter为一个判断语句]
>    [action列表：每一个action代表一个动作]
><task>
>```
>task相当于是一段if( .. && .. && .. ){}逻辑，每一个filter将会根据参数返回一个明确的boolean值，每一个action代表着一类功能动作。task的参数中包含了三个字段，其中id和hook一般至少会出现一个。task的标签中包含着一个filter列表和一个action列表，filter列表写在上面，action列表写在下面。当filter列表中的判断条件全部返回true的时候，按照顺序，逐个执行action列表中的action。
>
>调用一段task有两种方式，一种是主动触发，比如某个控件点击时，一种是被动触发，比如某个数据被填充时。当主动触发时，需要显式的在task标签上声明task的id字段。比如点击某张图片时需要执行这段task，那么imageview的参数列表中需要指定click="[taskid]",click的值就是这个task的id。
>
>被动触发时，需要在hook字段中指定在哪种情况下被动触发task执行。
>```
><!-- 在content数据被填充或被更新时，触发当前task -->
><task hook="datachange" value="content">
><!-- 很少用，在初始化时触发 -->
><task hook="initialize">
><!-- 在加载完成时触发 -->
><task hook="loadfinish">
><!-- 在界面展示时触发 -->
><task hook="viewshow">
><!-- 在开始填充数据时触发 -->
><task hook="data_start">
><!-- 在数据填充完毕时触发 -->
><task hook="data_end">
>```
>在以上的触发时机中，datachange/loadfinish/data_end是特别常用的三个触发时间。需要特别说明的是，PhotonLoader.load时传入的数据一般用于初始化，数据在开始填充和填充完毕，以及界面展示，三个时机rapidView并不能监听到，需要在java代码中主动通知TaskCenter数据开始填充和填充完毕。通知示例：
>`rapidView.getParser().getTaskCenter().notify(IRapidTask.HOOK_TYPE.enum_data_end, "");`
>
>每一个IRapidView保存一份task池，task重名将会后者覆盖前者，task可以声明于XML中的任何位置。

### 控件说明
>RapidView默认提供了17个控件，他们是：
userview/relativelayout/linearlayout/absolutelayout/textview/imageview/progressbar/imagebutton/button/framelayout/scrollview/horizontalscrollview/shaderview/viewstub/runtimeview/viewpager/recyclerview
>
>在对RapidVeiw进行剥离时，我们移除了视频、网络图片、GIF等高级控件，因为这些控件非常依赖项目所使用的网络库和图片缓存组件。我们提供简单易于接入的方式供开发者插入自定义组件，以提倡开发团队复用自己的功能模块，减小安装包大小，关于这部分请参考再开发说明相关部分。
>
>RapidView的控件继承关系与Android原生控件继承关系完全一样，参数也基本相似。如果想详细了解某个标签的实现方式，请搜索[init+标签名]类，或打开对应的Parser文件在索引中找到对应类查看。

>以下竖线左边为参数key，右边为参数value或描述，形式为[key | value]。

___
### View extends null [标签：无]
控件基类，不能直接使用该控件

__background__ | 图片名

__backgroundresource__ | res@ + RapidResource声明的字段，如：res@pic_default

__backgrounddrawable__ | 同backgroundresource

__backgroundcolor__ | 8个16位数字，如:ffffffff，代表ARGB

__clickable__ | boolean

__contentdescription__ | String

__contextclickable__ | String

__drawingcachebackgroundcolor__ | 8个16位数字，如:ffffffff，代表ARGB

__drawingcacheenabled__ | boolean

__drawingcachequality__ | DRAWING_CACHE_QUALITY_AUTO/DRAWING_CACHE_QUALITY_HIGH/DRAWING_CACHE_QUALITY_LOW

__duplicateparentstateenabled__ | boolean

__duplicateparentstate__ | 同duplicateparentstateenabled

__enabled__ | boolean

__focusable__ | boolean

__focusableintouchmode__ | boolean

__hapticfeedbackenabled__ | boolean

__fadingedge__ | int

__horizontalfadingedgeenabled__ | boolean

__horizontalscrollbarenabled__ | boolean

__keepscreenon__ | boolean

__longclickable__ | boolean

__minimumheight__ | float

__minimumwidth__ | float

__padding__ | [float,float,float,float]

__saveenabled__ | boolean

__scrollcontainer__ | boolean

__scrollbarfadingenabled__boolean

__selected__ | boolean

__soundeffectsenabled__ | boolean

__verticalfadingedgeenabled__ | boolean

__verticalscrollbarenabled__ | boolean

__visibility__ | VISIBLE/INVISIBLE/GONE

__willnotcachedrawing__ | boolean

__willnotdraw__ | boolean

__click__ | 任务id

__touchdown__ | 任务id

__touchmove__ | 任务id

__touchup__ | 任务id

__longclick__ | 任务id

__keyevent__ | [id:任务id,event:int]

__createcontextmenu__ | 任务id

__focuschange__[id:任务id,focus:boolean]

__touch__ | 任务id

__animation__ | 动画id

__startanimation__ | 动画id

__clearanimation__ | 动画id

__realid__ | 设置真实的Viewid

__scrollexposure__ | boolean

__statelistdrawable__ | [enabled:drawable,pressed:drawable,selected:drawable,activated:drawable,active:drawable,first:drawable,focused:drawable,last:drawable,middle:drawable,single:drawable,window_focused:drawable]，drawable可以是res@xxx或ffffffff两种形式。
___
### ViewGroup extends View [标签：无]
容器基类，无法直接使用

__descendantfocusability__ | beforedescendants/afterdescendants/blocksdescendants
___
### AbsoluteLayout extends ViewGroup [标签：absolutelayout]

同原生控件
___
### Button extends TextView [标签：button]

同原生控件


__gradientdrawable__ | [cornerradius:int,color:8位0~f,alpha:0~255,strokewidth:int,strokecolor:8位0~f]
___
### FrameLayout extends ViewGroup [标签：framelayout]
同原生控件
___
### HorizontalScrollView extends FrameLayout [标签：horizontalscrollview]
同原生控件

__fling__ | int

__fullscroll__ | int

__scrollto__ | [int,int]

__smoothscrollingenabled__ | [boolean]

__smoothscrollby__ | [int,int]

__smoothscrollto__ | [int,int]
___
### UserView extends null [标签：userview]
UserView可用于直接加载本地已经写好的View，RapidView可以和原生View高度融合，其中加载一些项目已经实现好的通用view，就可以直接使用这个view配置。

__class__ | 类名，混淆的类需要在RapidConfig中msMapUserView对象中配置，非混淆类可填写类全路径
___
### ImageButton extends ImageView [标签：imagebutton]
同原生控件

___
### ImageView extends View [标签：imageview]
同原生控件

__image__ | 图片名

__resizeimage__ | 拉伸图片

__scaletype__ | matrix/fix_xy/fix_start/fit_center/fit_end/center/center_crop/center_inside

__frameanimation__ | frame动画名

__startframeanimation__ | 任意值

__stopframeanimation__ | 任意值

__oneshotframeanimation__ | boolean

__visibleframeanimation__ | [boolean,boolean]
(visible,restart)

__startoffsetframeanimation__ | long

__adjustviewbounds__ | boolean

__maxheight__ | int/0~100%/0~100%x/0~100%y

__minheight__ | int/0~100%/0~100%x/0~100%y

__maxwidth__ | int/0~100%/0~100%x/0~100%y

__minwidth__ | int/0~100%/0~100%x/0~100%y

___
### LinearLayout extends ViewGroup [标签：linearlayout]
同原生控件

__gravity__ | int

__horizontalgravity__ | int

__verticalgravity__ | int

__baselinealigned__ | boolean

__weightsum__ | float

__orientation__ | horizontal/vertical

___
### ProgressBar extends View [标签：progressbar]
同原生控件

__progressbackgroundcolor__ | 8个0~f

__progresscolor__ | 8个0~f

__progressimage__ | 图片名

__indeterminate__ | boolean

__progress__ | int

__max__ | int

__secondaryprogress__ | int

___
### RuntimeView extends ViewGroup[标签：runtimeview]
一个实时加载的View控件，使用这个控件需要服务器配合接入网络部分

__rapidid__ | string

__limitlevel__ | int

__url__ | string

__md5__ | string

__params__ | 传入参数[xx:xx,xx:xx...]

___
### RecyclerView extends ViewGroup[标签：recyclerview]
同原生控件，这个控件的使用一般用lua或java配合传入数据

__layoutmanager__ | linearlayoutmanager(,horizontal/invalid_offset,boolean)/girdlayoutmanager

___
### RelativeLayout extends ViewGroup[标签：relativelayout]
同原生控件

__gravity__ | int

__horizontalgravtiy__ | int

__verticalgravity__ | int

___
### ScrollView extends FrameLayout[标签：scrollview]
同原生控件

__fling__ | int

__fullscroll__ | int

__scrollto__ | int,int

__scrolltochild__ | 控件id

__smoothscrollingenabled__ | boolean

__smoothscrollby__ | int,int

__smoothscrollto__ | int,int

__notifychildscroll__ | boolean

__overscrollmode__ | int

___
### ShaderView extends RelativeLayout[shaderview]

一个用于画渐变色的控件

__lineargradient__ | [int,int,int,int,8位0~f,8位0~f,mirror/repeat]
(x0,y0,x1,y1,color0,color1,type)

___
### TextView extends View [标签：textview]
同系统控件

__ems__ | int

__maxems__ | int

__minems__ | int

__singleline__ | boolean

__ellipsize__ | end/start/middle/marquee

__text__ | String(如果要换行可以在中间插入#)

__textstyle__ | bold/italic/leftitalic/strikethru/underline

__textsize__ | float

__textcolor__ | 8位0~f

__line__ | int

__flag__ | int

__gravity__ | int

__maxlines__ | int

__linespacingextra__ | float

__linespacingmultiplier__ | float

__scalex__ | float

__scaley__ | float

__textscalex__ | float

__freezestext__ | boolean

__maxheight__ | int/0~100%/0~100%x/0~100%y

__minheight__ | int/0~100%/0~100%x/0~100%y

__maxwidth__ | int/0~100%/0~100%x/0~100%y

__minwidth__ | int/0~100%/0~100%x/0~100%y

__autolink__ | [xx,xx,xx,xx]
(xx:phone/web/email/map/all)

__buffertype__ | normal/spannable/editable

__cursorvisible__ | boolean

__hint__ | string

__imeactionid__ | int

__imeactionlabel__ | string

__imeoptions__ | [[xx,xx,xx,xx]]
(xx:normal/actionunspecified/actionNone/actionGo/actionSearch/actionSend/actionNext/actionDone/actionPrevious/flagNoFullscreen/flagNavigatePrevious/flagNavigateNext/flagNoExtractUi/flagNoAccessoryAction/flagNoEnterAction/flagForceAscii)

__includefontpadding__ | boolean

__inputtype__ | int

__rawinputtype__ | int

___
### ViewPager extends ViewGroup [标签：viewpager]
同系统控件，RapidView封装了常用Listener，需要配合lua或java使用

___
### ViewStub extends View [标签：viewstub]

__layout__ | xml名


### LayoutParams说明
>LayoutParams系列参数可以直接在XML中使用，注意容器搭配，使用方法与原生布局文件相同。

___
### ViewGroupParams extends null

__height__ | fill_parent/match_parent/wrap_content/int/0~100%/0~100%x/0~100%y

__width__ | fill_parent/match_parent/wrap_content/int/0~100%/0~100%x/0~100%y

___
### MarginParams extends ViewGroupParams

__margin__ | [float,float,float,float]

__marginleft__ | float

__margintop__ | float

__marginright__ | float

__marginbottom__ | float

___
### AbsListViewLayoutParams extends ViewGroupParams

___
### AbsoluteLayoutParams extends ViewGroupParams

___
### FrameLayoutParams extends MarginParams

__layoutgravity__ | no_gravity/top/bottom/left/right/center_vertical/fill_vertical/center_horizontal/fill_horizontal/center/fill

___
### LinearLayoutParams extends MarginParams

__weight__ | float

__layoutgravity__ | no_gravity/top/bottom/left/right/center_vertical/fill_vertical/center_horizontal/fill_horizontal/center/fill

___
### RecyclerViewLayoutParams extends MarginParams

___
### RelativeLayoutParams extends MarginParams

__alignleft__ | 控件id

__aligntop__ | 控件id

__alignright__ | 控件id

__alignbottom__ | 控件id

__leftof__ | 控件id

__above__ | 控件id

__rightof__ | 控件id

__below__ | 控件id

__centervertical__ | true

__centerhorizontal__ | true

__centerinparent__ | true

__alignparenttop__ | true

__alignparentright__ | true

__alignparentleft__ | true

__alignparentbottom__ | true

___
### ViewPagerParams extends ViewGroupParams


### Action和Filter说明
>Action和Filter都属于需团队扩展的内容，在剥离RapidView时只保留了与RapidView相关的动作和过滤器，以下做以简单介绍。

#### addviewaction | 加载并添加一个View到指定的位置

#### attributeaction | 修改某个控件的某项属性

#### backaction | 模拟返回按键

#### dataaction | 更新数据池中的某个数据

#### integeropertaionaction | 进行整数计算

#### luaaction | 非常重要，调用lua的action

#### outeraction | 发送消息通知外部，会调用IRapidActionListener.notify方法

#### taskaction | 调用其他的task

#### toastaction | 弹出一个toast

#### datafilter | 对数据池中的某项数据进行判断

#### networkfilter | 判断网络环境

## Lua规则说明

>RapidView中Lua的使用全部需要通过XML中的luaaction进行。如：
>
>```
>	<!-- 参数最多出现三个，可选-->
>	<task id="lua_call_click_2">
>		<luaaction function="second" param1="点击了按钮2" param2="这里是一个toast"/>
>	</task>
>	
>	<task hook="loadfinish">
>		<luaaction load="lua_call_demo.lua"/>
>	</task>
>```
>在上面这个例子中在loadfinish时加载了一个lua文件，在按钮点击时调用了其中的second方法。在这个例子中这两个luaaction合并并没什么影响，但是如果反复load的话会导致lua上下文中保存的局部变量被重置。
>
>被调用的Lua需要在头部声明2个局部变量，用以接收IRapidView和ILuaJavaInterface两个接口实力，如：
>
>```
>local mRapidView,mJavaInterface = ...
>
>function second(btnText, toastText)
>	local textControl = mRapidView:getParser():getChildView("lua_call_content")
>
>	--getView()方法取到了真实的界面对象，可以使用原生方法更新数据
>	textControl:getView():setText(btnText)
>
>
>	local toast = luajava.bindClass('android.widget.Toast')
>
>	--弹出一个toast
>	toast:makeText(mRapidView:getView():getContext(), toastText, 0):show()
>end
>```
>
>这两个接收的局部变量将用于操作界面以及调用java接口。

## 再开发说明

>RapidView开源时，移除了与组件无关的LuaJava接口、Action、Filter、控件、网络模块、服务端等模块。考虑到每个项目都有自己维护的控件、功能库、网络模块、图片模块，因此希望接入后能够复用自己项目的模块，以减少功能维护成本、降低安装包大小以及学习成本。

### 动作（action）再开发
全新开发action需要做以下操作：

* 新建一个类，放在action目录下
* 继承ActionObject方法
* 重写`public boolean run()`方法
* 在mMapAttribute中取得action参数，并执行操作
* 在ActionChooser类中添加该类

示例：
```
public class AttributeAction extends ActionObject{

    public AttributeAction(Element element, Map<String, String> mapEnv){
        super(element, mapEnv);
    }

    @Override
    public boolean run(){
        IRapidView view;
        Var id = mMapAttribute.get("cid");
        Var key = mMapAttribute.get("key");
        Var value = mMapAttribute.get("value");

        if( mRapidView == null ){
            return false;
        }


        if( mRapidView.getParser().getID().compareToIgnoreCase(id.getString()) == 0 ) {

            mRapidView.getParser().update(key.getString(), value);
            return true;
        }

        view = mRapidView.getParser().getChildView(id.getString());
        if (view == null) {
            return false;
        }

        view.getParser().update(key.getString(), value);

        return true;
    }
}
```

### 过滤器（filter）再开发
全新开发一个过滤器需要执行以下操作：

* 新建一个类，放在filter目录下
* 重写`public boolean pass()`方法
* 在mMapAttribute中取得action参数，并执行判断
* 在FilterChooser中声明该类

示例：
```
public class NetWorkFilter extends FilterObject{

    public NetWorkFilter(Element element, Map<String, String> mapEnv){
        super(element, mapEnv);
    }

    @Override
    public boolean pass(){
        Var type = mMapAttribute.get("type");
        Var style = mMapAttribute.get("style");
        
        ……

        return isEqual(style.getString());
    }

    ……
}
```

### 控件再开发
控件的开发需要认清继承关系，在view目录中新建一个view，在parser目录中新建一个解析器。在RapidChooser.java中声明该控件。

新建View类示例：

```
public class RapidRuntimeView extends RapidViewGroupObject {

    public RapidRuntimeView(){}

    @Override
    protected RapidParserObject createParser(){
        return new RapidRuntimeViewParser();
    }

    @Override
    protected View createView(Context context){
        return new RuntimeView(context);
    }

    @Override
    public ParamsObject createParams(Context context){
        return new RelativeLayoutParams(context);
    }
}

```
如果是非容器控件，需要继承RapidViewObject，不需要重写createParams方法，如果是容器控件，则需要像示例中的方式继承并开发。

新建Parser类示例：

```
public class RecyclerViewParser extends ViewGroupParser {

    private static Map<String, IFunction> mRecyclerViewClassMap = new ConcurrentHashMap<String, IFunction>();

    static{
        try{
            mRecyclerViewClassMap.put("layoutmanager", initlayoutmanager.class.newInstance());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public RecyclerViewParser(){}

    @Override
    protected void loadFinish(){
        ……
    }

    @Override
    protected IFunction getAttributeFunction(String key, IRapidView view){
        IFunction function = super.getAttributeFunction(key, view);
        if( function != null ){
            return function;
        }

        if( view == null || key == null ){
            return null;
        }

        RapidParserObject.IFunction clazz = mRecyclerViewClassMap.get(key);

        return clazz;
    }

    private static class initlayoutmanager implements IFunction {
        public initlayoutmanager(){}

        public void run(RapidParserObject object, Object view, Var value) {
            ……
        }
    }
}
```

Parser类需要注意的地方：

* 参数请以Map方式进行查询

* Map中的key请保持小写，否则会找不到标签

* loadFinish/onResume/onPause等方法都不是必须重写的。

* 需要认清当前类的继承关系，继承合适的父类，保证参数的复用性。

### 网络模块再开发
RapidView并没有移除网络相关逻辑，如果要使用服务器下发文件，可以复用原有逻辑。

#### 文件下载接口：IDownload
RapidView下载文件使用RapidDownloadWrapper.IDownload接口进行下载，在源码中预留了一个空类RapidDownload.java，实现该类即可完成文件下载功能的接入。


#### 更新、删除文件：RapidUpdate
如果要更新新文件，需要使用RapidUpdate类，更新示例：

`PhotonUpdate.getInstance().load(rsp.vecPhotonSkinFile, rsp.vecDelPhotonSkinFile)`

第一个参数是需要更新的文件列表，第二个参数是需要删除的文件列表。


#### 视图文件更新
更新的文件中需要包含视图文件，视图文件的文件名即为视图名，内容是一段json，json的格式如下：

{"fileList":[{"fileName":"a.xml"},{"fileName":"b.xml"},{"fileName":"c.lua"},{"fileName":"d.png"}],"mainFile":"a.xml","viewName":"[同文件名]","grayCode":"","viewVer":""}

RapidView在后台下拉文件后，会对文件执行完整性校验，视图进行完整性校验，因此fileList表示该视图加载时需要依赖的文件列表。mainFile表示主文件名，viewName表示视图名，grayCode和viewVer选留后台字段。

完整性校验不通过的视图是无法进行加载的，即文件列表中的文件必须存在才能进行加载。

#### RuntimeView请求
需要实现RapidRuntimeEngine.java类，通过rapidID请求到下载url，MD5等信息。

#### 文件保存路径
RapidView需要4个目录：文件保存目录、配置保存目录、DEBUG目录、沙盘目录，可以在FileUtil中进行配置。

## 开发与调试

>RapidView提供了一些简单的开发和调试方法，在这个部分做以简单介绍。

### 开发说明
>RapidView开发建议通过Rapid Studio小工具进行。Rapid Studio开发前需要新建一个文件夹，使用Rapid Studio打开该文件夹，新建脚本（xml或lua）。调试时需要连接手机并保持ADB连接，并将RapidConfig.java 中的DEBUG_MODE改为true编译并安装，发布时需要改回false。

### 调试说明
>RapidView调试主要通过Log进行

* 过滤RAPID_ENGINE关键字可打印全部Log

* 过滤RAPID_ENGINE_NORMAL关键字可打印加相关Log

* 过滤RAPID_ENGINE_ERROR可打印错误Log

* 过滤RAPID_ENGINE_TASK可查看task加载情况

* 过滤RAPID_ENGINE_BENCHMARK可查看性能情况

### Rapid Studio
点击[下载链接](http://cms.gtimg.com/android_cms/gzopen/8302bfa6dff45c03d0f5ba57ffdde469.zip)下载Rapid Studio工具包

### 其它
>如果有其它问题，可以扫描下方二维码，加入QQ群咨询

![](http://cms.gtimg.com/android_cms/gzskin/c89fb14e3ae91058b2098072dd32965f.png)
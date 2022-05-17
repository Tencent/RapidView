--rapidView中调用的Lua文件默认会带上两个参数，一个是View的对象，一个是LuaJavaBridge接口
local mRapidView,mJavaInterface = ...

local mCalCount = 0

function main()
	--通过界面对象可以获取子控件界面对象
	local textControl = mRapidView:getParser():getChildView("lua_call_content")
	
	mCalCount = mCalCount + 1

	--rapidview提供了用于更新attribute的方法
	textControl:getParser():update("text", "点击次数：" .. mCalCount)
end

function second(btnText, toastText)
	local textControl = mRapidView:getParser():getChildView("lua_call_content")

	--getView()方法取到了真实的界面对象，可以使用原生方法更新数据
	textControl:getView():setText(btnText)


	local toast = luajava.bindClass('android.widget.Toast')

	--弹出一个toast
	toast:makeText(mRapidView:getView():getContext(), toastText, 0):show()
end
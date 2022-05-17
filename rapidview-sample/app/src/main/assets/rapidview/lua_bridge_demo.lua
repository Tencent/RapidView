local mRapidView,mJavaInterface = ...

--[[rapidview在开源时裁剪了很多的Action/Filter能力以及javaInterface库方法，只保留了少量与rapidview本身关系密切，或占用安装包极小的方法，希望在其它项目接入时能够更多的
    复用项目已有的库方法，发展一套和项目最为匹配的调用库。--]]
function main()
	local params = {}

	params["size_type"] = "compressed"

	--[[ takePicture接口在光子库中是一个删减版的方法，仅为demo保留，去掉了很多逻辑，不建议直接拿来使用。 --]]
	mJavaInterface:takePicture(params, succeedListener, failedListener)
end

function succeedListener(bytesBmp)
	local image = mRapidView:getParser():getChildView("lua_bridge_image")

	local bmp = mJavaInterface:getBitmapFromBytes(bytesBmp)
	
	if( bmp == nil or image == nil ) then
		return
	end

	image:getView():setImageBitmap(bmp)
end

function failedListener()
end

function second()
	local textCtrl = mRapidView:getParser():getChildView("lua_bridge_image")

	--创建一个listener对象
	local listener = luajava.createProxy('android.view.View$OnClickListener', ClickListener)
	
	if( textCtrl == nil ) then
		return
	end

	--设置Listener
	textCtrl:getView():setOnClickListener(listener)

	local toast = luajava.bindClass('android.widget.Toast')
	toast:makeText(mRapidView:getView():getContext(), "图片可以点击了", 0):show()
end

--声明一个OnClickListener
ClickListener = {}
function ClickListener.onClick(v)
	local toast = luajava.bindClass('android.widget.Toast')
	toast:makeText(mRapidView:getView():getContext(), "点击了图片", 0):show()
end
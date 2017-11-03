local mRapidView,mJavaBridge = ...

local mRecyclerView

local mRequsting = false
local mReqTimes = 0

function onPageSelect(first)

	if( first ) then
		load()
	end
end

function init()
	mRecyclerView = mRapidView:getParser():getChildView("recycler_view")
	
	if( mRecyclerView == nil ) then
		return
	end

	mRecyclerView:getView():setFooter("tab_page_bottom_loading_view.xml", nil)
	mRecyclerView:getView():hideFooter()

	local bottomListener = luajava.createProxy('com.tencent.rapidview.deobfuscated.control.IRapidRecyclerView$IScrollBottomListener', RecyclerBottomListener)

	mRecyclerView:getView():setScrollBottomListener(bottomListener)
end

function initNextPage( hasNext )
	if( mRecyclerView == nil ) then
		return
	end

	if( hasNext == "1" ) then
		mRecyclerView:getView():showFooter()
	else
		mRecyclerView:getView():hideFooter()
	end
end

function load()
	local params = {}
	params["demo_req"] = 1

	if( mReqTimes > 5 ) then
		return
	end

	mReqTimes = mReqTimes + 1

	mRequsting = true
	local id = tonumber(mRapidView:getParser():getBinder():get("tab_id"))


	mJavaBridge:request(id, params, onRequestListener)
end

function onRequestListener(succeed, viewNameList, dataList)
	if( succeed ) then
		onRequestSucceed(viewNameList, dataList)
	else
		onRequestFailed()
	end

	mRequsting = false
end

function onRequestSucceed(viewNameList, dataList)

	if( viewNameList == nil or dataList == nil ) then
		return
	end

	mRecyclerView:getView():updateData(dataList, viewNameList, false)
	if( mReqTimes > 5 ) then
		mRapidView:getParser():getBinder():update("has_next", "0")
	else
		mRapidView:getParser():getBinder():update("has_next", "1")
	end

	mRapidView:getParser():getBinder():update("loading_show", "gone")
	mRapidView:getParser():getBinder():update("error_page_show", "gone")
end

function onRequestFailed()
	mRapidView:getParser():getBinder():update("loading_show", "gone")
	mRapidView:getParser():getBinder():update("error_page_show", "visible")
end

RecyclerBottomListener = {}
function RecyclerBottomListener.onScrollToBottom()
	load()
end

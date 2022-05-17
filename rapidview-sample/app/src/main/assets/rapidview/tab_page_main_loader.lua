local mRapidView,mJavaBridge = ...

local mTabDataMap
local mViewPager
local mTabView = {}
local mItemWidth = 0
local mCurrentItem = 0
local mTabItem = {}
local mLastExposureTab = 0

function load()
	local params = {}
	params["test_req_data"] = "1"

	mJavaBridge:request(1, params, onRequestListener)
end

function onRequestListener(succeed, viewNameList, dataList)
	if( succeed ) then
		onRequestSucceed(viewNameList, dataList)
	else
		onRequestFailed()
	end
end

function onRequestSucceed(viewNameList, dataList)
	
	if( viewNameList == nil or dataList == nil ) then
		onRequestFailed()
		return
	end

	count = viewNameList:size() - 1

	for i=0,count do
		if( viewNameList:get(i) == "tab_data" ) then
			mTabDataMap = dataList:get(i)
			
			viewNameList:remove(i)
			dataList:remove(i)
			break
		end
	end

	if( mTabDataMap == nil ) then
		onRequestFailed()
		return
	end

	mViewPager = mRapidView:getParser():getChildView("view_pager")

	initTab()
	initViewPager()
	initMainTab(viewNameList, dataList)

	mRapidView:getParser():getBinder():update("loading_show", "gone")
	mRapidView:getParser():getBinder():update("error_page_show", "gone")
end

function initTab()
	local slider = mRapidView:getParser():getChildView("slide_block")
	local screenWidth = mRapidView:getParser():getScreenWidth()
	local tabCount = mTabDataMap:get("tab_count")
	local actionListener = luajava.createProxy('com.tencent.rapidview.deobfuscated.IRapidActionListener', RapidActionListener)

	if( tabCount == nil ) then
		return
	end
	if( tabCount:getInt() >= 5 ) then
		mItemWidth = mJavaBridge:px2dip(screenWidth / 5)
	else
		mItemWidth = mJavaBridge:px2dip(screenWidth / tabCount:getInt())
	end

	slider:getParser():update("width", mItemWidth)

	for i=1,tabCount:getInt() do
		local params = {}

		params["tab_id"] = mTabDataMap:get("tab_id_" .. i):getInt()
		params["tab_index"] = mTabDataMap:get("tab_index_" .. i):getString()
		params["tab_name"] = mTabDataMap:get("tab_name_" .. i):getString()
		params["item_width"] = mItemWidth
		
		mTabItem[i] = mJavaBridge:addView("tab_page_tab_item.xml", "tab_container", "", nil, params, actionListener)
	end
end


function initViewPager()
	local tabCount = mTabDataMap:get("tab_count")
	
	if( tabCount == nil ) then
		return
	end

	local viewPagerListener = luajava.createProxy('com.tencent.rapidview.deobfuscated.control.IViewPagerListener', ViewPagerListener)
	mViewPager:getView():setViewPagerListener(viewPagerListener)

	for i=1,tabCount:getInt() do
		local params = {}
		params["tab_id"] = mTabDataMap:get("tab_id_" .. i):getString()
		params["tab_index"] = mTabDataMap:get("tab_index_" .. i):getString()
		mTabView[i] = mJavaBridge:loadView('tab_page_common_scroll_container.xml', 'viewpagerparams', params)
	end

	mViewPager:getView():getAdapter():refresh(mTabView)
end

function initMainTab(listViewName, listData)

	if( listViewName == nil or listData == nil ) then
		return
	end

	mTabView[1]:getParser():getChildView("recycler_view"):getView():updateData(listData, listViewName, true)
	mTabView[1]:getParser():getBinder():update("has_next", "1")
	mTabView[1]:getParser():getBinder():update("loading_show", "gone")
	mTabView[1]:getParser():getBinder():update("error_page_show", "gone")
end

function onRequestFailed()
	mRapidView:getParser():getBinder():update("loading_show", "gone")
	mRapidView:getParser():getBinder():update("error_page_show", "visible")
end

RapidActionListener = {}
function RapidActionListener.notify(key, value)
	if( key == "click" and mTabView[tonumber(value)] ~= nil ) then
		mViewPager:getView():setCurrentItem(tonumber(value - 1))
	end
end

ViewPagerListener = {}
function ViewPagerListener.onPause(pos, tag)
end

function ViewPagerListener.onResume(pos, tag)
end

function ViewPagerListener.onPageSelected(pos, tag, first)

	onTabChange(pos)

	if( mTabView[pos + 1] ~= nil ) then
		if( first and pos ~= 0 ) then
			mTabView[pos + 1]:getParser():getBinder():update("onPageSelected", "true")
		else
			mTabView[pos + 1]:getParser():getBinder():update("onPageSelected", "false")
		end
	end
end

function onTabChange(index)
	local slidingView = mRapidView:getParser():getChildView("slide_block")
	local tabScrollContainer = mRapidView:getParser():getChildView("tab_scroll_container")
	local scrollWidth = tabScrollContainer:getView():getScrollX()
	local itemWidth = mJavaBridge:dip2px(mItemWidth)
	local xNewPos = itemWidth * index - scrollWidth
	local newScrollWidth = scrollWidth

	local screenWidth = mRapidView:getParser():getScreenWidth()

	if( xNewPos < 0 ) then
		xNewPos = 0
	end

	if( xNewPos + itemWidth > screenWidth) then
		xNewPos = screenWidth - itemWidth
	end

	newScrollWidth = itemWidth * index - xNewPos

	if( slidingView ~= nil ) then
		local ObjectAnimator = luajava.bindClass('android.animation.ObjectAnimator')
		local params = {}
		
		params[1] = mCurrentItem * itemWidth
		params[2] = index * itemWidth
		
		ObjectAnimator:ofFloat(slidingView:getView(), "X", params):setDuration(200):start()
	end 

	if(mTabItem[index + 1] ~= nil) then
		mTabItem[index + 1]:getParser():getBinder():update("text_color", "ff1d82ff")
	end

	if(mTabItem[mCurrentItem + 1] ~= nil) then
		mTabItem[mCurrentItem + 1]:getParser():getBinder():update("text_color", "ff000000")
	end

	tabScrollContainer:getView():scrollTo(newScrollWidth, 0)

	mCurrentItem = index
end

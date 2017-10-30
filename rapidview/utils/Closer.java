/**  

 * Copyright © 2014Tencent. All rights reserved.

 *

 * @Title: Closer.java

 * @Prject: MobileAssistant_trunk

 * @Package: com.tencent.assistant.utils

 * @Description: 简化流关闭操作，不用每处地方都写个try...catch

 * @author: jieshao  

 * @date: 2014-6-20 下午6:01:17

 * @version: V1.0  

 */
package com.tencent.rapidview.utils;

import java.io.Closeable;

/**
 * @author jieshao
 *
 */
public class Closer {

	public static void close(Closeable stream ){
		if (stream == null) {
			return ;
		}
		try {
			stream.close();
		} catch (Exception e) {
		}
		
	}

}

package com.att.sales.nexxus.util;

import java.util.Map;
import org.slf4j.MDC;
import com.att.sales.framework.model.ServiceMetaData;

public class ThreadMetaDataUtil {
	
	private ThreadMetaDataUtil() {
		// empty constructor
	}
	
	public static void initThreadMetaData(Map<String, Object> requestMetaDataMap) {
		if (!requestMetaDataMap.containsKey(ServiceMetaData.XTraceId)) {
			requestMetaDataMap.put(ServiceMetaData.XTraceId, requestMetaDataMap.get(ServiceMetaData.CURRENT_TRACE) == null ? null : String.valueOf(requestMetaDataMap.get(ServiceMetaData.CURRENT_TRACE)));
		}
		if (!requestMetaDataMap.containsKey(ServiceMetaData.XSpanId)) {
			requestMetaDataMap.put(ServiceMetaData.XSpanId, requestMetaDataMap.get(ServiceMetaData.CURRENT_SPAN) == null ? null : String.valueOf(requestMetaDataMap.get(ServiceMetaData.CURRENT_SPAN)));
		}
		ServiceMetaData.add(requestMetaDataMap);
		MDC.put(ServiceMetaData.THREAD_ID, String.valueOf(Thread.currentThread().getId()));
		MDC.put(ServiceMetaData.XCONVERSATIONID, (String) ServiceMetaData.getRequestMetaData().get(ServiceMetaData.XCONVERSATIONID));
	}
	
	public static void initThreadMetaDataIfNull(Map<String, Object> requestMetaDataMap) {
		if (ServiceMetaData.getRequestMetaData() == null) {
			ServiceMetaData.add(requestMetaDataMap);
		}
		MDC.put(ServiceMetaData.THREAD_ID, String.valueOf(Thread.currentThread().getId()));
		MDC.put(ServiceMetaData.XCONVERSATIONID, (String) ServiceMetaData.getRequestMetaData().get(ServiceMetaData.XCONVERSATIONID));
	}
	
	public static void destroyThreadMetaData() {
		ServiceMetaData.getThreadLocal().remove();
		MDC.remove(ServiceMetaData.THREAD_ID);
		MDC.remove(ServiceMetaData.XCONVERSATIONID);
	}
}

package com.ca.umg.modelet.ioconverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ca.umg.modelet.common.FieldInfo;
import com.ca.umg.modelet.common.ModelRequestInfo;

@SuppressWarnings("PMD")
public class ME2RequestExpander {

	public static void expand(final ModelRequestInfo requestInfo) {
		final List<FieldInfo> mainList = requestInfo.getPayload();
		final List<FieldInfo> newMainList = new ArrayList<FieldInfo>();
		
		for (final FieldInfo mainListElement : mainList) {
			if (mainListElement.getModelParameterName() == null) {
				mainListElement.getP();
			} else {
				final FieldInfo newMainListElement = new FieldInfo();
				newMainListElement.setP(mainListElement.getP());
				newMainListElement.setValue(createFieldInfo(mainListElement.getValue()));
				newMainListElement.setP(null);
				newMainList.add(newMainListElement);
			}
		}
		
		requestInfo.setPayload(newMainList);
	}
	
	private static Object createFieldInfo(final Object p) {
		Object newValue = null;
		if (p instanceof List) {
			final List<Object> pList = (List<Object>) p;
			final List<Object> fieldInfoList = new ArrayList<Object>();
			for (final Object pListElement : pList) {
				fieldInfoList.add(createFieldInfo(pListElement));
			}
			newValue = fieldInfoList;
		} else if (p instanceof Map) {
			final Map<String, Object> pMap = (Map<String, Object>) p;
			final Object actualP = pMap.get("p");
			final FieldInfo newFieldInfo = new FieldInfo();
			newFieldInfo.setP(actualP);
			newFieldInfo.setValue(createFieldInfo(newFieldInfo.getValue()));
			newFieldInfo.setP(null);
			newValue = newFieldInfo;
		} else {
//			final FieldInfo newFieldInfo = new FieldInfo();
//			newFieldInfo.setP(p);
//			newValue = newFieldInfo;
			return p;
		}
		return newValue;

	}
}

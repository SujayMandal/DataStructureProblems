package com.fa.dp.core.systemparam.util;

import java.util.stream.Stream;

import com.fa.dp.core.exception.SystemException;
import com.fa.dp.core.exception.codes.CoreExceptionCodes;

public enum ClassificationType {
	OCN("OcnWeekZeroAdapter"), NRZ("NrzWeekZeroAdapter"), OCNWN("OcnWeekNAdapter"), NRZWN("NrzWeekNAdapter");

	ClassificationType(String classificationType) {
		this.classificationType = classificationType;
	}

	private String classificationType;

	public String getClassificationType() {
		return classificationType;
	}

	public static Stream<ClassificationType> stream() {
		return Stream.of(ClassificationType.values());
	}

	public static String getBeanType(String type) throws SystemException {
		ClassificationType classType = ClassificationType.stream().filter(d -> d.name().equals(type)).findAny()
				.orElseThrow(() -> new SystemException(CoreExceptionCodes.SYSPAR003, new Object[] { type }));
		return classType.getClassificationType();
	}
	
	public static Boolean isWeekNType(String type) throws SystemException {
		return (ClassificationType.OCNWN.name().equals(type)||ClassificationType.NRZWN.name().equals(type));
	}
}

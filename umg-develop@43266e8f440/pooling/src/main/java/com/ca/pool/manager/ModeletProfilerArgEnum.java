package com.ca.pool.manager;

import com.ca.framework.core.constants.PoolConstants;
import com.ca.pool.constant.PoolingConstant;
import com.ca.pool.modelet.profiler.info.ModeletProfileParamsInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ModeletProfilerArgEnum {
	EXPORT_TYPE {
		@Override
		String operation(ModeletProfileParamsInfo profileParam) {
			String paramData = null;
			if(StringUtils.isNotEmpty(profileParam.getParamValue())) {
				paramData = StringUtils.join(PoolingConstant.EXPORT, PoolingConstant.SPACE, profileParam.getCode(), profileParam.getDelimitter(),
						profileParam.getParamValue(), PoolingConstant.SEMI_COLON);
			}
			return paramData;
		}
	},

	X_ARG_TYPE {
		@Override
		String operation(ModeletProfileParamsInfo profileParam) {
			String paramData = null;
			if(StringUtils.isEmpty(profileParam.getDelimitter()) && StringUtils
					.equalsIgnoreCase(Boolean.TRUE.toString(), profileParam.getParamValue())) {
				paramData = StringUtils.join(PoolingConstant.X_ARG_START, profileParam.getCode());
			} else if(StringUtils.isEmpty(profileParam.getDelimitter()) && StringUtils
					.equalsIgnoreCase(Boolean.FALSE.toString(), profileParam.getParamValue())) {
				paramData = StringUtils.EMPTY;
			} else {
				paramData = StringUtils
						.join(PoolingConstant.X_ARG_START, profileParam.getCode(), profileParam.getDelimitter(), profileParam.getParamValue());
			}
			return paramData;
		}
	},

	D_ARG_TYPE {
		@Override
		String operation(ModeletProfileParamsInfo profileParam) {
			String paramData = null;
			if(StringUtils.isNotEmpty(profileParam.getParamValue())) {
				paramData = StringUtils
						.join(PoolingConstant.D_ARG_START, profileParam.getCode(), profileParam.getDelimitter(), profileParam.getParamValue());
			}
			return paramData;
		}
	};

	private static Map<String, ModeletProfilerArgEnum> process = new ConcurrentHashMap<String, ModeletProfilerArgEnum>() {
		{
			this.put(PoolConstants.PROFILER_TYPE_EXPORT, EXPORT_TYPE);
			this.put(PoolConstants.PROFILER_TYPE_X_ARG, X_ARG_TYPE);
			this.put(PoolConstants.PROFILER_TYPE_D_ARG, D_ARG_TYPE);
		}
	};

	static String getArgumentValue(ModeletProfileParamsInfo profileParam) {
		return process.get(profileParam.getType()).operation(profileParam);
	}

	abstract String operation(ModeletProfileParamsInfo profileParam);
}


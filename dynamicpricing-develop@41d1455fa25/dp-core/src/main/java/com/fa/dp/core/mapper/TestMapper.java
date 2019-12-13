/**
 * 
 */
package com.fa.dp.core.mapper;

import com.fa.dp.business.test.domain.TestEntity;
import com.fa.dp.business.test.info.TestInfo;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface TestMapper {

	TestInfo mapEntityToInfo(TestEntity testEntity);

	TestEntity mapInfoToEntity(TestInfo testInfo);

}

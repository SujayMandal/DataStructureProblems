/**
 * 
 */
package com.fa.dp.business.test.delegate;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.fa.dp.business.test.info.TestInfo;
import com.fa.dp.core.mapper.TestMapper;
import com.fa.dp.business.test.bo.TestBo;
import com.fa.dp.business.test.domain.TestEntity;
import com.fa.dp.core.exception.SystemException;

import org.mapstruct.factory.Mappers;

import lombok.extern.slf4j.Slf4j;


@Named
@Slf4j
public class TestDelegateImpl implements TestDelegate {

	@Inject
	private TestBo testBo;

	@Inject
	private TestMapper testMapper;

	/* (non-Javadoc)
	 * @see TestDelegate#save(TestInfo)
	 */
	@Override
	public List<TestInfo> getAll() throws SystemException {
		//log.info("in delegate");
		List<TestInfo> testInfos = new ArrayList<>();
		List<TestEntity> testEntities = testBo.getAll();
		for(TestEntity testEntity : testEntities){
			testInfos.add(testMapper.mapEntityToInfo(testEntity));
			//testInfos.add(Mappers.getMapper(TestMapper.class).mapEntityToInfo(testEntity));
		}
		return testInfos;
	}

}

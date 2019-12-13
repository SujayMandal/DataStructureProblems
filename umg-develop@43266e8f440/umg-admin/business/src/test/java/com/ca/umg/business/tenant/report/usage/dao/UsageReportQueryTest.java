package com.ca.umg.business.tenant.report.usage.dao;

import static com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum.BATCH_ID;
import static com.ca.umg.business.tenant.report.usage.dao.UsageReportQuery.createLimitClause;
import static com.ca.umg.business.tenant.report.usage.dao.UsageReportQuery.createOrderByClause;
import static com.ca.umg.business.tenant.report.usage.dao.UsageReportQuery.createTransactionCountQuery;
import static com.ca.umg.business.tenant.report.usage.dao.UsageReportQuery.creategetAllUniqueModelVersionQuery;
import static com.ca.umg.business.tenant.report.usage.dao.UsageReportQuery.getLimitOffset;
import static com.ca.umg.business.tenant.report.usage.dao.UsageReportQuery.getLimitRowCount;
import static com.ca.umg.business.tenant.report.usage.dao.UsageReportQuery.getPageCount;
import static com.ca.umg.business.tenant.report.usage.dao.UsageReportQuery.getTableAlias;
import static com.ca.umg.business.tenant.report.usage.dao.UsageReportQuery.isLastPage;
import static com.ca.umg.business.tenant.report.usage.dao.UsageReportQuery.isPagingRequired;
import static com.ca.umg.business.tenant.report.usage.dao.UsageReportQuery.isValueNotBlankAndNotAnyOrNotAll;
import static java.lang.Integer.valueOf;
import static java.util.Locale.getDefault;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.ca.umg.business.tenant.report.usage.UsageReportColumnEnum;
import com.ca.umg.business.tenant.report.usage.UsageReportFilter;

@SuppressWarnings("PMD")
public class UsageReportQueryTest {

	private UsageReportFilter filter;
	private static final String TENANT_ID = "Localhost";
	private static final String TENANT_MODEL_NAME = "Model_Tenant";
	private static final Integer MAJOR_VERSION = valueOf(2);
	private static final Integer MINOR_VERSION = valueOf(1);
	private static final String ALL = "All";
	private static final String ANY = "Any";
	private static final String EMPTY = "";
	private static final String SUCESS = "Success";
	private static final String FAILURE = "Failure";

	@Before
	public void before() {
		filter = mock(UsageReportFilter.class);
	}

	@Test
	public void testIsValueNotBlank() {
		assertFalse(isValueNotBlankAndNotAnyOrNotAll(null));
		assertFalse(isValueNotBlankAndNotAnyOrNotAll(EMPTY));
		assertTrue(isValueNotBlankAndNotAnyOrNotAll("not empty"));
	}

	@Test
	public void testIsValueNotAny() {
		assertFalse(isValueNotBlankAndNotAnyOrNotAll(ANY));
		assertFalse(isValueNotBlankAndNotAnyOrNotAll(ANY.toLowerCase()));
		assertFalse(isValueNotBlankAndNotAnyOrNotAll(ANY.toUpperCase()));
		assertTrue(isValueNotBlankAndNotAnyOrNotAll("not empty"));
	}

	@Test
	public void testIsValueNotAll() {
		assertFalse(isValueNotBlankAndNotAnyOrNotAll(ALL));
		assertFalse(isValueNotBlankAndNotAnyOrNotAll(ALL.toLowerCase()));
		assertFalse(isValueNotBlankAndNotAnyOrNotAll(ALL.toUpperCase()));
		assertTrue(isValueNotBlankAndNotAnyOrNotAll("not empty"));
	}

	@Test
	public void testCreateWhereClauseForTeanntIdOnly() {
		when(filter.getTenantCode()).thenReturn(TENANT_ID);
		when(filter.getRunAsOfDateFrom()).thenReturn(null);
		when(filter.getRunAsOfDateTo()).thenReturn(null);
		when(filter.getTenantModelName()).thenReturn(null);
		when(filter.getMajorVersion()).thenReturn(null);
		when(filter.getMinorVersion()).thenReturn(null);
		when(filter.getTransactionStatus()).thenReturn(null);

		final String whereCluase = createTransactionCountQuery(filter);
		assertTrue(whereCluase != null);
		assertTrue(whereCluase.contains(" where lower(urt.TENANT_ID) = '" + TENANT_ID.toLowerCase(getDefault()) + "'"));
	}

	@Test
	public void testCreateWhereClauseModelNameOnly() {
		when(filter.getTenantModelName()).thenReturn(TENANT_MODEL_NAME);
		when(filter.getRunAsOfDateFrom()).thenReturn(null);
		when(filter.getRunAsOfDateTo()).thenReturn(null);
		when(filter.getTenantCode()).thenReturn(null);
		when(filter.getMajorVersion()).thenReturn(null);
		when(filter.getMinorVersion()).thenReturn(null);
		when(filter.getTransactionStatus()).thenReturn(null);

		final String whereCluase = createTransactionCountQuery(filter);
		assertTrue(whereCluase != null);
		assertTrue(whereCluase.contains(" where lower(urt.VERSION_NAME) = '" + TENANT_MODEL_NAME.toLowerCase(getDefault()) + "'"));
	}

	@Test
	public void testCreateWhereClauseModelNameAll() {
		when(filter.getTenantModelName()).thenReturn(ALL);
		when(filter.getRunAsOfDateFrom()).thenReturn(null);
		when(filter.getRunAsOfDateTo()).thenReturn(null);
		when(filter.getTenantCode()).thenReturn(null);
		when(filter.getMajorVersion()).thenReturn(null);
		when(filter.getMinorVersion()).thenReturn(null);
		when(filter.getTransactionStatus()).thenReturn(null);

		final String whereCluase = createTransactionCountQuery(filter);
		assertTrue(whereCluase != null);
		assertFalse(whereCluase.contains("lower(urt.VERSION_NAME)"));
	}

	@Test
	public void testCreateWhereClauseMajorVersionOnly() {
		when(filter.getMajorVersion()).thenReturn(MAJOR_VERSION);
		when(filter.getTenantModelName()).thenReturn(null);
		when(filter.getRunAsOfDateFrom()).thenReturn(null);
		when(filter.getRunAsOfDateTo()).thenReturn(null);
		when(filter.getTenantCode()).thenReturn(null);
		when(filter.getMinorVersion()).thenReturn(null);
		when(filter.getTransactionStatus()).thenReturn(null);

		final String whereCluase = createTransactionCountQuery(filter);
		assertTrue(whereCluase != null);
		assertTrue(whereCluase.contains(" where urt.MAJOR_VERSION = " + MAJOR_VERSION));
	}

	@Test
	public void testCreateWhereClauseMinorVersionOnly() {
		when(filter.getMinorVersion()).thenReturn(MINOR_VERSION);
		when(filter.getMajorVersion()).thenReturn(null);
		when(filter.getTenantModelName()).thenReturn(null);
		when(filter.getRunAsOfDateFrom()).thenReturn(null);
		when(filter.getRunAsOfDateTo()).thenReturn(null);
		when(filter.getTenantCode()).thenReturn(null);
		when(filter.getTransactionStatus()).thenReturn(null);

		final String whereCluase = createTransactionCountQuery(filter);
		assertTrue(whereCluase != null);
		assertTrue(whereCluase.contains(" where urt.MINOR_VERSION = " + MINOR_VERSION));
	}

	@Test
	public void testCreateWhereClauseSucessStatusOnly() {
		when(filter.getTransactionStatus()).thenReturn(SUCESS);
		when(filter.getMinorVersion()).thenReturn(null);
		when(filter.getMajorVersion()).thenReturn(null);
		when(filter.getTenantModelName()).thenReturn(null);
		when(filter.getRunAsOfDateFrom()).thenReturn(null);
		when(filter.getRunAsOfDateTo()).thenReturn(null);
		when(filter.getTenantCode()).thenReturn(null);

		final String whereCluase = createTransactionCountQuery(filter);
		assertTrue(whereCluase != null);
		assertTrue(whereCluase.contains(" where lower(urt.STATUS) = '" + SUCESS.toLowerCase() + "'"));
	}

	@Test
	public void testCreateWhereClauseFailureStatusOnly() {
		when(filter.getTransactionStatus()).thenReturn(FAILURE);
		when(filter.getMinorVersion()).thenReturn(null);
		when(filter.getMajorVersion()).thenReturn(null);
		when(filter.getTenantModelName()).thenReturn(null);
		when(filter.getRunAsOfDateFrom()).thenReturn(null);
		when(filter.getRunAsOfDateTo()).thenReturn(null);
		when(filter.getTenantCode()).thenReturn(null);

		final String whereCluase = createTransactionCountQuery(filter);
		assertTrue(whereCluase != null);
		assertTrue(whereCluase.contains(" where lower(urt.STATUS) != '" + SUCESS.toLowerCase() + "'"));
	}

	@Test
	public void testCreateWhereClauseAllStatus() {
		when(filter.getTransactionStatus()).thenReturn(ALL);
		when(filter.getMinorVersion()).thenReturn(null);
		when(filter.getMajorVersion()).thenReturn(null);
		when(filter.getTenantModelName()).thenReturn(null);
		when(filter.getRunAsOfDateFrom()).thenReturn(null);
		when(filter.getRunAsOfDateTo()).thenReturn(null);
		when(filter.getTenantCode()).thenReturn(null);

		final String whereCluase = createTransactionCountQuery(filter);
		assertTrue(whereCluase != null);
		assertFalse(whereCluase.contains("lower(urt.STATUS)"));
	}

	@Test
	public void testCreateWhereClauseAnyStatus() {
		when(filter.getTransactionStatus()).thenReturn(ANY);
		when(filter.getMinorVersion()).thenReturn(null);
		when(filter.getMajorVersion()).thenReturn(null);
		when(filter.getTenantModelName()).thenReturn(null);
		when(filter.getRunAsOfDateFrom()).thenReturn(null);
		when(filter.getRunAsOfDateTo()).thenReturn(null);
		when(filter.getTenantCode()).thenReturn(null);

		final String whereCluase = createTransactionCountQuery(filter);
		assertTrue(whereCluase != null);
		assertFalse(whereCluase.contains("lower(urt.STATUS)"));
	}

	@Test
	public void testCreateWhereClauseRunAsOfDate() {
		final long startDate = System.currentTimeMillis();
		when(filter.getRunAsOfDateFrom()).thenReturn(startDate);
		final long endDate = System.currentTimeMillis();
		when(filter.getRunAsOfDateTo()).thenReturn(endDate);

		when(filter.getTransactionStatus()).thenReturn(null);
		when(filter.getMinorVersion()).thenReturn(null);
		when(filter.getMajorVersion()).thenReturn(null);
		when(filter.getTenantModelName()).thenReturn(null);
		when(filter.getTenantCode()).thenReturn(null);

		final String whereCluase = createTransactionCountQuery(filter);
		assertTrue(whereCluase != null);
		assertTrue(whereCluase.contains(" where urt.CREATED_ON between " + startDate + " and " + endDate));
	}

	@Test
	public void testCreateWhereClauseForTwoConditions() {
		final long startDate = System.currentTimeMillis();
		when(filter.getRunAsOfDateFrom()).thenReturn(startDate);
		final long endDate = System.currentTimeMillis();
		when(filter.getRunAsOfDateTo()).thenReturn(endDate);
		when(filter.getTenantCode()).thenReturn(TENANT_ID);

		when(filter.getTransactionStatus()).thenReturn(null);
		when(filter.getMinorVersion()).thenReturn(null);
		when(filter.getMajorVersion()).thenReturn(null);
		when(filter.getTenantModelName()).thenReturn(null);

		final String whereCluase = createTransactionCountQuery(filter);
		assertTrue(whereCluase != null);

		assertTrue(whereCluase.contains(" where urt.CREATED_ON between " + startDate + " and " + endDate));
		assertTrue(whereCluase.contains(" and lower(urt.TENANT_ID) = '" + TENANT_ID.toLowerCase(getDefault()) + "'"));
	}

	@Test
	public void testCreateWhereClauseForAllConditions() {
		final long startDate = System.currentTimeMillis();
		when(filter.getRunAsOfDateFrom()).thenReturn(startDate);
		final long endDate = System.currentTimeMillis();
		when(filter.getRunAsOfDateTo()).thenReturn(endDate);

		when(filter.getTenantModelName()).thenReturn(TENANT_MODEL_NAME);
		when(filter.getTenantCode()).thenReturn(TENANT_ID);
		when(filter.getMajorVersion()).thenReturn(MAJOR_VERSION);
		when(filter.getMinorVersion()).thenReturn(MINOR_VERSION);
		when(filter.getTransactionStatus()).thenReturn(SUCESS);

		final String whereCluase = createTransactionCountQuery(filter);
		assertTrue(whereCluase != null);
		assertTrue(whereCluase.contains(" where urt.CREATED_ON between " + startDate + " and " + endDate //
				+ " and lower(urt.VERSION_NAME) = '" + TENANT_MODEL_NAME.toLowerCase(getDefault()) + "'" //
				+ " and lower(urt.TENANT_ID) = '" + TENANT_ID.toLowerCase(getDefault()) + "'" //
				+ " and urt.MAJOR_VERSION = " + MAJOR_VERSION //
				+ " and urt.MINOR_VERSION = " + MINOR_VERSION //
				+ " and lower(urt.STATUS) = '" + SUCESS.toLowerCase() + "'")); //
	}

	@Test
	public void testCreateTransactionCountQuery() {
		final long startDate = System.currentTimeMillis();
		when(filter.getRunAsOfDateFrom()).thenReturn(startDate);
		final long endDate = System.currentTimeMillis();
		when(filter.getRunAsOfDateTo()).thenReturn(endDate);
		when(filter.getTenantCode()).thenReturn("localhost");

		when(filter.getTransactionStatus()).thenReturn(null);
		when(filter.getMinorVersion()).thenReturn(null);
		when(filter.getMajorVersion()).thenReturn(null);
		when(filter.getTenantModelName()).thenReturn(null);

		final String countQuery = createTransactionCountQuery(filter);
		assertTrue(countQuery != null);
		assertTrue(countQuery.equals("select count(*) as totalcount from UMG_RUNTIME_TRANSACTION urt where urt.CREATED_ON between " + startDate + " and "
				+ endDate + " and lower(urt.TENANT_ID) = '" + TENANT_ID.toLowerCase(getDefault()) + "'"));
	}

	@Test
	public void testCreateOrderByClauseDefault() {
		when(filter.getSortColumn()).thenReturn(null);
		final String orderByClause = createOrderByClause(filter);
		assertTrue(orderByClause != null);
		assertTrue(orderByClause.equals(" order by urt.created_on desc"));
	}

	@Test
	public void testCreateOrderByClauseNonDefault() {
		when(filter.isDescending()).thenReturn(true);

		final UsageReportColumnEnum[] values = UsageReportColumnEnum.values();
		for (UsageReportColumnEnum value : values) {
			if (value.isReportField()) {
				when(filter.getSortColumn()).thenReturn(value.getExcelHeaderName());
				String orderByClause = createOrderByClause(filter);
				System.out.println(orderByClause);
				assertTrue(orderByClause != null);
				switch (value) {
					case MODEL_VERSION:
						assertTrue(orderByClause.equals(" order by " + getTableAlias(value) + "." + UsageReportColumnEnum.MAJOR_VERSION.getDbColumnName()
								+ ", " + getTableAlias(value) + "." + UsageReportColumnEnum.MINOR_VERSION.getDbColumnName() + " desc"));
						break;
					case REASON:
					case PROCESSING_TIME:
					case TRANSACTION_MODE:
					case TRANSACTION_TYPE:
						assertTrue(orderByClause.equals(" order by " + value.getDbColumnName() + " desc"));
						break;
					default :
						assertTrue(orderByClause.equals(" order by " + getTableAlias(value) + "." + value.getDbColumnName() + " desc"));
						break;
				}
			}
		}
	}

	@Test
	public void testGetTableAlias() {
		final UsageReportColumnEnum[] values = UsageReportColumnEnum.values();
		for (UsageReportColumnEnum value : values) {
			if (value.isDbField()) {
				String alias = getTableAlias(value);
				assertTrue(alias != null);
				if (value == BATCH_ID) {
					assertTrue("mapping".equals(alias));
				} else {
					assertTrue("urt".equals(alias));
				}
			}
		}
	}

	@Test
	public void testCreateGetAllUniqueModelNamesQuery() {
		final String query = UsageReportQuery.createGetAllUniqueModelNamesQuery(TENANT_ID);
		assertTrue(query != null);
		assertTrue(query.equals("select distinct NAME from UMG_VERSION where lower(TENANT_ID) = '" + TENANT_ID.toLowerCase() + "' order by NAME"));
	}

	@Test
	public void testCreategetAllUniqueModelVersionQuery() {
		final String query = creategetAllUniqueModelVersionQuery(TENANT_ID, TENANT_MODEL_NAME);
		assertTrue(query != null);
		assertTrue(query.equals("select distinct concat(MAJOR_VERSION, '.', MINOR_VERSION) as umg_version from UMG_VERSION where lower(TENANT_ID) = '"
				+ TENANT_ID.toLowerCase() + "' and lower(name) = '" + TENANT_MODEL_NAME.toLowerCase() + "' order by MAJOR_VERSION, MINOR_VERSION"));
	}

	@Test
	public void testIsPagingRequired() {
		when(filter.getPage()).thenReturn(1);
		when(filter.getPageSize()).thenReturn(100);
		when(filter.getMatchedTransactionCount()).thenReturn(10L);
		assertTrue(isPagingRequired(filter));

		when(filter.getPage()).thenReturn(0);
		assertFalse(isPagingRequired(filter));

		when(filter.getPage()).thenReturn(-1);
		assertFalse(isPagingRequired(filter));

		when(filter.getPage()).thenReturn(1);
		when(filter.getPageSize()).thenReturn(0);
		assertFalse(isPagingRequired(filter));

		when(filter.getPage()).thenReturn(1);
		when(filter.getPageSize()).thenReturn(-1);
		assertFalse(isPagingRequired(filter));

		when(filter.getPage()).thenReturn(1);
		when(filter.getPageSize()).thenReturn(1);
		when(filter.getMatchedTransactionCount()).thenReturn(-1L);
		assertFalse(isPagingRequired(filter));

		when(filter.getPage()).thenReturn(1);
		when(filter.getPageSize()).thenReturn(1);
		when(filter.getMatchedTransactionCount()).thenReturn(0L);
		assertFalse(isPagingRequired(filter));
	}

	@Test
	public void testIsLastPage() {
		when(filter.getPage()).thenReturn(1);
		when(filter.getPageSize()).thenReturn(10);
		when(filter.getMatchedTransactionCount()).thenReturn(100L);
		assertFalse(isLastPage(filter));

		when(filter.getPage()).thenReturn(2);
		when(filter.getPageSize()).thenReturn(10);
		when(filter.getMatchedTransactionCount()).thenReturn(100L);
		assertFalse(isLastPage(filter));

		when(filter.getPage()).thenReturn(9);
		when(filter.getPageSize()).thenReturn(10);
		when(filter.getMatchedTransactionCount()).thenReturn(100L);
		assertFalse(isLastPage(filter));

		when(filter.getPage()).thenReturn(10);
		when(filter.getPageSize()).thenReturn(10);
		when(filter.getMatchedTransactionCount()).thenReturn(100L);
		assertTrue(isLastPage(filter));

		when(filter.getPage()).thenReturn(10);
		when(filter.getPageSize()).thenReturn(10);
		when(filter.getMatchedTransactionCount()).thenReturn(98L);
		assertTrue(isLastPage(filter));
	}

	@Test
	public void testGetPageCount() {
		when(filter.getPage()).thenReturn(1);
		when(filter.getPageSize()).thenReturn(10);
		when(filter.getMatchedTransactionCount()).thenReturn(100L);
		assertTrue(getPageCount(filter) == 10);

		when(filter.getPage()).thenReturn(1);
		when(filter.getPageSize()).thenReturn(10);
		when(filter.getMatchedTransactionCount()).thenReturn(98L);
		assertTrue(getPageCount(filter) == 10);
	}

	@Test
	public void testGetLimitRowCount() {
		when(filter.getPage()).thenReturn(1);
		when(filter.getPageSize()).thenReturn(10);
		when(filter.getMatchedTransactionCount()).thenReturn(100L);
		assertTrue(getLimitRowCount(filter) == 10);

		when(filter.getPage()).thenReturn(10);
		when(filter.getPageSize()).thenReturn(10);
		when(filter.getMatchedTransactionCount()).thenReturn(100L);
		assertTrue(getLimitRowCount(filter) == 10);

		when(filter.getPage()).thenReturn(10);
		when(filter.getPageSize()).thenReturn(10);
		when(filter.getMatchedTransactionCount()).thenReturn(98L);
		assertTrue(getLimitRowCount(filter) == 8);
	}

	@Test
	public void testGetLimitOffset() {
		when(filter.getPage()).thenReturn(1);
		when(filter.getPageSize()).thenReturn(10);
		when(filter.getMatchedTransactionCount()).thenReturn(100L);
		assertTrue(getLimitOffset(filter) == 0);

		when(filter.getPage()).thenReturn(2);
		when(filter.getPageSize()).thenReturn(10);
		when(filter.getMatchedTransactionCount()).thenReturn(100L);
		assertTrue(getLimitOffset(filter) == 10);

		when(filter.getPage()).thenReturn(9);
		when(filter.getPageSize()).thenReturn(10);
		when(filter.getMatchedTransactionCount()).thenReturn(100L);
		assertTrue(getLimitOffset(filter) == 80);

		when(filter.getPage()).thenReturn(10);
		when(filter.getPageSize()).thenReturn(10);
		when(filter.getMatchedTransactionCount()).thenReturn(100L);
		assertTrue(getLimitOffset(filter) == 90);
	}

	@Test
	public void testCreateLimitClause() {
		when(filter.getPage()).thenReturn(-1);
		when(filter.getPageSize()).thenReturn(10);
		when(filter.getMatchedTransactionCount()).thenReturn(100L);
		assertTrue(createLimitClause(filter) == "");

		when(filter.getPage()).thenReturn(1);
		when(filter.getPageSize()).thenReturn(10);
		when(filter.getMatchedTransactionCount()).thenReturn(100L);
		assertTrue(createLimitClause(filter).equals(" limit 0, 10"));

		when(filter.getPage()).thenReturn(2);
		when(filter.getPageSize()).thenReturn(10);
		when(filter.getMatchedTransactionCount()).thenReturn(100L);
		assertTrue(createLimitClause(filter).equals(" limit 10, 10"));

		when(filter.getPage()).thenReturn(9);
		when(filter.getPageSize()).thenReturn(10);
		when(filter.getMatchedTransactionCount()).thenReturn(100L);
		assertTrue(createLimitClause(filter).equals(" limit 80, 10"));

		when(filter.getPage()).thenReturn(10);
		when(filter.getPageSize()).thenReturn(10);
		when(filter.getMatchedTransactionCount()).thenReturn(100L);
		assertTrue(createLimitClause(filter).equals(" limit 90, 10"));

		when(filter.getPage()).thenReturn(10);
		when(filter.getPageSize()).thenReturn(10);
		when(filter.getMatchedTransactionCount()).thenReturn(98L);
		assertTrue(createLimitClause(filter).equals(" limit 90, 8"));
	}
}
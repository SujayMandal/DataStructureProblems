/**
 * 
 */
package com.ca.umg.business.migration.bo;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.business.migration.audit.entity.MigrationAudit;
import com.ca.umg.business.migration.dao.MigrationAuditDAO;

/**
 * @author nigampra
 * 
 */
public class MigrationBOTest {

    @InjectMocks
    private MigrationBO classUnderTest = new MigrationBOImpl();

    @Mock
    private MigrationAuditDAO daoMock;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateMigrationAudit() throws BusinessException, SystemException {
        MigrationAudit migrationAudit = buildMigrationAudit("12345", "SUCCESS", "EXPORT");
        when(daoMock.saveAndFlush(any(MigrationAudit.class))).thenReturn(migrationAudit);
        migrationAudit = classUnderTest.createMigrationAudit(migrationAudit);
        assertEquals("SUCCESS", migrationAudit.getStatus());
        assertEquals("EXPORT", migrationAudit.getType());
        verify(daoMock, times(1)).saveAndFlush(migrationAudit);
    }

    @Test
    public void testFailExport() {
        MigrationAudit migrationAudit = buildMigrationAudit("6789", "SUCCESS", "EXPORT");
        when(daoMock.findOne("6789")).thenReturn(migrationAudit);
        classUnderTest.markExportAsFailed("6789");
        assertEquals("FAILED", migrationAudit.getStatus());
        verify(daoMock, times(1)).findOne("6789");
    }

    private MigrationAudit buildMigrationAudit(String id, String status, String type) {
        MigrationAudit migrationAudit = new MigrationAudit();
        migrationAudit.setId(id);
        migrationAudit.setStatus(status);
        migrationAudit.setType(type);
        return migrationAudit;
    }

}

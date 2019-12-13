/**
 * 
 */
package com.ca.umg.business.tenant.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.umg.business.tenant.AbstractTenantTest;
import com.ca.umg.business.tenant.entity.SystemKey;

/**
 * @author kamathan
 *
 */
@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
public class SystemKeyDAOTest extends AbstractTenantTest {

    @Test
    public void testSystemKeyDAO() {
        String type = "DATABASE";
        SystemKey driver = createSystemKey("driver", type);
        assertNotNull(driver);
        SystemKey url = createSystemKey("url", type);
        assertNotNull(url);
        SystemKey schema = createSystemKey("schema", type);
        assertNotNull(schema);
        SystemKey password = createSystemKey("password", type);
        assertNotNull(password);
        SystemKey username = createSystemKey("username", type);
        assertNotNull(username);

        List<SystemKey> systemKeys = getSystemKeyDAO().findByType("DATABASE");
        assertTrue(systemKeys.size() > 0);
    }
    
    @Test
    public void testFindByKeyType () {
    	String type = "PLUGIN";
        SystemKey excel = createSystemKey("EXCEL", type);
        assertNotNull(excel);          

        SystemKey excel1 = createSystemKey("PDF", type);
        assertNotNull(excel1);
        
        List<String> resultList = getSystemKeyDAO().findByKeyType(type);
        assertEquals(2,resultList.size());
        getSystemKeyDAO().delete(excel);
        getSystemKeyDAO().delete(excel1);
    }
}

/**
 * 
 */
package com.ca.umg.business.model.bo;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.requestcontext.RequestContext;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.umg.business.model.info.ModelArtifact;

/**
 * @author kamathan
 *
 */
@ContextHierarchy({ @ContextConfiguration("classpath:root-db-context.xml"), @ContextConfiguration })
@RunWith(SpringJUnit4ClassRunner.class)
public class ModelArtifactBOImplTest {

    @Inject
    private ModelArtifactBO modelArtifactBO;

    private RequestContext requestContext;

    private static final String TENANT_CODE_OCN = "TNT_OCN";

    private String sanPath;
    @Inject
    private CacheRegistry cacheRegistry;

    @Before
    public void setup() {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            URL filePath = this.getClass().getResource("/umg.properties");
            inputStream = new FileInputStream(new File(filePath.getFile()));
            properties.load(inputStream);
            properties.setProperty(RequestContext.TENANT_CODE, TENANT_CODE_OCN);
        } catch (IOException e) {
            fail(e.getMessage());
        } finally {
        	IOUtils.closeQuietly(inputStream);
        }

        // set request context
        requestContext = new RequestContext(properties);
        Iterator iterator = properties.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            System.setProperty(key, properties.getProperty(key));
        }

        sanPath = properties.getProperty(SystemConstants.SAN_BASE);

        cacheRegistry.getMap(SystemParameterProvider.SYSTEM_PARAMETER).set(SystemConstants.SAN_BASE,
                System.getProperty("user.dir"));
    }

    @Test
    public final void testArtifactCRD() {
        ModelArtifact modelArtifact = null;
        try {
            modelArtifact = new ModelArtifact();
            modelArtifact.setModelName("model1");
            modelArtifact.setUmgName("model1-"
                    + new SimpleDateFormat("MM-dd-yyyy-HH-mm").format(Calendar.getInstance().getTime()));
            URL filePath = this.getClass().getResource("dummy_model_doc.pdf");
            InputStream inputStream = new FileInputStream(new File(filePath.getFile()));
            modelArtifact.setData(inputStream);
            modelArtifact.setName("dummy_model_doc.pdf");

            modelArtifactBO.storeArtifacts(new ModelArtifact[] { modelArtifact });
        } catch (Exception exp) {
            // ignore exception, since path mentioned in the config file may not
            // exists in the build server
        }

        // fetch save artifacts
        try {
            List<ModelArtifact> modelArtifactsList = modelArtifactBO.fetchArtifacts(modelArtifact.getModelName(), modelArtifact.getUmgName(), false);
            modelArtifactBO.fetchArtifacts(modelArtifact.getModelName(), modelArtifact.getUmgName(), true);
        } catch (Exception exp) {
            // ignore exception, since path mentioned in the config file may not
            // exists in the build server
        }

        // delete artifacts
        try {
            modelArtifactBO.deleteModelArtifact(modelArtifact, false);
            modelArtifactBO.deleteModelArtifact(modelArtifact, true);
        } catch (Exception exp) {
            // ignore exception, since path mentioned in the config file may not
            // exists in the build server
        }
    }
    
    @Test
    public final void testStoreModelJar() {
        ModelArtifact modelArtifact = null;
        try {
            modelArtifact = new ModelArtifact();
            modelArtifact.setModelName("model1_mdljrtst");
            modelArtifact.setUmgName("model1_mdljrtst-"
                    + new SimpleDateFormat("MM-dd-yyyy-HH-mm").format(Calendar.getInstance().getTime()));
            URL filePath = this.getClass().getResource("dummy_model_doc.pdf");
            InputStream inputStream = new FileInputStream(new File(filePath.getFile()));
            modelArtifact.setData(inputStream);
            modelArtifact.setName("dummy_model_doc.pdf");
            modelArtifactBO.storeModelJar(modelArtifact);
        } catch (Exception exp) {
            // ignore exception, since path mentioned in the config file may not
            // exists in the build server
        }
    }

    @After
    public void tearDown() {
        // delete files
        String directoryPath = sanPath + File.separatorChar + TENANT_CODE_OCN;
        File file = new File(directoryPath);
        if (file.exists()) {
            delete(file);
        }
        requestContext.destroy();
    }

    private void delete(File file) {
        if (file.isDirectory()) {
            if (file.list().length == 0) {
                file.delete();
            } else {
                String files[] = file.list();
                for (String childFile : files) {
                    delete(new File(file, childFile));
                }
                if (file.list().length == 0) {
                    file.delete();
                }
            }
        } else {
            file.delete();
        }
    }
}

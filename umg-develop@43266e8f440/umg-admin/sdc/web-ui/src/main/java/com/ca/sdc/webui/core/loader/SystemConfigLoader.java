package com.ca.sdc.webui.core.loader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.ca.framework.core.cache.registry.CacheRegistry;
import com.hazelcast.core.IMap;

@Named
public class SystemConfigLoader extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemConfigLoader.class);

    /**
	 * 
	 */
    private static final long serialVersionUID = 2766029526953120075L;
    
    @Inject
	@Named(value = "cacheRegistry")
    private CacheRegistry cacheRegistry;

    public void init(ServletConfig servletConfig) throws ServletException {

        Properties properties = new Properties();
        FileInputStream fileInputStream = null;
        try {
            String umgProperties = servletConfig.getInitParameter("umg.properties");
            fileInputStream = new FileInputStream(umgProperties);
            properties.load(fileInputStream);
        } catch (IOException e) {
            LOGGER.error("Exception occured", e);
        }finally {
        	IOUtils.closeQuietly(fileInputStream);
		}
        servletConfig.getServletContext().setAttribute("sysEnv", properties);
        
        if (cacheRegistry == null) {
            ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletConfig.getServletContext());
            this.cacheRegistry = ctx.getBean(CacheRegistry.class);
        }
        
        final IMap<String, String> map = cacheRegistry.getMap(CacheRegistry.UMG_PROPERTIES_MAP);
        
        Iterator iterator = properties.keySet().iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            System.setProperty(key, properties.getProperty(key));
            map.put(key, properties.getProperty(key));
        }
    }
}

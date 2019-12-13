	package com.ca.umg.notification.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.SystemException;
import com.ca.umg.notification.NotificationExceptionCodes;
import com.ca.umg.notification.model.MailDetails;
import com.ca.umg.notification.model.NotificationEventData;

public class VelocityUtil {
	 
	private static final Logger LOGGER = LoggerFactory.getLogger(VelocityUtil.class);
	
	private static final String VM_EXT = ".vm";
	
	public static final String dirLocation = File.pathSeparator + "opt" + File.pathSeparator + "tmp";
	
	public static String getBody(final MailDetails mailDetails, final NotificationEventData eventData, final String dirLocation) throws SystemException {
		return getTemplate(mailDetails, eventData.getBodyMap(), mailDetails.getBodyDefinition(), dirLocation);
	}
	
	public static String getSubject(final MailDetails mailDetails, final NotificationEventData eventData, final String dirLocation) throws SystemException {
		return getTemplate(mailDetails, eventData.getSubjectMap(), mailDetails.getSubjectDefinition(), dirLocation);
	}
	
	private static String getTemplate(final MailDetails mailDetails, final Map<String, Object> map, final byte[] definition, final String dirLocation ) 
			throws SystemException {
		FileOutputStream fileOutputStream = null;
		String template = null;
		File tempFile = null;
		try {
			tempFile = createTempFile(dirLocation);				
			fileOutputStream = new FileOutputStream(tempFile);		
			final String s = new String(definition);
			LOGGER.info("Raw Template is : {} " + s);
			fileOutputStream.write(definition);
			fileOutputStream.flush();
			
			LOGGER.info("Temaplte definition is written to temp file successfully");
			template = getConvertedText(tempFile, map, dirLocation);
		} catch (IOException ioe) {
			LOGGER.error(ioe.getMessage());
			SystemException.newSystemException(NotificationExceptionCodes.TEMPLATE_CONVERT_FAILED.getCode(), new String[] {ioe.getMessage()});
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException ioe) {
					LOGGER.error(ioe.getMessage());
					SystemException.newSystemException(NotificationExceptionCodes.TEMPLATE_CONVERT_FAILED.getCode(), new String[] {ioe.getMessage()});
				} finally {
					if (tempFile != null) {
						deleteTempFile(tempFile);
					}					
				}
			}
			
			if (tempFile != null) {
				deleteTempFile(tempFile);
			}
		}
		
		return template;
	}
	
	private static String getConvertedText(final File tempFile, final Map<String, Object> map, final String dirLocation) {
		final VelocityContext context = new VelocityContext();
		addHeadersToContext(context, map);
		
		final VelocityEngine ve = new VelocityEngine();
		ve.init(createVelocityProperties(dirLocation));
		
		final Template t = ve.getTemplate(tempFile.getName());
		final StringWriter writer = new StringWriter();
		t.merge(context, writer);
		
		LOGGER.info("Text after converting is : {}", writer.toString());
		return writer.toString();
	}
	
	private static void addHeadersToContext(final VelocityContext context, final Map<String, Object> map) {
		LOGGER.info("Adding headers to Velocity context");
		for (String key : map.keySet()) {
			context.put(key, map.get(key));
		}		
	}
	
	private static File createTempFile(final String dirLocation) throws IOException {
		LOGGER.info("Creating temp file");
		final File dir = new File(dirLocation);
		final File file = File.createTempFile("temp_" + System.currentTimeMillis(), VM_EXT, dir);
		
		if (!dir.exists()) {
			LOGGER.info("Directory {} does not exists, hence creating it", dirLocation);
			dir.mkdirs();			
		} else {
			LOGGER.info("Directory {} does already exists, hence not creating it", dirLocation);			
		}
		
		if (!file.exists()) {
			LOGGER.info("Temp file {} does not exists, hence creating it", file.getAbsolutePath());
			file.createNewFile();
		}
		
		LOGGER.info("temp File is created : file name is {}, and path is {}", file.getName(), file.getAbsolutePath());
		return file;	
	}
	
	private static void deleteTempFile(final File tempFile) {
		if (tempFile.exists()) {
			tempFile.deleteOnExit();
			LOGGER.info("temp File is deleted : file name is {}, and path is {}", tempFile.getName(), tempFile.getAbsolutePath());
		}
	}
	
	private static Properties createVelocityProperties(final String dirLocation) {
		final Properties props = new Properties();
		props.setProperty("file.resource.loader.path", dirLocation);
		props.setProperty("file.resource.loader.cache", "false");
		
		props.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, dirLocation);
		
		props.setProperty("resource.loader", "file");
		props.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
		
		return props;
	}

	public static String getTemplateWithData(final MailDetails mailDetails, final NotificationEventData eventData) throws Exception {

		FileOutputStream fileOutputStream = null;
		StringWriter writer = null;
		try {

			final VelocityEngine ve = new VelocityEngine();
			ve.init(createVelocityProperties(dirLocation));
			
			final Map<String, Object> map = new HashMap<>();
			
			map.put("environment", "environment");
			map.put("modelName", "modelName");
			map.put("modelVersion", "modelVersion");
			
			final VelocityContext context = new VelocityContext();
			addHeadersToContext(context, map);
			
			final File dir = new File(dirLocation);
			final File file = File.createTempFile("temp_" + System.currentTimeMillis(), ".vm", dir);
			fileOutputStream = new FileOutputStream(file);
			if (!file.exists()) {
				file.createNewFile();
			}
			
			fileOutputStream.write("REALAnalytics $environment: $modelName $modelVersion model published".getBytes());
			fileOutputStream.flush();
			LOGGER.info("Proccessing template to bind with data ...");
			
			final Template t = ve.getTemplate(file.getName());
			writer = new StringWriter();
			t.merge(context, writer);
			
			LOGGER.info("Template data binding is done");
			System.out.println(writer.toString());

		} catch (Exception e) {
			LOGGER.error(e.getLocalizedMessage());
			throw e;
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch(IOException ioe) {
					LOGGER.error(ioe.getLocalizedMessage());
				}
			}
		}
		
		return writer.toString();
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(getTemplateWithData(null, null));
	}
}

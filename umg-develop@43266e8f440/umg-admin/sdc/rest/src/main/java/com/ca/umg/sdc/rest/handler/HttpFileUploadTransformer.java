package com.ca.umg.sdc.rest.handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.ca.framework.core.constants.SystemConstants;
import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.systemparameter.SystemParameterProvider;
import com.ca.framework.core.util.UmgFileProxy;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.model.delegate.MediateModelLibraryDelegate;
import com.ca.umg.business.model.info.MediateModelLibraryInfo;
import com.ca.umg.business.util.AdminUtil;

@Named
@SuppressWarnings({"PMD.CyclomaticComplexity"})
public class HttpFileUploadTransformer implements HttpRequestHandler {

	private final int maxMemSize = 500 * 1024;

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpFileUploadTransformer.class);

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(request.getSession().getServletContext());
		MediateModelLibraryDelegate mediateModelLibraryDelegate = ctx.getBean(MediateModelLibraryDelegate.class);
		SystemParameterProvider sysParam = ctx.getBean(SystemParameterProvider.class);
		UmgFileProxy umgFileProxy = ctx.getBean(UmgFileProxy.class);
		try {
			String sanPath = AdminUtil
					.getSanBasePath(umgFileProxy.getSanPath(sysParam.getParameter(SystemConstants.SAN_BASE)));
			String uploadPath = sanPath + File.separatorChar
					+ sysParam.getParameter(SystemConstants.FILE_UPLOAD_TEMP_PATH);
			File tempFolder = new File(uploadPath);
			if (!tempFolder.exists()) {
				tempFolder.mkdirs();
			}
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			response.setContentType("text/html");
			if (isMultipart) {
				DiskFileItemFactory factory = new DiskFileItemFactory();
				factory.setSizeThreshold(maxMemSize);
				ServletFileUpload upload = new ServletFileUpload(factory);
				long startTime = System.currentTimeMillis();
				MediateModelLibraryInfo mediateModelLibraryInfo = new MediateModelLibraryInfo();
				FileItemIterator iter = upload.getItemIterator(request);
				while (iter.hasNext()) {
					FileItemStream item = iter.next();
					if (item.getContentType() != null) {
						writeDataintoOutputFile(uploadPath, mediateModelLibraryInfo, item);
					} else {
						InputStream stream = item.openStream();
						if (BusinessConstants.VERSION.equals(item.getFieldName())) {
							mediateModelLibraryInfo.setVersion(IOUtils.toString(stream));
						} else if (BusinessConstants.CHECKSUM.equals(item.getFieldName())) {
							mediateModelLibraryInfo.setChecksum(IOUtils.toString(stream));
						}
						stream.close();
					}
				}
				LOGGER.info("File Uploaded in " + (System.currentTimeMillis() - startTime) + " ms.");
				AdminUtil.setAdminAwareTrue();
				MediateModelLibraryInfo mediateModelLibInfo = mediateModelLibraryDelegate
						.setMediateModelLibrary(mediateModelLibraryInfo);
				AdminUtil.setAdminAwareFalse();
				mediateModelLibraryDelegate.createMediateModelLibrary(mediateModelLibInfo);
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
				PrintWriter printWriter = response.getWriter();
				printWriter.write("Uploaded Successfully");
			}

		}

		catch (BusinessException | SystemException ex) {// NOPMD
			LOGGER.error("Exception :" + ex.getLocalizedMessage());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write(ex.getLocalizedMessage());
		} catch (Exception ex) {// NOPMD
			LOGGER.error("Exception while storing the uploaded file. Exception is : ", ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write(
					"Exception while storing the Model Library into temporary path.Exception is :" + ex.getMessage());
		} finally {
			AdminUtil.setAdminAwareFalse();
			if(response.getWriter() != null){
				response.getWriter().close();
			}
		}

	}

	private void writeDataintoOutputFile(String uploadPath, MediateModelLibraryInfo mediateModelLibraryInfo,
			FileItemStream item) throws IOException, FileNotFoundException {
		OutputStream outputStream = null;
		InputStream inputStream = null;
		try {
			mediateModelLibraryInfo.setTarName(item.getName());
			inputStream = item.openStream();
			outputStream = new FileOutputStream(new File(uploadPath, item.getName()));
			int read = 0;
			long startTime = System.currentTimeMillis();
			LOGGER.info("started writting data to temp folder");
			byte[] bytes = new byte[1024 * 2];
			while ((read = inputStream.read(bytes)) != -1) { // NOPMD
				outputStream.write(bytes, 0, read);
			}
			LOGGER.info("completed writting data to temp folder and timw taken to write the data is :"
					+ (System.currentTimeMillis() - startTime));

		} catch (IOException e) {
			LOGGER.error("Error Msg :" + e);
		} finally {
			closeResources(outputStream, inputStream);
		}
	}

	private void closeResources(OutputStream outputStream, InputStream inputStream) {
		IOUtils.closeQuietly(inputStream);
		IOUtils.closeQuietly(outputStream);
	}
}
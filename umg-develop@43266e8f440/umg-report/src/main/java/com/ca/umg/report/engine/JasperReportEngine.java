package com.ca.umg.report.engine;


import static com.ca.umg.report.ReportExceptionCodes.REPORT_COMPILE_ERROR;
import static com.ca.umg.report.ReportExceptionCodes.REPORT_DATA_SRC_CREATION_ERROR;
import static com.ca.umg.report.ReportExceptionCodes.REPORT_GENERATE_ERROR;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.umg.report.model.ModelReportTemplateInfo;
import com.ca.umg.report.model.ReportEngineNames;
import com.ca.umg.report.model.ReportTypes;
import com.ca.umg.report.util.ReportUtil;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

public class JasperReportEngine implements ReportEngine {

	private static final Logger LOGGER = LoggerFactory.getLogger(JasperReportEngine.class);

	private static final String COMPILED_RPT_EXT = ".jasper";
	private static final String TEMP_TEXT_FILE_EXT = ".txt";
//	private static final String TEMPLATE_RPT_EXT = ".jrxml";
	
	@Override
	public void execute(final ModelReportTemplateInfo reportTemplateInfo, OutputStream reportOutputStream) throws SystemException, BusinessException {
		final String compiledFilename = compile(reportTemplateInfo);
		try {
			  final JsonDataSource ds = createDataSource(reportTemplateInfo);
			  final Map<String, Object> parameters = new HashMap<String, Object>();
			  BufferedImage image = ImageIO.read(this.getClass().getResource("/images/logo/Altisource_logo_new.png"));
			  parameters.put("logo", image );	
			  final JasperPrint jasperPrint = JasperFillManager.fillReport(compiledFilename, parameters, ds);
			  generatePDFReport(jasperPrint, reportOutputStream);
		} catch (JRException | IOException jre) {
			LOGGER.error(jre.getMessage());
			LOGGER.error(jre.getLocalizedMessage());
			SystemException.newSystemException(REPORT_GENERATE_ERROR.getErrorCode(), new String[] {jre.getLocalizedMessage()});
		}
	}
	
	private JsonDataSource createDataSource(final ModelReportTemplateInfo reportTemplateInfo) throws SystemException {
		JsonDataSource ds = null;
		FileOutputStream fileOutputStream = null;
		FileInputStream fileInputStream = null;
		try {
			final File file = File.createTempFile(ReportUtil.getFileNameWithoutExt(reportTemplateInfo.getTemplateFileName()), TEMP_TEXT_FILE_EXT);
			fileOutputStream = new FileOutputStream(file);
			if (!file.exists()) {
				file.createNewFile();
			}
			byte[] contentInBytes = reportTemplateInfo.getReportJsonString().getBytes();
			fileOutputStream.write(contentInBytes);
			fileInputStream = new FileInputStream(file.getPath());
			ds = new JsonDataSource(fileInputStream);
		} catch (JRException jre) {
			LOGGER.error(jre.getMessage());
			LOGGER.error(jre.getLocalizedMessage());
			SystemException.newSystemException(REPORT_DATA_SRC_CREATION_ERROR.getErrorCode(), new String[] {jre.getLocalizedMessage()});
		} 
		catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage());
			LOGGER.error(e.getLocalizedMessage());
			SystemException.newSystemException(REPORT_DATA_SRC_CREATION_ERROR.getErrorCode(), new String[] {e.getLocalizedMessage()});
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			LOGGER.error(e.getLocalizedMessage());
			SystemException.newSystemException(REPORT_DATA_SRC_CREATION_ERROR.getErrorCode(), new String[] {e.getLocalizedMessage()});
		} finally {
              closeResources(fileOutputStream, fileInputStream);
		}
		
		return ds;
	}

	private void closeResources(FileOutputStream fileOutputStream, FileInputStream fileInputStream) {
		try {
			if(fileOutputStream !=null) {
				fileOutputStream.flush();
				fileOutputStream.close();
			}
			  IOUtils.closeQuietly(fileInputStream);
		} catch (Exception e) {
			LOGGER.error("Exception while closing the Output Stream :" +e.getMessage());
		} 
		
	}
	
	private String compile(final ModelReportTemplateInfo reportTemplateInfo) throws BusinessException {
		LOGGER.info("Compiling Report Design ...");
		OutputStream os = null;
		
		String compiledFileName = null;
		try {
			final InputStream is = new ByteArrayInputStream(reportTemplateInfo.getTemplateDefinition());
			final File compiledFile = File.createTempFile(ReportUtil.getFileNameWithoutExt(reportTemplateInfo.getTemplateFileName()), COMPILED_RPT_EXT);
			compiledFileName = compiledFile.getPath();
			LOGGER.info("Temparary Filename is: {}", compiledFileName);
			os = new FileOutputStream(compiledFileName);
			JasperCompileManager.compileReportToStream(is, os);
		} catch (IOException ioe) {
			LOGGER.error(ioe.getLocalizedMessage());
			LOGGER.error(ioe.getMessage());
			BusinessException.newBusinessException(REPORT_COMPILE_ERROR.getErrorCode(), new String[] {ioe.getLocalizedMessage()});
		} catch (JRException e) {
			LOGGER.error(e.getMessage());
			LOGGER.error(e.getLocalizedMessage());
			BusinessException.newBusinessException(REPORT_COMPILE_ERROR.getErrorCode(), new String[] {e.getLocalizedMessage()});
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException ioe) {
					LOGGER.error(ioe.getMessage());
					LOGGER.error(ioe.getLocalizedMessage());
					BusinessException.newBusinessException(REPORT_COMPILE_ERROR.getErrorCode(), new String[] {ioe.getLocalizedMessage()});
				}
			}
		}
		
		return compiledFileName;
	} 
	
	private void generatePDFReport(final JasperPrint jasperPrint, final OutputStream outputStream) throws BusinessException {
		JRPdfExporter pdfExporter = new JRPdfExporter();
		try {
			pdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
			pdfExporter.exportReport();
		} catch (JRException e) {
			LOGGER.error(e.getMessage());
			LOGGER.error(e.getLocalizedMessage());
			BusinessException.newBusinessException(REPORT_GENERATE_ERROR.getErrorCode(), new String[] {e.getLocalizedMessage()});
		}
	}
	
	
	
	
	/*public static void main(String[] args) {
		OutputStream reportOutputStream = null;
		InputStream inputStream = null;
		try {
			final ModelReportTemplateInfo reportTemplateInfo = new ModelReportTemplateInfo();
			
			final String templateName = "D:\\Jaspar\\Blank_A4_11.jrxml";
			final String outputFile = "D:\\Jaspar\\report1.pdf";
			String  jsonString = "{\"header\" : { \"modelName\" : \"HUBZU_WEEK_0\", \"majorVersion\" : 1, \"minorVersion\" : 0, \"date\" : \"2015-11-02T00:00:00.000\", \"transactionId\" : \"Backlog\", \"umgTransactionId\" : \"60a5fbd0-02db-4941-8ab3-27ac27a35458\", \"success\" : true},\"data\" : { \"hubzuwk0output\" : {   \"processed_data\" : {     \"PROPZIP\" : 44882.0,     \"State\" : \"OH\",     \"LoanNum\" : \"89691620\",     \"ADDRESS\" : \"5976  Marion Melmore Rd\",     \"ADDRESS2\" : \"null\",     \"PROP_CITY_VC_FK\" : \"Sycamore\",     \"ROOM_COUNT\" : \"9/4/1.00\",     \"land_area\" : 100000.0,     \"AGE\" : 115.0,     \"living_area\" : 2804.0,     \"bath_rooms\" : 1.0,     \"bedrooms\" : 3.0,     \"liv__to__lot\" : 0.02804,     \"FIRST_LIST_STRT_DATE\" : 16742.0,     \"REO_DATE_DT\" : 16661.0,     \"ASISHIGHMKTVAL_before_reo\" : 105000.0,     \"ASISLOWMKTVAL_before_reo\" : 95000.0,     \"CONDITIONCDE_before_reo\" : \"AVERAGE\",     \"vendor_LOWMKTVAL_before_reo\" : 109000.0,     \"ASISMIDMKTVAL_before_reo\" : 100000.0,     \"APPRTYP_First_reo\" : \"M\",     \"ASISHIGHMKTVAL_First_reo\" : 44000.0,     \"ASISLOWMKTVAL_First_reo\" : 44000.0,     \"CONDITIONCDE_First_reo\" : \"AVERAGE\",     \"REPLOWMKTVAL_First_reo\" : 113000.0,     \"REPHIGHMKTVAL_First_reo\" : 115000.0,     \"TOTREPAIRAMT_First_reo\" : 70000.0,     \"vendor_LOWMKTVAL_First_reo\" : 43000.0,     \"REPMIDMKTVAL_First_reo\" : 114000.0,     \"FIRST_REO_ASSET_VALUE\" : 44000.0,     \"APPRTYP_FIRST_LIST\" : \"M\",     \"ASISHIGHMKTVAL\" : 44000.0,     \"ASISLOWMKTVAL\" : 44000.0,     \"CONDITIONCDE_FIRST_LIST\" : \"AVERAGE\",     \"REPLOWMKTVAL\" : 113000.0,     \"REPHIGHMKTVAL\" : 115000.0,     \"TOTREPAIRAMT\" : 70000.0,     \"vendor_LOWMKTVAL_FIRST_LIST\" : 43000.0,     \"REPMIDMKTVAL\" : 114000.0,     \"AV\" : 44000.0,     \"PROPTYPE\" : \"SF\",     \"STYLECDE\" : \"TRADITIONAL\",     \"cv_rep_FIRST_LIST\" : 0.045454545454545456,     \"prem_rep_FIRST_LIST\" : 1.0,     \"cv_rep_First_reo\" : 0.017543859649122806,     \"prem_rep_First_reo\" : 1.0,     \"cv_asis_before_reo\" : 0.1,     \"vnd_adj_val_FIRST_LIST\" : -1000.0,     \"vnd_adj_pct_FIRST_LIST\" : 0.9772727272727273,     \"vnd_adj_val_First_reo\" : -1000.0,     \"vnd_adj_pct_First_reo\" : 0.9772727272727273,     \"vnd_adj_val_before_reo\" : 14000.0,     \"vnd_adj_pct_before_reo\" : 1.1473684210526316,     \"dep_pre_to_post_reo\" : 0.44,     \"RegOnly_AVM_Value\" : \"2015-11-03\",     \"RegOnly_FSD_Score\" : 103000.0,     \"ReoOnly_AVM_Value\" : 0.5,     \"ReoOnly_FSD_Score\" : 84081.63265306121,     \"Reg__to__Reo__val\" : 0.21,     \"Reg__to__Reo__pct\" : 18918.367346938787,     \"ORGPRINBAL\" : 1.225,     \"PIPMTAMT\" : 45000.0,	     \"ESCROWPMT\" : 450.91,     \"CREDITSCORE\" : 117.13,     \"PURCHASEPRICE\" : 697.0,     \"ORGAPPRVAL\" : 45000.0,     \"CURPRINBAL\" : 62000.0,		     \"enter__yrmon\" : 38372.02,     \"HZ_PS\" : \"2015M08\",     \"Stigma\" : \"HZ.OH\",     \"HPI\" : 0.41280514700000004,     \"HPA_M\" : 155.8628,     \"HPA_Q\" : 0.0,     \"HPA_H\" : 0.02397958647367382,     \"HPA_Y\" : 0.05472764749748604,     \"HPA_2Y\" : 0.04897012851100202,     \"HPA_3Y\" : 0.10725692786833063,     \"enter__month\" : 0.22312389302056568,     \"worsened\" : \"08-AUG\",     \"Reg__to__Reo__FSD__diff\" : 0.0,     \"Reo__to__AV__val\" : 0.29000000000000004,     \"Reo__to__AV__pct\" : 40081.63265306121,     \"Reg__to__AV__val\" : 1.9109461966604822,     \"Reg__to__AV__pct\" : 59000.0,     \"vnd_val_pre__to__post\" : 2.340909090909091,     \"vnd_pct_pre__to__post\" : -66000.0,     \"ltv__value__pre_reo\" : 0.3944954128440367,     \"ltv__ratio__pre_reo\" : -56627.98,     \"ltv__value\" : 0.40391599999999994,     \"ltv__ratio\" : -5627.980000000003,     \"orig__prin__to__av__value\" : 0.8720913636363635,     \"orig__prin__to__av__ratio\" : 1000.0,     \"escrow__to__pip__value\" : 1.0227272727272727,     \"escrow__to__pip__ratio\" : -333.78000000000003,     \"orig__appr__to__purch__value\" : 0.2597635891863121,     \"orig__appr__to__purch__ratio\" : 17000.0,     \"apprcn__value\" : 1.3777777777777778,     \"apprcn__ratio\" : 1000.0,     \"totrep__to__av\" : 1.0227272727272727,     \"rep__hi__to__av\" : 1.5909090909090908,     \"rep__lo__to__av\" : 2.6136363636363638,     \"rep__to__av\" : 2.5681818181818183,     \"bed__to__bath\" : 2.590909090909091,     \"area__per__bedroom\" : 3.0,     \"living__to__lot\" : 934.6666666666666,     \"rep__hi__roi\" : 0.02804,     \"rep__hi__va\" : 0.014285714285714285,     \"rep__lo__roi\" : 1000.0,     \"rep__lo__va\" : -0.014285714285714285,     \"rep__mid__roi\" : -1000.0,     \"rep__mid__va\" : 0.0,     \"cv__rep\" : 0.0,     \"rep__reo__to__list__value\" : 0.017543859649122806,     \"rep__reo__to__list__ratio\" : 0.0,     \"prem_rep\" : 1.0,     \"Label__AV\" : 1.0,     \"Label__RegOnly_AVM_Value\" : \"2\",     \"Label__ReoOnly_AVM_Value\" : \"3\",     \"Label__RegOnly_FSD_Score\" : \"2\",     \"Label__ReoOnly_FSD_Score\" : \"5\",     \"Label__Reg__to__Reo__FSD__diff\" : \"3\",     \"Label__CREDITSCORE\" : \"5\",     \"Label__rep__mid__roi\" : \"4\",     \"Label__AGE\" : \"1\",     \"Label__bedrooms\" : \"5\",     \"Label__bath_rooms\" : \"2\",     \"Label__living_area\" : \"1\",     \"Label__land_area\" : \"5\",     \"AgeModern\" : \"5\",     \"Age2000s\" : 7.641655411587203E-31,     \"Age1980s\" : 1.9823478475732273E-90,     \"Age1960s\" : 8.200081071666523E-59,     \"Age1940s\" : 3.817198269273618E-34,     \"Age1990s\" : 1.9996757496994427E-16,     \"Age1970s\" : 9.420804006180129E-74,     \"Age1950s\" : 1.3072853550637358E-45,     \"Age1930s\" : 2.041461118861227E-24,     \"YearBuilt\" : 3.587567815928164E-10,     \"CenteredLogHHInc__PerCapita_2012\" : 1899.0,     \"CenteredLogHHSize__Own__to__Rent_2012\" : 0.12680442698590966,     \"CenteredPop__Change__Value\" : 0.13467727075308028,     \"CenteredLogNumHHOwner__to__Renter_2012\" : -269.0,     \"CenteredLogNumHHOwner_2012\" : 0.7959452324460525,     \"CenteredLogNumHH_2012\" : -1.8261966085470451,     \"CenteredUnempRate16_2012\" : -2.0616135912781166,     \"CenteredLogPop16_2012\" : -5.31,     \"CenteredLogHHSize_OwnerOcc_2012\" : -2.151589332742632,     \"CenteredLogHHSize_2012\" : -0.05390664082966523,     \"CenteredLogHHIncom_2012\" : -0.06578184936011411,     \"CenteredExpected_pct_gain\" : 0.0610225775972939,     \"Centeredpct_flipped\" : 4.529999999999999,     \"CenteredEvictionCost\" : 0.0,     \"enter__date\" : 287.0,     \"dateCurve1\" : 16661.0,     \"dateCurve2\" : 0.00013383022576488537,     \"dateCurve3\" : 0.10320507006168182   },   \"model_predictions\" : {     \"gain__pct__adj__SPwofee__cubist\" : 0.792336106300354,     \"gain__pct__adj__SPwofee__gbm\" : 0.6923992918603271,     \"gain__pct__adj__SPwofee__rf\" : 0.796762185430578,     \"gain__val__adj__SPwofee__cubist\" : -3904.98095703125,     \"gain__val__adj__SPwofee__gbm\" : -9918.924310389228,     \"gain__val__adj__SPwofee__rf\" : -8052.085447985861,     \"ln__gain__pct__adj__SPwofee__cubist\" : -0.30124950408935547,     \"ln__gain__pct__adj__SPwofee__gbm\" : -0.39224549051679564,     \"ln__gain__pct__adj__SPwofee__rf\" : -0.407422553160388,     \"ln__sold__price__adj__SPwofee__cubist\" : 10.454164505004883,     \"ln__sold__price__adj__SPwofee__gbm\" : 10.306174421728537,     \"ln__sold__price__adj__SPwofee__rf\" : 10.441637840720418,     \"mod__gain__pct__adj__SPwofee__cubist\" : -30.95269012451172,     \"mod__gain__pct__adj__SPwofee__gbm\" : -74.80040529119091,     \"mod__gain__pct__adj__SPwofee__rf\" : -37.78708473118301,     \"mod__price__per__bed__adj__SPwofee__cubist\" : 3.727217197418213,     \"mod__price__per__bed__adj__SPwofee__gbm\" : 3.5631107618331574,     \"mod__price__per__bed__adj__SPwofee__rf\" : 3.6867333630017667,     \"mod__price__per__living__adj__SPwofee__cubist\" : 1.7352994680404663,     \"mod__price__per__living__adj__SPwofee__gbm\" : 1.7082523557855362,     \"mod__price__per__living__adj__SPwofee__rf\" : 1.75949659235032,     \"mod2__gain__pct__adj__SPwofee__cubist\" : 12.9940767288208,     \"mod2__gain__pct__adj__SPwofee__gbm\" : 11.34663286099238,     \"mod2__gain__pct__adj__SPwofee__rf\" : 12.287943122817046,     \"adj__SPwofee__cubist\" : 33190.7734375,     \"adj__SPwofee__gbm\" : 33417.77568933573,     \"adj__SPwofee__rf\" : 40234.03580850785,     \"sqrt__sold__price__adj__SPwofee__cubist\" : 188.0573272705078,     \"sqrt__sold__price__adj__SPwofee__gbm\" : 175.24338229447594,     \"sqrt__sold__price__adj__SPwofee__rf\" : 191.4796221210321,     \"AV\" : 44000.0,     \"bedrooms\" : 3.0,     \"living_area\" : 2804.0   },   \"model_ensemble\" : {     \"AV\" : 44000.0,     \"bedrooms\" : 3.0,     \"living_area\" : 2804.0,     \"gain__pct__SPwofee__final\" : 33392.65382073206,     \"gain__val__SPwofee__final\" : 36623.44040792158,     \"ln__gain__pct__SPwofee__final\" : 30484.462207771357,     \"ln__sold__price__SPwofee__final\" : 32880.28247489457,     \"mod__gain__pct__SPwofee__final\" : 33709.12825921144,     \"mod__price__per__bed__SPwofee__final\" : 28850.242148325022,     \"mod__price__per__living__SPwofee__final\" : 33408.575274892515,     \"mod2__gain__pct__SPwofee__final\" : 31173.633102608834,     \"SPwofee__final\" : 35470.13462574068,     \"sqrt__sold__price__SPwofee__final\" : 34148.3427608297,     \"ensemble\" : 33346.32572265334   },   \"individual_preds\" : {     \"gain__pct__adj__SPwofee__cubist\" : 34862.788677215576,     \"gain__pct__adj__SPwofee__gbm\" : 30465.568841854394,     \"gain__pct__adj__SPwofee__rf\" : 35057.53615894543,     \"gain__val__adj__SPwofee__cubist\" : 40095.01904296875,     \"gain__val__adj__SPwofee__gbm\" : 34081.07568961077,     \"gain__val__adj__SPwofee__rf\" : 35947.91455201414,     \"ln__gain__pct__adj__SPwofee__cubist\" : 32555.29830739203,     \"ln__gain__pct__adj__SPwofee__gbm\" : 29723.68323617782,     \"ln__gain__pct__adj__SPwofee__rf\" : 29275.971105155353,     \"ln__sold__price__adj__SPwofee__cubist\" : 34687.534900214756,     \"ln__sold__price__adj__SPwofee__gbm\" : 29915.768504553125,     \"ln__sold__price__adj__SPwofee__rf\" : 34255.71355617613,     \"mod__gain__pct__adj__SPwofee__cubist\" : 37507.30894454865,     \"mod__gain__pct__adj__SPwofee__gbm\" : 28309.734616777114,     \"mod__gain__pct__adj__SPwofee__rf\" : 36073.714237476,     \"mod__price__per__bed__adj__SPwofee__cubist\" : 32917.07402718706,     \"mod__price__per__bed__adj__SPwofee__gbm\" : 23941.084396789633,     \"mod__price__per__bed__adj__SPwofee__rf\" : 30470.764189163932,     \"mod__price__per__living__adj__SPwofee__cubist\" : 33501.99658173114,     \"mod__price__per__living__adj__SPwofee__gbm\" : 31215.248424601145,     \"mod__price__per__living__adj__SPwofee__rf\" : 35656.308647627025,     \"mod2__gain__pct__adj__SPwofee__cubist\" : 35417.44205571363,     \"mod2__gain__pct__adj__SPwofee__gbm\" : 27006.005004143855,     \"mod2__gain__pct__adj__SPwofee__rf\" : 31672.6734520454,     \"adj__SPwofee__cubist\" : 33190.7734375,     \"adj__SPwofee__gbm\" : 33417.77568933573,     \"adj__SPwofee__rf\" : 40234.03580850785,     \"sqrt__sold__price__adj__SPwofee__cubist\" : 35365.55834012688,     \"sqrt__sold__price__adj__SPwofee__gbm\" : 30710.24303800784,     \"sqrt__sold__price__adj__SPwofee__rf\" : 36664.445687613246   },   \"wk0_price\" : {     \"sugg__list__final\" : 46600.0,     \"sugg__list__trunc__rnd\" : 46600.0,     \"sugg__list__trunc\" : 46640.0,     \"sugg__list\" : 39059.84516704485,     \"final_ensemble\" : 33346.32572265334,     \"AV\" : 44000.0,     \"notes\" : \"Truncated at lower bound; Rounded to nearest hundred\"   },   \"graph_data\" : [{\"name\":\"Asset Value\",\"value\":\"88000.0\"},{\"name\":\"REO Value - Absolute Data\",\"value\":\"73000\"}]   },   \"final_ensemble\" : 33346.32572265334,   \"final_week0_price\" : 46600.0,   \"run_time\" : \"2015-11-03 22:03:56\"} }";
			//String jsonString = "{ \"userName\": \"Nageswara\", \"details\": {\"email\": \"anand.kumar@altisource.com\" },\"score\": {\"hindi\": \"80\",\"math\": \"900\" ,\"english\": \"72\"  }}\"";
			
			final String reportType = ReportTypes.PDF.getType();
			final String reportEngine = ReportEngineNames.JASPER_ENGINE.getEngineName();
			
			
			byte[] reportTemplateArray = null;			
			inputStream = new FileInputStream(new File(templateName));
			try {
				reportTemplateArray = new byte[inputStream.available()];
				inputStream.read(reportTemplateArray);
			} catch (IOException ioe) {
				LOGGER.error(ioe.getLocalizedMessage());
			}
			
			reportTemplateInfo.setTemplateDefinition(reportTemplateArray);
			reportTemplateInfo.setTemplateFileName(templateName);
			reportTemplateInfo.setReportType(reportType);
			reportTemplateInfo.setReportEngine(reportEngine);
			reportTemplateInfo.setReportJsonString(jsonString);
			reportOutputStream = new FileOutputStream(new File(outputFile));
			
			final ReportEngine engine = ReportEngineFactory.getReportEngine(reportTemplateInfo);
			engine.execute(reportTemplateInfo, reportOutputStream);
			
		} catch (IOException ioe) {
			LOGGER.error(ioe.getLocalizedMessage(), ioe);

		} catch (BusinessException | SystemException be) {
			LOGGER.error(be.getLocalizedMessage(), be);

		}
		finally {
			if (reportOutputStream != null) {
				try {
					reportOutputStream.close();
				} catch (IOException ioe) {
					LOGGER.error(ioe.getLocalizedMessage(), ioe);

				}
			}
			
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException ioe) {
					LOGGER.error(ioe.getLocalizedMessage(), ioe);

				}
			}
		}
	}*/
}
/*package com.ca.umg.report;

import static net.sf.jasperreports.engine.JasperCompileManager.compileReportToFile;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JsonExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.view.JRViewer;

public class SimpleUMGDashboardReport {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleUMGDashboardReport.class);
	
//	private static final Log LOGGER = LogFactory.getLog(SimpleUMGDashboardReport.class.getName());
	
//	private static final Log LOGGER = LogFactory.getLog(SimpleUMGDashboardReport.class.getName());
	
//	private static final Log LOGGER = new Log4JLogger(SimpleUMGDashboardReport.class.getName());
	
	public static final String BASE_DIRECTORY = "D://Work/SVNRepository/ConsumerAnalytics/umg/trunk/umg-report/src/main/resources/";
	
	public static final String FILE_NAME = "Blank_A4_Table_Based";
	
	public static void main(String[] args) {
		final SimpleUMGDashboardReport report = new SimpleUMGDashboardReport();
		report.generateReport("M7457%");
	}
	
	public void generateReport(final String clientTransactionId) {
		compile();
		final JasperPrint jasperPrint = fillReport(clientTransactionId);
		generatePDFReport(jasperPrint);
//		generateCSVReport(jasperPrint);
//		generateJSONReport(jasperPrint);
//		generateHTMLReport(jasperPrint);
//		generateXlsReport(jasperPrint);
//		generateXlsxReport(jasperPrint);
//		generateTextReport(jasperPrint);
		viewReport(jasperPrint);
	}
	
	public void compile() {
		String sourceFileName = BASE_DIRECTORY + FILE_NAME + ".jrxml";
		LOGGER.info("Compiling Report Design ...");
		try {
			compileReportToFile(sourceFileName);
		} catch (JRException e) {
			e.printStackTrace();
		}
		LOGGER.info("Done compiling!!! ...");
	} 
	
	public JasperPrint fillReport (final String clientTransactionId) {
		System.out.println("Filling report");
		String sourceFileName = BASE_DIRECTORY + FILE_NAME + ".jasper";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("CLIENT_TRANSACTION_NAME", clientTransactionId);
		params.put("MY_CLIENT_TRANSACTION_NAME", clientTransactionId);
		try {
			JasperPrint jasperPrint = JasperFillManager.fillReport(sourceFileName, params, getConnection());
			return jasperPrint;
		} catch (JRException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Connection getConnection() {
		final String url = "jdbc:mysql://10.0.20.94:3306/umg_admin";
		final String user = "umgprod";
		final String psswdKy = "Prod#2014";
		final String driver="com.mysql.jdbc.Driver";
		
		try {
			Class.forName(driver);
			return DriverManager.getConnection(url, user, psswdKy);
		} catch (ClassNotFoundException cnfe) {
			LOGGER.error("ClassNotFoundException: ", cnfe);
		} catch (SQLException sqle) {
			LOGGER.error("SQLException: ", sqle);
		}
		
		return null;
	}
	
	public void generateCSVReport(final JasperPrint jasperPrint) {
		try {
			JRCsvExporter csvExporter = new JRCsvExporter();
			csvExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			csvExporter.setExporterOutput(new SimpleWriterExporterOutput(BASE_DIRECTORY + FILE_NAME + ".csv"));
			csvExporter.exportReport();
		} catch (JRException e) {
			e.printStackTrace();
		}
	}
	
	public void generateXlsReport(final JasperPrint jasperPrint) {
		try {
			JRXlsExporter xlsExporter = new JRXlsExporter();
			xlsExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			xlsExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(BASE_DIRECTORY + FILE_NAME + ".xls"));
			xlsExporter.exportReport();
		} catch (JRException e) {
			e.printStackTrace();
		}
	}
	
	public void generateXlsxReport(final JasperPrint jasperPrint) {
		try {
			JRXlsxExporter xlsExporter = new JRXlsxExporter();
			xlsExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			xlsExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(BASE_DIRECTORY + FILE_NAME + ".xlsx"));
			xlsExporter.exportReport();
		} catch (JRException e) {
			e.printStackTrace();
		}
	}
	
	public void generateJSONReport(final JasperPrint jasperPrint) {
		try {
			JsonExporter jsonExporter = new JsonExporter();
			jsonExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			jsonExporter.setExporterOutput(new SimpleWriterExporterOutput(BASE_DIRECTORY + FILE_NAME + ".json"));
			jsonExporter.exportReport();
		} catch (JRException e) {
			e.printStackTrace();
		}
	}
	
	public void generatePDFReport(final JasperPrint jasperPrint) {
		JRPdfExporter pdfExporter = new JRPdfExporter();
		try {
			pdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(BASE_DIRECTORY + FILE_NAME + ".pdf"));
			pdfExporter.exportReport();
		} catch (JRException e) {
			e.printStackTrace();
		}
	}
	
	public void generateHTMLReport(final JasperPrint jasperPrint) {
		HtmlExporter htmlExporter = new HtmlExporter();
		try {
			htmlExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			htmlExporter.setExporterOutput(new SimpleHtmlExporterOutput(BASE_DIRECTORY + FILE_NAME + ".html"));
			htmlExporter.exportReport();
		} catch (JRException e) {
			e.printStackTrace();
		}
	}
	
	public void generateTextReport(final JasperPrint jasperPrint) {
		JRTextExporter textExporter = new JRTextExporter();
		try {
			textExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			textExporter.setExporterOutput(new SimpleHtmlExporterOutput(BASE_DIRECTORY + FILE_NAME + ".txt"));
			textExporter.exportReport();
		} catch (JRException e) {
			e.printStackTrace();
		}
	}
	
	public void viewReport(final JasperPrint jasperPrint) {		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					final JRViewer viewer = new JRViewer(jasperPrint);
					final JFrame frame = new JFrame();
					final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
					frame.setSize(new Dimension((int)screenSize.getWidth(), (int)screenSize.getHeight() - 50));
					final Container c = frame.getContentPane();
					c.add(viewer);
					frame.setVisible(true);
				}
			});
		} catch (InvocationTargetException e) {
			LOGGER.error("InvocationTargetException: ", e);
		} catch (InterruptedException e) {
			LOGGER.error("InterruptedException: ", e);
		}
	}
}*/

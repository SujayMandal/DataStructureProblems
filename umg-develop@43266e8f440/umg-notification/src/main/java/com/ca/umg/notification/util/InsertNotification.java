package com.ca.umg.notification.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.umg.notification.model.NotificationMailTemplate;
import com.ca.umg.notification.model.NotificationTypes;

@SuppressWarnings("PMD")
public class InsertNotification {
	private static final Logger LOGGER = LoggerFactory.getLogger(InsertNotification.class);
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/umg_admin";
	static final String USER = "root";
	static final String PASS = "";

	private static final String INSERT_INTO_MAIL_TEMPLATE = "INSERT INTO notification_email_template ("
			+ " ID,"
			+ " NAME,"
			+ " DESCRIPTION,"
			+ " BODY_DEFINITION,"
			+ " SUBJECT_DEFINITION,"
			+ " IS_ACTIVE,"
			+ " MAJOR_VERSION,"
			+ " MAIL_CONTENT_TYPE,"
			+ " CREATED_BY,"
			+ " CREATED_ON "
			+ ") VALUES ("
			+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


	private static final String GET_EMAIL_TEMPLATE = "SELECT TEMPLATE_DEFINITION FROM notification_email_template WHERE ID = ?";

	public void  insertTemplate(final NotificationMailTemplate notificationEmailTemplate , Connection con){
		LOGGER.info("Storing new  template in db");
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = con.prepareStatement(INSERT_INTO_MAIL_TEMPLATE);
			preparedStatement.setString(1,"10");
			preparedStatement.setString(2,notificationEmailTemplate.getName());
			preparedStatement.setString(3,notificationEmailTemplate.getDescription());
			preparedStatement.setBytes(4,notificationEmailTemplate.getBodyDefinition());
			preparedStatement.setBytes(5,notificationEmailTemplate.getSubjectDefinition());
			preparedStatement.setInt(6,1);
			preparedStatement.setInt(7,	1);
			preparedStatement.setString(8,"MIME");
			preparedStatement.setString(9,"System");
			preparedStatement.setString(10, "1234567890");
			preparedStatement.executeUpdate();
			LOGGER.info("New Template Stored in db");
		} catch (SQLException e) {
			LOGGER.error("SQLException: ", e);
		}
		finally{ 
				releaseResource(con,preparedStatement);
		}
	} 

	public static String getTemplateEmail(String ID){
		Connection con = null;
		String template = null;
		PreparedStatement preparedStatement=null;
		try {
			con = InsertNotification.getConnection();
			LOGGER.info("Looking for template in db");
			  preparedStatement = con.prepareStatement(GET_EMAIL_TEMPLATE);
			preparedStatement.setString(1,ID);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				template = rs.getString(1);
				LOGGER.info("Tamplate is " +  template);
			}
		} catch (SQLException | ClassNotFoundException e) {
			LOGGER.error("Exception: ", e);
		}
		finally{
			releaseResource(con,preparedStatement);
		}
		return template;
	}
	public static void main(String sd[]) throws SQLException, ClassNotFoundException {
		final NotificationMailTemplate t =  new NotificationMailTemplate();
		t.setName("ddddd");
		t.setDescription("ssdss");
		final String subject = "REALAnalytics $environment: $modelName $modelVersion model published"; 
		final String body = "Following Model has been published in $environment \n"
				+ "Model Name: $modelName \n"
				+ "Model Version: $modelVersion \n"
				+ "Model Published Timestamp: $publishedDate \n" 
				+ "Tenant Name: $TenantName \n" 
				+ "Publisher Name: $publisherName";
		
		t.setBodyDefinition(body.getBytes());
		t.setMailContentType(NotificationTypes.MAIL.getType());
		t.setSubjectDefinition(subject.getBytes());
		
		InsertNotification n = new InsertNotification();
		n.insertTemplate(t , InsertNotification.getConnection());
	}
	
	public static Connection getConnection() throws ClassNotFoundException, SQLException  {
		Connection conn = null;
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(DB_URL, USER, PASS);
		return conn;

	}
	private static void releaseResource(Connection connection, Statement stmt)
	{
		try {
			if(stmt!=null && !stmt.isClosed()) {
				LOGGER.error("Relesing Statement");
				stmt.close();
			}
			if(connection!= null && !connection.isClosed())
			{
				LOGGER.error("Relesing Connection");
				connection.close();
			} 
		}catch(Exception e) {
			LOGGER.error("Exception while releasing connection and statement ", e);
		}
	}
}



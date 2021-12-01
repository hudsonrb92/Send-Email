package com.elaudos.report;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectToDb {
	private String user;
	private String password;
	private Integer port;
	private String host;
	private String dataBase;
	private String urlConn;
	private Connection conn;

	public ConnectToDb(String user, String password, Integer port, String host, String dataBase) {
		this.user = user;
		this.password = password;
		this.port = port;
		this.host = host;
		this.dataBase = dataBase;
		this.updateUrl();
	}

	public ConnectToDb(String user, String password, String host, String dataBase) {
		this.user = user;
		this.password = password;
		this.host = host;
		this.dataBase = dataBase;
		this.port = 5432;
		this.updateUrl();
	}

	public void closeConn() throws SQLException {
		this.conn.close();
	}
	
	private void updateUrl() {
		this.urlConn = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dataBase;
	}

	public Connection getConn() throws SQLException {
		this.conn = DriverManager.getConnection(urlConn, user, password);
		return this.conn;
	}

}

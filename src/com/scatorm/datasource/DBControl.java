package com.scatorm.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import com.scatorm.tools.Constant;
import com.scatorm.tools.XMLFactory;

/**
 * 数据库放入连接操作类
 * @author Luohong
 *
 */
public class DBControl {

	private static Map<String, String> DBMap = null;
	private Connection conn = null;
	static{
		load();
	}
	private static void load() {
		if(DBMap == null){
			XMLFactory.getXMLInfo(XMLFactory.class.getResource("/scatorm.datasource.xml").getFile());
			DBMap = Constant.DBMAP;
		}
		
	}
	public DBControl() {
		if (null == DBMap) {
			load();
		}else {
			getSimpleConnection();
		}
	}
	private Connection getSimpleConnection() {
		try {
			Class.forName(DBMap.get("driver"));
			conn = DriverManager.getConnection(DBMap.get("url"), DBMap.get("username"), 
					DBMap.get("password"));
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}
	
	/**
	 * 处理添加，修改，删除的数据库工具方法
	 * @param cql
	 */
	public void setData(String cql){
		Statement statement = null;
		try {
			statement = conn.createStatement();
			statement.executeUpdate(cql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 查询返回结果集
	 * @param cql
	 * @return
	 */
	public ResultSet getData(String cql){
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = conn.createStatement();
			resultSet = statement.executeQuery(cql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultSet;
	}
	
	/**
	 * 关闭数据库连接
	 */
	public void close(){
		try {
			if (conn != null || !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

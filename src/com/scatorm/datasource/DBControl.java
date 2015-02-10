package com.scatorm.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import com.scatorm.cache.CacheThread;
import com.scatorm.tools.Constant;
import com.scatorm.tools.ReflectionUtils;
import com.scatorm.tools.XMLFactory;

/**
 * 数据库放入连接操作类
 * @author Luohong
 *
 */
public class DBControl {

	private static Map<String, String> DBMap = null;
	private Connection conn = null;
	private DataSource dataSource = null;
	static{
		load();
	}
	private static void load() {
		if(DBMap == null){
			XMLFactory.getXMLInfo(XMLFactory.class.getResource("/scatorm.datasource.xml").getFile());
			DBMap = Constant.DBMAP;
			//开启缓存线程
			CacheThread cacheThread = new CacheThread();
			Thread thread = new Thread(cacheThread);
			thread.start();
		}
		
	}
	public DBControl() {
		if (null == DBMap) {
			load();
		}else if (DBMap.containsKey("datasource")) {
			if (dataSource == null) {
				try {
					Class datasourceClass = Class.forName(DBMap.get("datasource"));
					dataSource = (DataSource) datasourceClass.newInstance();
					for(Entry<String, String> entry : DBMap.entrySet()){
						String key = entry.getKey();
						String value = entry.getValue();
						if (!key.equals("datasource")) {
							ReflectionUtils.setFieldValue(dataSource, key, value);
						}
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				getDataSourceConn();
			}else {
				getDataSourceConn();
			}
		}
		else {
			getSimpleConnection();
		}
	}
	/**
	 * 获得数据源的连接
	 * @return
	 */
	private Connection getDataSourceConn() {
		try {
			this.conn = dataSource.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.conn;
	}
	/**
	 * 获得jdbc的连接
	 * @return
	 */
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

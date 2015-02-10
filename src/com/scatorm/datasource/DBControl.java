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
 * ���ݿ�������Ӳ�����
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
			//���������߳�
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
	 * �������Դ������
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
	 * ���jdbc������
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
	 * ������ӣ��޸ģ�ɾ�������ݿ⹤�߷���
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
	 * ��ѯ���ؽ����
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
	 * �ر����ݿ�����
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

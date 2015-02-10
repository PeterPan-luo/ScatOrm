package com.scatorm.sqltools;

import java.rmi.server.LoaderHandler;

import com.scatorm.datasource.DBControl;
import com.scatorm.tableinfo.TableInfo;
import com.scatorm.tools.Constant;

public class ScatDetachedCriteria {

	private String sql;
	private TableInfo table;
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	
	public TableInfo getTable() {
		return table;
	}
	public void setTable(TableInfo table) {
		this.table = table;
	}

	static{
		load();
	}
	private static void load() {
		if (null == Constant.TABLEMAP) {
			new DBControl();
		}
	}
	/**
	 * 首先要拼接SQL语句的前半部分：select * from book
	 * @param c
	 */
	public void setEntity(Class c) {
		TableInfo tableInfo = Constant.TABLEMAP.get(c.getName());
		setTable(tableInfo);
		String sqlString = "select * from " + tableInfo.getTablename();
		setSql(sqlString);
	}
	
	public void add(Restrictions restrictions) {
		String sql = getSql();
		if (!sql.contains("where")) {
			sql += " where ";
		}
		sql += restrictions.getCondition();
		setSql(sql);
	}
}

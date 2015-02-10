package com.scatorm.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.plaf.SliderUI;

import com.scatorm.beans.Book;
import com.scatorm.beans.Style;
import com.scatorm.cache.Cache;
import com.scatorm.cache.CacheFactory;
import com.scatorm.datasource.DBControl;
import com.scatorm.sqltools.SQLInfo;
import com.scatorm.sqltools.SQLTools;
import com.scatorm.sqltools.ScatDetachedCriteria;
import com.scatorm.tableinfo.Id;
import com.scatorm.tableinfo.ManytoOne;
import com.scatorm.tableinfo.OnetoMany;
import com.scatorm.tableinfo.Property;
import com.scatorm.tableinfo.TableInfo;
import com.scatorm.tools.Constant;
import com.scatorm.tools.ReflectionUtils;

public class ScatormDAOImpl implements ScatDAO {
	private DBControl dbControl = null;
	
	public void save(Object o) {
		//保存语句：insert into 表名(字段, 字段) Values(值1, 值2) 
		dbControl = new DBControl();
		String className = o.getClass().getName();
	
		TableInfo tableInfo = Constant.TABLEMAP.get(className);
		String tableName = tableInfo.getTablename();
		Id id = tableInfo.getId();
		List<Property> properties = tableInfo.getProperties();
		List<ManytoOne> manytoOnes = tableInfo.getManytoOnes();
		List<OnetoMany> onetoManies = tableInfo.getOnetoManies();
		//主键操作
		String idName = id.getIdname();
		String idCol = id.getIdcolumn();
		Object idVal = ReflectionUtils.getFieldValue(o, idName);
		//拼sql语句
		StringBuffer sb = new StringBuffer("insert into ");
		sb.append(tableName).append("(");
		//false非自增
		if (!id.getIdentity()) {
			sb.append(idCol).append(",");
		}
		List<Object> valueList = new ArrayList<Object>();
		if (properties != null) {
			for(Property property : properties){
				String colName = property.getPropertycolumn();
				String name = property.getPropertyname();
				sb.append(colName).append(",");
				Object val = ReflectionUtils.getFieldValue(o, name);
				valueList.add(val);
			}
		}
		//有外键，需要添加
		if (manytoOnes != null) {
			for(ManytoOne manytoOne : manytoOnes){
				String oneName = manytoOne.getManyname();
				String oneCol = manytoOne.getManycolumn();
				String oneClassName = manytoOne.getManyclass();
				Object oneObject = ReflectionUtils.getFieldValue(o, oneName);
				Object val = ReflectionUtils.getFieldValue(oneObject, oneCol);
				//有问题，oneCol应该对应的是Manytoone 的class的字段，而不是数据库表的字段
				sb.append(oneCol).append(",");
				valueList.add(val);
			}
		}
		
	    sb.deleteCharAt(sb.length() - 1).append(") values(");
	    
	    if (!id.getIdentity()) {
			if(idVal instanceof String)
				sb.append("'").append(idVal).append("',");
			else
				sb.append(idVal).append(",");
		}
	    for(Object value : valueList){
	    	if (value instanceof String) {
	    		sb.append("'").append(value).append("',");
			}else {
				sb.append(idVal).append(",");
			}
	    }
	    String sql = sb.deleteCharAt(sb.length() - 1).append(")").toString();
	    System.out.println(sql);
	    dbControl.setData(sql);
	    dbControl.close();
	}

	public void update(Object o) {
		//update 表名 set 字段=值1, 字段=值2 where 
		dbControl = new DBControl();
		String className = o.getClass().getName();
		TableInfo tableInfo = Constant.TABLEMAP.get(className);
		String tableName = tableInfo.getTablename();
		Id id = tableInfo.getId();
		List<Property> properties = tableInfo.getProperties();
		List<ManytoOne> manytoOnes = tableInfo.getManytoOnes();
		List<OnetoMany> onetoManies = tableInfo.getOnetoManies();
		String idName = id.getIdname();
		String idColumn = id.getIdcolumn();
		Object idVal = ReflectionUtils.getFieldValue(o, idName);
		List<Object> valueList = new ArrayList<Object>();
		List<String> columnList = new ArrayList<String>();
		if (properties != null) {
			for(Property property : properties){
				String proName = property.getPropertyname();
				String proColumn = property.getPropertycolumn();
				Object proVal = ReflectionUtils.getFieldValue(o, proName);
				if(proVal != null){
					valueList.add(proVal);
					columnList.add(proColumn);
				}
			}
		}
		//有外键，需要更新外键
		if (manytoOnes != null) {
			for(ManytoOne manytoOne : manytoOnes){
				String oneName = manytoOne.getManyname();
				String oneColumn = manytoOne.getManycolumn();
				Object oneObject = ReflectionUtils.getFieldValue(o, oneName);
				if (oneObject != null) {
					Object value = ReflectionUtils.getFieldValue(oneObject, oneColumn);
					valueList.add(value);
					columnList.add(oneColumn);
				}
			}
		}
		
		if (valueList.size() == 0) {
			System.out.println("没有更新的内容!");
			dbControl.close();
			return;
		}
		
		StringBuffer sb = new StringBuffer("update ").append(tableName).append(" set ");
		for (int i = 0; i < valueList.size(); i++) {
			Object value = valueList.get(i);
			String column = columnList.get(i);
			if (value instanceof String) {
				sb.append(column).append(" = '").append(value).append("',");
			}else {
				sb.append(column).append(" = ").append(value).append(",");
			}
			
		}
		if (idVal instanceof String) {
			sb.deleteCharAt(sb.length() - 1).append(" where ").append(idColumn).
			append("'").append(idVal).append("'");
		}else {
			sb.deleteCharAt(sb.length() - 1).append(" where ").append(idColumn).append(idVal);
		}
		
		String sql = sb.toString();
		System.out.println(sql);
		dbControl.setData(sql);
		dbControl.close();
	}

	public void delete(Object o) {
		//delete from 表名 where 
		dbControl = new DBControl();
		String className =o.getClass().getName();
		TableInfo tableInfo = Constant.TABLEMAP.get(className);
		String tableName = tableInfo.getTablename();
		Id id = tableInfo.getId();
		List<OnetoMany>onetoManies = tableInfo.getOnetoManies();
		if (onetoManies != null) {
			System.out.println("有级联关系，不可以删除！");
		}
		String idName = id.getIdname();
		String idColumn = id.getIdcolumn();
		Object idVal = ReflectionUtils.getFieldValue(o, idName);
		if (idVal != null) {
			System.out.println("没有主键，不能进行删除！");
			return;
		}
		String sql= "";
		if (idVal instanceof String) {
			sql = "delete from " + tableName + " where " + idColumn+ " = '" + idVal + "'";
		}else {
			sql = "delete from " + tableName + " where " + idColumn + " = "+ idVal;
		}
		System.out.println(sql);
		dbControl.setData(sql);
		dbControl.close();
	}

	public Object query(String cql) {
		Object object = null;
		List objList = queryForList(cql);
		if (objList != null && objList.size() > 0) {
			object = objList.get(0);
		}
		return object;
	}

	public List queryForList(String cql) {
		//select 字段1，字段2... from 表名 where ...
		dbControl = new DBControl();
		SQLInfo sqlInfo = SQLTools.getSQLInfo(cql);
		String tableName = sqlInfo.getTableName();
		Map<String, TableInfo> map = Constant.TABLEMAP;
		TableInfo tableInfo = null;
		for(Entry<String, TableInfo>entry : map.entrySet()){
			TableInfo beanTable = entry.getValue();
			if (tableName.equals(beanTable.getTablename())) {
				tableInfo = beanTable;
				break;
			}
		}
		String sql = "";
		StringBuffer sqlBuffer = new StringBuffer("select * from ");
		sqlBuffer.append(tableInfo.getTablename()).append(" ");
		List<ManytoOne> manytoOnes = tableInfo.getManytoOnes();
		List<String> manyConditionList = new ArrayList<String>();
		if (manytoOnes != null) {
			for(ManytoOne manytoOne : manytoOnes){
				String manyClassName = manytoOne.getManyclass();
				TableInfo manyTableInfo = map.get(manyClassName);
				String manyTableName = manyTableInfo.getTablename();
				sqlBuffer.append(",").append(manyTableName).append(" ");
				String manyCondition = tableInfo.getTablename() + "." + manytoOne.getManycolumn() +
						"=" + manyTableName + "." + manyTableInfo.getId().getIdcolumn();
				manyConditionList.add(manyCondition);
			}
			sqlBuffer.append(" where ");
			for (String manyCondition : manyConditionList) {
				sqlBuffer.append(manyCondition).append(" and ");
			}
			//去除最后的and
		   sql = sqlBuffer.substring(0, sqlBuffer.length() - 4);
		}
		
		List<String> operationList = sqlInfo.getOperation();
		List<String> conditionList = sqlInfo.getCondition();
		if (operationList != null) {
			StringBuffer conditonSb = new StringBuffer();
			if (!sql.contains("where")) {
				conditonSb.append(" where ");
			}else {
				conditonSb.append(" and ");
			}
			
			for (int i = 0; i < conditionList.size(); i++) {
				conditonSb.append(tableInfo.getTablename() + "." + conditionList.get(i)).append(" ");
				if (i != conditionList.size() - 1) {
				    conditonSb.append(operationList.get(i)).append(" ");
				}
			}
			sql += conditonSb.toString();
		}
		System.out.println(sql);
		ResultSet rs = dbControl.getData(sql);
		Id id = tableInfo.getId();
		List<Property> properties = tableInfo.getProperties();
		List<Object> objectList = new ArrayList<Object>();
		try {
			while(rs.next()){
				Class objClass = Class.forName(tableInfo.getClassname());
				Object object = objClass.newInstance();
				String idName = id.getIdname();
				Object idValue = rs.getObject(tableInfo.getTablename() + "." + id.getIdcolumn());
				ReflectionUtils.setFieldValue(object, idName, idValue);
				if (properties != null) {
					for(Property property : properties){
						String propertyName = property.getPropertyname();
						String propertyColumn = property.getPropertycolumn();
						Object propertyValue = rs.getObject(tableInfo.getTablename() + "." + propertyColumn);
						ReflectionUtils.setFieldValue(object, propertyName, propertyValue);
					}
				}
				if (manytoOnes != null) {
					for(ManytoOne manytoOne : manytoOnes){
						String manyClassName = manytoOne.getManyclass();
						String manyName = manytoOne.getManyname();
						TableInfo manyTableInfo = map.get(manyClassName);
						Class manyClass = Class.forName(manyClassName);
						Object manyObject = manyClass.newInstance();
						Id manyId = manyTableInfo.getId();
						String manyIdName = manyId.getIdname();
						String manyIdCloumn = manyId.getIdcolumn();
						Object manyIdValue = rs.getObject(manyTableInfo.getTablename() + "." + manyIdCloumn);
						ReflectionUtils.setFieldValue(manyObject, manyIdName, manyIdValue);
						List<Property> manyProperties = manyTableInfo.getProperties();
						if (manyProperties != null) {
							for(Property manyPro : manyProperties){
								String manyProName = manyPro.getPropertyname();
								String manyProColumn = manyPro.getPropertycolumn();
								Object manyProValue = rs.getObject(manyTableInfo.getTablename() + "." + manyProColumn);
								ReflectionUtils.setFieldValue(manyObject, manyProName, manyProValue);
							}
						}
						//设置外键对象
						ReflectionUtils.setFieldValue(object, manyName, manyObject);
					}
				}
				objectList.add(object);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return objectList;
	}
	
	public static void main(String[] args) {
		
		ScatDAO scatDAO = new ScatormDAOImpl();
//		Style style = new Style();
//		style.setStyle("文学");	
//		scatDAO.save(style);
		List<Book> books = scatDAO.queryForList("from book where bookId > 1");
		System.out.println(books.size());
	}

	public List queryForList(ScatDetachedCriteria deta) {
		dbControl = new DBControl();
		String sql = deta.getSql();
		ResultSet rs = dbControl.getData(sql);
		TableInfo tableInfo = deta.getTable();
		Id id = tableInfo.getId();
		List<Property> properties = tableInfo.getProperties();
		List<Object> objectList = new ArrayList<Object>();
		try {
			while(rs.next()){
				String className = tableInfo.getClassname();
				Object object = Class.forName(className).newInstance();
				String idName = id.getIdname();
				String idColumn = id.getIdcolumn();
				Object idValue = rs.getObject(tableInfo.getTablename() + "." + idColumn);
				ReflectionUtils.setFieldValue(object, idName, idValue);
				if (properties != null) {
					for(Property property : properties){
						String proName = property.getPropertyname();
						String ProCloumn = property.getPropertycolumn();
						Object proValue = rs.getObject(tableInfo.getTablename() + "." + ProCloumn);
						ReflectionUtils.setFieldValue(object, proName, proValue);
					}
				}
				objectList.add(object);
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objectList;
	}

	/**
	 * 从缓存中查询
	 */
	public Object queryFromCache(String name, String cql, long time) {
		Object result = null;
		CacheFactory cf = new CacheFactory();
		result = cf.getFromCache(name);
		if (result == null) {
			List objectList = queryForList(cql);
			//把查询结果放到缓存中
			cf.addCache(name, objectList, time);
			
			result = cf.getFromCache(name);
		}
		return result;
	}

	
}

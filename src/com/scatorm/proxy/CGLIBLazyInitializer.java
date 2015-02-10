package com.scatorm.proxy;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.sf.cglib.proxy.MethodProxy;

import com.scatorm.datasource.DBControl;
import com.scatorm.sqltools.SQLTools;
import com.scatorm.sqltools.ScatDetachedCriteria;
import com.scatorm.tableinfo.Id;
import com.scatorm.tableinfo.ManytoOne;
import com.scatorm.tableinfo.Property;
import com.scatorm.tableinfo.TableInfo;
import com.scatorm.tools.Constant;
import com.scatorm.tools.ReflectionUtils;

public class CGLIBLazyInitializer implements LazyInitializer {

	private Class c;
	private Serializable id;
	private ScatDetachedCriteria deta;
	private String condition;
	private Object object;
	private DBControl dbControl;
	private List<ManytoOne> manytoOnes;
	public Object intercept(Object object, Method method, Object[] args,
			MethodProxy proxy) throws Throwable {
		//首先判断是对哪个方法进行拦截
		String methodName = method.getName();
		methodName = methodName.substring(3).substring(0, 1).toLowerCase() + methodName.substring(4);
		Object proxyObject = object.getClass().newInstance();
		if (null == this.object) {
			setValue();
		}
		Object result = null;
		
		if (manytoOnes != null) {
			for(ManytoOne manytoOne : manytoOnes){
				if (manytoOne.getManyname().equals(methodName)) {
					setManyValue(proxyObject, manytoOne.getManyclass(), methodName);
					result = proxy.invokeSuper(proxyObject, args);
				}else
					result = ReflectionUtils.getFieldValue(this.object, methodName);
			}
		}else
			result = ReflectionUtils.getFieldValue(this.object, methodName);
		
		return result;
	}
	private void setManyList() {
		if (null == dbControl) {
			dbControl = new DBControl();
		}
		manytoOnes = Constant.TABLEMAP.get(c.getName()).getManytoOnes();
	}
	public void load(Class c, Serializable id) {
		this.c = c;
		this.id = id;
		setManyList();
	}

	public void load(Class c, ScatDetachedCriteria deta) {
		this.c = c;
		this.deta = deta;
		setManyList();
	}

	public void load(Class c, String condition) {
		this.c = c;
		this.condition = condition;
		setManyList();
	}

	private void setValue(){
		dbControl = new DBControl();
		TableInfo tableInfo = Constant.TABLEMAP.get(c.getName());
		String sqlString = "select * from " + tableInfo.getTablename() + " where ";
		if (id != null) {
			sqlString += tableInfo.getId().getIdcolumn() + " = " + id;
		}else if (deta != null) {
			sqlString = deta.getSql();
		}else if (condition != null) {
			sqlString += condition;
		}
		System.out.println(sqlString);
		Id id = tableInfo.getId();
		List<Property> properties = tableInfo.getProperties();
		List<ManytoOne> manytoOnes = tableInfo.getManytoOnes();
		ResultSet rs = dbControl.getData(sqlString);
		try {
			while(rs.next()){
				String className = c.getName();
				Object object = Class.forName(className).newInstance();
				String idName = id.getIdname();
				String idColumn = id.getIdcolumn();
				Object idValue = rs.getObject(tableInfo.getTablename() + "." + idColumn);
				ReflectionUtils.setFieldValue(object, idName, idValue);
				if (properties != null) {
					for(Property property : properties){
						String proName = property.getPropertyname();
						String proColumn = property.getPropertycolumn();
						Object proValue = rs.getObject(tableInfo.getTablename() + "." + proColumn);
						ReflectionUtils.setFieldValue(object, proName, proValue);
					}
				}
				if (manytoOnes != null) {
					for(ManytoOne manytoOne : manytoOnes){
						String manyName =manytoOne.getManyname();
						String manyColumn = manytoOne.getManycolumn();
						String manyClassName = manytoOne.getManyclass();
						Object manyObject = Class.forName(manyClassName).newInstance();
						Object manyIdValue = rs.getObject(tableInfo.getTablename() + "." + manyColumn);
						//设置Styleid到Style对象
						ReflectionUtils.setFieldValue(manyObject, manyColumn, manyIdValue);
						//设置Style对象到Object对象中
						ReflectionUtils.setFieldValue(object, manyName, manyObject);
						
					}
				}
				this.object = object;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			dbControl.close();
		}
	}

	/**
	 * 二次查询，设置外键的值
	 * @param obj
	 * @param manyClassName
	 * @param methodName
	 */
	private void setManyValue(Object obj, String manyClassName, String methodName){
		dbControl = new DBControl();
		TableInfo tableInfo = Constant.TABLEMAP.get(manyClassName);
		Id id = tableInfo.getId();
		List<Property> properties = tableInfo.getProperties();
		//获取Style对象
		Object manyValue = ReflectionUtils.getFieldValue(this.object, methodName);
		//获取StyleId
		Object manyIdValue = ReflectionUtils.getFieldValue(manyValue, id.getIdname());
		String sql = "select * from "+ tableInfo.getTablename() + " where " + id.getIdcolumn() + " = " + manyIdValue;
		ResultSet rs = dbControl.getData(sql);
		try {
			while(rs.next()){
				Object manyObject = Class.forName(manyClassName).newInstance();
				String idName = id.getIdname();
				ReflectionUtils.setFieldValue(manyObject, idName, manyIdValue);
				if (properties != null) {
					for(Property property : properties){
						String proName = property.getPropertyname();
						String proColumn = property.getPropertycolumn();
						Object proValue = rs.getObject(tableInfo.getTablename() + "." + proColumn);
						ReflectionUtils.setFieldValue(manyObject, proName, proValue);
					}
				}
				//将外键注入原来对象
				ReflectionUtils.setFieldValue(obj, methodName, manyObject);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			dbControl.close();
		}
	}
}

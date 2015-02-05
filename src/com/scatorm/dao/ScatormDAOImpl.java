package com.scatorm.dao;

import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.List;

import com.scatorm.beans.Book;
import com.scatorm.beans.Style;
import com.scatorm.datasource.DBControl;
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
		//������䣺insert into ����(�ֶ�, �ֶ�) Values(ֵ1, ֵ2) 
		dbControl = new DBControl();
		String className = o.getClass().getName();
	
		TableInfo tableInfo = Constant.TABLEMAP.get(className);
		String tableName = tableInfo.getTablename();
		Id id = tableInfo.getId();
		List<Property> properties = tableInfo.getProperties();
		List<ManytoOne> manytoOnes = tableInfo.getManytoOnes();
		List<OnetoMany> onetoManies = tableInfo.getOnetoManies();
		//��������
		String idName = id.getIdname();
		String idCol = id.getIdcolumn();
		Object idVal = ReflectionUtils.getFieldValue(o, idName);
		//ƴsql���
		StringBuffer sb = new StringBuffer("insert into ");
		sb.append(tableName).append("(");
		//false������
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
		//���������Ҫ���
		if (manytoOnes != null) {
			for(ManytoOne manytoOne : manytoOnes){
				String oneName = manytoOne.getManyname();
				String oneCol = manytoOne.getManycolumn();
				String oneClassName = manytoOne.getManyclass();
				Object oneObject = ReflectionUtils.getFieldValue(o, oneName);
				Object val = ReflectionUtils.getFieldValue(oneObject, oneCol);
				//�����⣬oneColӦ�ö�Ӧ����Manytoone ��class���ֶΣ����������ݿ����ֶ�
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
		//update ���� set �ֶ�=ֵ1, �ֶ�=ֵ2 where 
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
		//���������Ҫ�������
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
			System.out.println("û�и��µ�����!");
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
		//delete from ���� where 
		dbControl = new DBControl();
		String className =o.getClass().getName();
		TableInfo tableInfo = Constant.TABLEMAP.get(className);
		String tableName = tableInfo.getTablename();
		Id id = tableInfo.getId();
		List<OnetoMany>onetoManies = tableInfo.getOnetoManies();
		if (onetoManies != null) {
			System.out.println("�м�����ϵ��������ɾ����");
		}
		String idName = id.getIdname();
		String idColumn = id.getIdcolumn();
		Object idVal = ReflectionUtils.getFieldValue(o, idName);
		if (idVal != null) {
			System.out.println("û�����������ܽ���ɾ����");
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
		// TODO Auto-generated method stub
		return null;
	}

	public List queryForList(String cql) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String[] args) {
		Style style = new Style();
		style.setStyle("��ѧ");
		ScatDAO scatDAO = new ScatormDAOImpl();
		scatDAO.save(style);
	}
}

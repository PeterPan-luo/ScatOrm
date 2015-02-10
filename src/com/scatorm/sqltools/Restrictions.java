package com.scatorm.sqltools;

import javax.xml.validation.Validator;

/**
 * Æ´½ÓÌõ¼þ
 * @author Administrator
 *
 */
public class Restrictions {

	private Restrictions restrictions = null;
	private String condition;
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	public Restrictions eq(String column, Object value) {
		String condition = "";
		if (value instanceof String) {
			condition = column + "='" + value + "'";
		}else {
			condition = column + "=" + value;
		}
		restrictions = new Restrictions();
		restrictions.setCondition(condition);
		return restrictions;
	}
	
	public Restrictions noteq(String column, Object value) {
		String condition ="";
		if (value instanceof String) {
			condition = column + "<>'" + value + "'";
		}else {
			condition = column + "<>" + value;
		}
		restrictions = new Restrictions();
		restrictions.setCondition(condition);
		return restrictions;
	}
	
	public  Restrictions lt(String column, Object value) {
		String condition = "";
		if (value instanceof String) {
			condition = column + "<'" + value + "'";
 		}else {
			condition = column + "<" + value;
		}
		restrictions = new Restrictions();
		restrictions.setCondition(condition);
		return restrictions;
	}
	
	public  Restrictions le(String column, Object value) {
		String condition = "";
		if (value instanceof String) {
			condition = column + "<='" + value + "'";
 		}else {
			condition = column + "<=" + value;
		}
		restrictions = new Restrictions();
		restrictions.setCondition(condition);
		return restrictions;
	}
	
	public  Restrictions gt(String column, Object value) {
		String condition = "";
		if (value instanceof String) {
			condition = column + ">'" + value + "'";
 		}else {
			condition = column + ">" + value;
		}
		restrictions = new Restrictions();
		restrictions.setCondition(condition);
		return restrictions;
	}
	
	public  Restrictions ge(String column, Object value) {
		String condition = "";
		if (value instanceof String) {
			condition = column + ">='" + value + "'";
 		}else {
			condition = column + ">=" + value;
		}
		restrictions = new Restrictions();
		restrictions.setCondition(condition);
		return restrictions;
	}
	
	public  Restrictions in(String column, Object[] values) {
		String condition = column + " in (";
		for (int i = 0; i < values.length; i++) {
			Object value = values[i];
			if (value instanceof String) {
				condition += "'" + value + "',";
			}else {
				condition += value + ",";
			}
		}
		condition = condition.substring(0, condition.length() - 1) + ")";
		restrictions = new Restrictions();
		restrictions.setCondition(condition);
		return restrictions;
	}
	
	public  Restrictions like(String columnn, Object value) {
		String condition = "";
		if (value instanceof String) {
			condition = columnn + " like '%" + value + "%'";
		}else {
			condition = columnn + " like %" + value + "%";
		}
		restrictions = new Restrictions();
		restrictions.setCondition(condition);
		return restrictions;
	}
	
	public Restrictions between(String column, Object value1, Object value2) {
		String conditionString = column + " between ";
		if (value1 instanceof String) {
			conditionString += "'" + value1 + "'" + " and '" + value2 + "'";
		}else {
			conditionString += value1 + " and " + value2;
		}
		restrictions = new Restrictions();
		restrictions.setCondition(conditionString);
		return restrictions;
	}
	
	public static Restrictions and(Restrictions restrictions1, Restrictions restrictions2) {
		String condition = restrictions1.getCondition() + " and " + restrictions2.getCondition();
		Restrictions restrictions = new Restrictions();
		restrictions.setCondition(condition);
		return restrictions;
	}
	
	public static Restrictions or(Restrictions restrictions1,Restrictions restrictions2) {
		String condition = restrictions1.getCondition() + " or " + restrictions2.getCondition();
		Restrictions restrictions = new Restrictions();
		restrictions.setCondition(condition);
		return restrictions;
	}
	
	public static void main(String[] args) {
		Restrictions restrictions = new Restrictions();
		Restrictions result = Restrictions.or(Restrictions.and(restrictions.eq("bookname", "java"), restrictions.eq("bookid", 1)),
				restrictions.like("author", "java"));
		System.out.println(result.getCondition());
	}
}

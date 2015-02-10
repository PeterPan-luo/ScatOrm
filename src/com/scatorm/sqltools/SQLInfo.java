package com.scatorm.sqltools;

import java.util.List;

public class SQLInfo {

	private String tableName;
	private String selectValue;
	private List<String> condition;
	private List<String> operation;
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getSelectValue() {
		return selectValue;
	}
	public void setSelectValue(String selectValue) {
		this.selectValue = selectValue;
	}
	public List<String> getCondition() {
		return condition;
	}
	public void setCondition(List<String> condition) {
		this.condition = condition;
	}
	public List<String> getOperation() {
		return operation;
	}
	public void setOperation(List<String> operation) {
		this.operation = operation;
	}
	
}

package com.scatorm.tableinfo;

import java.util.List;

public class TableInfo {
	private String classname;
	private String tablename;
	private Id id;
	private List<Property> properties;
    private List<OnetoMany> onetoManies;
    private List<ManytoOne> manytoOnes;
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public String getTablename() {
		return tablename;
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	public Id getId() {
		return id;
	}
	public void setId(Id id) {
		this.id = id;
	}
	public List<Property> getProperties() {
		return properties;
	}
	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}
	public List<OnetoMany> getOnetoManies() {
		return onetoManies;
	}
	public void setOnetoManies(List<OnetoMany> onetoManies) {
		this.onetoManies = onetoManies;
	}
	public List<ManytoOne> getManytoOnes() {
		return manytoOnes;
	}
	public void setManytoOnes(List<ManytoOne> manytoOnes) {
		this.manytoOnes = manytoOnes;
	}
    
}

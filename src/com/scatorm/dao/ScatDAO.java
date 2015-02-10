package com.scatorm.dao;

import java.util.List;

import com.scatorm.sqltools.ScatDetachedCriteria;

public interface ScatDAO {

	public void save(Object o);
	public void update(Object o);
	public void delete(Object o);
	
	public Object query(String cql);
	
	public List queryForList(String cql);
	
	public List queryForList(ScatDetachedCriteria deta);
	
	public Object queryFromCache(String name, String cql, long time);
}

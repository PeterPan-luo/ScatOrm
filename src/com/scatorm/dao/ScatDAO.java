package com.scatorm.dao;

import java.util.List;

public interface ScatDAO {

	public void save(Object o);
	public void update(Object o);
	public void delete(Object o);
	
	public Object query(String cql);
	
	public List queryForList(String cql);
}

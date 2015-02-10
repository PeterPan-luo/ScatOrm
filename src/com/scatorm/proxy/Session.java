package com.scatorm.proxy;

import java.io.Serializable;

import com.scatorm.sqltools.ScatDetachedCriteria;

public interface Session {

	public Object load(Class c, Serializable id);
	public Object load(Class c, ScatDetachedCriteria deta);
	public Object load(Class c, String condition);
}

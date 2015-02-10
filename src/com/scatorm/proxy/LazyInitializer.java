package com.scatorm.proxy;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.scatorm.sqltools.ScatDetachedCriteria;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public interface LazyInitializer extends MethodInterceptor{

	public void load(Class c, Serializable id);
	
	public void load(Class c, ScatDetachedCriteria deta);
	
	public void load(Class c, String condition);
}

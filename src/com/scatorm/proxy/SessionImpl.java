package com.scatorm.proxy;

import java.io.Serializable;

import net.sf.cglib.proxy.Enhancer;

import com.scatorm.beans.Book;
import com.scatorm.beans.Style;
import com.scatorm.sqltools.ScatDetachedCriteria;

public class SessionImpl implements Session {

	public Object load(Class c, Serializable id) {
		LazyInitializer lazy = new CGLIBLazyInitializer();
		lazy.load(c, id);
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(c);
		enhancer.setCallback(lazy);
		return enhancer.create();
	}

	public Object load(Class c, ScatDetachedCriteria deta) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object load(Class c, String condition) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String[] args) {
		Session session = new SessionImpl();
		Book book = (Book) session.load(Book.class, 3);
		System.out.println(book.getBookname() + " " + book.getBookId());
		Style style = book.getStyle();
		System.out.println(style.getStyle() + " " + style.getStyleId());
	}
}

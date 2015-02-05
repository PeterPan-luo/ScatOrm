package com.scatorm.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.scatorm.tableinfo.Id;
import com.scatorm.tableinfo.ManytoOne;
import com.scatorm.tableinfo.OnetoMany;
import com.scatorm.tableinfo.Property;
import com.scatorm.tableinfo.TableInfo;
import com.scatorm.tools.Constant;
import com.scatorm.tools.XMLFactory;

//读取配置文件
public class XMLFactory {

	private static final String Element = null;

	public static Map<String, String> getXMLInfo(String fileName) {
		Map<String,String> jdbcMap = new HashMap<String, String>();
		
		File file = new File(fileName);
		SAXReader reader = new SAXReader();
		Document document;
		try {
			document = reader.read(file);
			Element rootElement = document.getRootElement();
			Element datasourceEle = rootElement.element("datasource");
			Element jdbcEle = datasourceEle.element("jdbc");
			List<Element> propertys =  jdbcEle.elements();
			for(Element property : propertys){
				Attribute nameAttr = property.attribute("name");
				Attribute valueAttr = property.attribute("value");
				if (valueAttr != null) {
					jdbcMap.put(nameAttr.getText(), valueAttr.getText());
				}else {
					jdbcMap.put(nameAttr.getText(), property.getText());
				}
			}
			Constant.DBMAP = jdbcMap;
			ReadBeanXML(rootElement);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jdbcMap;
	}
	
	private static void ReadBeanXML(Element rootElement) {
		// TODO Auto-generated method stub
		Element ormMappingEle = rootElement.element("orm-mapping");
		Element listEle = ormMappingEle.element("list");
		List<Element> beanElements = listEle.elements();
		List<String> fileList = new ArrayList<String>();
		for(Element element : beanElements){
			fileList.add(element.getText().trim());
		}
		readTableXMLInfo(fileList);
	}
	private static void readTableXMLInfo(List<String>fileList) {
		Constant.TABLEMAP = TableInfoXMLUtils.getTableInfoXML(fileList);
	}

	public static void main(String[] args) {
		getXMLInfo(XMLFactory.class.getResource("/scatorm.datasource.xml").getFile());
		Map<String, TableInfo> map=Constant.TABLEMAP;
		Set<String> set=map.keySet();
		Iterator<String> it=set.iterator();
		while(it.hasNext()){
			String key=it.next();
			TableInfo table=map.get(key);
			System.out.println("配置文件的信息");
			System.out.println("classname:"+table.getClassname()+" tablename:"+table.getTablename());
			Id id=table.getId();
			System.out.println("主键的信息: idname:"+id.getIdname()+" idcolumn:"+id.getIdcolumn());
			List<Property> plist=table.getProperties();
			if(plist!=null){
				System.out.println("属性标签的内容");
				for(Property p:plist){
					System.out.println("pname:"+p.getPropertyname()+" pcolumn:"+p.getPropertycolumn());
				}
			}
			List<OnetoMany> onelist=table.getOnetoManies();
			if(onelist!=null){
				System.out.println("一对多");
				for(OnetoMany one:onelist){
					System.out.println("onename:"+one.getOnename()+" oneclass:"+one.getOneclass()+" onecolumn:"+one.getOnecolumn());
				}
			}
			List<ManytoOne> manylist=table.getManytoOnes();
			if(manylist!=null){
				System.out.println("多对一");
				for(ManytoOne many:manylist){
					System.out.println("manyname:"+many.getManyname()+" manyclass:"+many.getManyclass()+" manycolumn:"+many.getManycolumn());
				}
			}
			System.out.println();
		}
	}
}

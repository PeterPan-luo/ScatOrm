package com.scatorm.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

//读取所有实体映射文件的工具类
public class TableInfoXMLUtils {

	public static Map<String, TableInfo> getTableInfoXML(List<String> fileList) {
		Map<String, TableInfo> tableMap = new HashMap<String, TableInfo>();
		for(String fileName:fileList){
			File file = new File(TableInfoXMLUtils.class.getResource(fileName).getFile());
			SAXReader reader = new SAXReader();
			TableInfo tableInfo = new TableInfo();
			Id id = null;
			List<Property> properties = null;
			List<ManytoOne> manytoOnes = null;
			List<OnetoMany> onetoManies = null;
			try {
				Document doc = reader.read(file);
				Element rootEle = doc.getRootElement();
				tableInfo.setClassname(rootEle.attributeValue("name"));
				tableInfo.setTablename(rootEle.attributeValue("table"));
				List<Element> eList = rootEle.elements();
				for(Element element : eList){
					if (element.getName().equals("Id")) {
						id = new Id();
						id.setIdname(element.attributeValue("name"));
						id.setIdcolumn(element.attributeValue("column"));
						Attribute identityAttr = element.attribute("identity");
						
						if (identityAttr != null) {
							id.setIdentity(Boolean.parseBoolean(identityAttr.getValue()));
						}
					}else if (element.getName().equals("property")) {
						if(null == properties)
							properties = new ArrayList<Property>();
						Property property = new Property();
						property.setPropertyname(element.attributeValue("name"));
						property.setPropertycolumn(element.attributeValue("column"));
						Attribute defaultValueAttr = element.attribute("default");
						if (defaultValueAttr != null) {
							property.setDefaultValue(defaultValueAttr.getValue());
						}
						properties.add(property);
					}else if (element.getName().equals("manytoone")) {
						if(null == manytoOnes)
							manytoOnes = new ArrayList<ManytoOne>();
						ManytoOne manytoOne = new ManytoOne();
						manytoOne.setManyname(element.attributeValue("name"));
						manytoOne.setManyclass(element.attributeValue("class"));
						manytoOne.setManycolumn(element.element("column").attributeValue("name"));
						manytoOnes.add(manytoOne);
					}else if (element.getName().equals("onetomany")) {
						if(null == onetoManies)
							onetoManies = new ArrayList<OnetoMany>();
						OnetoMany onetoMany = new OnetoMany();
						onetoMany.setOnename(element.attributeValue("name"));
						onetoMany.setOneclass(element.attributeValue("class"));
						onetoMany.setOnecolumn(element.element("column").attributeValue("name"));
						onetoManies.add(onetoMany);
					}
					
				}
				tableInfo.setId(id);
				tableInfo.setProperties(properties);
				tableInfo.setManytoOnes(manytoOnes);
				tableInfo.setOnetoManies(onetoManies);
				tableMap.put(tableInfo.getClassname(), tableInfo);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return tableMap;
	}
}

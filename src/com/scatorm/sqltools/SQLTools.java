package com.scatorm.sqltools;

import java.util.ArrayList;
import java.util.List;

public class SQLTools {

	public static SQLInfo getSQLInfo(String cql){
		List<String> operationList = new ArrayList<String>();
		operationList.add("and");
		operationList.add("or");
		operationList.add("in");
		SQLInfo sqlInfo = new SQLInfo();
		cql = cql.replaceAll("\\s{1,}", " ");
		String[] cqls = cql.split("where");
	
		String preCondition = cqls[0].trim();
		String [] preConditions = preCondition.split(" ");
		List<String> conditonList = null;
		List<String> operation = null;
		for (int i = 0; i < preConditions.length; i++) {
			if (preConditions[i].equalsIgnoreCase("from")) {
				sqlInfo.setTableName(preConditions[i + 1]);
				break;
			}
		}
		//ÓÐwhereÌõ¼þ
		if (cqls.length > 1) {
			conditonList = new ArrayList<String>();
			operation = new ArrayList<String>();
			String condition = cqls[1].trim();
			String[] conditions = condition.split(" ");
			String c = " ";
			for (int i = 0; i < conditions.length; i++) {
				if (operationList.contains(conditions[i])) {
					operation.add(conditions[i]);
					conditonList.add(c.trim());
					c = " ";
				}else {
					c = c + conditions[i]+ " ";
				}
			}
			conditonList.add(c.trim());
		}
		sqlInfo.setCondition(conditonList);
		sqlInfo.setOperation(operation);
		return sqlInfo;
	}
	
	public static void main(String[] args) {
		String cqlString =" from    book  where bookid = 1";
		getSQLInfo(cqlString);
	}
}

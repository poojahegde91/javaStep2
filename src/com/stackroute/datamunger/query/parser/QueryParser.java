package com.stackroute.datamunger.query.parser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.stackroute.datamunger.DataMunger;

public class QueryParser extends DataMunger{

	
	private QueryParameter queryParameter = new QueryParameter();
	
	/*
	 * this method will parse the queryString and will return the object of
	 * QueryParameter class
	 */
	public QueryParameter parseQuery(String queryString) {
		
		/*
		 * extract the name of the file from the query. File name can be found after the
		 * "from" clause.
		 */
		queryParameter.setFile(getFile(queryString));
		
		
		
		/*
		 * extract the order by fields from the query string. Please note that we will
		 * need to extract the field(s) after "order by" clause in the query, if at all
		 * the order by clause exists. For eg: select city,winner,team1,team2 from
		 * data/ipl.csv order by city from the query mentioned above, we need to extract
		 * "city". Please note that we can have more than one order by fields.
		 */
		
		queryParameter.setOrderByFields(getOrderByFields(queryString));
		
		/*
		 * extract the group by fields from the query string. Please note that we will
		 * need to extract the field(s) after "group by" clause in the query, if at all
		 * the group by clause exists. For eg: select city,max(win_by_runs) from
		 * data/ipl.csv group by city from the query mentioned above, we need to extract
		 * "city". Please note that we can have more than one group by fields.
		 */
		
		queryParameter.setGroupByFields(getGroupByFields(queryString));
		
		/*
		 * extract the selected fields from the query string. Please note that we will
		 * need to extract the field(s) after "select" clause followed by a space from
		 * the query string. For eg: select city,win_by_runs from data/ipl.csv from the
		 * query mentioned above, we need to extract "city" and "win_by_runs". Please
		 * note that we might have a field containing name "from_date" or "from_hrs".
		 * Hence, consider this while parsing.
		 */
		queryParameter.setFields(getFields(queryString));
		
		/*
		 * extract the conditions from the query string(if exists). for each condition,
		 * we need to capture the following: 
		 * 1. Name of field 
		 * 2. condition 
		 * 3. value
		 * 
		 * For eg: select city,winner,team1,team2,player_of_match from data/ipl.csv
		 * where season >= 2008 or toss_decision != bat
		 * 
		 * here, for the first condition, "season>=2008" we need to capture: 
		 * 1. Name of field: season 
		 * 2. condition: >= 
		 * 3. value: 2008
		 * 
		 * the query might contain multiple conditions separated by OR/AND operators.
		 * Please consider this while parsing the conditions.
		 * 
		 */
		queryParameter.setRestrictions(getcondtionalfunctions(queryString));
		
		/*
		 * extract the logical operators(AND/OR) from the query, if at all it is
		 * present. For eg: select city,winner,team1,team2,player_of_match from
		 * data/ipl.csv where season >= 2008 or toss_decision != bat and city =
		 * bangalore
		 * 
		 * the query mentioned above in the example should return a List of Strings
		 * containing [or,and]
		 */
		
		queryParameter.setLogicalOperators(getLogicalOperators(queryString));
		
		/*
		 * extract the aggregate functions from the query. The presence of the aggregate
		 * functions can determined if we have either "min" or "max" or "sum" or "count"
		 * or "avg" followed by opening braces"(" after "select" clause in the query
		 * string. in case it is present, then we will have to extract the same. For
		 * each aggregate functions, we need to know the following: 
		 * 1. type of aggregate function(min/max/count/sum/avg) 
		 * 2. field on which the aggregate function is being applied
		 * 
		 * Please note that more than one aggregate function can be present in a query
		 * 
		 * 
		 */
		
		queryParameter.setAggregateFunctions(getAggregateFunctions(queryString));
		
		return queryParameter;
	}
	
	
	// get and display the filename
			public String getFile(String queryString) {
				
				String filename = queryString.split("from")[1].trim().split(" ")[0];
				
				return filename;
			}
			
			// getting the baseQuery and display
			public String getBaseQuery(String queryString) {
				
				if(queryString.contains("where"))
				{
					String basequery = queryString.split("where")[0];
					return basequery;
				}
				else if(queryString.contains("order by"))
				{
					String basequery1 = queryString.split("order by")[0];
					return basequery1;
				}
				else if(queryString.contains("group by"))
				{
					String basequery2 = queryString.split("group by")[0];
					return basequery2;
				}
				
				return queryString;

			}
			
			// get and display the where conditions part(if where condition exists)
			public String getConditionsPartQuery(String queryString) {
				
				if(queryString.contains("where"))
				{
					String cpq = queryString.split("where")[1].toLowerCase();
					if(cpq.contains("group by"))
					{
						String cpq1 = cpq.split("group by")[0];
						return cpq1;
					}else if(cpq.contains("order by"))
					{
						String cpq3 = cpq.split("order by")[0];
						return cpq3;
					}else
					
						return cpq;
				}else
			
				return null;

			}
			
			/* parse the where conditions and display the propertyName, propertyValue and
			 conditionalOperator for each conditions*/
			public String[] getConditions(String queryString) {
				
				String[] finalArray=null;
				if(queryString.contains("where"))
				{
					String cd1 =  queryString.split("where")[1].trim().toLowerCase();
					if(cd1.contains("group by") || cd1.contains("order by"))
					{
						String cd2 = cd1.split("order by | group by")[0].trim();
						if(cd2.contains("and") || cd2.contains("or")|| cd2.contains("not"))
						{
							finalArray = cd2.split("and | or | not");
							String [] finalArray1=new String[finalArray.length];
							int count=0;
							for(String a :finalArray)
							{
								finalArray1[count]=a.trim();
								count++;
							}
							return finalArray1;
						}
					}
					else
					{
										
						finalArray = cd1.split("and | or | not");
						String [] finalArray1=new String[finalArray.length];
						int count=0;
						for(String a :finalArray)
						{
							finalArray1[count]=a.trim();
							count++;
						}
						return finalArray1;
							
					}
					
				}
				
				return finalArray; 
				
				
			}
			
			// get the logical operators(applicable only if multiple conditions exist)
			public ArrayList<String> getLogicalOperators(String queryString) {
					
				if((queryString.contains("and"))  || (queryString.contains("or")) || (queryString.contains("not")))
				{
					String [] logicalOperators=queryString.split("\\s+");
					ArrayList<String> logicalOperators1 = new ArrayList<String>();
					for (String a  : logicalOperators) 
			        {
			             if(a.equals("and")  || a.equals("or") || a.equals("not"))
			             {
			            	 logicalOperators1.add(a);
			             }
			        }
					return logicalOperators1;
				}
				else
				{
					return null;
				}
				
					
			}
			
			/*get the fields from the select clause*/
			public ArrayList<String> getFields(String queryString) {
				
				String [] fields = queryString.split("from")[0].split("select")[1].trim().split(",");
							
				ArrayList<String> fields1 = new ArrayList<>();
				for (String temp : fields) 
				{
					fields1.add(temp.trim());
				}
				
				return fields1;
				
			}
			// get order by fields if order by clause exists
			public ArrayList<String> getOrderByFields(String queryString) {
				
				if(queryString.contains("order by"))
				{
					String [] orderby = queryString.split("order by")[1].trim().split("\\s");
					ArrayList<String> orderby1 = new ArrayList<>();
					for (String temp : orderby )
					{
						orderby1.add(temp.trim());
					}
				
					return orderby1;
				}
				else 
					return null;
			}
			
			// get group by fields if group by clause exists
			public ArrayList<String> getGroupByFields(String queryString) {			
				
				if(queryString.contains("group by"))
				{
					if(queryString.contains("order by"))
					{
						String [] groupby = queryString.split("group by")[1].trim().split("order by")[0].split("\\s");
						ArrayList<String> groupby1 = new ArrayList<>();
						for (String temp : groupby )
						{
							groupby1.add(temp.trim());
						}
					
						return groupby1;
					}
					else
					{
					String [] groupby = queryString.split("group by")[1].trim().split("\\s");
					ArrayList<String> groupby1 = new ArrayList<>();
					for (String temp : groupby )
					{
						groupby1.add(temp.trim());
					}
				
					return groupby1;
					}
				}
				else 
					return null;
			
				
			}
			
			
			
			
			// parse and display aggregate functions(if applicable)
			
			
					public ArrayList<AggregateFunction> getAggregateFunctions(String queryString) {
						AggregateFunction Aggregate = new AggregateFunction();
						String query = queryString.toLowerCase();
						String temp[] = null;
						String[] aggregateFunctions = new String[] { "min", "max", "count", "avg", "sum" };
						ArrayList<AggregateFunction> list = new ArrayList<AggregateFunction>();
						temp = query.split("from ")[0].trim().split("select")[1].trim().split(",");
						for (String s : aggregateFunctions) {
							for (String i : temp) {
								if (i.contains(s)) {
									Aggregate.setFunction(s.trim());
									Aggregate.setField(i.substring(i.indexOf("(") + 1, i.indexOf(")")).trim());
									list.add(Aggregate);
								}
									
							}
						}
						
						return list;
					}
				
					
					//restrictions
					
					public List<Restriction> getcondtionalfunctions(String queryString) {
//						String query = queryString.toLowerCase();
						String query = queryString;
						String[] matches = new String[] { ">", "<", "=", "!=", "<=", ">=", "<>", "is ", "like ", "in ",};
						String[] conditionsArray = null;
						boolean flag = false;
						List<Restriction> list = new ArrayList<>();
						if (query.contains("where ")) {
							if (query.contains("group by")) {
								query = query.split("group by")[0].trim();
							}
							if (query.contains("order by")) {
								query = query.split("order by")[0].trim();
								if (query.contains("group by")) {
									query = query.split("group by")[0].trim();
								}
							}
							conditionsArray = query.split("where ")[1].trim().split("and |or |not");
							
							for (String k : conditionsArray) {
								for (String s : matches) {
									if (k.contains(s)) {
										Restriction Restrict = new Restriction();
										Restrict.setPropertyName(k.substring(0, k.indexOf(s)));
										Restrict.setCondition(s);
										Restrict.setPropertyValue(k.substring(k.indexOf(s) + 1, k.length()));
										list.add(Restrict);
										flag=true;
									}
								}
							}
						}
						if (flag == true)
						{
						return list;
						}
						else 
						{
							return null;
						}
					}
					

	
	
}

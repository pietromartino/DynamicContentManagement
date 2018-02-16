import java.util.ArrayList;
import java.util.List;

import download.Query;
import download.WebService;
import parsers.ParseResultsForWS;
import parsers.WebServiceDescription;

public class QueryEngine {

	private static String queryFormat = "webservicename1(\"input1\", ?out1, ?out2, ?out3)" +
										"#webservicename2(?query1output, ?out4, ?out5, ?out6)" +
										"#webservicename3(?query2output, ?out7, ?out8) etc....\n" + 
										"Please note that initial input should be delimited by \" \"" +
										" and outputs should be preceded by a ?.";
	
	//Workflow -> Users put into the query engine xml and xsl file (that they wrote?), then they can
	//				play around with loaded webservices as much as they like
	
	/* We have two levels, query level and ws level ? 
	 * i. e: for ws that have multiple io parameters,  
	 */
	
	/*
	 * Shortcomings : 
	 * 1. If I have two params in query1, I don't have any mean to know which will
	 * 		be the input of query2 if not for ordering
	 */
	
	public static void executeQuery(String query){
		
		List<Query> queries = parse(query);
		System.out.println("--- QUERIES PARSED ---");
		for ( int i = 0; i < queries.size(); i++) {
			System.out.println((i+1) + ". " + queries.get(i));			
		}
		validate(queries);
		System.out.println();
		System.out.println("--- QUERIES VALIDATED ---");
		System.out.println();
		
		for (int i = 0; i < queries.size(); i++) {
			Query q = queries.get(i);
			
			System.out.println("--- QUERY " + (i+1) + " WEBSERVICE DESCRIPTION ---");
			WebService ws = WebServiceDescription.loadDescription(q.getWsName());
		    
			List<String> inputs;
			if ( i == 0 ) {
				inputs = new ArrayList<>();
				inputs.add(q.getInput());
			} else {
				Query prev = queries.get(i-1);
				inputs = prev.getResults(prev.matchParameters(q.getParameters()));
			}
			
			for ( String input : inputs) {
				String fileWithCallResult;
			    fileWithCallResult = ws.getCallResult(input);				
				System.out.println("QUERY " + (i+1) + " - CALL PATH: "+fileWithCallResult);
				String fileWithTransfResults;
				
				try {
					fileWithTransfResults = ws.getTransformationResult(fileWithCallResult);
					ArrayList<String[]>  listOfTupleResult= ParseResultsForWS.showResults(fileWithTransfResults, ws);
					
					System.out.println(" --- QUERY " + (i+1) + " " + q.getGlobalString() + "  RESULTS ---");
					for(String [] tuple:listOfTupleResult){
						System.out.print("( ");
					 	for(String t:tuple){
					 		System.out.print(t+", ");
					 	}
					 	System.out.print(") ");
					 	System.out.println();
					}
					q.addResults(listOfTupleResult);	
					
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
		}
	}
	
	public static List<Query> parse(String globalQuery) {
		//Splits into single queries
		String[] singleQueries = globalQuery.trim().split("#");
		
		List<Query> res = new ArrayList<>();
		try {
			for ( int i = 0; i < singleQueries.length; i++ ) {
				String query = singleQueries[i];
				String wsName = query.substring(0, query.indexOf("("));
				String parameterStr = query.substring(query.indexOf("(")+1, query.indexOf(")")).trim();
				String[] parameters = parameterStr.split(",");
				
				// Initialize query with WS name			
				Query q = new Query(query);
				q.setWsName(wsName);
				
				int paramOrder = 0;
				
				for ( int j = 0; j < parameters.length; j++ ) {
					if ( parameters[j].contains("\"") ) 
						q.addInputParameter(paramOrder++, parameters[j].replaceAll("\"", "").trim());
					else
						q.addParameter(paramOrder++, parameters[j].trim());
				}
				
				res.add(q);				
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Wrong format for Query " + globalQuery + " .\n " 
					+ "Correct format example : " + queryFormat);				
		}
	
		return res;
	}
	
	public static void validate(List<Query> queries) {
		
		for (int i = 0; i < queries.size(); i++ ) {
			Query cur = queries.get(i);
			if ( cur.getParameters().isEmpty() )
				throw new IllegalStateException("Query " + cur.getGlobalString() 
					+ " doesn't contain neither inputs nor outputs.\n" 
					+ "Query format is: " + queryFormat);
			
			// Start validating from second query
			if ( i != 0 ) {
				Query prev = queries.get(i-1);
				// Validate if at least one variable of actual query
				// match (i.e. has the same name of) output of previous query
				String match = prev.matchParameters(cur.getParameters());
				if ( match == prev.getInput() ) {
					//Second query may skip this validation, if initial
					//input parameter is the same input as his input
					System.out.println("## WARNING ## Assuming that input for query 2 " 
							+ "is the same as input for query 1 : " + prev.getParameters().get(0));
				} else if ( match == null ) 
					throw new IllegalStateException("Query is not valid: no parameter " 
								+ "of query " + cur.getWsName() 
								+ " is not contained in output of " + prev.getWsName());
			}
		}
	}

}

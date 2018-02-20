import java.util.ArrayList;
import java.util.List;

import download.Query;
import download.CallResult;
import download.WebService;
import parsers.ParseResultsForWS;
import parsers.WebServiceDescription;

public class QueryEngine {

	private static String queryFormat = "webservicename1(\"input1\", ?out1, ?out2, ?out3)" +
										"#webservicename2(?out2, ?out4, ?out5, ?out6)" +
										"#webservicename3(?out4, ?out7, ?out8) etc....\n" + 
										"Please note that initial input should be delimited by \" \"," +
										" outputs should be preceded by a ? and output / input names " +
										" should match. Moreover, parameter names in queries should " + 
										" match parameter name in webservice.";
	/*
	 * Shortcomings : 
	 * 1. If I have two params in query1, I don't have any mean to know which will
	 * 		be the input of query2 if not for ordering
	 */
	
    /**
     * @param query String containing query/queries to be executed, with format compliant with the one above
     * @return 
     * @return List of Queries executed, in order to allow further use / analysis
     * Core function of query engine. Takes query/queries string, parses it in single queries and validates them.
     * Then executes queries matching input and output.
     */
	
	public static List<Query> executeQuery(String query){
		
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
				// First query should have an input
				inputs.add(q.getInput());
			} else {
				// From second query on, retrieve input from previous queries
				Query prev = queries.get(i-1);
				inputs = prev.getAllResults(prev.matchParameters(q.getParameters()));
			}
			
			if ( inputs.isEmpty() )
				throw new IllegalStateException("Error: no inputs for QUERY " + (i+1) 
						+ " " + q.getGlobalString() + ". Either previous query didn't "
						+ "return results or no matching was found between inputs and outputs.");
			
			for ( String input : inputs ) {
				String fileWithCallResult;
			    fileWithCallResult = ws.getCallResult(input);				
				String fileWithTransfResults;
				
				try {
					fileWithTransfResults = ws.getTransformationResult(fileWithCallResult);
					ArrayList<String[]>  listOfTupleResult= ParseResultsForWS.showResults(fileWithTransfResults, ws);
					
					if ( !listOfTupleResult.isEmpty() ) {
						System.out.println(" --- QUERY " + (i+1) + " " + q.getGlobalString() + " INPUT " + input + " RESULTS ---");
						for(String [] tuple:listOfTupleResult){
							System.out.print("( ");
						 	for(String t:tuple){
						 		System.out.print(t+", ");
						 	}
						 	System.out.print(") ");
						 	System.out.println();
						}
						CallResult result = new CallResult(q, input);
						result.addResults(listOfTupleResult, ws);
						q.addResult(result);						
					} else {
						System.out.println(" --- NO RESULTS FOR QUERY " + (i+1) + " INPUT " + input + " --- ");
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		
		System.out.println(" --- QUERY EXECUTION TERMINATED --- ");
		return queries;
	}
	
    /**
     * @param globalQuery general String of queries, with queries separated by '#' character
     * @returns a list of Query objects
     * @throws IllegalArgumentException if queries are not in a valid format
     */
	
	public static List<Query> parse(String globalQuery) {
		// Split into single queries
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
	
    /**
     * @param queries List of single queries.
     * @throws IllegalStateException if queries are not valid
     * Assigns output of previous query to input of the following one
     */
	
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
					// Second query may skip this validation, if initial
					// input parameter is the same input as his input
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

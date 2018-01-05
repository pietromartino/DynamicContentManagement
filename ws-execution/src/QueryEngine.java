import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import download.Query;
import download.WebService;
import parsers.ParseResultsForWS;
import parsers.WebServiceDescription;

public class QueryEngine {

	
	/*
	 * Query format: 
	 * wsname1(iiooo)(params) # wsname2(ioooo)(params) # ....
	 */

	//TODO enable support for other format of query : outputs are preceded by a ?, inputs are not
	
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
		
		for ( Query q : queries ) {
			
		    WebService ws=WebServiceDescription.loadDescription(q.getWsName());

		    //Ensure that ws outputs desired variables
		    for ( String qOut : q.getOutputs() ) {
		    	if ( !ws.headVariables.contains(qOut) ) 
					throw new IllegalStateException("Query is not valid: Web Service " + ws.name  
							+ " doesn't output desired variable " + qOut);
		    }
		    
		    String fileWithCallResult = ws.getCallResult(q.getInputs().toArray(new String[0]));
			System.out.println("The call is   **"+fileWithCallResult+"**");
			String fileWithTransfResults;
			
			try {
				fileWithTransfResults = ws.getTransformationResult(fileWithCallResult);
				ArrayList<String[]>  listOfTupleResult= ParseResultsForWS.showResults(fileWithTransfResults, ws);
				
				System.out.println("The tuple results are ");
				for(String [] tuple:listOfTupleResult){
					System.out.print("( ");
				 	for(String t:tuple){
				 		System.out.print(t+", ");
				 	}
				 	System.out.print(") ");
				 	System.out.println();
				}
			 	
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	public static List<Query> parse(String query) {
		//Will get us the single query
		String[] queries = query.trim().split("#");
		
		List<Query> res = new ArrayList<>();
		for ( int i = 0; i < queries.length; i++ ) {
			List<String> parts = Arrays.asList(queries[i].trim().split("[\\(\\)]"));
			
			//Remove empty strings (regex malfunctioning)
			parts = parts.stream().filter(s -> !s.isEmpty()).collect(Collectors.toList());
			
			// Query should have 3 parts : WS Name, inputs-outputs and variables
			if ( parts.size() != 3 ) 
				throw new IllegalArgumentException("Wrong format for Query " + query);
			// Initialize query with WS name			
			Query q = new Query(parts.get(0));

			String[] variables = parts.get(2).trim().split(",");
			
			// Query should have an in-out declaration for each variable
			if ( variables.length != parts.get(1).length() )
				throw new IllegalArgumentException("Input-Output declaration "
						+ "and variables not matching for Query " + query);

			int outputOrder = 0;
			int inputOrder = 0;
			
			for ( int j = 0; j < variables.length; j++ ) {
				if ( parts.get(1).charAt(j) == 'i')
					q.addInput(variables[j].trim(), inputOrder++);
				else
					q.addOutput(variables[j].trim(), outputOrder++);
			}
			
			res.add(q);
		}
	
		return res;
	}
	
	public static void validate(List<Query> queries) {
		for (int i = 0; i < queries.size(); i++ ) {
			Query cur = queries.get(i);
			// Start validating from the second query on 
			if ( i != 0 ) {
				Query prev = queries.get(i-1);
				// Validate the query if input variables of the actual query
				// match output of previous query
				List<String> out = prev.getOutputs();
				for ( String s : cur.getInputs() ) {
					if ( !out.contains(s) )
						throw new IllegalStateException("Query is not valid: input variable " 
					+ s + "of query " + cur.getWsName() + " is not contained in output of " + prev.getWsName());
				}				
			}
		}
	}

}

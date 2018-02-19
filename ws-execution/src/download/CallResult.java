package download;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallResult{
	
	/*
	 * Class containing results of a single web service call, mapped to query parameters
	 */

	private Query query;
	private Map<String, List<String>> results;
	
	public CallResult(Query query, String input) {
		this.query = query;
		this.results = new HashMap<>();
		
		//Initialize results
		for ( String parameter : query.getParameters() ) {
			this.results.put(parameter, new ArrayList<>());			
		}
	}
	
    /**
     * @param results List of tuples containing query call result
     * @param ws WebService object, needed to map results
     * Stores call results of a query, mapping them from the WebService to the query. 
     */

	public void addResults(ArrayList<String[]> results, WebService ws) {
		for ( String queryParam : this.query.getParameters() ) {
			for ( String wsParam : ws.headVariableToPosition.keySet() ) {
				// Find match between query parameters and ws ones
				if ( queryParam.equals(wsParam) ) {
					// Get column index in result
					int tupleCol = ws.headVariableToPosition.get(wsParam);
					// Get list of results corresponding to parameter
					List<String> listRes = this.results.get(queryParam);
					for ( String[] tuple : results ) {
						// Add avoiding duplicates
						for ( int i = 0; i < tuple.length; i++ ) {
							if ( !listRes.contains(tuple[tupleCol]) ) 
								listRes.add(tuple[tupleCol]);
						}			
					}					
				}
			}
		}
	}

	public List<String> getResults(String parameter) {
		return this.results.get(parameter);
	}

	@Override
	public String toString() {
		return "QueryResult [query=" + query + ", results=" + results + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((query == null) ? 0 : query.hashCode());
		result = prime * result + ((results == null) ? 0 : results.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CallResult other = (CallResult) obj;
		if (query == null) {
			if (other.query != null)
				return false;
		} else if (!query.equals(other.query))
			return false;
		if (results == null) {
			if (other.results != null)
				return false;
		} else if (!results.equals(other.results))
			return false;
		return true;
	}		
}

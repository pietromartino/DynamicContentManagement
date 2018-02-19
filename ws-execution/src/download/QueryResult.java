package download;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryResult{

	private Query query;
	private Map<String, List<String>> results;
	
	public QueryResult(Query query, String input) {
		this.query = query;
		this.results = new HashMap<>();
		
		//Initialize result
		for ( String parameter : query.getParameters() ) {
			this.results.put(parameter, new ArrayList<>());			
		}
	}
	
	public void addResults(ArrayList<String[]> results, WebService ws) {
		for ( String queryParam : this.query.getParameters() ) {
			for ( String wsParam : ws.headVariableToPosition.keySet() ) {
				if ( queryParam.equals(wsParam) ) {
					int tupleCol = ws.headVariableToPosition.get(wsParam);
					List<String> listRes = this.results.get(queryParam);
					for ( String[] tuple : results ) {
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
		QueryResult other = (QueryResult) obj;
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

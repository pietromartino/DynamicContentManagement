package download;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Query {
	
	private String globalString;
	private String wsName;
	private TreeMap<Integer, String> parameters; // order matters!
	private int inputIndex=-1; // index of input, if present. 
							   // Input parameter means user prompted input.
							   // Thus, only first level queries will have an input. 
	private List<CallResult> results;
	
	public Query(String globalString) {
		this.globalString = globalString;
		this.parameters = new TreeMap<>();
		this.results = new ArrayList<>();
	}
	
    /**
     * @param order Order of the parameter in the query
     * @param parameter Actual name of query parameter
     * @throws IllegalArgumentException if more than one parameter with same name
     * Store parameter in a map, with order as key and parameter as value
     */
		
	public void addParameter(int order, String parameter){
		if ( this.parameters.containsKey(order) )
			throw new IllegalArgumentException("Output already contained in variables");
		this.parameters.put(order, parameter);
	}
	
    /**
     * @param order Order of the parameter in the query
     * @param parameter Actual name of the parameter
     * @throws IllegalArgumentException If input already provided
     * Same behavior as previous function, but store also input index.
     */

	public void addInputParameter(int order, String parameter){
		if ( this.inputIndex != -1 )
			throw new IllegalArgumentException("More than one input provided. Framework accepts only single input queries.");			
		this.inputIndex = order;
		this.addParameter(order, parameter);
	}
	
    /**
     * @param toMatch List of Strings containing parameters to match
     * @return String of matched parameter. If no match found, return return input if present
     * 		   otherwise null
     * Matches parameters of the current query with provided ones. 
     * Used to match inputs to outputs when concatenating queries.
     */
	
	public String matchParameters(List<String> toMatch) {
		for ( String otherParam : toMatch ) {
			for ( String myParam : this.parameters.values() ) {
				if ( myParam.equals(otherParam) )
					return myParam;
			}
		}
		//In case of no match, return input if present
		if ( this.inputIndex != -1 ) 
			return this.parameters.get(this.inputIndex);
		//Otherwise return null
		else
			return null;
	}
	
    /**
     * @param parameter Query parameter of which return results
     * @return List of Strings corresponding to results of all the calls of the query for input parameter.
     * Collect results from all performed calls. 
     * Used to retrieve results from previous query to be fed to current query.
     */
	
	public List<String> getAllResults(String parameter) {
		List<String> out = new ArrayList<>();
		for ( CallResult r : this.results ) {
			List<String> res = r.getResults(parameter);
			if ( !res.isEmpty() ) 
				out.addAll(res);
		}
		return out;
	}
	
	/*
	 * Getters and Setters
	 */
	
	public String getWsName() {
		return wsName;
	}

	public void setWsName(String wsName) {
		this.wsName = wsName;
	}
	
	public List<String> getParameters() {
		return new ArrayList<String>(parameters.values());
	}
	
	public String getInput() {
		if ( this.inputIndex == -1 )
			return null;
		else
			return this.parameters.get(this.inputIndex);
	}
	
	public Map<Integer, String> getParameterMap() {
		return parameters;
	}
	
	public String getGlobalString() {
		return globalString;
	}

	public void setGlobalString(String globalString) {
		this.globalString = globalString;
	}
	
	public void addResult(CallResult result) {
		this.results.add(result);
	}
	
	public List<CallResult> getResults() {
		return this.results;
	}
		
	@Override
	public String toString() {
		String out = "WS " + wsName + " - ";
		out += " parameters: ";
		for ( String s : this.getParameters() ) {
			out += s + ", ";
		}
		out = out.substring(0, out.length()-2);		
		return  out;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((globalString == null) ? 0 : globalString.hashCode());
		result = prime * result + ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + ((wsName == null) ? 0 : wsName.hashCode());
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
		Query other = (Query) obj;
		if (globalString == null) {
			if (other.globalString != null)
				return false;
		} else if (!globalString.equals(other.globalString))
			return false;
		if (parameters == null) {
			if (other.parameters != null)
				return false;
		} else if (!parameters.equals(other.parameters))
			return false;
		if (wsName == null) {
			if (other.wsName != null)
				return false;
		} else if (!wsName.equals(other.wsName))
			return false;
		return true;
	}

}

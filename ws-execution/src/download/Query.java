package download;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Query {
	
	private String globalString;
	private String wsName;
	private TreeMap<Integer, String> parameters;
	private int inputIndex=-1;
	private List<QueryResult> results;
	
	public Query(String globalString) {
		this.globalString = globalString;
		this.parameters = new TreeMap<>();
		this.results = new ArrayList<>();
	}
		
	public void addParameter(int order, String parameter){
		if ( this.parameters.containsKey(order) )
			throw new IllegalArgumentException("Output already contained in variables");
		this.parameters.put(order, parameter);
	}
	
	public void addInputParameter(int order, String parameter){
		if ( this.inputIndex != -1 )
			throw new IllegalArgumentException("More than one input provided. Framework accepts only single input queries.");			
		this.inputIndex = order;
		this.addParameter(order, parameter);
	}
	
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
	
	public String matchParameters(List<String> toMatch) {
		for ( String otherParam : toMatch ) {
			for ( String myParam : this.parameters.values() ) {
				if ( myParam.equals(otherParam) )
					return myParam;
			}
		}
		//In case of no match, if we have an input we return it
		//Otherwise return null
		if ( this.inputIndex != -1 ) 
			return this.parameters.get(this.inputIndex);
		else
			return null;
	}
	
	public void addResult(QueryResult result) {
		this.results.add(result);
	}
	
	public List<QueryResult> getResults() {
		return this.results;
	}
	
	public List<String> getAllResults(String parameter) {
		List<String> out = new ArrayList<>();
		for ( QueryResult r : this.results ) {
			List<String> res = r.getResults(parameter);
			if ( !res.isEmpty() ) 
				out.addAll(res);
		}
		return out;
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

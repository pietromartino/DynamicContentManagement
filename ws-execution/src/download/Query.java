package download;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Query {
	
	private String wsName;
	private Map<String, Boolean> variables;
	
	public Query(String wsName) {
		this.wsName = wsName;
		this.variables = new HashMap<>();
	}

	public Query(String wsName, Map<String, Boolean> params) {
		this.wsName = wsName;
		this.variables = params;
	}
	
	public void addParameter(String variable, boolean input){
		String var = variable.replace("?", "");
		if ( this.variables.containsKey(var) )
			throw new IllegalArgumentException("Variable already contained in variables");
		this.variables.put(var, input);
	}

	public String getWsName() {
		return wsName;
	}

	public void setWsName(String wsName) {
		this.wsName = wsName;
	}

	public Map<String, Boolean> getVariables() {
		return variables;
	}
	
	public List<String> getInputVariables() {
		return variables.keySet().stream()
				.filter(var -> variables.get(var) == true)
				.collect(Collectors.toList());
	}
	
	public List<String> getOutputVariables() {
		return variables.keySet().stream()
				.filter(var -> variables.get(var) == false)
				.collect(Collectors.toList());	
	}

	@Override
	public String toString() {
		String out = "WS " + wsName + " - ";
		out += " input: ";
		for ( String s : this.getInputVariables() ) {
			out += s + ", ";
		}
		out = out.substring(0, out.length()-2);
		out += " - output: ";
		for ( String s : this.getOutputVariables() ) {
			out += s + ", ";
		}
		out = out.substring(0, out.length()-2);		
		return  out;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((variables == null) ? 0 : variables.hashCode());
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
		if (variables == null) {
			if (other.variables != null)
				return false;
		} else if (!variables.equals(other.variables))
			return false;
		if (wsName == null) {
			if (other.wsName != null)
				return false;
		} else if (!wsName.equals(other.wsName))
			return false;
		return true;
	}
	
	
	

}

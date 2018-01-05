package download;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Query {
	
	private String wsName;
	private Map<String, Integer> input;
	private Map<String, Integer> output;
	
	public Query(String wsName) {
		this.wsName = wsName;
		this.input = new HashMap<>();
		this.output = new HashMap<>();
	}
	
	public void addInput(String parameter, int order){
		if ( this.input.containsKey(parameter) )
			throw new IllegalArgumentException("Input already contained in variables");
		input.put(parameter, order);
	}
	
	public void addOutput(String parameter, int order){
		if ( this.output.containsKey(parameter) )
			throw new IllegalArgumentException("Output already contained in variables");
		output.put(parameter, order);
	}
	
	public String getWsName() {
		return wsName;
	}

	public void setWsName(String wsName) {
		this.wsName = wsName;
	}

	public List<String> getInputs() {
		return new ArrayList<String>(input.keySet());
	}
	
	public List<String> getOutputs() {
		return new ArrayList<String>(output.keySet());
	}
	
	public Map<String, Integer> getOutputsFull() {
		return output;
	}

	@Override
	public String toString() {
		String out = "WS " + wsName + " - ";
		out += " input: ";
		for ( String s : this.getInputs() ) {
			out += s + ", ";
		}
		out = out.substring(0, out.length()-2);
		out += " - output: ";
		for ( String s : this.getOutputs() ) {
			out += s + ", ";
		}
		out = out.substring(0, out.length()-2);		
		return  out;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((input == null) ? 0 : input.hashCode());
		result = prime * result + ((output == null) ? 0 : output.hashCode());
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
		if (input == null) {
			if (other.input != null)
				return false;
		} else if (!input.equals(other.input))
			return false;
		if (output == null) {
			if (other.output != null)
				return false;
		} else if (!output.equals(other.output))
			return false;
		if (wsName == null) {
			if (other.wsName != null)
				return false;
		} else if (!wsName.equals(other.wsName))
			return false;
		return true;
	}

}

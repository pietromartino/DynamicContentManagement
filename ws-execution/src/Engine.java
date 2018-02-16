import java.util.ArrayList;
import java.util.List;

import parsers.ParseResultsForWS;
import parsers.WebServiceDescription;
import download.WebService;



public class Engine {

	public Engine() {

	}
	/**
	 * Splits the call by hashtags and creates a list of webservice1 objects.
	 * @param wscall
	 * @return
	 */
	public List<WebService1> parseCall(String wscall)
	{
		List<WebService1> webServiceCalls = new ArrayList<WebService1>();
		String query = wscall;
		query = query.trim();
		String[] calls = query.split("#");
		for(String call: calls)
		{
			List<String> inputs = new ArrayList<>();
			List<String> outputs = new ArrayList<>();
			String webservice = call.substring(0,call.indexOf("("));
			String parameterString = call.substring(call.indexOf("(")+1, call.indexOf(")"));
			String[] parameters = parameterString.split(",");
			String inputoutputs = webservice.substring(webservice.length()-parameters.length,webservice.length());
			webservice = call.substring(0,call.indexOf("(")-inputoutputs.length());
			for(int i =  0;i< parameters.length;i++)
			{
				if(inputoutputs.charAt(i)=='i')
				{	inputs.add(parameters[i].trim()); 
				}else if(inputoutputs.charAt(i)=='o')
				{	outputs.add(parameters[i].trim());
				}	
			}
			WebService1 webService2 = new WebService1();
			webService2.setName(webservice);
			webService2.setInputs(inputs);
			webService2.setOutputs(outputs);
			webServiceCalls.add(webService2);
		}
		return webServiceCalls;
	}
	/**
	 * Thie method receives a list of WebService1 objects and executes them in order. 
	 * @param webServiceList
	 * @throws Exception
	 */
	public void callWebServices(List<WebService1> webServiceList) throws Exception
	{
		WebService ws=null;
		WebService ws1=WebServiceDescription.loadDescription("mb_getArtistInfoByName");
		WebService ws2=WebServiceDescription.loadDescription("mb_getAlbumArtistById");
		WebService ws3=WebServiceDescription.loadDescription("mb_getSongByAlbum");
		String fileWithCallResult = "";
		String fileWithTransfResults="";
		ArrayList<ArrayList<String[]>>  listOflistOfTupleResult = new ArrayList<>();
		ArrayList<String[]>  listOfTupleResult = new ArrayList<>();
		ArrayList<String[]>  tempListOfTupleResult = new ArrayList<>();
		String[] filesWithTransfResults;
		for(int i=0;i<webServiceList.size();i++)
		{
			WebService1 webs = webServiceList.get(i);
			String name = webs.getName();
			System.out.println(name);
			switch (name) {
			case "getArtistInfoByName":
				ws = ws1;
				break;
			case "getAlbumByArtistId":
				ws = ws2;
				break;
			case "getSongByAlbumId":
				ws = ws3;
				break;
			default:
				break;
			}	
			if(i==0)
			{
				filesWithTransfResults=new String[1];
				fileWithCallResult =ws.getCallResult(webs.constantInputs.toArray(new String[webs.constantInputs.size()]));
				fileWithTransfResults = ws.getTransformationResult(fileWithCallResult);
				filesWithTransfResults[0]=fileWithTransfResults;
				listOfTupleResult = ParseResultsForWS.showResults(fileWithTransfResults, ws);
				listOflistOfTupleResult.add(listOfTupleResult);
			}else
			{
				ArrayList<ArrayList<String[]>>  tempListOflistOfTupleResult = new ArrayList<>();
				for(ArrayList<String[]> lotr:listOflistOfTupleResult)
				{
					filesWithTransfResults=new String[(lotr.isEmpty()?0:lotr.get(0).length)];
				
					for(int l = 0;l<(lotr.isEmpty()?0:lotr.get(0).length);l++)
					{
						List<String> tuple = new ArrayList<>();
						for(String[] arrs:lotr)
						{
							tuple.add(arrs[l]);
						}
						fileWithCallResult =ws.getCallResult(tuple.toArray(new String[tuple.size()]));
						fileWithTransfResults = ws.getTransformationResult(fileWithCallResult);
						filesWithTransfResults[l]=fileWithTransfResults;
						listOfTupleResult = ParseResultsForWS.showResults(fileWithTransfResults, ws);
						tempListOflistOfTupleResult.add(listOfTupleResult);
					}
				}
				listOflistOfTupleResult = tempListOflistOfTupleResult;
				
			}
			if(i+1<webServiceList.size())
			{
				WebService1 next = webServiceList.get(i+1);
				List<Integer> outputs = new ArrayList<>();
				for(int j=0;j<webs.getOutputs().size();j++)
				{
					String output = webs.getOutputs().get(j);
					if(next.getInputs().contains(output))
					{
						outputs.add(j);
					}
				}
				
				
				ArrayList<ArrayList<String[]>>  tempListOflistOfTupleResult = new ArrayList<>();
				for(ArrayList<String[]> lotr:listOflistOfTupleResult)
				{
					String[] tempss = new String[outputs.size()];
					tempListOfTupleResult = new ArrayList<>();
					for(String[] ss:lotr)
					{
						for(int g=0;g<tempss.length;g++)
						{
							tempss[g]=ss[outputs.get(g)+webs.getInputs().size()+webs.getConstantInputs().size()-1];
						}
						tempListOfTupleResult.add(tempss);
					}
					
					tempListOflistOfTupleResult.add(tempListOfTupleResult);
				}
				listOflistOfTupleResult = tempListOflistOfTupleResult;


			}else
			{
				listOflistOfTupleResult.forEach(lot ->lot.forEach(a -> {for(String s:a){System.out.println(s);}}) );
				
			}
		}
	}


	public static void main(String[] args) throws Exception {
		Engine engine = new Engine();
		engine.callWebServices(engine.parseCall(args[0]));
	}
}
/**
 * Class that represents the inputs outputs and name of a web service
 * @author CARLOS
 *
 */
class WebService1
{
	private String name;
	List<String> inputs = new ArrayList<>();
	List<String> outputs = new ArrayList<>();
	List<String> constantInputs = new ArrayList<>();

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getInputs() {
		return inputs;
	}
	public void setInputs(List<String> inputs) {

		List<String> constantInputs = getConstantInputs();
		List<String> otherInputs = getConstantInputs();
		for(String input:inputs)
		{
			if(input.contains("\""))
			{
				constantInputs.add(input.replaceAll("\"", ""));
			}else
			{
				otherInputs.add(input);
			}
		}
		this.inputs = inputs;
		setConstantInputs(constantInputs);
	}
	public List<String> getOutputs() {
		return outputs;
	}
	public void setOutputs(List<String> outputs) {
		this.outputs = outputs;
	}

	public List<String> getConstantInputs() {
		return constantInputs;
	}

	private void setConstantInputs(List<String> constantInputs) {
		this.constantInputs = constantInputs;
	}

}

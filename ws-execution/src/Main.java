public class Main {

	public static final void main(String[] args) throws Exception{
		
//		List<String> params = Arrays.asList("http://musicbrainz.org/ws/1/artist/?name=", null);
//
//	    //Testing without loading the description of the WS
//	    WebService ws=new WebService("mb_getArtistInfoByName",params);	
//	    String fileWithCallResult = ws.getCallResult("Frank Sinatra");
//		System.out.println("The call is   **"+fileWithCallResult+"**");
//		ws.getTransformationResult(fileWithCallResult);
//		
		QueryEngine.executeQuery("mb_getArtistInfoByName(iooo)(Frank Sinatra, ?id, ?b, ?e)#mb_getAlbumByArtistId(ioo)(?id, ?aid, ?albumName)");
		
	}
	
}

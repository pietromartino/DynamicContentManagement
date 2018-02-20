import java.util.List;

import download.Query;

public class Main {

	public static final void main(String[] args) throws Exception{
		
		List<Query> queries = QueryEngine.executeQuery(
   				"mb_getArtistInfoByName(\"Frank Sinatra\", ?artistId, ?beginDate, ?endDate)"
				+"#mb_getAlbumByArtistId(?artistId, ?albumId, ?albumName, ?albumYear)"
				+"#mb_getSongByAlbumId(?albumId, ?songId, ?songName, ?duration)");

		Query last = queries.get(queries.size()-1);
		System.out.println(" --- FINAL RESULTS: Songs by artits named Frank Sinatra --- ");
		for ( String res : last.getAllResults("?songName") ) {
			System.out.println(res);
		}
		
//		QueryEngine.executeQuery("mb_getSongByName(\"This Christmas\", ?songId, ?songName, ?duration, ?artistId)"
//				+"#mb_getAlbumByArtistId(?artistId, ?albumId, ?albumName, ?albumYear)");
//
//		
	}
	
}

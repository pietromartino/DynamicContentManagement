public class Main {

	public static final void main(String[] args) throws Exception{
		QueryEngine.executeQuery("mb_getArtistInfoByName(\"Frank Sinatra\", ?artistId, ?b, ?e)"
								+"#mb_getAlbumByArtistId(?artistId, ?albumId, ?albumName, ?albumYear)"
								+"#mb_getSongsByAlbumId(?albumId, ?songId, ?songName, ?duration)");
	}
	
}

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;

public class SearchMovieTitleUsingArray{
	public static void main(String[] args) {
		String [] titleArray = getMovieTitleInArray(keyInMovieTitleKeyword());
		for(int i=0;i<titleArray.length;i++){
			System.out.println(titleArray[i]);
		}
		System.out.println("==================== Searching End ====================");
	}
	
	private static String keyInMovieTitleKeyword(){
		System.out.println("========== Please key in movie title keyword ==========");
		Scanner in = new Scanner(System.in);
		return in.nextLine();
	}
	
	private static String [] getMovieTitleInArray(String movieTitleKeyword){
		System.out.println();
		System.out.println("=================== Searching Begin ===================");
		String json = getMovieJsonResponseWithPageIndex(movieTitleKeyword,1);//beginning searching with first page
		String []  titleArray = null;

		if(StringUtils.isNotBlank(json)){
			titleArray = processJsonContent(json, movieTitleKeyword);
		}
		else{
			System.out.println("No database is found!");
		}
		return titleArray;
	}	
	
	private static String getMovieJsonResponseWithPageIndex(String movieTitleKeyword, int page){
		String json = null;		
		String url = "https://jsonmock.hackerrank.com/api/movies/search?Title=".concat(encodeKeyword(movieTitleKeyword)).concat("&page=").concat(String.valueOf(page));
		try {
			System.out.println(".......................................................");
			json = Jsoup.connect(url).method(Method.GET).ignoreContentType(true).execute().body();

		} catch (Exception e) {
			System.out.println("error in getAllMovieList|"+e);
		}
		return json;
	}
	
	private static String encodeKeyword(String keyword){
		String result = "";
		try {
			result = URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("error in encodeKeyword|"+e);
		}
		return result;
	}
	
	private static String [] processJsonContent(String json, String movieTitleKeyword){
		JSONObject jsonObject = parseJsonContent(json);
		int totalMovie = getTotalMovie(jsonObject);
	    String titlesArray[] = new String[totalMovie]; //Initialize the titles array 
		int masterArrayIndex=0;
		
	    if(totalMovie==0){
	    	System.out.println("No movie found with your keyword!");
	    }
	    else{
	    	insertDataIntoMovieTitleArray(jsonObject, masterArrayIndex, titlesArray,movieTitleKeyword);//insert movie title into array
	    	sortArray(titlesArray);//sort movie title in ascending order
	    }
	    return titlesArray;
	}
	
	private static JSONObject parseJsonContent(String json){
		JSONParser jsonParser = new JSONParser();
		Object obj = null;
		try {
			obj = jsonParser.parse(json);
		} catch (Exception e) {
			System.out.println("parseJsonContentAndSaveIntoArray|"+e);
		}
		JSONObject jsonObjectResult = (JSONObject)obj;
		
		return jsonObjectResult;
	}	
	
	private static int getTotalMovie(JSONObject jsonObject){
		int totalRecords = Integer.parseInt(jsonObject.get("total").toString());
		return totalRecords;
	}
	
	private static int getFinalPage(JSONObject jsonObject){
		int finalPage = Integer.parseInt(jsonObject.get("total_pages").toString());
		return finalPage;
	}
	
	private static String getTitle(JSONObject jsonObject){
		String title = jsonObject.get("Title").toString();
		return title;
	}
	
	private static JSONArray getDataArray(JSONObject jsonObject){
		JSONArray jsonArray = (JSONArray)jsonObject.get("data");
		return jsonArray;
	}
	
	private static void insertDataIntoMovieTitleArray(JSONObject jsonObject, int masterArrayIndex, String [] titlesArray, String movieTitleKeyword){
		int finalPage = getFinalPage(jsonObject);
		for(int pageIndex=1;pageIndex<=finalPage;pageIndex++){
			if(pageIndex==1){//jsonObject refer to page 1 json response
				JSONArray dataArrayResult = getDataArray(jsonObject);
				for(int i=0;i<dataArrayResult.size();i++){
			    	JSONObject object = (JSONObject) dataArrayResult.get(i);
			    	titlesArray[masterArrayIndex] = getTitle(object);
			    	masterArrayIndex++;
			    }
			}
			else{ //page index more than 1
				String json = getMovieJsonResponseWithPageIndex(movieTitleKeyword,pageIndex);
				JSONObject jsonObject2 = parseJsonContent(json);
				JSONArray dataArrayResult = getDataArray(jsonObject2);
				for(int i=0;i<dataArrayResult.size();i++){
			    	JSONObject object = (JSONObject) dataArrayResult.get(i);
			    	titlesArray[masterArrayIndex] = getTitle(object);
			    	masterArrayIndex++;
			    }
			}
		}
	}
	
	private static void sortArray(String [] titlesArray){
		Arrays.sort(titlesArray, String.CASE_INSENSITIVE_ORDER); //Sort string array in case insensitive order and case sensitive order
	}
}
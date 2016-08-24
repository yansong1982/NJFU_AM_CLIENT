package cn.njfu.ams;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class Utils {
	public static final String URL = "http://192.168.1.102/NJFU_AM_Server/login";
	// public static final String URL =
	// "http://192.168.1.107/NJFU_AM_Server/login";
	public static final String BACKUP_URL = "http://420217d8.nat123.net/NJFU_AM_Server/login";
	public static final String HTTP_KEY = "REQUEST";
	public static final String HTTP_JSON_KEY = "JSON";
	public static final String HTTP_LOGIN_VALUE = "login";
	public static final String HTTP_COMMUNITY_KEY = "COMMUNITY";
	public static final String HTTP_BUILDING_KEY = "BUILDING";
	public static final String HTTP_ROOM_VALUE = "room";
	public static final String HTTP_UPLOAD_SECURITY_VALUE = "upload_security";
	public static final String HTTP_UPLOAD_CLEANING_VALUE = "upload_cleaning";
	public static final boolean IS_POST = true;
	
	public static final String JSON_COMMA_KEY = " , ";

	public static final String EXTRA_COMMUNITY_NUM = "extra_community_num";
	public static final String EXTRA_BUILDING_NUM = "extra_building_num";
	public static final String EXTRA_ROOM_INDEX = "extra_room_index";
	public static final String EXTRA_CHECK_TYPE = "extra_check_type";
	public static final String EXTRA_ROOM_NUM_ARRAY = "extra_room_num_array";
	public static final String ADMIN_JSON_KEY = "admins";
	public static final String ADMIN_JSON_COMMUNITY_KEY = "community";
	public static final String ADMIN_JSON_BUILDING_KEY = "building";
	public static final String ADMIN_JSON_PASSWORD_KEY = "password";
	public static final String ADMIN_PWD_LOCAL_JSON = "["
			+
			"{ \"community\":1 , \"building\":1 , \"password\":\"11\"}, "
			+ "{ \"community\":1 , \"building\":2 , \"password\":\"12\"}, "
			+ "{ \"community\":2 , \"building\":1 , \"password\":\"21\"}, "
			+ "{ \"community\":2 , \"building\":2 , \"password\":\"22\"}, "
			+ "{ \"community\":2 , \"building\":3 , \"password\":\"23\"}, "
			+ "{ \"community\":3 , \"building\":1 , \"password\":\"31\"}, "
			+ "{ \"community\":3 , \"building\":2 , \"password\":\"32\"}, "
			+ "{ \"community\":3 , \"building\":\"3\" , \"password\":\"33\"}, "
			+ "{ \"community\":3 , \"building\":4 , \"password\":\"34\"}"
			+
			"]";
	
	public static final String ROOM_INFO_LOCAL_JSON = "["
			+ "{ \"floor\":\"1\" , \"room\":\"1-2101\"}, "
			+ "{ \"floor\":\"2\" , \"room\":\"1-2201\"}, "
			+ "{ \"floor\":\"2\" , \"room\":\"1-2202\"}, "
			+ "{ \"floor\":\"3\" , \"room\":\"1-2301\"}, "
			+ "{ \"floor\":\"3\" , \"room\":\"1-2302\"}, "
			+ "{ \"floor\":\"3\" , \"room\":\"1-2303\"}, "
			+ "{ \"floor\":\"4\" , \"room\":\"1-2401\"}, "
			+ "{ \"floor\":\"4\" , \"room\":\"1-2402\"}, "
			+ "{ \"floor\":\"4\" , \"room\":\"1-2403\"}, "
			+ "{ \"floor\":\"4\" , \"room\":\"1-2404\"}, "
			+ "{ \"floor\":\"5\" , \"room\":\"1-2501\"}, "
			+ "{ \"floor\":\"5\" , \"room\":\"1-2502\"}, "
			+ "{ \"floor\":\"5\" , \"room\":\"1-2503\"}, "
			+ "{ \"floor\":\"5\" , \"room\":\"1-2504\"}, "
			+ "{ \"floor\":\"5\" , \"room\":\"1-2505\"}" + "]";

	public static final String ROOM_JSON_FLOOR_KEY = "floor";
	public static final String ROOM_JSON_ROOMNUM_KEY = "room";

	public static final String CHECK_TYPE_SECURITY = "security";
	public static final String CHECK_TYPE_CLEANING = "cleaning";
	
	public static final String ROOM_UPLOAD_JSON_ROOMFLOOR = "floor";
	public static final String ROOM_UPLOAD_JSON_ROOMNUM = "room";
	public static final String ROOM_UPLOAD_JSON_ROOMUPDATE = "updatetime";
	public static final String ROOM_UPLOAD_JSON_ROOMSCORE = "score";
	public static final String ROOM_UPLOAD_JSON_ROOMREASON= "reason";
	public static final String ROOM_UPLOAD_JSON_ROOMNOTES = "notes";
	public static final String ROOM_UPLOAD_JSON_MD = "\", ";
	public static final String ROOM_UPLOAD_JSON_START = "{ ";
	public static final String ROOM_UPLOAD_JSON_END = "\" },";

	public static String doGet(String url, String value, String community,
			String building) {
		String responseStr = "";
		try {
			String getUrl = URL + "?" + HTTP_KEY + "=" + HTTP_LOGIN_VALUE + ","
					+ HTTP_COMMUNITY_KEY + "=" + community + ","
					+ HTTP_BUILDING_KEY + "=" + building;

			HttpGet httpRequest = new HttpGet(getUrl);
			HttpParams params = new BasicHttpParams();
			ConnManagerParams.setTimeout(params, 1000);
			HttpConnectionParams.setConnectionTimeout(params, 10000);
			HttpConnectionParams.setSoTimeout(params, 10000);
			httpRequest.setParams(params);

			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			final int ret = httpResponse.getStatusLine().getStatusCode();
			if (ret == HttpStatus.SC_OK) {
				responseStr = EntityUtils.toString(httpResponse.getEntity(),
						HTTP.UTF_8);
			} else {
				responseStr = "-1";
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseStr;
	}

	public static String doGet(String url, String value) {
		return doGet(url, value, null, null);

	}
	
	public static String doPost(String url, String value, String community,
			String building) {
		return doPost(url,value,null,community,building);
	}

	public static String doPost(String url, String value, String json, String community,
			String building) {
		String responseStr = "";
		try {
			HttpPost httpRequest = new HttpPost(url);
			HttpParams params = new BasicHttpParams();
			ConnManagerParams.setTimeout(params, 1000);
			HttpConnectionParams.setConnectionTimeout(params, 10000);
			HttpConnectionParams.setSoTimeout(params, 10000);
			httpRequest.setParams(params);
			List<BasicNameValuePair> paramsList = new ArrayList<BasicNameValuePair>();
			paramsList.add(new BasicNameValuePair(HTTP_KEY, value));
			paramsList.add(new BasicNameValuePair(HTTP_JSON_KEY, json));
			paramsList
					.add(new BasicNameValuePair(HTTP_COMMUNITY_KEY, community));
			paramsList.add(new BasicNameValuePair(HTTP_BUILDING_KEY, building));
			UrlEncodedFormEntity mUrlEncodeFormEntity = new UrlEncodedFormEntity(
					paramsList, HTTP.UTF_8);
			httpRequest.setEntity(mUrlEncodeFormEntity);
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			final int ret = httpResponse.getStatusLine().getStatusCode();
			if (ret == HttpStatus.SC_OK) {
				responseStr = EntityUtils.toString(httpResponse.getEntity(),
						HTTP.UTF_8);
			} else {
				responseStr = "-1";
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return responseStr;
	}

	public static String doPost(String url, String value) {
		return doPost(url, value, null, null);
	}
}

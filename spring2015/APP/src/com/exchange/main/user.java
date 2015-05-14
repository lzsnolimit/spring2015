package com.exchange.main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class user {
	private static String username;
	private static String pass;
	private static String credit="0";
//	private static Map<String, String> donateBooks[];
//	private static Map<String, String> requireBooks[];
	Context currentContext;

	private static int loginStatus = 0;

	public user(Context ctx) {
		// TODO Auto-generated constructor stub
		currentContext = ctx;
		// SharedPreferences sp = currentContext.getSharedPreferences("user",
		// Context.MODE_PRIVATE);
		// Editor editor = sp.edit();
		SharedPreferences sharedPreferences = currentContext
				.getSharedPreferences("name", Context.MODE_APPEND);
		// getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值
		// String name = sharedPreferences.getString("name", "");
		// int age = sharedPreferences.getInt("age", 1);
		if (sharedPreferences.contains("username")) {
			username = sharedPreferences.getString("username", "");
			pass = sharedPreferences.getString("pass", "");
			//Toast.makeText(currentContext, username+pass, Toast.LENGTH_LONG).show();
			login(username, pass);
		} else {
			username = new String("");
			pass = new String("");
		}
	}

	public void login() {
		login(username, pass);
	}

	public void login(final String myName, final String myPass) {
		//Toast.makeText(currentContext,"mima2"+pass,Toast.LENGTH_LONG).show();
		username = myName;
		pass = myPass;
		
		String urlString = "http://192.168.1.107/exchange/text_book.php?type=4&name="
				+ username + "&pass=" + pass;
		//Toast.makeText(currentContext,urlString,Toast.LENGTH_LONG).show();
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(urlString, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				if (!new String(arg2).equals("0")) {
					//Toast.makeText(currentContext, new String(arg2), Toast.LENGTH_LONG).show();
					loginStatus = 1;
					update(new String(arg2));
					SharedPreferences sharedPreferences = currentContext
							.getSharedPreferences("name", Context.MODE_APPEND);
					Editor editor=sharedPreferences.edit();
					editor.putString("username", myName);
					editor.putString("pass", myPass);
					editor.commit();
					//Log.w("Login", new String(arg2));
//					Toast.makeText(currentContext, new String(arg2),
//							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(currentContext,new String(arg2),Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	public void register(String email, String myPass, String firstName,
			String lastName) {
		username = email;
		pass = myPass;
		String urlString = "http://192.168.1.107/exchange/text_book.php?type=4&name="
				+ username + "&pass=" + pass;
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(urlString, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				if (!new String(arg2).equals("0")) {

				} else {
					loginStatus = 0;
				}
			}
		});
	}

	public void update(String jsonStr) {
		JSONObject json;
		try {
			json = new JSONObject(jsonStr);
			// userString[0]
			credit = json.getString("credit");
			username=json.getString("email");
			pass=json.getString("pass");
//			JSONArray jsonArray = new JSONArray(json.getString("books_donate"));
//
//			// donateBooks.put("id", value)
//			for (int i = 0; i < jsonArray.length(); i++) {
//				donateBooks[i] = new HashMap<String, String>();
//				JSONObject obj = jsonArray.getJSONObject(i);
//				donateBooks[i].put("courseDepartment",
//						obj.getString("courseDepartment"));
//				donateBooks[i].put("courseId", obj.getString("courseId"));
//				donateBooks[i].put("courseSection",
//						obj.getString("courseSection"));
//				donateBooks[i].put("instructure", obj.getString("instructure"));
//			}
//
//			jsonArray = new JSONArray(json.getString("books_require"));
//
//			// donateBooks.put("id", value)
//			for (int i = 0; i < jsonArray.length(); i++) {
//				requireBooks[i] = new HashMap<String, String>();
//				JSONObject obj = jsonArray.getJSONObject(i);
//				requireBooks[i].put("courseDepartment",
//						obj.getString("courseDepartment"));
//				requireBooks[i].put("courseId", obj.getString("courseId"));
//				requireBooks[i].put("courseSection",
//						obj.getString("courseSection"));
//				requireBooks[i]
//						.put("instructure", obj.getString("instructure"));
//			}
			// requireBooks = json.getString("books_require");
			// JSONArray jsonArray = new JSONArray(userString[1]);
			// JSONObject obj = jsonArray.getJSONObject(0);
			// Log.w("test", obj.getString("instructure"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean checkEmail(String email) {
		EmailValidator reg = new EmailValidator();
		return reg.validate(email);
	}

	public void loginOut() {
		loginStatus=0;
		SharedPreferences sharedPreferences = currentContext
				.getSharedPreferences("name", Context.MODE_APPEND);
		Editor editor=sharedPreferences.edit();
		editor.remove("username");
		editor.remove("pass");
		editor.commit();
	}
	
	public int getStatus() {
		return loginStatus;
	}
	
	public void setStatus(int status) {
		loginStatus=status;
	}

	public String getUser() {
		return username;
	}

	public String getPass() {
		SharedPreferences sharedPreferences = currentContext
			.getSharedPreferences("name", Context.MODE_APPEND);
		pass = sharedPreferences.getString("pass", "");
		return pass;
	}

	public String getCredit() {
		return credit;
	}

	public String getEmail() {
		return username;
	}
	public class EmailValidator {

		private Pattern pattern;
		private Matcher matcher;

		private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@uwyo.edu";

		public EmailValidator() {
			pattern = Pattern.compile(EMAIL_PATTERN);
		}

		/**
		 * Validate hex with regular expression
		 * 
		 * @param hex
		 *            hex for validation
		 * @return true valid hex, false invalid hex
		 */
		public boolean validate(final String hex) {

			matcher = pattern.matcher(hex);
			return matcher.matches();

		}
	}
}

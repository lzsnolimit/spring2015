package com.exchange.main;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.manish.tabdemo.R;

public class SearchActivity extends Activity {
	int status = 0;
	// user myUser=new user(getApplicationContext());
	user myUser;
	ArrayList<ArrayList<String>> coursesList = new ArrayList<ArrayList<String>>();
	String currentBook[] = new String[6];
	String requiredBook[] = new String[2];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myUser = new user(getApplicationContext());
		   
		drawSerachPages();
		loadCourses();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setContentView(R.layout.activity_search);
			drawSerachPages();
		}
		return true;
	}

	private void loadCourses() {

		String fileName = "courses"; // 文件名字
		String res = "";
		try {
			InputStream in = getResources().getAssets().open(fileName);
			int length = in.available();
			byte[] buffer = new byte[length];
			in.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			JSONArray jsonArray = new JSONArray(res);
			// ArrayList<ArrayList<String>> list = new
			// ArrayList<ArrayList<String>>();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject obj = jsonArray.getJSONObject(i);
				ArrayList<String> temList = new ArrayList<String>();
				temList.add(obj.getString("0"));
				temList.add(obj.getString("1"));
				temList.add(obj.getString("2"));
				temList.add(obj.getString("3"));
				coursesList.add(temList);
				// Log.w("testLog1", temList.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void search() {
		// Toast.makeText(getApplicationContext(), "Please input all fileds!",
		// 5).show();

		EditText departEditText = (EditText) findViewById(R.id.editTextDepart);
		EditText idEditText = (EditText) findViewById(R.id.editTextCourseNum);
		EditText sectionEditText = (EditText) findViewById(R.id.editTextCourseSec);
		// Toast.makeText(getApplicationContext(),
		// departEditText.getText().toString(), 1000).show();
		if (departEditText.getText().toString().equals("")
				|| idEditText.getText().toString().equals("")
				|| sectionEditText.getText().toString().equals("")) {
			Toast.makeText(getApplicationContext(), "Please input all fileds!",
					100).show();
			return;
		} else {
			

			for (int i = 0; i < coursesList.size(); i++) {
				// Log.w("input",
				// departEditText.getText().toString()+idEditText.getText().toString()+sectionEditText.getText().toString());
				// Log.w("compared",
				// coursesList.get(i).get(1)+coursesList.get(i).get(2)+coursesList.get(i).get(3));
				if (coursesList.get(i).get(1)
						.equalsIgnoreCase(departEditText.getText().toString())
						&& coursesList
								.get(i)
								.get(2)
								.equalsIgnoreCase(
										idEditText.getText().toString())
						&& coursesList
								.get(i)
								.get(3)
								.equalsIgnoreCase(
										sectionEditText.getText().toString())) {
					// return Integer.parseInt(coursesList.get(i).get(3));

					// Toast.makeText(getApplicationContext(),
					// "Got this course!", 100).show();
					String urlString = "http://192.168.1.107/exchange/text_book.php?type=1&id="
							+ coursesList.get(i).get(0);
					AsyncHttpClient client = new AsyncHttpClient();
					client.get(urlString, new AsyncHttpResponseHandler() {

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							// TODO Auto-generated method stub
							// Toast.makeText(getApplicationContext(), new
							// String(arg2), 10).show();
							try {
								JSONObject json = new JSONObject(new String(
										arg2));
								// Toast.makeText(getApplicationContext(),
								// json.getString("sharedId"), 10).show();
								// Toast.makeText(getApplicationContext(),
								// jsonArray.getString(index), 1000).show();

								currentBook[0] = json.getString("id");
								currentBook[1] = json.getString("instructure");
								currentBook[2] = json
										.getString("courseDepartment");
								currentBook[3] = json.getString("courseId");
								currentBook[4] = json
										.getString("courseSection");
								currentBook[5] = json.getString("sharedId");
								drawDetailPage();

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(),
									"Connection error!", Toast.LENGTH_LONG)
									.show();
						}
					});
					return;
				}
			}
			// Toast.makeText(getApplicationContext(), "2!", 100).show();
			Toast.makeText(getApplicationContext(), "Class not exist!",
					Toast.LENGTH_LONG).show();
		}
		// Toast.makeText(getApplicationContext(), departEditText.getText(),
		// 5).show();

		return;
	}

	private void drawSerachPages() {
		
		status = 0;
		setContentView(R.layout.activity_search);
		Button searchButton = (Button) findViewById(R.id.search);
		EditText departEditText = (EditText) findViewById(R.id.editTextDepart);
		EditText idEditText = (EditText) findViewById(R.id.editTextCourseNum);
		EditText sectionEditText = (EditText) findViewById(R.id.editTextCourseSec);
		departEditText.setText("");
		idEditText.setText("");
		sectionEditText.setText("");
		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// setContentView(R.layout.results);
				if (myUser.getStatus() != 1) {
					Toast.makeText(getApplicationContext(),
							"Please login in first!", Toast.LENGTH_LONG).show();
					// Intent intent = new Intent();
					// //intent.setClass(getApplicationContext(),
					// MeActivity.class);
					// startActivity(intent);
					// //如果不关闭当前的会出现好多个页面
					// //t.finish();
				} else {
					search();
				}

			}
		});
	}

	private void drawDetailPage() {
		
		status = 1;
		setContentView(R.layout.results);
		TextView departView = (TextView) findViewById(R.id.detailCourseDepart);
		TextView idView = (TextView) findViewById(R.id.detailCourseId);
		TextView sectionView = (TextView) findViewById(R.id.detailCourseSection);
		TextView instructureView = (TextView) findViewById(R.id.detailCourseInstructure);
		Button requireButton = (Button) findViewById(R.id.detailRequire);
		Button donateButton = (Button) findViewById(R.id.detailDonate);
		departView.setText("Depart:" + currentBook[2]);
		idView.setText("Id:" + currentBook[3]);
		sectionView.setText("Section:" + currentBook[4]);
		instructureView.setText("Instructor:" + currentBook[1]);
		if (Integer.parseInt(currentBook[5]) == 0) {
			requireButton.setVisibility(View.GONE);
			// requireButton.setClickable(false);
		}
		requireButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				drawRequirePage();
			}
		});
		donateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				drawDonatePage();
			}
		});
	}

	private void drawDonatePage() {
		status = 2;
		setContentView(R.layout.donate_page);
		Button submit = (Button) findViewById(R.id.donateSubmit);
		final EditText emailEditText = (EditText) findViewById(R.id.donateEmailEdit);
		emailEditText.setText(myUser.getUser());
		final EditText phoneEditText = (EditText) findViewById(R.id.donatePhoneEdit);
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (emailEditText.getText().toString().equals("")
						|| phoneEditText.getText().toString().equals("")) {
					Toast.makeText(getApplicationContext(),
							"Please fill both fileds!", Toast.LENGTH_LONG)
							.show();
					return;
				} else {
					String urlString = "http://192.168.1.107/exchange/text_book.php?type=3&user="
							+ myUser.getUser()
							+ "&pass="
							+ myUser.getPass()
							+ "&id="
							+ currentBook[0]
							+ "&phone="
							+ phoneEditText.getText().toString()
							+ "&email="
							+ emailEditText.getText().toString();
					//Toast.makeText(getApplicationContext(), urlString, Toast.LENGTH_LONG).show();
					AsyncHttpClient client = new AsyncHttpClient();
					client.get(urlString, new AsyncHttpResponseHandler() {

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							Toast.makeText(getApplicationContext(), "Done!",
									Toast.LENGTH_LONG).show();
							emailEditText.setText("");
							phoneEditText.setText("");
							drawSerachPages();
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							// TODO Auto-generated method stub
							Toast.makeText(getApplicationContext(),
									"Connection error!", Toast.LENGTH_LONG)
									.show();
						}
					});

				}

			}
		});
	}

	private void drawRequirePage() {
		status = 3;
		setContentView(R.layout.required_book);
		String urlString = "http://192.168.1.107/exchange/text_book.php?type=2&user="
				+ myUser.getUser()
				+ "&pass="
				+ myUser.getPass()
				+ "&id="
				+ currentBook[5];
		AsyncHttpClient client = new AsyncHttpClient();
		final TextView message = (TextView) findViewById(R.id.require_response);
		final TextView emailTextView=(TextView)findViewById(R.id.require_response_email);
		final TextView phoneTextView=(TextView)findViewById(R.id.require_response_phone);
		emailTextView.setVisibility(8);
		phoneTextView.setVisibility(8);
		message.setText("");
		// final TextView phoneTextView = (TextView)
		// findViewById(R.id.donatorPhone);
		client.get(urlString, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				try {
					JSONArray json=new JSONArray(new String(arg2));
					if (json.getInt(0)!=0) {
						message.setText(json.getString(1));
					}
					else {
						message.setText("Please contact the donator!");
						JSONObject object=new JSONObject(json.getString(1));
						emailTextView.setVisibility(0);
						emailTextView.setText("Email:"+object.getString("email"));
						phoneTextView.setVisibility(0);
						phoneTextView.setText("Phone:"+object.getString("phone"));
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//message.setText("Please contact the donator!");
				//message.setVisibility(0);
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
			}
		});
	}
	// private void drawSerachPages() {
	// status = 0;
	// textView[0] = (TextView) findViewById(R.id.departText);
	// spinner[0] = (Spinner) findViewById(R.id.departSpinner);
	// adapter[0] = new ArrayAdapter<String>(this,
	// android.R.layout.simple_spinner_item, departs);
	// textView[1] = (TextView) findViewById(R.id.numberText);
	// spinner[1] = (Spinner) findViewById(R.id.numberSpinner);
	// adapter[1] = new ArrayAdapter<String>(this,
	// android.R.layout.simple_spinner_item, numbers);
	// textView[2] = (TextView) findViewById(R.id.sectionText);
	// spinner[2] = (Spinner) findViewById(R.id.sectionSpinner);
	// adapter[2] = new ArrayAdapter<String>(this,
	// android.R.layout.simple_spinner_item, sections);
	//
	// for (int i = 0; i < textView.length; i++) {
	//
	// adapter[i]
	// .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	//
	// spinner[i].setAdapter(adapter[i]);
	//
	// spinner[i]
	// .setOnItemSelectedListener(new SpinnerSelectedListener(i));
	//
	// spinner[i].setVisibility(View.VISIBLE);
	//
	// }
	// Button searchButton = (Button) findViewById(R.id.search);
	// searchButton.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View arg0) {
	// setContentView(R.layout.results);
	// drawDetailPages();
	// }
	// });
	// }
	//
	// private void drawDetailPages() {
	// status = 1;
	// Button requireButton = (Button) findViewById(R.id.require);
	// requireButton.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View arg0) {
	// setContentView(R.layout.results);
	// // check if you have enough credits.
	//
	// }
	// });
	// }
	//
	// class SpinnerSelectedListener implements OnItemSelectedListener {
	//
	// private int viewNum;
	//
	// public SpinnerSelectedListener(int num) {
	// viewNum = num;
	// }
	//
	// public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
	// long arg3) {
	// String content = new String();
	// switch (viewNum) {
	// case 0:
	// content = departs[arg2];
	// break;
	// case 1:
	// content = numbers[arg2];
	// break;
	// case 2:
	// content = sections[arg2];
	// break;
	// default:
	// break;
	// }
	// textView[viewNum].setText("Department:" + content);
	// }
	//
	// public void onNothingSelected(AdapterView<?> arg0) {
	// }
	// }
}
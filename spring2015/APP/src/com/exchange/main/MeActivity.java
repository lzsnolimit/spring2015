package com.exchange.main;

import org.apache.http.Header;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.manish.tabdemo.R;

public class MeActivity extends Activity {
	user myUser;
	int status;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myUser = new user(getApplicationContext());
		drawInitialPage();

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			switch (status) {
			case 2:
				drawLoginUpPage();
				break;
			case 0:
				break;
			default:
				break;
			}
		}
		return true;
	}

	public void drawInitialPage() {
		// setContentView(R.layout.activity_me);
		if (myUser.getStatus() == 0) {
			// setContentView(R.layout.sign_up);
			// Toast.makeText(getApplicationContext(), "here",
			// Toast.LENGTH_LONG).show();
			drawLoginUpPage();
		} else if (myUser.getStatus() == 1) {
			drawLoginedPage();
		}
	}

	public void drawLoginedPage() {
		status = 0;
		setContentView(R.layout.activity_me);
		TextView infoEditText = (TextView) findViewById(R.id.meInfo);
		TextView creditEditText = (TextView) findViewById(R.id.meCredit);
		creditEditText.setText(creditEditText.getText().toString()
				+ myUser.getCredit());
		infoEditText.setText(infoEditText.getText().toString()
				+ myUser.getEmail());
		Button loginOut = (Button) findViewById(R.id.button_login_out);
		loginOut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				myUser.loginOut();
				drawLoginUpPage();
			}
		});

		// donateEditText.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		//
		// requireEditText.setClickable(true);
		// requireEditText.setFocusable(true);
		// requireEditText.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
	}

	public void drawLoginPage() {
		status = 2;
		setContentView(R.layout.sign_in);
		Button submit = (Button) findViewById(R.id.userLoginInSubmit);
		submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final EditText user = (EditText) findViewById(R.id.userEmailEdit);
				final EditText pass = (EditText) findViewById(R.id.userPassEdit);
				if (user.getText().toString().equals("")
						|| pass.getText().toString().equals("")) {
					Toast.makeText(getApplicationContext(),
							"Please fill both fileds!", Toast.LENGTH_LONG)
							.show();
				} else {

					String urlString = "http://192.168.1.107/exchange/text_book.php?type=4&name="
							+ user.getText().toString()
							+ "&pass="
							+ pass.getText().toString();

					AsyncHttpClient client = new AsyncHttpClient();
					client.get(urlString, new AsyncHttpResponseHandler() {

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							// TODO Auto-generated method stub
							if (!new String(arg2).equals("0")) {
								// Toast.makeText(getApplicationContext(),"mima1"+pass.getText().toString(),Toast.LENGTH_LONG).show();
								myUser.login(user.getText().toString(), pass
										.getText().toString());
								drawLoginedPage();
							} else {
								Toast.makeText(getApplicationContext(),
										"Wrong password! ", Toast.LENGTH_LONG)
										.show();
							}
						}
					});

				}
			}
		});
	}

	public void drawLoginUpPage() {
		status = 1;
		setContentView(R.layout.sign_up);
		Button signUpButton = (Button) findViewById(R.id.registerRegisterSubmit);
		Button signInButton = (Button) findViewById(R.id.registerLoginIn);
		signInButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				drawLoginPage();
			}
		});
		signUpButton.setOnClickListener(new OnClickListener() {
			EditText emailEditText = (EditText) findViewById(R.id.registerEmailEdit);
			EditText passEditText = (EditText) findViewById(R.id.registerPassEdit);
			EditText firstNameEditText = (EditText) findViewById(R.id.registerFirstnameEdit);
			EditText lastNameEditText = (EditText) findViewById(R.id.registerLastnameEdit);

			// passEditText.setTransformationMethod();

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (emailEditText.getText().toString().equals("")
						|| passEditText.getText().toString().equals("")
						|| firstNameEditText.getText().toString().equals("")
						|| lastNameEditText.getText().toString().equals("")) {
					Toast.makeText(getApplicationContext(),
							"Please fille all fields!", Toast.LENGTH_LONG)
							.show();
					return;
				} else if (!myUser.checkEmail(emailEditText.getText()
						.toString())) {
					Toast.makeText(getApplicationContext(),
							"Please input a valid uwyo email!",
							Toast.LENGTH_LONG).show();
					return;
				} else if (passEditText.getText().toString().length() < 6) {
					Toast.makeText(getApplicationContext(),
							"Please input a password more than 6 charactors!",
							Toast.LENGTH_LONG).show();
					return;
				} else {
					String urlString = "http://192.168.1.107/exchange/text_book.php?type=5&email="
							+ emailEditText.getText().toString()
							+ "&pass="
							+ passEditText.getText().toString()
							+ "&first_name="
							+ firstNameEditText.getText().toString()
							+ "&last_name="
							+ lastNameEditText.getText().toString();
					AsyncHttpClient client = new AsyncHttpClient();
					client.get(urlString, new AsyncHttpResponseHandler() {

						@Override
						public void onSuccess(int arg0, Header[] arg1,
								byte[] arg2) {
							String resultString = new String(arg2);
							if (resultString.equals("0")) {
								Toast.makeText(getApplicationContext(),
										"Email occupied", Toast.LENGTH_LONG)
										.show();
							} else {
								// Toast.makeText(getApplicationContext(),
								// emailEditText.getText(),
								// Toast.LENGTH_LONG).show();
								// myUser.setStatus(1);
								myUser.login(
										emailEditText.getText().toString(),
										passEditText.getText().toString());
								drawLoginedPage();
							}
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							Toast.makeText(getApplicationContext(),
									"Connection error!", Toast.LENGTH_LONG)
									.show();
						}
					});
				}
			}
		});
	}
}
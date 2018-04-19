package com.adminx.bookstore;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.adminx.bookstore.API.WebServiceAPI;
import com.adminx.bookstore.config.AppConfig;
import com.adminx.bookstore.helper.SessionManager;
import com.adminx.bookstore.model.UserRequestModel;
import com.adminx.bookstore.model.UserResponseModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by admin-x on 6/7/15.
 */
public class LoginActivity extends Activity {

    private EditText etEmail;
    private EditText etPassword;
    private ProgressDialog pDialog;

    private SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText) findViewById(R.id.email);
        etPassword = (EditText) findViewById(R.id.password);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String email = etEmail.getText().toString();
                    if (!(email.trim().length() > 0)) {
                        etEmail.setError("You can't leave this empty");
                    } else if (!isValidEmailPattern(email)) {
                        etEmail.setError("Invalid Email");
                    } else {
                        etEmail.setError(null);
                    }
                }
            }
        });

        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String password = etPassword.getText().toString();
                    if (!(password.trim().length() > 0)) {
                        etPassword.setError("Please enter your password");
                    } else {
                        etPassword.setError(null);
                    }
                }

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                boolean isInputValid = true;
                if (!(email.trim().length() > 0)) {
                    etEmail.setError("Please enter your email");
                    isInputValid = false;
                }
                if (!(password.trim().length() > 0)) {
                    etPassword.setError("Please enter your password");
                    isInputValid = false;
                }
                if (isInputValid) {
                    checkLogin(email, password);

                }
            }
        });

        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    /**
     * function to verify login details in mysql db
     */
    private void checkLogin(final String email, final String password) {
        pDialog.setMessage("Logging in ...");
        showDialog();

        final UserRequestModel userRequestBody = new UserRequestModel();
        userRequestBody.setEmail(email);
        userRequestBody.setPassword(password);

        RestAdapter restAdapter = new RestAdapter
                .Builder()
                .setEndpoint(AppConfig.URL_LOGIN)
                .build();

        WebServiceAPI WebServiceAPI = restAdapter.create(WebServiceAPI.class);
        Callback<UserResponseModel> callback = new Callback<UserResponseModel>() {
            @Override
            public void success(UserResponseModel userResponseBody, retrofit.client.Response response) {
                hideDialog();

                if (!userResponseBody.getError()) {
                    // user successfully logged in

                    // Create login session
                    session.setLogin(true);

                    // Now store the user in SharedPreferences
                    session.setLoginInfo(userResponseBody.getUser());

                    // Launch main activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Error in login
                    Toast.makeText(getApplicationContext(), userResponseBody.getErrorMsg(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        };
        WebServiceAPI.getUserResponse(userRequestBody, callback);
    }

    // check email pattern
    private boolean isValidEmailPattern(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}

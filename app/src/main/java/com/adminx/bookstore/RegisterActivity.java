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
import retrofit.client.Response;

/**
 * Created by admin-x on 6/7/15.
 */
public class RegisterActivity extends Activity {

    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etPhone;
    private EditText etAddress;
    private ProgressDialog pDialog;
    private SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = (EditText) findViewById(R.id.name);
        etEmail = (EditText) findViewById(R.id.email);
        etPassword = (EditText) findViewById(R.id.password);
        etPhone = (EditText) findViewById(R.id.phone);
        etAddress = (EditText) findViewById(R.id.address);
        Button btnRegister = (Button) findViewById(R.id.btnRegister);
        Button btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String name = etName.getText().toString();
                    if (!(name.trim().length() > 0)) {
                        etName.setError("You can't leave this empty");
                    } else if (!isValidNamePattern(name)) {
                        etName.setError("Use only letters");
                    } else {
                        etName.setError(null);
                    }
                }
            }
        });

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
                        etPassword.setError("You can't leave this empty");
                    } else if (!(password.trim().length() >= 8)) {
                        etPassword.setError("Use at least 8 characters");
                    } else {
                        etPassword.setError(null);
                    }
                }

            }
        });

        etPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String password = etPhone.getText().toString();
                    if (!(password.trim().length() > 0)) {
                        etPhone.setError("You can't leave this empty");
                    } else {
                        etPhone.setError(null);
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
                        etPassword.setError("You can't leave this empty");
                    }else if (!(password.trim().length() >= 8)) {
                        etPassword.setError("Use at least 8 characters");
                    }else {
                        etPassword.setError(null);
                    }
                }

            }
        });

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String phone = etPhone.getText().toString();
                String address = etAddress.getText().toString();

                boolean InputValid = true;

                //check FullName input
                if (!(name.trim().length() > 0)) {
                    etName.setError("You can't leave this empty");
                    InputValid = false;
                } else if (!isValidNamePattern(name)) {
                    etName.setError("Use only letters");
                    InputValid = false;
                } else {
                    etName.setError(null);
                }

                //check Email input
                if (!(email.trim().length() > 0)) {
                    etEmail.setError("You can't leave this empty");
                    InputValid = false;
                } else if (!isValidEmailPattern(email)) {
                    etEmail.setError("Invalid Email");
                    InputValid = false;
                } else {
                    etEmail.setError(null);
                }

                //check password input
                if (!(password.trim().length() > 0)) {
                    etPassword.setError("You can't leave this empty");
                    InputValid = false;
                } else if (!(password.trim().length() >= 8)) {
                    etPassword.setError("Use at least 8 characters");
                    InputValid = false;
                } else {
                    etPassword.setError(null);
                }

                //check phone input
                if (!(phone.trim().length() > 0)) {
                    etPhone.setError("You can't leave this empty");
                    InputValid = false;
                } else {
                    etPhone.setError(null);
                }

                //check address input
                if (!(address.trim().length() > 0)) {
                    etAddress.setError("You can't leave this empty");
                    InputValid = false;
                } else {
                    etAddress.setError(null);
                }

                if (InputValid) {
                    registerUser(name, email, password, phone, address);
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * Function to store user in MySQL database
     */
    private void registerUser(final String name, final String email, final String password, final String phone, final String address) {

        pDialog.setMessage("Registering ...");
        showDialog();

        UserRequestModel userRequestBody = new UserRequestModel();
        userRequestBody.setName(name);
        userRequestBody.setEmail(email);
        userRequestBody.setPassword(password);
        userRequestBody.setPhone(phone);
        userRequestBody.setAddress(address);

        RestAdapter restAdapter = new RestAdapter
                .Builder()
                .setEndpoint(AppConfig.URL_REGISTER)
                .build();
        WebServiceAPI WebServiceAPI = restAdapter.create(WebServiceAPI.class);
        Callback<UserResponseModel> callback = new Callback<UserResponseModel>() {
            @Override
            public void success(UserResponseModel o, Response response) {
                hideDialog();
                UserResponseModel userResponseBody = o;
                if (!userResponseBody.getError()) {
                    // Create login session
                    session.setLogin(true);
                    // User successfully stored in MySQL

                    // Now store the user in SharedPreferences
                    session.setLoginInfo(userResponseBody.getUser());

                    // Launch main activity
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), userResponseBody.getErrorMsg(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideDialog();
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        WebServiceAPI.getUserResponse(userRequestBody, callback);
    }

    // check Name pattern
    private boolean isValidNamePattern(String name) {
        String NAME_PATTERN = "[^!@\\\\#$£€¥%^&*()_+×÷=<>:,'\"|/`?~\\[\\]{}.\\d]*";

        Pattern pattern = Pattern.compile(NAME_PATTERN);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
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

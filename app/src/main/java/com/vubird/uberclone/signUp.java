package com.vubird.uberclone;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;


public class signUp extends AppCompatActivity
{
    private EditText usernameBox, passwordBox, emailBox;
    private RadioGroup radioGroup;
    String radioBtnValue;

@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sing Up");
        usernameBox = findViewById(R.id.usernameBox);
        passwordBox = findViewById(R.id.passwordBox);
        emailBox =    findViewById(R.id.EmailBox);
        radioGroup = findViewById(R.id.radioGroup);
        emailBox.setOnKeyListener(new View.OnKeyListener() {
@Override
public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

        if(keyCode==KeyEvent.KEYCODE_ENTER && keyEvent.getAction()==KeyEvent.ACTION_DOWN)
        {
        createNewUser(view);

        }

        return false;
        }
        });

        if(ParseUser.getCurrentUser()!=null)
        {
            Intent i = new Intent(this, PassengerActivity.class);
            finish();
            startActivity(i);

        }
        }

    public void createNewUser(View view)
    {
        int radioId =radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(radioId);
        if(usernameBox.getText().toString().equals("") || passwordBox.getText().toString().equals("")|| emailBox.getText().toString().equals(""))
        {
            FancyToast.makeText(this, "Oops! Field is Empty " , FancyToast.LENGTH_LONG, FancyToast.WARNING, true).show();
            return;
        }
        ParseUser user = new ParseUser();
        user.setUsername(usernameBox.getText().toString().trim());
        user.setPassword(passwordBox.getText().toString().trim());
        user.setEmail(emailBox.getText().toString().trim());
        user.put("category", radioButton.getText().toString().trim());
        radioBtnValue =  radioButton.getText().toString().trim();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing up "+usernameBox.getText().toString() + "......");
        progressDialog.show();
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null)
                {
                    //User Signed in
                    FancyToast.makeText(getApplicationContext(), "Welcome back! " + usernameBox.getText().toString(), FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
                    progressDialog.dismiss();
                    gotoWelcome();

                }
            }
        });
    }

    public  void gotoLogin(View view)
    {
        Intent i = new Intent(this, login.class);
        startActivity(i);
    }

    public void gotoWelcome()
    {
        if(radioBtnValue.equals("Custome"))
        {
            Intent i = new Intent(this, PassengerActivity.class);
            finish();
            startActivity(i);
        }
        else if(radioBtnValue.equals("Driver"))
        {
            Intent i = new Intent(this, DriverRequestList.class);
            finish();
            startActivity(i);
        }
    }

    public  void rootClick(View view)
    {
        try
        {
            InputMethodManager inputMethod = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethod.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        catch (Exception e)
        { }
    }

}

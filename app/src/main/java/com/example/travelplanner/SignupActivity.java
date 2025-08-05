// SignupActivity.java
package com.example.travelplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {
    private EditText emailInput, usernameInput, passwordInput;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        userManager = new UserManager(this);

        emailInput = findViewById(R.id.signup_email);
        usernameInput = findViewById(R.id.signup_name);
        passwordInput = findViewById(R.id.signup_password);
        Button signupButton = findViewById(R.id.loginbutton);

        signupButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (!userManager.isEmailValid(email)) {
                emailInput.setError("ایمیل معتبر نیست");
                return;
            }

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "نام کاربری و رمز عبور الزامی است", Toast.LENGTH_SHORT).show();
                return;
            }

            userManager.saveUser(email, username, password);
            Toast.makeText(this, "ثبت‌نام با موفقیت انجام شد", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }
}



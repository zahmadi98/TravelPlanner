// MainActivity.java
package com.example.travelplanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.developer.gbuttons.GoogleSignInButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.internal.GoogleSignInOptionsExtensionParcelable;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {
    private EditText usernameInput, passwordInput;
    private UserManager userManager;
    private GoogleSignInButton googleBtn;
    private GoogleSignInClient gClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userManager = new UserManager(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView signupText = findViewById(R.id.signinText);
        signupText.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        usernameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.loginbutton);
        googleBtn = findViewById(R.id.googlebtn);

        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (!userManager.userExists(username)) {
                Toast.makeText(this, "کاربری با این نام وجود ندارد", Toast.LENGTH_SHORT).show();
            } else if (!userManager.isPasswordCorrect(username, password)) {
                Toast.makeText(this, "رمز عبور اشتباه است", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "ورود موفق", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            }
        });
        // تنظیمات گوگل ساین
        GoogleSignInOptions gOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gClient = GoogleSignIn.getClient(this, gOptions);

        // بررسی اینکه قبلاً کاربر وارد شده است
        GoogleSignInAccount gAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (gAccount != null) {
            goToHome();
        }

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            task.getResult(ApiException.class); // اگر موفق بود
                            goToHome();
                        } catch (ApiException e) {
                            Toast.makeText(MainActivity.this, "ورود با گوگل ناموفق بود", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
        googleBtn.setOnClickListener(view -> {
            Intent signInIntent = gClient.getSignInIntent();
            activityResultLauncher.launch(signInIntent);
        });
    }
    private void goToHome() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}

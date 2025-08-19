package com.example.travelplanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.developer.gbuttons.GoogleSignInButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    private UserManager userManager;

    /* ---------- Google Sign-In launcher ---------- */
    private final ActivityResultLauncher<Intent> googleLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Task<GoogleSignInAccount> task =
                            GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        if (account != null && account.getIdToken() != null) {
                            firebaseAuthWithGoogle(account.getIdToken());
                        }
                    } catch (ApiException e) {
                        Log.w("GoogleSignIn", "signInResult:failed code=" + e.getStatusCode());
                        Toast.makeText(this, "ورود با گوگل ناموفق بود", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        userManager = new UserManager(this);

        if (mAuth.getCurrentUser() != null) {
            goToHome();
            return;
        }

        initUI();
        initGoogleSignIn();
    }

    private void initUI() {
        EditText usernameInput = findViewById(R.id.username);
        EditText passwordInput = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.loginbutton);
        GoogleSignInButton googleBtn = findViewById(R.id.googlebtn);

        /* ورود معمولی */
        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            if (!userManager.userExists(username)) {
                Toast.makeText(this, "کاربری با این نام وجود ندارد", Toast.LENGTH_SHORT).show();
            } else if (!userManager.isPasswordCorrect(username, password)) {
                Toast.makeText(this, "رمز عبور اشتباه است", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "ورود موفق", Toast.LENGTH_SHORT).show();
                goToHome();
            }
        });

        googleBtn.setOnClickListener(v -> signInWithGoogle());
    }

    private void initGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        googleLauncher.launch(signInIntent);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this, "ورود موفق: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        goToHome();
                    } else {
                        Log.w("FirebaseAuth", "signInWithCredential:failure", task.getException());
                        Toast.makeText(this, "ورود با گوگل موفق نبود", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToHome() {
        startActivity(new Intent(MainActivity.this, HomeActivity.class));
        finish();
    }
}
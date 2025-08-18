package com.example.travelplanner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private MaterialButton googleBtn;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private EditText usernameInput, passwordInput;
    private UserManager userManager;

    private final ActivityResultLauncher<IntentSenderRequest> signInLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartIntentSenderForResult(),
                    result -> {
                        try {
                            SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                            String idToken = credential.getGoogleIdToken();
                            if (idToken != null) {
                                firebaseAuthWithGoogle(idToken);
                            } else {
                                Toast.makeText(this, "ورود با گوگل موفق نبود", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("GoogleSignIn", "Sign-in failed", e);
                            Toast.makeText(this, "ورود با گوگل موفق نبود", Toast.LENGTH_SHORT).show();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        userManager = new UserManager(this);
        mAuth = FirebaseAuth.getInstance();

        // اگر قبلاً لاگین کرده بود
        if (mAuth.getCurrentUser() != null) {
            goToHome();
        } else {
            initUI();
            initGoogleSignIn();
        }
    }

    private void initUI() {
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

        // ورود معمولی با نام کاربری و رمز
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
    }

    private void initGoogleSignIn() {
        oneTapClient = Identity.getSignInClient(this);

        signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId(getString(R.string.default_web_client_id))
                                .setFilterByAuthorizedAccounts(false)
                                .build()
                )
                .setAutoSelectEnabled(false)
                .build();

        googleBtn.setOnClickListener(v -> startSignIn());
    }

    private void startSignIn() {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        signInLauncher.launch(
                                new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build()
                        );
                    } catch (Exception e) {
                        Log.e("GoogleSignIn", "Launcher failed", e);
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.e("GoogleSignIn", "Sign-in failed", e);
                    Toast.makeText(this, "ورود با گوگل موفق نبود", Toast.LENGTH_SHORT).show();
                });
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

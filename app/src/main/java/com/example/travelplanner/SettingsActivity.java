package com.example.travelplanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    Button logoutBtn;
    SignInClient oneTapClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        logoutBtn = findViewById(R.id.btnLogout);

        oneTapClient = Identity.getSignInClient(this);

        logoutBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("خروج از حساب")
                    .setMessage("آیا مطمئن هستید که می‌خواهید خارج شوید؟")
                    .setPositiveButton("بله", (dialog, which) -> {
                        // خروج از گوگل
                        oneTapClient.signOut().addOnCompleteListener(task -> {
                            // خروج از Firebase
                            FirebaseAuth.getInstance().signOut();

                            Toast.makeText(SettingsActivity.this, "با موفقیت خارج شدید", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finishAffinity();
                        });
                    })
                    .setNegativeButton("خیر", null)
                    .show();
        });
    }
}

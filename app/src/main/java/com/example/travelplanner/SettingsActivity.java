package com.example.travelplanner;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;

public class SettingsActivity extends AppCompatActivity {

    Button logoutBtn;
    SignInClient oneTapClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        logoutBtn = findViewById(R.id.btnLogout);

        // ایجاد client برای Google Identity Services
        oneTapClient = Identity.getSignInClient(this);

        logoutBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(SettingsActivity.this)
                    .setTitle("خروج از حساب")
                    .setMessage("آیا مطمئن هستید که می‌خواهید خارج شوید؟")
                    .setPositiveButton("بله", (dialog, which) -> {
                        // خروج از حساب با Google Identity Services
                        oneTapClient.signOut().addOnCompleteListener(task -> {
                            Toast.makeText(SettingsActivity.this, "با موفقیت خارج شدید", Toast.LENGTH_SHORT).show();
                            finish(); // برگرد به صفحه قبلی (HomeActivity)
                        });
                    })
                    .setNegativeButton("خیر", null)
                    .show();
        });
    }
}

package com.example.travelplanner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // ایمیل
        TextView emailText = findViewById(R.id.emailText);
        emailText.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:ms.zeinab.ahmadi@gmail.com"));
            startActivity(intent);
        });

        // گیت هاب
        TextView githubText = findViewById(R.id.githubText);
        githubText.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/zahmadi98"));
            startActivity(intent);
        });

        // لینکدین
        TextView linkedinText = findViewById(R.id.linkedinText);
        linkedinText.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.linkedin.com/in/zeinab-ahmadi-27a66b296"));
            startActivity(intent);
        });
    }
}


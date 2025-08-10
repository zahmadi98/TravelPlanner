package com.example.travelplanner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class UploadPhotoFragment extends Fragment {

    private ImageView uploadIcon, uploadedImage;
    private Button btnChoosePhoto;
    private ProgressBar uploadProgressBar;

    private ActivityResultLauncher<String> galleryLauncher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upload_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        uploadIcon = view.findViewById(R.id.uploadIcon);
        uploadedImage = view.findViewById(R.id.uploadedImage);
        btnChoosePhoto = view.findViewById(R.id.btnChoosePhoto);
        uploadProgressBar = view.findViewById(R.id.uploadProgressBar);

        uploadProgressBar.setProgress(0);

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        onPhotoSelected(uri);
                    }
                });

        btnChoosePhoto.setOnClickListener(v -> openGallery());
    }

    private void openGallery() {
        galleryLauncher.launch("image/*");
    }

    private void onPhotoSelected(Uri photoUri) {
        // تغییر آیکون وسط به آیکون آپلود در حال انجام
        uploadIcon.setImageResource(R.drawable.baseline_drive_folder_upload_24);

        // نمایش عکس انتخاب شده و مخفی کردن آیکون آپلود
        uploadIcon.setVisibility(View.GONE);
        uploadedImage.setVisibility(View.VISIBLE);
        uploadedImage.setImageURI(photoUri);

        // شروع آپلود و نوار پیشرفت شبیه سازی شده
        startUploadingPhoto(photoUri);
    }

    private void startUploadingPhoto(Uri photoUri) {
        // شبیه‌سازی آپلود با افزایش تدریجی نوار پیشرفت
        new Thread(() -> {
            for (int progress = 0; progress <= 100; progress += 10) {
                int finalProgress = progress;
                requireActivity().runOnUiThread(() -> {
                    uploadProgressBar.setProgress(finalProgress);

                    if (finalProgress == 100) {
                        // آپلود تموم شد: تغییر آیکون به حالت عادی
                        uploadIcon.setImageResource(R.drawable.baseline_cloud_upload_24);
                        uploadIcon.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "آپلود عکس کامل شد!", Toast.LENGTH_SHORT).show();
                    }
                });
                try {
                    Thread.sleep(300); // وقفه بین آپدیت‌ها
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

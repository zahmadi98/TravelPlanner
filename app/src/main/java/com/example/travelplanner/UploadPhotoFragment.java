package com.example.travelplanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UploadPhotoFragment extends Fragment {

    private ImageView uploadIcon, uploadedImage;
    private Button btnChoosePhoto, btnSubmitPhoto;
    private ProgressBar uploadProgressBar;
    private com.google.android.material.textfield.MaterialAutoCompleteTextView choseTrip;

    private List<HomeFragment.Trip> tripList = new ArrayList<>();
    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onPhotoSelected);
    private String selectedTripName = "";
    private Uri currentPhotoUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upload_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // بستن با کلیک روی پس‌زمینه
        View background = requireActivity().findViewById(R.id.root_home);
        if (background != null) {
            background.setOnClickListener(v -> {
                background.setVisibility(View.GONE);
                requireActivity().getSupportFragmentManager().popBackStack();
            });
        }

        // init views
        choseTrip      = view.findViewById(R.id.choseTrip);
        uploadIcon     = view.findViewById(R.id.uploadIcon);
        uploadedImage  = view.findViewById(R.id.uploadedImage);
        btnChoosePhoto = view.findViewById(R.id.btnChoosePhoto);
        btnSubmitPhoto = view.findViewById(R.id.btnSubmitPhoto);
        uploadProgressBar = view.findViewById(R.id.uploadProgressBar);

        loadTrips();
        setupTripDropdown();

        choseTrip.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && !choseTrip.isPopupShowing()) {
                choseTrip.showDropDown();
                return true;
            }
            return false;
        });

        choseTrip.setOnItemClickListener((parent, view1, position, id) ->
                selectedTripName = (String) parent.getItemAtPosition(position));

        btnChoosePhoto.setOnClickListener(v -> galleryLauncher.launch("image/*"));
        btnSubmitPhoto.setOnClickListener(v -> savePhotoToFavorites());
    }

    private void loadTrips() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("MyTrips", Context.MODE_PRIVATE);
        String json = prefs.getString("trip_list", null);
        if (json != null) {
            tripList = new Gson().fromJson(json,
                    new TypeToken<List<HomeFragment.Trip>>() {}.getType());
        }
    }

    private void setupTripDropdown() {
        List<String> names = new ArrayList<>();
        for (HomeFragment.Trip t : tripList) names.add(t.getName());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(), R.layout.dropdown_item, R.id.dropdownItemText, names);
        choseTrip.setAdapter(adapter);
        choseTrip.setText("", false);
    }

    private void onPhotoSelected(Uri uri) {
        if (uri == null) return;
        currentPhotoUri = uri;
        uploadedImage.setImageURI(uri);
        uploadIcon.setVisibility(View.GONE);
        uploadedImage.setVisibility(View.VISIBLE);
        uploadProgressBar.setProgress(0);
        startFakeUpload();
    }

    private void startFakeUpload() {
        new Thread(() -> {
            for (int p = 0; p <= 100; p += 10) {
                int progress = p;
                requireActivity().runOnUiThread(() -> uploadProgressBar.setProgress(progress));
                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            }
            requireActivity().runOnUiThread(() ->
                    Toast.makeText(getContext(), "آپلود عکس کامل شد!", Toast.LENGTH_SHORT).show());
        }).start();
    }

    private void savePhotoToFavorites() {
        if (selectedTripName.isEmpty()) {
            Toast.makeText(getContext(), "ابتدا سفر را انتخاب کنید", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentPhotoUri == null) {
            Toast.makeText(getContext(), "عکسی انتخاب نشده", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = requireContext()
                .getSharedPreferences("FAVORITE_PHOTOS", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("list", null);
        Type type = new TypeToken<ArrayList<FavoritePhoto>>() {}.getType();
        ArrayList<FavoritePhoto> list = (json == null) ? new ArrayList<>()
                : gson.fromJson(json, type);

        list.add(new FavoritePhoto(selectedTripName, currentPhotoUri.toString()));
        prefs.edit().putString("list", gson.toJson(list)).apply();

        Toast.makeText(getContext(), "ذخیره شد", Toast.LENGTH_SHORT).show();

        // اطلاع‌رسانی به FavoriteFragment
        requireActivity().getSupportFragmentManager()
                .setFragmentResult("newPhoto", new Bundle());

        View popupRoot = getActivity().findViewById(R.id.root_home);
        if (popupRoot != null) {
            popupRoot.setVisibility(View.GONE);
        }
        getParentFragmentManager().popBackStack();
    }
}
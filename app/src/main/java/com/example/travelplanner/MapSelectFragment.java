package com.example.travelplanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapSelectFragment extends Fragment {

    private MapView mapView;
    private Marker selectedMarker;
    private GeoPoint selectedPoint;
    private Button btnConfirm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        return inflater.inflate(R.layout.fragment_map_select, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnConfirm = view.findViewById(R.id.btnConfirm);
        mapView = view.findViewById(R.id.map);
        mapView.setMultiTouchControls(true);

        checkLocationPermissions();

        // نقطه شروع (مرکز ایران)
        GeoPoint startPoint = new GeoPoint(32.4279, 53.6880);
        mapView.getController().setZoom(5.0);
        mapView.getController().setCenter(startPoint);

        mapView.invalidate();

        mapView.setOnClickListener(v -> Toast.makeText(getContext(), "روی نقشه کلیک کن!", Toast.LENGTH_SHORT).show());

        mapView.getOverlays().clear();

        mapView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                GeoPoint point = (GeoPoint) mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY());
                if (selectedMarker != null) {
                    mapView.getOverlays().remove(selectedMarker);
                }
                selectedPoint = point;
                selectedMarker = new Marker(mapView);
                selectedMarker.setPosition(point);
                selectedMarker.setTitle("مکان انتخاب شده");
                mapView.getOverlays().add(selectedMarker);
                mapView.invalidate();
            }
            return true;
        });

        btnConfirm.setOnClickListener(v -> {
            if (selectedPoint != null) {
                String locationText = "Lat: " + selectedPoint.getLatitude() + ", Lon: " + selectedPoint.getLongitude();
                Toast.makeText(getContext(), "مکان انتخاب شد:\n" + locationText, Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                bundle.putString("location", locationText);

                getParentFragmentManager().setFragmentResult("locationKey", bundle);
                getParentFragmentManager().popBackStack();
            } else {
                Toast.makeText(getContext(), "لطفا ابتدا مکان را انتخاب کنید", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
                if (Boolean.TRUE.equals(fineLocationGranted) && Boolean.TRUE.equals(coarseLocationGranted)) {
                    startLocationRelatedTask();
                } else {
                    Toast.makeText(getContext(), "مجوز دسترسی به موقعیت داده نشد!", Toast.LENGTH_SHORT).show();
                }
            });

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        } else {
            startLocationRelatedTask();
        }
    }

    private void startLocationRelatedTask() {
        Toast.makeText(getContext(), "مجوزهای لوکیشن داده شدند، آماده کار!", Toast.LENGTH_SHORT).show();
        // اینجا میتونی کاری مثل گرفتن لوکیشن فعلی یا کار دیگه‌ای انجام بدی
    }
}

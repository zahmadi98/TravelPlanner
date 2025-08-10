package com.example.travelplanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;

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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapSelectFragment extends Fragment {

    private MapView mapView;
    private Marker selectedMarker;
    private GeoPoint selectedPoint;
    private Button btnConfirm, btnCurrentLocation;
    private FusedLocationProviderClient fusedLocationClient;

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
        btnCurrentLocation = view.findViewById(R.id.btnCurrentLocation);
        mapView = view.findViewById(R.id.map);
        mapView.setMultiTouchControls(true);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        checkLocationPermissions();

        // نقطه شروع (مرکز ایران)
        GeoPoint startPoint = new GeoPoint(32.4279, 53.6880);
        mapView.post(() -> {
            mapView.getController().setZoom(5.0);
            mapView.getController().animateTo(startPoint);
        });

        // انتخاب مکان با لمس
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
            return false;
        });

        // دکمه تأیید
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

        // دکمه مکان فعلی
        btnCurrentLocation.setOnClickListener(v -> startLocationRelatedTask());
    }

    private void checkLocationPermissions() {
        // اینجا می‌تونی سیستم درخواست مجوز رو اضافه کنی
    }

    private void startLocationRelatedTask() {
        Toast.makeText(getContext(), "مجوزهای لوکیشن داده شدند، آماده کار!", Toast.LENGTH_SHORT).show();

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();

                            GeoPoint currentLocation = new GeoPoint(latitude, longitude);
                            mapView.getController().setZoom(15.0);
                            mapView.getController().animateTo(currentLocation);

                            // مارکر مکان فعلی
                            if (selectedMarker != null) {
                                mapView.getOverlays().remove(selectedMarker);
                            }
                            selectedMarker = new Marker(mapView);
                            selectedMarker.setPosition(currentLocation);
                            selectedMarker.setTitle("مکان فعلی من");
                            mapView.getOverlays().add(selectedMarker);
                            mapView.invalidate();

                            Toast.makeText(getContext(), "موقعیت فعلی: " + latitude + ", " + longitude, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "موقعیت فعلی پیدا نشد", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "خطا در دریافت موقعیت: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "مجوز لوکیشن داده نشده", Toast.LENGTH_SHORT).show();
        }
    }
}

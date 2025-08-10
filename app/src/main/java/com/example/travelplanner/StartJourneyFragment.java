package com.example.travelplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class StartJourneyFragment extends Fragment {

    public StartJourneyFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start_journey, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View background = requireActivity().findViewById(R.id.root_home);
        background.setOnClickListener(v -> {
            background.setVisibility(View.GONE);
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        Button btnSetAlarm = view.findViewById(R.id.btnSetAlarm);

        btnSetAlarm.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new MapSelectFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // گرفتن نتیجه انتخاب مکان
        getParentFragmentManager().setFragmentResultListener("locationKey", this, (requestKey, bundle) -> {
            String location = bundle.getString("location");
            EditText editText = view.findViewById(R.id.showAlarm);
            editText.setText(location);
        });
    }


}

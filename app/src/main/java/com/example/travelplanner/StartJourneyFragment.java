package com.example.travelplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class StartJourneyFragment extends Fragment {

    public StartJourneyFragment() {
        // Constructor باید خالی باشه!
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

        // وقتی روی بک‌گراند کلیک شد، مخفی شه
        background.setOnClickListener(v -> {
            background.setVisibility(View.GONE);
            // و اگه خواستی فرگمنت رو هم از استک برداری:
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

}

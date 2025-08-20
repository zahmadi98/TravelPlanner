package com.example.travelplanner;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoriteAdapter adapter;
    private ArrayList<FavoritePhoto> photoList = new ArrayList<>();

    private View emptyView;
    private View listContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView   = view.findViewById(R.id.recyclerFavorite);
        emptyView      = view.findViewById(R.id.emptyView);
        listContainer  = view.findViewById(R.id.photoListContainer);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new FavoriteAdapter(photoList);
        recyclerView.setAdapter(adapter);

        loadPhotos();
        toggleEmptyView();

        // هر بار که از UploadPhotoFragment برگشتیم، لیست را تازه کنیم
        getParentFragmentManager()
                .setFragmentResultListener("newPhoto", getViewLifecycleOwner(),
                        (requestKey, result) -> {
                            loadPhotos();
                            adapter.notifyDataSetChanged();
                            toggleEmptyView();
                        });
    }

    private void loadPhotos() {
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("FAVORITE_PHOTOS", Context.MODE_PRIVATE);
        String json = prefs.getString("list", null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<FavoritePhoto>>() {}.getType();
            photoList.clear();
            photoList.addAll(new Gson().fromJson(json, type));
        }
    }

    private void toggleEmptyView() {
        if (photoList.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            listContainer.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            listContainer.setVisibility(View.VISIBLE);
        }
    }

    /* ---------- Adapter ---------- */
    private static class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.Holder> {

        private final ArrayList<FavoritePhoto> data;

        FavoriteAdapter(ArrayList<FavoritePhoto> data) { this.data = data; }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_favorite_photo, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            FavoritePhoto item = data.get(position);
            holder.txtName.setText(item.getTripName());
            Glide.with(holder.itemView.getContext())
                    .load(Uri.parse(item.getPhotoUri()))
                    .into(holder.imgPhoto);
        }

        @Override
        public int getItemCount() { return data.size(); }

        static class Holder extends RecyclerView.ViewHolder {
            ImageView imgPhoto;
            TextView txtName;

            Holder(@NonNull View itemView) {
                super(itemView);
                imgPhoto = itemView.findViewById(R.id.imgFavorite);
                txtName  = itemView.findViewById(R.id.txtTripName);
            }
        }
    }
}
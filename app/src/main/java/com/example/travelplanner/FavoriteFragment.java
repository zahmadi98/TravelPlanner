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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoriteAdapter adapter;
    private final ArrayList<FavoritePhoto> photoList = new ArrayList<>();

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

    public void toggleEmptyView() {
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

            // فرمت تاریخ
            String date = new SimpleDateFormat("yyyy/MM/dd  HH:mm", Locale.getDefault())
                    .format(new Date(item.getTimestamp()));
            holder.txtDate.setText(date);

            Glide.with(holder.itemView.getContext())
                    .load(Uri.parse(item.getPhotoUri()))
                    .into(holder.imgPhoto);

            holder.btnDelete.setOnClickListener(v -> {
                new androidx.appcompat.app.AlertDialog.Builder(holder.itemView.getContext())
                        .setTitle("حذف عکس")
                        .setMessage("آیا از حذف این عکس مطمئن هستید؟")
                        .setPositiveButton("بله", (dialog, which) -> {
                            // حذف قطعی
                            data.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, data.size());

                            SharedPreferences prefs = holder.itemView.getContext()
                                    .getSharedPreferences("FAVORITE_PHOTOS", Context.MODE_PRIVATE);
                            prefs.edit()
                                    .putString("list", new Gson().toJson(data))
                                    .apply();

                            if (holder.itemView.getContext() instanceof FragmentActivity) {
                                FragmentActivity act = (FragmentActivity) holder.itemView.getContext();
                                Fragment f = act.getSupportFragmentManager()
                                        .findFragmentById(R.id.fragment_container);
                                if (f instanceof FavoriteFragment) {
                                    ((FavoriteFragment) f).toggleEmptyView();
                                }
                            }
                        })
                        .setNegativeButton("خیر", null)
                        .show();
            });
        }

        @Override
        public int getItemCount() { return data.size(); }

        static class Holder extends RecyclerView.ViewHolder {
            ImageView imgPhoto, btnDelete;
            TextView txtName, txtDate;

            Holder(@NonNull View itemView) {
                super(itemView);
                imgPhoto = itemView.findViewById(R.id.imgFavorite);
                txtName  = itemView.findViewById(R.id.txtTripName);
                btnDelete = itemView.findViewById(R.id.btnDelete);
                txtDate   = itemView.findViewById(R.id.txtDate);
            }
        }
    }
}
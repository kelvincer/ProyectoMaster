package com.example.proyectomaster.detail.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyectomaster.CommonHelper;
import com.example.proyectomaster.lib.di.DaggerLibsComponent;
import com.example.proyectomaster.photo.PhotoActivity;
import com.example.proyectomaster.R;
import com.example.proyectomaster.detail.entities.Photo;
import com.example.proyectomaster.detail.entities.Result;
import com.example.proyectomaster.detail.fragments.listener.PhotoClickListener;
import com.example.proyectomaster.lib.ImageLoader;
import com.example.proyectomaster.lib.di.LibsModule;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PhotosFragment extends Fragment implements PhotoClickListener {

    Unbinder unbinder;
    @BindView(R.id.rcv_photos)
    RecyclerView rcvPhotos;
    Result result;
    @Inject
    ImageLoader imageLoader;

    public static Fragment getInstance(Result result) {
        Fragment fragment = new PhotosFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(CommonHelper.RESULT, result);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        result = (Result) bundle.getSerializable(CommonHelper.RESULT);
        setupInjection();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rcvPhotos.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rcvPhotos.setAdapter(new PhotosAdapter(result, imageLoader, this));
        int spanCount = 2; // 3 columns
        int spacing = 50; // 50px
        boolean includeEdge = true;
        rcvPhotos.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setupInjection() {

        DaggerLibsComponent.builder()
                .libsModule(new LibsModule(getActivity()))
                .build()
                .inject(this);
    }

    @Override
    public void onPhotoItemClickListner(Photo photo) {
        Intent intent = new Intent(getActivity(), PhotoActivity.class);
        intent.putExtra(CommonHelper.PHOTO_REF, photo.getPhotoReference());
        startActivity(intent);
    }
}

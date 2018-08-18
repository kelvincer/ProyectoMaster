package com.example.proyectomaster.detail.fragments.notes.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.proyectomaster.CommonHelper;
import com.example.proyectomaster.R;
import com.example.proyectomaster.app.MainApplication;
import com.example.proyectomaster.detail.activity.di.DetailApiModule;
import com.example.proyectomaster.detail.activity.di.DetailModule;
import com.example.proyectomaster.detail.activity.ui.DetailActivity;
import com.example.proyectomaster.detail.entities.Result;
import com.example.proyectomaster.detail.fragments.notes.NoteFragmentPresenter;
import com.example.proyectomaster.detail.fragments.notes.adapter.NotesFirestoreAdapter;
import com.example.proyectomaster.detail.fragments.notes.di.NotesFragmentModule;
import com.example.proyectomaster.detail.fragments.notes.entities.Comment;
import com.example.proyectomaster.lib.ImageLoader;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NotesFragment extends Fragment implements NoteFragmentView {

    private static final String TAG = NotesFragment.class.getSimpleName();
    Result result;
    NotesFirestoreAdapter adapter;
    @BindView(R.id.ryv_notes)
    RecyclerView ryvNotes;
    @BindView(R.id.lnl_no_notes)
    LinearLayout lnlNoNotes;
    Unbinder unbinder;

    @Inject
    NoteFragmentPresenter presenter;
    @Inject
    ImageLoader imageLoader;

    public static Fragment getInstance(Result result) {

        Fragment fragment = new NotesFragment();
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
        presenter.onCreate();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadNotes();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    private void setupInjection() {
        /*DaggerNoteFragmentComponent.builder()
                .notesFragmentModule(new NotesFragmentModule(this))
                .build()
                .inject(this);*/
        MainApplication.getAppComponent()
                .newDetailComponent(new DetailApiModule(), new DetailModule(((DetailActivity) getActivity()).getView()))
                .newNoteFragmentComponent(new NotesFragmentModule(this))
                .inject(this);
    }

    private void loadNotes() {
        presenter.getNotes(result.getPlaceId());
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayNotes(FirestoreRecyclerOptions<Comment> options) {
        adapter = new NotesFirestoreAdapter(options, imageLoader);
        // ryvFavoritos.setLayoutManager(new GridLayoutManager(getContext(), 2));
        ryvNotes.setLayoutManager(new LinearLayoutManager(getContext()));
        ryvNotes.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        ryvNotes.setAdapter(adapter);
        //ryvFavoritos.addItemDecoration(new ItemOffsetDecoration(2));
        adapter.startListening();
        ryvNotes.setVisibility(View.VISIBLE);
        lnlNoNotes.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}

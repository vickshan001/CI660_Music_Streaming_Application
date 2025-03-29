package com.example.musicapplication;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapplication.adapter.AlbumAdapter;
import com.example.musicapplication.adapter.TabAdapter;
import com.example.musicapplication.model.Album;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AlbumListFragment extends Fragment implements AlbumAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private List<Album> albumList = new ArrayList<>();
    private AlbumAdapter adapter;

    private int albumId;
    private ViewPager2 viewPager;

    private void fetchAlbumsFromFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("album");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                albumList.clear();
                for (DataSnapshot albumSnapshot : dataSnapshot.getChildren()) {
                    Album album = albumSnapshot.getValue(Album.class);
                    albumList.add(album);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album_list, container, false);
        recyclerView = view.findViewById(R.id.album_list);

        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new AlbumAdapter(requireContext(), albumList, AlbumListFragment.this, albumId);
        recyclerView.setAdapter(adapter);

        viewPager = requireActivity().findViewById(R.id.viewPagerMain);

        Bundle bundle = getArguments();
        fetchAlbumsFromFirebase();

        return view;
    }

    @Override
    public void onItemClick(int position) {
        if (position >= 0 && position < albumList.size()) {
            Album clickedAlbum = albumList.get(position);
            int albumId = clickedAlbum.getAlbumId();

            Bundle bundle = new Bundle();
            bundle.putInt("AlbumId", albumId);

            TabAdapter tabAdapter = (TabAdapter) viewPager.getAdapter();
            if (tabAdapter != null) {

                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                AlbumsFragment albumsFragment = new AlbumsFragment();
                albumsFragment.setArguments(bundle);

                transaction.replace(R.id.albumviewcontainer, albumsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }
    }
}

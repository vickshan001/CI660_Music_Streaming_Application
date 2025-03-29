package com.example.musicapplication;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.musicapplication.adapter.SongAdapter;
import com.example.musicapplication.adapter.TabAdapter;
import com.example.musicapplication.model.Song;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class SongsListFragment extends Fragment implements SongAdapter.OnItemClickListener {

    private ViewPager2 viewPager;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private int songKey;
    private List<Song> songsList = new ArrayList<>();
    private List<Song> filteredSongsList = new ArrayList<>(); // New filtered list
    private SongAdapter adapter;
    private FloatingActionButton playAllButton;

    private void fetchSongsFromFirebase(Bundle bundle) {
        final int albumId = bundle != null ? bundle.getInt("albumId", -1) : -1;
        final String searchText = bundle != null ? bundle.getString("searchText") : null;

        databaseReference = FirebaseDatabase.getInstance().getReference("song");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                songsList.clear();

                for (DataSnapshot songSnapshot : dataSnapshot.getChildren()) {
                    songKey = Integer.parseInt(songSnapshot.getKey());
                    Song song = songSnapshot.getValue(Song.class);
                    song.setSongKey(songKey);

                    if (checkSongCriteria(song, albumId, searchText)) {
                        songsList.add(song);
                    }
                }

                // Update the adapter with the filtered song list
                adapter = new SongAdapter(requireContext(), songsList, SongsListFragment.this, songKey);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }




    private boolean checkSongCriteria(Song song, int albumId, String searchText) {
        if (albumId != -1 && song.getAlbumId() != albumId) {
            return false; // Album ID doesn't match
        }

        if (searchText != null && !searchText.isEmpty()) {
            String songName = song.getName().toLowerCase();
            String search = searchText.toLowerCase();

            if (!songName.contains(search)) {
                return false;
            }
        }

        // The song matches the criteria
        return true;
    }


    private void playAllSongs() {
        if (songsList.isEmpty()) {
            return;
        }

        ArrayList<Integer> songKeysList = new ArrayList<>();
        Bundle bundle = new Bundle();

        for (Song song : songsList) {
            int songKey = song.getSongKey();
            songKeysList.add(songKey);
            Log.d("SongKeyGet", String.valueOf(songKeysList));
        }

        bundle.putSerializable("SongKeysList", songKeysList);

        TabAdapter tabAdapter = (TabAdapter) viewPager.getAdapter();
        MusicPlayerFragment musicPlayerFragment = (MusicPlayerFragment) tabAdapter.getFragment(1);

        if (musicPlayerFragment != null) {
            tabAdapter.updateMusicPlayerFragment(bundle);
            Log.d("Bundle", "set");
        }

        viewPager.setCurrentItem(1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        playAllButton = view.findViewById(R.id.floatingActionButton2);
        recyclerView = view.findViewById(R.id.album_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        viewPager = requireActivity().findViewById(R.id.viewPagerMain);

        Bundle bundle = getArguments();
        fetchSongsFromFirebase(bundle);

        playAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAllSongs();
            }
        });

        return view;
    }

    @Override
    public void onItemClick(int position) {
        if (position >= 0 && position < songsList.size()) {
            Song clickedSong = songsList.get(position);
            int songKey = clickedSong.getSongKey();

            Bundle bundle = new Bundle();
            bundle.putInt("SongKey", songKey);
            Log.d("BUNDLE: SongsListFragment", String.valueOf(bundle));

            TabAdapter tabAdapter = (TabAdapter) viewPager.getAdapter();
            if (tabAdapter != null) {
                MusicPlayerFragment musicPlayerFragment = (MusicPlayerFragment) tabAdapter.getFragment(1);
                if (musicPlayerFragment != null) {
                    tabAdapter.updateMusicPlayerFragment(bundle);
                    Log.d("Bundle", "set");
                }
            }
            viewPager.setCurrentItem(1);
        }
    }
}

package com.example.musicapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class AlbumsFragment extends Fragment {
    private ImageButton backButton;
    private ImageView albumArtImageView;
    private TextView albumNameTextView;
    private TextView artistNameTextView;

    private int albumId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            albumId = arguments.getInt("AlbumId");
            Log.d("Bundle AlbumFragment", String.valueOf(arguments));
            Log.d("AlbumId", String.valueOf(albumId));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums, container, false);

        // Initialize views
        backButton = view.findViewById(R.id.button_back);
        albumArtImageView = view.findViewById(R.id.image_view_album_art);
        albumNameTextView = view.findViewById(R.id.text_view_album_name);
        artistNameTextView = view.findViewById(R.id.text_view_artist_name);

        // Retrieve album details with ID
        Bundle arguments = getArguments();
        int albumId = arguments.getInt("AlbumId");
        retrieveAlbumDetails(albumId);

        // Set click listener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to AlbumListFragment
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void retrieveAlbumDetails(int albumId) {
        DatabaseReference albumRef = FirebaseDatabase.getInstance().getReference("album").child(String.valueOf(albumId));
        albumRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String albumArtUrl = dataSnapshot.child("albumart").getValue(String.class);
                    String albumName = dataSnapshot.child("albumname").getValue(String.class);
                    String artistName = dataSnapshot.child("artist").getValue(String.class);

                    Picasso.get().load(albumArtUrl).into(albumArtImageView);

                    albumNameTextView.setText(albumName);
                    artistNameTextView.setText(artistName);
                    navigateToSongsFragment(albumId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void navigateToSongsFragment(int albumId) {
        Bundle bundle = new Bundle();
        bundle.putInt("albumId", albumId);
        SongsListFragment songsListFragment = new SongsListFragment();
        songsListFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, songsListFragment)
                .addToBackStack(null)
                .commit();
    }
}

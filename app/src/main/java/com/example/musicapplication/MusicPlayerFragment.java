package com.example.musicapplication;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.musicapplication.model.SongMusicPlayer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MusicPlayerFragment extends Fragment implements View.OnClickListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private TextView songNameTextView;
    private TextView artistNameTextView;
    private ImageView albumArtImageView;
    private ImageButton playPauseButton;
    private ImageButton previousButton;
    private ImageButton nextButton;
    private SeekBar seekBar;
    private ProgressBar loadingProgressBar;

    private MediaPlayer mediaPlayer;
    public List<SongMusicPlayer> playlist;
    private int currentSongIndex;
    private Timer seekBarTimer;
    private boolean isSeekBarTracking = false;

    private ViewPager2 viewPager;

    private MainActivity mainActivity;

    public MusicPlayerFragment() {
        // Required empty public constructor
    }

    public void updateArguments(Bundle arguments) {
        setArguments(arguments);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            Log.d("Bundle MusicPlayerFragment", String.valueOf(arguments));
            int songKey = arguments.getInt("SongKey");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        Log.d("Bundle in MusicPlayerFragment", String.valueOf(bundle));
        if (bundle != null) {
            mainActivity = (MainActivity) requireActivity();
            View rootView = inflater.inflate(R.layout.fragment_music_player, container, false);

            // Initialize views
            songNameTextView = rootView.findViewById(R.id.text_view_album_name);
            artistNameTextView = rootView.findViewById(R.id.text_view_artist_name);
            albumArtImageView = rootView.findViewById(R.id.image_view_album_art);
            playPauseButton = rootView.findViewById(R.id.button_play_pause);
            previousButton = rootView.findViewById(R.id.button_previous);
            nextButton = rootView.findViewById(R.id.button_next);
            seekBar = rootView.findViewById(R.id.seek_bar);
            loadingProgressBar = rootView.findViewById(R.id.progress_bar_loading);

            // Set click listeners
            playPauseButton.setOnClickListener(this);
            previousButton.setOnClickListener(this);
            nextButton.setOnClickListener(this);

            // Set seek bar listener
            seekBar.setOnSeekBarChangeListener(this);

            // Initialize MediaPlayer and playlist
            mediaPlayer = new MediaPlayer();
            playlist = new ArrayList<>();
            currentSongIndex = 0;

            int songKey = bundle.getInt("SongKey", -1);
            ArrayList<Integer> SongKeysList = (ArrayList<Integer>) bundle.getSerializable("SongKeysList");
            Log.d("List Shown", String.valueOf(SongKeysList));
            ArrayList<Integer> SongKeysPlaylist = new ArrayList<>();
            if (SongKeysList != null){
                for ( Integer SongKeys : SongKeysList) {
                    SongKeysPlaylist.add(SongKeys);
                }
            }else if (songKey != -1){
                SongKeysPlaylist.add(songKey);
            }
            Log.d("Playlist", String.valueOf(SongKeysPlaylist));
            fetchSongDetails(SongKeysPlaylist);

            return rootView;
        } else {
            View rootView = inflater.inflate(R.layout.fragment_no_music_playing, container, false);
            View view = inflater.inflate(R.layout.fragment_no_music_playing,
                    container, false);

            Button BrowseButton = rootView.findViewById(R.id.BrowseButton);
            BrowseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewPager = requireActivity().findViewById(R.id.viewPagerMain);
                    viewPager.setCurrentItem(0);
                }
            });

            return rootView;
        }
    }

    //FETCHING

    private void fetchSongDetails(ArrayList SongKeysPlaylist) {
        // Retrieve song details based on the songKey
        int PlaylistItemCount = SongKeysPlaylist.size();
        final int[] LoopCounter = {0};
        Log.d("FetchFunction", String.valueOf(SongKeysPlaylist));
        for (Object songKey : SongKeysPlaylist) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("song");
        String songKeyString = String.valueOf(songKey);
        databaseReference.child(songKeyString).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve song data
                    String album = dataSnapshot.child("album").getValue(String.class);
                    String artist = dataSnapshot.child("artist").getValue(String.class);
                    String name = dataSnapshot.child("name").getValue(String.class);
                    int albumId = dataSnapshot.child("albumId").getValue(Integer.class);
                    String url = dataSnapshot.child("url").getValue(String.class);
                    String albumArt = dataSnapshot.child("albumart").getValue(String.class);

                    // Create SongMusicPlayer object and add it to the playlist
                    playlist.add(new SongMusicPlayer(album, albumId, albumArt, artist, name, url));

                    Log.d("Song", "Database process");
                    LoopCounter[0]++;
                    if (PlaylistItemCount == LoopCounter[0]) {
                        UpdateUI();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error retrieving song: " + databaseError.getMessage());
            }
        });
    }

    }

    public void UpdateUI() {
        // Set song details
        updateSongDetails();

        // Start playing the first song
        playSong(0);
    }
    ///MUSIC PLAYER FUNCTIONS
    private void updateSongDetails() {
        SongMusicPlayer currentSong = playlist.get(currentSongIndex);
        songNameTextView.setText(currentSong.getName());
        artistNameTextView.setText(currentSong.getArtist());

        // Load album art using Glide library
        String albumArtUrl = currentSong.getAlbumart();
        if (albumArtUrl != null && !albumArtUrl.isEmpty()) {
            Glide.with(requireContext()).load(albumArtUrl).into(albumArtImageView);
        } else {
            // Set a default album art if no album art is available
            albumArtImageView.setImageResource(R.drawable.background_gradient);
        }
    }

    private void playSong(int songIndex) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(playlist.get(songIndex).getUrl());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnCompletionListener(this);
            loadingProgressBar.setVisibility(View.VISIBLE);
            seekBar.setEnabled(false);
            playPauseButton.setEnabled(false);
            previousButton.setEnabled(false);
            nextButton.setEnabled(false);
        } catch (IOException e) {
            Log.e("MediaPlayer", "Error setting data source: " + e.getMessage());
        }
    }

    private void playPauseSong() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playPauseButton.setImageResource(R.drawable.baseline_play_circle_filled_24);
        } else {
            mediaPlayer.start();
            playPauseButton.setImageResource(R.drawable.baseline_pause_circle_filled_24);
        }
    }

    private void playPreviousSong() {
        if (currentSongIndex > 0) {
            currentSongIndex--;
            updateSongDetails();
            playSong(currentSongIndex);
        } else {
            // Restart the current song from the beginning
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        }
    }

    private void playNextSong() {
        if (currentSongIndex < playlist.size() - 1) {
            currentSongIndex++;
            updateSongDetails();  // Update the song details before playing the next song
            playSong(currentSongIndex);
        } else {
            Toast.makeText(requireContext(), "End of list. Restarting playlist.", Toast.LENGTH_SHORT).show();
            currentSongIndex = 0;
            updateSongDetails();  // Update the song details before playing the next song
            playSong(currentSongIndex);
        }
    }


    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.button_play_pause) {
            playPauseSong();
        } else if (viewId == R.id.button_previous) {
            playPreviousSong();
        } else if (viewId == R.id.button_next) {
            playNextSong();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        loadingProgressBar.setVisibility(View.GONE);
        seekBar.setEnabled(true);
        playPauseButton.setEnabled(true);
        previousButton.setEnabled(true);
        nextButton.setEnabled(true);

        // Set the maximum value of the seek bar to the song duration
        seekBar.setMax(mediaPlayer.getDuration());

        // Start updating the seek bar progress
        startUpdatingSeekBar();
        mp.start();
        playPauseButton.setImageResource(R.drawable.baseline_pause_circle_filled_24);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("MediaPlayer", "Error occurred: " + what + ", " + extra);
        Toast.makeText(requireContext(), "Error occurred. Playing next song.", Toast.LENGTH_SHORT).show();
        playNextSong();
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // Song playback completed, play the next song
        playNextSong();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // Update the current progress when the user drags the seek bar thumb
        if (fromUser) {
            mediaPlayer.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Pause the MediaPlayer when the user starts dragging the seek bar thumb
        isSeekBarTracking = true;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            playPauseButton.setImageResource(R.drawable.baseline_play_circle_filled_24);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Resume or start the MediaPlayer when the user stops dragging the seek bar thumb
        isSeekBarTracking = false;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(seekBar.getProgress());
        } else {
            mediaPlayer.seekTo(seekBar.getProgress());
            mediaPlayer.start();
            playPauseButton.setImageResource(R.drawable.baseline_pause_circle_filled_24);
        }
    }



    private void startUpdatingSeekBar() {
        if (seekBarTimer != null) {
            seekBarTimer.cancel();
        }

        seekBarTimer = new Timer();
        seekBarTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (mediaPlayer != null && !isSeekBarTracking && mediaPlayer.isPlaying()) {
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                }
            }
        }, 0, 1000); // Update seek bar every second
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (seekBarTimer != null) {
            seekBarTimer.cancel();
            seekBarTimer = null;
        }
    }


}

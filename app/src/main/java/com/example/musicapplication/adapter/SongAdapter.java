package com.example.musicapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapplication.R;
import com.example.musicapplication.model.Song;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    private Context context;
    private List<Song> songs;
    private OnItemClickListener listener;

    private int SongKey;
    public SongAdapter(Context context, List<Song> songs, OnItemClickListener listener, int SongKey) {
        this.context = context;
        this.songs = songs;
        this.listener = listener;
        this.SongKey = SongKey;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Song song = songs.get(position);

        holder.songNameTextView.setText(song.getName());
        holder.artistTextView.setText(song.getArtist());

        Glide.with(context)
                .load(song.getAlbumart())
                .into(holder.albumArtImageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {

        return songs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView albumArtImageView;
        TextView songNameTextView;
        TextView artistTextView;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumArtImageView = itemView.findViewById(R.id.Albumart);
            songNameTextView = itemView.findViewById(R.id.Songname);
            artistTextView = itemView.findViewById(R.id.Artist);
        }
    }
}

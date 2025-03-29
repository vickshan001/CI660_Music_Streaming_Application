package com.example.musicapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapplication.R;
import com.example.musicapplication.model.Album;
import com.example.musicapplication.model.Song;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private Context context;
    private List<Album> albumList;

    private OnItemClickListener listener;

    private int albumId;

    public AlbumAdapter(Context context, List<Album> albumList, OnItemClickListener listener, int albumId) {
        this.context = context;
        this.albumList = albumList;
        this.listener = listener;
        this.albumId = albumId;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album album = albumList.get(position);
        holder.bind(album);

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
        return albumList.size();
    }

    static class AlbumViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewAlbumArt;
        private TextView textViewAlbumName;
        private TextView textViewArtistName;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewAlbumArt = itemView.findViewById(R.id.image_view_album_art);
            textViewAlbumName = itemView.findViewById(R.id.text_view_album_name);
            textViewArtistName = itemView.findViewById(R.id.text_view_artist_name);
        }

        public void bind(Album album) {
            textViewAlbumName.setText(album.getAlbumName());
            textViewArtistName.setText(album.getArtist());
            Picasso.get().load(album.getAlbumart()).into(imageViewAlbumArt);
        }
    }
}

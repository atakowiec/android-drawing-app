package pl.atakowiec.drawingapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private final List<Photo> photoList;
    private final OnPhotoClickListener listener;

    public interface OnPhotoClickListener {
        void onPhotoClick(Photo photo);
    }

    public PhotoAdapter(List<Photo> photoList, OnPhotoClickListener listener) {
        this.photoList = photoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Photo photo = photoList.get(position);

        File photoFile = new File(photo.getPath());
        holder.path = photoFile.getAbsolutePath();
        if (photoFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(photo.getPath());
            holder.imageView.setImageBitmap(bitmap);
        }

        holder.textView.setText(photoFile.getName().split("\\.")[0].replace("-", " "));
        holder.itemView.setOnClickListener(v -> listener.onPhotoClick(photo));
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        String path;

        PhotoViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.listPhoto);
            textView = itemView.findViewById(R.id.photoName);
        }
    }
}

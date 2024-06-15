package pl.atakowiec.drawingapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import java.io.File;

public class PhotoDetailFragment extends Fragment {
    private static final String ARG_PHOTO = "photo";

    private Photo photo;

    public static PhotoDetailFragment newInstance(Photo photo) {
        PhotoDetailFragment fragment = new PhotoDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PHOTO, photo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            photo = getArguments().getParcelable(ARG_PHOTO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_detail, container, false);

        ImageView imageView = view.findViewById(R.id.listPhoto);

        File photoFile = new File(photo.getPath());
        if (photoFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(photo.getPath());
            imageView.setImageBitmap(bitmap);
        }

        return view;
    }
}

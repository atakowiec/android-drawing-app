package pl.atakowiec.drawingapp;

import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhotoListFragment extends Fragment implements PhotoAdapter.OnPhotoClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Photo> photoList = new ArrayList<>();
        if (getContext() == null) return view;

        File photosDir = new File(getContext().getFilesDir(), "drawings");
        if (photosDir.exists() && photosDir.isDirectory()) {
            File[] files = photosDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".png"))
                        photoList.add(new Photo(file.getAbsolutePath()));
                }
            }
        }

        PhotoAdapter photoAdapter = new PhotoAdapter(photoList, this);
        recyclerView.setAdapter(photoAdapter);
        getItemTouchHelper().attachToRecyclerView(recyclerView);

        return view;
    }

    @Override
    public void onPhotoClick(Photo photo) {
        PhotoDetailFragment detailFragment = PhotoDetailFragment.newInstance(photo);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @NonNull
    private ItemTouchHelper getItemTouchHelper() {
        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(LEFT, LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                try {
                    PhotoAdapter.PhotoViewHolder photoViewHolder = (PhotoAdapter.PhotoViewHolder) viewHolder;
                    File photoFile = new File(photoViewHolder.path);
                    if(photoFile.delete())
                        Toast.makeText(getContext(), R.string.photo_deleted, Toast.LENGTH_SHORT).show();

                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, new PhotoListFragment());
                    transaction.commit();
                } catch (Exception e) {
                    Log.e("PhonesListActivity", "Error while deleting phone", e);
                }
            }
        };
        return new ItemTouchHelper(callback);
    }
}
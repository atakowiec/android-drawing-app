package pl.atakowiec.drawingapp;

import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable {
    private final String path;

    public Photo(String path) {
        this.path = path;
    }

    protected Photo(Parcel in) {
        path = in.readString();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public String getPath() {
        return path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
    }
}

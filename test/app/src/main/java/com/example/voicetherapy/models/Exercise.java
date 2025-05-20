package com.example.voicetherapy.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Exercise implements Parcelable {
    private String title;
    private String description;
    // Add other relevant fields later, e.g., audio demo URI

    public Exercise(String title, String description) {
        this.title = title;
        this.description = description;
    }

    protected Exercise(Parcel in) {
        title = in.readString();
        description = in.readString();
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
    }
} 
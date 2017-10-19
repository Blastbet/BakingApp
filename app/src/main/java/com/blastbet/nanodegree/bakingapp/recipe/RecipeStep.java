package com.blastbet.nanodegree.bakingapp.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ilkka on 1.9.2017.
 */

public class RecipeStep implements Parcelable {
    @SerializedName("id")
    @Expose
    private int index;

    @Expose
    private String shortDescription;

    @Expose
    private String description;

    @Expose
    private String videoURL;

    @Expose
    private String thumbnailUrl;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(index);
        parcel.writeString(shortDescription);
        parcel.writeString(description);
        parcel.writeString(videoURL);
        parcel.writeString(thumbnailUrl);
    }

    public RecipeStep(Parcel in) {
        this.index = in.readInt();
        this.shortDescription = in.readString();
        this.description = in.readString();
        this.videoURL = in.readString();
        this.thumbnailUrl = in.readString();
    }

    public RecipeStep(int index,
                      String shortDescription,
                      String description,
                      String videoURL,
                      String thumbnailUrl) {
        this.index = index;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.thumbnailUrl = thumbnailUrl;
    }

    public static final Parcelable.Creator<RecipeStep> CREATOR =
            new Parcelable.Creator<RecipeStep>() {
                public RecipeStep createFromParcel(Parcel in) { return new RecipeStep(in); }
                public RecipeStep[] newArray(int size) { return new RecipeStep[size]; }
            };

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Recipe step   : ").append(shortDescription).append('\n');
        builder.append("------------ ").append('\n');
        builder.append(description).append('\n');
        builder.append(" Video URL    : ").append(videoURL).append('\n');
        builder.append(" Thumbnail URL: ").append(thumbnailUrl).append('\n');
        return builder.toString();
    }
}

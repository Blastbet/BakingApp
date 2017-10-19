package com.blastbet.nanodegree.bakingapp.recipe;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ilkka on 1.9.2017.
 */

public class Ingredient implements Parcelable{

    @Expose
    private String measure;

    @Expose
    private double quantity;

    @SerializedName("ingredient")
    @Expose
    private String name;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(measure);
        parcel.writeDouble(quantity);
    }

    public Ingredient(Parcel in) {
        this.name = in.readString();
        this.measure = in.readString();
        this.quantity = in.readDouble();
    }

    public Ingredient(String name, String measure, double quantity) {
        this.name = name;
        this.measure = measure;
        this.quantity = quantity;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static final Parcelable.Creator<Ingredient> CREATOR =
            new Parcelable.Creator<Ingredient>() {
                public Ingredient createFromParcel(Parcel in) { return new Ingredient(in); }
                public Ingredient[] newArray(int size) { return new Ingredient[size]; }
            };
}

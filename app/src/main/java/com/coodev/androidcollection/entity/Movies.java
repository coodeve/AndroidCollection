package com.coodev.androidcollection.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Movies {
    public int count;
    public int start;
    public int total;
    @SerializedName("subjects")
    public List<Movie> moveList;

    public static class Movie {
        public String id;
        public String title;
        public String year;
        public Images images;

        public static class Images {
            public String small;
        }
    }

}

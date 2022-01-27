package com.dreamteam.carbnb;

public class Utils {
    public static final String storageKey = "4b807f08-e615-451b-a5e0-85f13ee14c61";

    public static String generateImageUrl(String imageName) {
        return "https://firebasestorage.googleapis.com/v0/b/carbnb-cf74d.appspot.com/o/cars%2F"
                + imageName
                + "?alt=media&token="
                + storageKey;
    }
}

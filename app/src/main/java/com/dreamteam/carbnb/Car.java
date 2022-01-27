package com.dreamteam.carbnb;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;
import java.util.List;

public class Car implements Serializable {
    @DocumentId
    public String ref;
    public String self_id;
    public String name;
    public String brand;
    public String type;
    public List<String> images_array;
    public String main_image;
    public String price_numerical;
    public String price_tag_line;
    public String description;

    public Car() {
    }
}

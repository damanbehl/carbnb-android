package com.dreamteam.carbnb;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;

public class Insurance implements Serializable {
    @DocumentId
    public String ref;
    public String title;
    public String brand;
    public String imageString;
    public String description;
    public String price;
}

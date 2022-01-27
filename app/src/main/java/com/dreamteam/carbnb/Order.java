package com.dreamteam.carbnb;

import java.io.Serializable;

public class Order implements Serializable {
    String car_id;
    String rented_at;
    String transaction_id;
    Object firebaseObject;
}

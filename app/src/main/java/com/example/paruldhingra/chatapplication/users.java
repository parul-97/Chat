package com.example.paruldhingra.chatapplication;

/**
 * Created by Parul Dhingra on 29-01-2018.
 */

public class users {

    public users(){

    }

    public String name;
    public String thumb_image;
    public String status;

    public users(String name, String image, String status) {
        this.name = name;
        this.thumb_image = image;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return thumb_image;
    }

    public void setImage(String image) {
        this.thumb_image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

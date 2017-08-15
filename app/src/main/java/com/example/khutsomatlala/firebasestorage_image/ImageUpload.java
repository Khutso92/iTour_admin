package com.example.khutsomatlala.firebasestorage_image;



public class ImageUpload {

    public String name;
    public String description;
    public String category;
    public String latitude;
    public String longtiude;

    public String urI;

    public ImageUpload() {

    }

    public ImageUpload(String name, String urI, String description, String category, String latitude, String longtiude) {
        this.urI = urI;
        this.name = name;
        this.description = description;
        this.category = category;
        this.latitude = latitude;
        this.longtiude = longtiude;


    }


    public String getName() {

        return name;
    }

    public String getUrI() {
        return urI;
    }


   /* public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongtiude() {
        return longtiude;
    }*/
}

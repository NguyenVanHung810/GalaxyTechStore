package com.example.galaxytechstore;

public class SliderModel {
    private String banner;
    private String backgroudnColor;

    public SliderModel(String banner, String backgroudnColor) {
        this.banner = banner;
        this.backgroudnColor = backgroudnColor;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getBackgroudnColor() {
        return backgroudnColor;
    }

    public void setBackgroudnColor(String backgroudnColor) {
        this.backgroudnColor = backgroudnColor;
    }
}

package com.hackaton.aplikacja.pckg;

import java.util.ArrayList;

public class CategoryClass {
    private String name;

    private int image;

    private ArrayList<MainCategoryEnum> mainCategory;

    public CategoryClass(String name, int image, MainCategoryEnum... mainCategory) {
        this.name = name;
        this.mainCategory = new ArrayList<>();
        for (MainCategoryEnum mainCategoryEnum : mainCategory) {
            this.mainCategory.add(mainCategoryEnum);
        }
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return image;
    }
}

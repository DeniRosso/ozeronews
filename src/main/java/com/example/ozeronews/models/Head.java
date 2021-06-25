package com.example.ozeronews.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Head {

    private String companyName;
    private String siteName;
    private String url;
    private String title;
    private String description;
    private String image;

    public Head() {
    }

    public Head(String companyName, String siteName, String url, String title, String description, String image) {
        this.companyName = companyName;
        this.siteName = siteName;
        this.url = url;
        this.title = title;
        this.description = description;
        this.image = image;
    }

    @Override
    public String toString() {
        return "Head{" +
                "companyName='" + companyName + '\'' +
                ", siteName='" + siteName + '\'' +
                ", url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}

package com.example.ozeronews.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Head {

    private String companyName;
    private String websiteName;
    private String websiteURL;
    private String websiteLogo;
    private String emailHelp;
    private String emailSupport;
    private String title;
    private String description;
    private String image;

    public Head() {
    }

    public Head(String companyName, String websiteName, String websiteURL, String websiteLogo,
                String emailHelp, String emailSupport, String title, String description, String image) {
        this.companyName = companyName;
        this.websiteName = websiteName;
        this.websiteURL = websiteURL;
        this.websiteLogo = websiteLogo;
        this.emailHelp = emailHelp;
        this.emailSupport = emailSupport;
        this.title = title;
        this.description = description;
        this.image = image;
    }

    @Override
    public String toString() {
        return "Head{" +
                "companyName='" + companyName + '\'' +
                ", websiteName='" + websiteName + '\'' +
                ", websiteURL='" + websiteURL + '\'' +
                ", websiteLogo='" + websiteLogo + '\'' +
                ", emailHelp='" + emailHelp + '\'' +
                ", emailSupport='" + emailSupport + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}

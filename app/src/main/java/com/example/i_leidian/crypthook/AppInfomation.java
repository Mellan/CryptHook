package com.example.i_leidian.crypthook;

import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

/**
 * Created by Mellan on 2017/6/11.
 */

public class AppInfomation {
    public Drawable getAppicon() {
        return appicon;
    }

    public void setAppicon(Drawable appicon) {
        this.appicon = appicon;
    }

    private Drawable appicon;
    private String appname;
    private String packagename;
    private String versionname;
    private PackageInfo packageInfo;
    public AppInfomation(){
        super();
        this.appicon=null;
        this.appname="";
        this.packageInfo=null;
        this.packagename="";
        this.versionname="";
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getPackagename() {
        return packagename;
    }

    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }

    public String getVersionname() {
        return versionname;
    }

    public void setVersionname(String versionname) {
        this.versionname = versionname;
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
    }
}

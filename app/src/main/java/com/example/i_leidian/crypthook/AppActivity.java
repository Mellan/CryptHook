package com.example.i_leidian.crypthook;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppActivity extends AppCompatActivity {
    private ListView listView;
    private PackageManager pm;
    private List<AppInfomation> mlistAppInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appactivity);
        Intent intent=this.getIntent();
        listView = (ListView) findViewById(R.id.app_list);
        int filter=intent.getIntExtra("type",0);
        mlistAppInfo = queryFilterAppInfo(filter);
        AppAdapter AppAdapter = new AppAdapter(
                this, mlistAppInfo);
        listView.setAdapter(AppAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String packagename=mlistAppInfo.get(position).getPackagename();
                Intent intent1=new Intent(AppActivity.this,LaunchActivity.class);
                intent1.putExtra("packagename",packagename);
                startActivity(intent1);
            }
        });
    }
    private List<AppInfomation> queryFilterAppInfo(int filter) {
        pm = this.getPackageManager();
        // 查询所有已经安装的应用程序
        List<ApplicationInfo> listAppcations = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations,
                new ApplicationInfo.DisplayNameComparator(pm));// 排序
        List<AppInfomation> appInfos = new ArrayList<AppInfomation>(); // 保存过滤查到的AppInfo
        // 根据条件来过滤
        switch (filter) {
            case 0: // 系统程序
                appInfos.clear();
                for (ApplicationInfo app : listAppcations) {
                    if ((app.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        appInfos.add(getAppInfo(app));
                    }
                }
                return appInfos;
            case 1: // 第三方应用程序
                appInfos.clear();
                for (ApplicationInfo app : listAppcations) {
                    if ((app.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                        appInfos.add(getAppInfo(app));
                    }
                }
                break;

            default:
                return null;
        }
        return appInfos;
    }
    // 构造一个AppInfo对象 ，并赋值
    private AppInfomation getAppInfo(ApplicationInfo app) {
        AppInfomation appInfo = new AppInfomation();
        appInfo.setAppname((String) app.loadLabel(pm));
        appInfo.setAppicon(app.loadIcon(pm));
        appInfo.setPackagename(app.packageName);
        try {
            appInfo.setVersionname(pm.getPackageInfo(app.packageName,0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appInfo;
    }
}

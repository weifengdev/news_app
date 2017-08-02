package com.qg.qgnews.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.qg.qgnews.App;
import com.qg.qgnews.R;
import com.qg.qgnews.controller.adapter.Controller;
import com.qg.qgnews.controller.adapter.DownLoadServer;
import com.qg.qgnews.controller.adapter.FragmentPagerAdapterNewsMessage;
import com.qg.qgnews.model.News;
import com.qg.qgnews.model.NewsDetailAdapter;
import com.qg.qgnews.model.PicAsnycTask;
import com.qg.qgnews.model.RequestAdress;
import com.qg.qgnews.ui.fragment.NewsMessage;
import com.qg.qgnews.util.Tool;

import java.util.ArrayList;
import java.util.List;

public class NewsMessageActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    Toolbar toolbar;
    ViewPager viewPager;
    ImageView cover;
    CollapsingToolbarLayout coll;
    List<News> newsList;
    List<View> viewList;
    public static final int MODE_VISIT = 0;
    public static final int MODE_MANAGE = 1;
    private int mode = MODE_VISIT;
    int startPos;
    int posNow;
    public DownLoadServer.DownLoadBinder binder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (DownLoadServer.DownLoadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_message);
        Intent intent = getIntent();
        newsList = (List<News>) intent.getSerializableExtra("news_list");
        for (News news : newsList) {
            System.out.println(news);
        }
        startPos = intent.getIntExtra("start_pos", 0);
        mode = intent.getIntExtra("mode", MODE_VISIT);
        Intent intent1 = new Intent(this, DownLoadServer.class);
        bindService(intent1, connection, BIND_AUTO_CREATE);
        initView();
    }

    /**
     * 初始化活动中的控件
     */
    private void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        viewPager = (ViewPager) findViewById(R.id.activity_news_message_view_pager);
        toolbar = (Toolbar) findViewById(R.id.activity_news_message_tool_bar);
        coll = (CollapsingToolbarLayout) findViewById(R.id.activity_news_message_coll);
        cover = (ImageView) findViewById(R.id.activity_news_message_news_cover);
        setSupportActionBar(toolbar);
        //初始化
        onPageSelected(startPos);

        coll.setExpandedTitleColor(Color.WHITE);
        coll.setCollapsedTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initViewPager();
    }

    private void initViewPager() {
        viewList = new ArrayList<>();
        for (int i = 0; i < newsList.size(); i++) {
            viewList.add(LayoutInflater.from(this).inflate(R.layout.news_details, null, false));
        }
        viewPager.setAdapter(new NewsDetailAdapter(viewList, newsList, this, mode));
        viewPager.setCurrentItem(startPos, true);
        viewPager.setOnPageChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mode == MODE_MANAGE) {
            getMenuInflater().inflate(R.menu.news_detial_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Controller.RequestWithString2(RequestAdress.DELETE_NEWS, "{\"newsId\":" + newsList.get(posNow).getNewsId() + "}", new Controller.OnRequestListener() {
            @Override
            public void onSuccess(String json) {
                Tool.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (newsList.size() == 1) {
                            finish();
                        }
                        //newsList.remove(posNow);
                        viewList.remove(posNow);
                        viewPager.removeAllViews();
                        viewPager.getAdapter().notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onFailed(int state) {
                Tool.toast("删除失败");
            }
        });
        return super.onOptionsItemSelected(item);
    }

    //viewpager华东监听
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        posNow = position;
        new PicAsnycTask(cover, newsList.get(position), App.bitmapLruCache).execute();
        Log.d("title", newsList.get(position).getNewsTitle());
        coll.setTitle(newsList.get(position).getNewsTitle());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }
    //viewpager华东监听
}

package com.qg.qgnews.ui.activity;

import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qg.qgnews.R;
import com.qg.qgnews.model.News;
import com.qg.qgnews.ui.fragment.NewsListFrag;
import com.qg.qgnews.util.Tool;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NewsListFrag.OnNewsItemClickListener {
    private Toolbar toolbar;
    private SearchView searchView;
    private FloatingActionButton plus, myNews, manager, edit;
    private LinearLayout myNewsLiner, managerLiner, editLiner;
    private static final int PLUS_OPEN = 1;
    private static final int PLUS_CLOSE = 0;
    private static int pulsButtonMode = PLUS_CLOSE;
    public static final int MODE_VISITOR = 0;
    public static final int MODE_MANAGER = 1;
    public static final int MODE_SUPPER_MANAGER = 2;
    public static int mode = MODE_SUPPER_MANAGER;
    private NewsListFrag newsListFrag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        newsListFrag = new NewsListFrag();
        plus = (FloatingActionButton) findViewById(R.id.activity_main_plus_button);
        myNews = (FloatingActionButton) findViewById(R.id.activity_main_mynews_button);
        manager = (FloatingActionButton) findViewById(R.id.activity_main_manager_button);
        edit = (FloatingActionButton) findViewById(R.id.activity_main_edit_button);
        myNewsLiner = (LinearLayout) findViewById(R.id.activity_main_mynews_liner);
        managerLiner = (LinearLayout) findViewById(R.id.activity_main_manager_liner);
        editLiner = (LinearLayout) findViewById(R.id.activity_main_edit_liner);
        newsListFrag.setOnNewsItemClickListener(this);
        plus.setOnClickListener(this);
        myNews.setOnClickListener(this);
        manager.setOnClickListener(this);
        edit.setOnClickListener(this);
        if (mode == MODE_VISITOR) {
            plus.setVisibility(View.GONE);
        } else {
            plus.setVisibility(View.VISIBLE);
        }
        setFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);
        searchView = ((SearchView) menu.findItem(R.id.main_menu_search).getActionView());
        setSearchViewOptions(searchView);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_search:
                break;
            case R.id.main_menu_logout:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSearchViewOptions(final SearchView s) {
        s.setQueryHint("搜索新闻...");
        //显示提交按钮
        s.setSubmitButtonEnabled(true);
        s.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Tool.toast("点击了搜索");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        //限制输入字符长度
        TextView et = (TextView) s.findViewById(R.id.search_src_text);
        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_main_plus_button:
                if (pulsButtonMode == PLUS_CLOSE) {
                    plus.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_to_45));
                    showButtons();
                    //TODO open
                } else {
                    plus.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_back_45));
                    hideButtons();
                    //TODO close
                }
                pulsButtonMode = pulsButtonMode == PLUS_CLOSE ? PLUS_OPEN : PLUS_CLOSE;
                break;
            case R.id.activity_main_edit_button:
                Tool.toast("点击了编辑");
                break;
            case R.id.activity_main_manager_button:
                Tool.toast("点击了管理");
                break;
            case R.id.activity_main_mynews_button:
                Tool.toast("点击了我的新闻");
                break;
        }
    }

    private void showButtons() {
        switch (mode) {
            case MODE_VISITOR:
                break;
            case MODE_MANAGER:
                myNewsLiner.setVisibility(View.VISIBLE);
                editLiner.setVisibility(View.VISIBLE);
                myNewsLiner.startAnimation(AnimationUtils.loadAnimation(this, R.anim.trans_up_offset_50));
                editLiner.startAnimation(AnimationUtils.loadAnimation(this, R.anim.trans_up_offset_0));
                break;
            case MODE_SUPPER_MANAGER:
                managerLiner.setVisibility(View.VISIBLE);
                myNewsLiner.setVisibility(View.VISIBLE);
                editLiner.setVisibility(View.VISIBLE);
                myNewsLiner.startAnimation(AnimationUtils.loadAnimation(this, R.anim.trans_up_offset_50));
                managerLiner.startAnimation(AnimationUtils.loadAnimation(this, R.anim.trans_up_offset_100));
                editLiner.startAnimation(AnimationUtils.loadAnimation(this, R.anim.trans_up_offset_0));
                break;
        }


    }

    private void hideButtons() {
        Animation hide_0, hide_50, hide_100;
        hide_0 = AnimationUtils.loadAnimation(this, R.anim.trans_down_offset_0);
        hide_50 = AnimationUtils.loadAnimation(this, R.anim.trans_down_offset_50);
        hide_100 = AnimationUtils.loadAnimation(this, R.anim.trans_down_offset_100);

        hide_0.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                editLiner.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        hide_50.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                myNewsLiner.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        hide_100.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                managerLiner.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        switch (mode) {
            case MODE_VISITOR:
                break;
            case MODE_MANAGER:
                myNewsLiner.startAnimation(hide_50);
                editLiner.startAnimation(hide_0);
                break;
            case MODE_SUPPER_MANAGER:
                myNewsLiner.startAnimation(hide_50);
                managerLiner.startAnimation(hide_100);
                editLiner.startAnimation(hide_0);
                break;
        }


    }

    private void setFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_frag_container, new NewsListFrag());
        ft.commit();
    }

    /** 新闻列表item点击监听
     * @param v
     * @param pos
     * @param news
     */
    @Override
    public void OnItemClickListener(View v, int pos, News news) {
            Tool.toast("点击了第"+pos);
    }
}

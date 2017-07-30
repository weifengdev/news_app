package com.qg.qgnews.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qg.qgnews.R;
import com.qg.qgnews.controller.adapter.ManagerPersonAdapter;
import com.qg.qgnews.model.FeedBack;
import com.qg.qgnews.model.Manager;
import com.qg.qgnews.util.Request;

import java.util.ArrayList;
import java.util.List;

import static android.icu.text.DateTimePatternGenerator.PatternInfo.OK;

/**
 * Created by linzongzhan on 2017/7/30.
 */

public class ManagerPerson extends Fragment {

    private static final int OK = 1;

    private View view;

    private ListView listView;

    private List<Manager> managers = new ArrayList<>();

    private SwipeRefreshLayout refreshLayout;

    private Handler handler = new Handler() {
        public void handleMessage (Message msg) {
            switch (msg.what) {
                case OK :
                    //刷新ListView（所有管理员）
                    ManagerPersonAdapter managerPersonAdapter = new ManagerPersonAdapter(getContext(),R.layout.manager_item,managers);
                    listView.setAdapter(managerPersonAdapter);
                    break;
                default:
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_manager_person,container,false);

        listView = (ListView) view.findViewById(R.id.manager_person_listView);




        Manager manager = new Manager();
        manager.setManagerName("6666");
        manager.setManagerStatus("待审批");
        for (int i = 0;i < 10;i++) {
            managers.add(manager);
        }
        ManagerPersonAdapter managerPersonAdapter = new ManagerPersonAdapter(getContext(),R.layout.manager_item,managers);
        listView.setAdapter(managerPersonAdapter);





        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.manager_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //接受数据
                        String respose = Request.RequestWithNoString("http://ip:80/admin/showmanager");
                        Gson gson = new Gson();
                        FeedBack feedBack = gson.fromJson(respose,FeedBack.class);
                        String data = feedBack.getData();
                        managers = gson.fromJson(data,new TypeToken<List<Manager>>(){}.getType());

                        Message message = new Message();
                        message.what = OK;
                        handler.sendMessage(message);

                    }
                });
            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                //接受数据
                String respose = Request.RequestWithNoString("http://ip:80/admin/showmanager");
                Gson gson = new Gson();
                FeedBack feedBack = gson.fromJson(respose,FeedBack.class);
                String data = feedBack.getData();
                managers = gson.fromJson(data,new TypeToken<List<Manager>>(){}.getType());

                Message message = new Message();
                message.what = OK;
                handler.sendMessage(message);

            }
        });

        return view;
    }

    private void setViewOnClick () {

        //点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


            }
        });

        //长按事件
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {




                return true;
            }
        });
    }
}
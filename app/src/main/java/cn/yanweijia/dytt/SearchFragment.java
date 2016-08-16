package cn.yanweijia.dytt;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import cn.yanweijia.beans.Link;
import cn.yanweijia.dao.analyzeWebPage;
import cn.yanweijia.utils.Tools;


public class SearchFragment extends Fragment{
    SearchView searchView = null;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private SimpleAdapterSearchResult adapter;  //recyclerview的适配器
    private Handler handler;    //用来更新界面的handler
    private List<HashMap<String,String>> list;  //放数据的list

    private static final String TAG = "SearchFragment";
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        //设置Activity标题并绑定控件
        getActivity().setTitle(R.string.search);
        searchView = (SearchView)view.findViewById(R.id.searchView);

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview_search);

        //Handler的CallBack回调方法
        handler = new Handler(new Handler.Callback(){
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what == 0) {//无结果.没有找到相关新闻
//                    Toast.makeText(getContext(), R.string.queryError, Toast.LENGTH_SHORT).show();
                    Snackbar.make(getView(),R.string.queryEmpty, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return false;
                }
                if(msg.what == 1) {
                    //通知adapter更新数据
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "handleMessage: 更新数据");
                    return false;
                }
                if(msg.what == 2){
                    //TODO:把Toast改成Dialog
                    Toast.makeText(getContext(),R.string.pleaseEnterKey,Toast.LENGTH_LONG).show();
                    //清空上次的查询结果
                    list.clear();
                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        });


        //初始化View
        initViews();




        return view;
    }


    /**
     * 从网络获取数据更新界面
     */
    private void getSearchDatasToListAndNotifyUpdate(final String key){
        //如果无网络
        if(!Tools.isNetworkAvailable(getActivity())){
            handler.sendEmptyMessage(0);
            return;
        }


        list.clear();
        //从网络获取数据更新list,用线程
        new Thread(new Runnable() {
            @Override
            public void run() {

                List<Link> linkList = analyzeWebPage.searchMovie(key);
                if (linkList.size() == 0){
                    handler.sendEmptyMessage(0);
                    return;
                }

                for(int i = 0 ; i < linkList.size() ; i++){
                    Link link = linkList.get(i);
                    String name = link.getName();
                    String url = link.getUrl();
                    String date = link.getDate();
                    //放数据的map
                    HashMap<String,String> map = new HashMap<String, String>();
                    map.put("name",name);
                    map.put("url",url);
                    map.put("date",date);

                    list.add(map);
                }
                //通知adapter更新数据
                handler.sendEmptyMessage(1);
            }
        }).start();
    }

    /**
     * 初始化View
     */
    private void initViews(){
        searchView.setIconifiedByDefault(false);    //取消默认缩小
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //隐藏软键盘
                Tools.hintKbTwo(getActivity());
                if (query.equals("")){
                    handler.sendEmptyMessage(2);    //提示用户先输入数据
                    return false;
                }
                //判断有无网络,如果没有网络,提示用户并返回
                if(!Tools.isNetworkAvailable(getActivity())){
                    Snackbar.make(getView(), "网络连接失败,请重新设置网络", Snackbar.LENGTH_LONG)
                            .setAction("设置", null).show();
                    return false;
                }
                //有网络,联网获取数据并更新
                getSearchDatasToListAndNotifyUpdate(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        adapter = new SimpleAdapterSearchResult();
        list = adapter.getList();
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }


}

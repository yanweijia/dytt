package cn.yanweijia.dytt;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.mobstat.StatService;

import java.util.HashMap;
import java.util.List;
import cn.yanweijia.beans.Link;
import cn.yanweijia.dao.analyzeWebPage;
import cn.yanweijia.utils.DBHelper;
import cn.yanweijia.utils.Tools;


public class NewMovieFragment extends Fragment {
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SimpleAdapterNewMovie adapter;  //recyclerview的适配器
    private boolean isLoading = false;  //当前swipeLayout是否正在更新的状态中
    private Handler handler;    //用来更新界面的handler
    private List<HashMap<String,String>> list;  //放数据的list
    private int lastVisibleItem;    //最后一个可视的View
    private LinearLayout linearLayout_ad;   //放广告的Layout


    private static final String TAG = "NewMovieFragment";
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_movie, container, false);
        //设置窗口标题并绑定控件
        getActivity().setTitle(R.string.newMovie);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview_newmovie);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_widget_newmovie);
        linearLayout_ad = (LinearLayout) view.findViewById(R.id.linearLayout_newMovieAd);

        //Handler的CallBack回调方法
        handler = new Handler(new Handler.Callback(){
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what == 0) {
                    Toast.makeText(getContext(), R.string.queryError, Toast.LENGTH_SHORT).show();
                }
                if(msg.what == 1) {
                    //通知adapter更新数据
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "handleMessage: 更新数据");
                    return false;
                }
                return true;
            }
        });

        //初始化View
        initViews();


        //如果获取过信息
        list.clear();
        if(((MainActivity)getActivity()).getList_newMovie() != null) {
            list.addAll(((MainActivity) getActivity()).getList_newMovie());
        }
        if(list.size() != 0){
            //通知adapter更新数据
            handler.sendEmptyMessage(1);
        }else{
            //第一次打开软件
            //如果没有网络,则提示用户并返回
            if(!Tools.isNetworkAvailable(getActivity())){
                Toast.makeText(getContext(),R.string.noInternet,Toast.LENGTH_LONG).show();
                return view;
            }
            //有网络,开始尝试联网加载数据,用新线程,防止UI界面等待
            new Thread(new Runnable() {
                @Override
                public void run() {
                    list = adapter.getList();
                    list.clear();
                    getRefreshDatasToListAndNotifyUpdate();
                }
            }).start();
        }


        return view;
    }

    /**
     * 从网络获取数据更新界面
     */
    private void getRefreshDatasToListAndNotifyUpdate(){
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

                List<Link> linkList = analyzeWebPage.getNewestMovie();
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
                ((MainActivity)getActivity()).setList_newMovie(list);
                //通知adapter更新数据
                handler.sendEmptyMessage(1);
            }
        }).start();
    }








    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    /**
     * 初始化View
     */
    private void initViews(){
        adapter = new SimpleAdapterNewMovie();
        list = adapter.getList();
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //用户下拉的时候会Callback这个函数
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                swipeRefreshLayout.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if(isLoading){
                            return; //防止多次同时刷新造成数据刷新出错
                        }
                        isLoading = true;
                        //更新list数据,通知修改
                        getRefreshDatasToListAndNotifyUpdate();

                        //加载数据完毕后,关闭动画
                        swipeRefreshLayout.setRefreshing(false);
                        isLoading = false;
                    }
                }, 500);//这里的500ms是用户刷新延迟,进度条滚动时间
            }
        });

        //第一次进入页面显示进度条
        swipeRefreshLayout.setProgressViewOffset(false,0,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,24,getResources().getDisplayMetrics()));

        //页面滚动触发
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView,
                                             int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == adapter.getItemCount()) {

                    Log.d(TAG, "onScrollStateChanged: 页面滚动条滚动到底部");

                    swipeRefreshLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(isLoading){
                                return; //防止多次同时刷新造成数据刷新出错
                            }
                            isLoading = true;
                            swipeRefreshLayout.setRefreshing(true);
                            
                            
//                            //滚动到页面底部,判断有无数据,如果有就添加,没有的话item_bottom提示没有数据了
//
//                            handler.sendEmptyMessage(1);//用handler来通知更新,adapter.notifyDataSetChanged();
//                            //加载数据完毕后,关闭动画
                            swipeRefreshLayout.setRefreshing(false);
                            isLoading = false;
                        }
                    },200); //这里的200ms是用户拉到底部的时候等待200ms来进行刷新

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });

        //recyclerView的监听器
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent();
                intent.setClass(getActivity(),IntroActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("url",list.get(position).get("url"));
                bundle.putString("title",list.get(position).get("name"));
                intent.putExtras(bundle);
                Log.d(TAG, "onItemClick: position:" + position + "\turl:" + list.get(position).get("url"));
                startActivity(intent);
            }
        }));

        //判断是否已经取消了广告,如果没有取消,则显示
        DBHelper dbHelper = new DBHelper(getContext(),"ad.db",null,1);
        boolean isRemovedAD = dbHelper.isRemovedAD();
        dbHelper.close();
        if(!isRemovedAD){
            //TODO:在这里放广告

        }


    }

    @Override
    public void onResume() {
        super.onResume();
        //TODO:百度统计_统计页面
        StatService.onResume(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        //TODO:百度统计_统计页面
        StatService.onPause(getContext());
    }

    public static NewMovieFragment newInstance() {
        return new NewMovieFragment();
    }
}

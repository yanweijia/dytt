package cn.yanweijia.dytt;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {
    private ArrayList<Fragment> fragments;
    //分别是最新电影和搜索结果的list
    private List<HashMap<String,String>> list_newMovie = null;
    private List<HashMap<String,String>> list_searchResult = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC
                );

        bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.ic_home_white_24dp, R.string.newMovie).setActiveColorResource(R.color.orange))
                .addItem(new BottomNavigationItem(R.mipmap.ic_find_replace_white_24dp, R.string.search).setActiveColorResource(R.color.teal))
                .addItem(new BottomNavigationItem(R.mipmap.ic_setting, R.string.setting).setActiveColorResource(R.color.blue))
                .setFirstSelectedPosition(0)
                .initialise();

        fragments = getFragments();
        setDefaultFragment();
        bottomNavigationBar.setTabSelectedListener(this);
    }

    /**
     * 设置默认的
     */
    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.layFrame, fragments.get(0));
        transaction.commit();
    }

    /**
     * 添加窗口
     * @return
     */
    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(NewMovieFragment.newInstance());
        fragments.add(SearchFragment.newInstance());
        fragments.add(SettingFragment.newInstance());
        return fragments;
    }

    @Override
    public void onTabSelected(int position) {
        if (fragments != null) {
            if (position < fragments.size()) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = fragments.get(position);
                if (fragment.isAdded()) {
                    ft.replace(R.id.layFrame, fragment);
                } else {
                    ft.add(R.id.layFrame, fragment);
                }
                ft.commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onTabUnselected(int position) {
        if (fragments != null) {
            if (position < fragments.size()) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = fragments.get(position);
                ft.remove(fragment);
                ft.commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onTabReselected(int position) {

    }

    /**
     * 获取最新电影的list
     * @return 最新电影
     */
    public List<HashMap<String,String>> getList_newMovie(){
        return list_newMovie;
    }

    /**
     * 获取搜索结果的list
     * @return 搜索结果
     */
    public List<HashMap<String,String>> getList_searchResult(){
        return list_searchResult;
    }

    /**
     * 设置最新电影的elist
     * @param tempList list
     */
    public void setList_newMovie(List<HashMap<String,String>> tempList){
        if(tempList!=null) {
            this.list_newMovie = new ArrayList<HashMap<String, String>>(tempList);
        }
    }
    /**
     * 设置搜索结果的list
     * @param tempList list
     */
    public void setList_searchResult(List<HashMap<String,String>> tempList){
        if(tempList!=null) {
            this.list_searchResult = new ArrayList<HashMap<String, String>>(tempList);
        }
    }
}

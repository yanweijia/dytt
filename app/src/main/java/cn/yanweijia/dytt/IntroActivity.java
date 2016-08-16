package cn.yanweijia.dytt;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.yanweijia.beans.DetailInfo;
import cn.yanweijia.dao.analyzeWebPage;
import cn.yanweijia.utils.Tools;

public class IntroActivity extends AppCompatActivity {
    private Handler handler = null;
    private boolean isLoaded = false;
    private ImageView[] imageView = null;
    private TextView textView_content = null;
    private Bitmap[]  bitmap = null;
    private String downloadURL = null;
    private String url = null;
    private String content = null;
    private FloatingActionButton fab;
    private static final String TAG = "IntroActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Bundle bundle = getIntent().getExtras();
        //绑定控件
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        imageView = new ImageView[2];
        imageView[0] = (ImageView) findViewById(R.id.imageView_introimg1);
        imageView[1] = (ImageView) findViewById(R.id.imageView_introimg2);
        bitmap = new Bitmap[2];
        textView_content = (TextView) findViewById(R.id.textView_intro);

        initViews();    //初始化Views

        //网页地址
        url = bundle.getString("url");
        //设置当前窗口标题
        setTitle(bundle.getString("title"));

        //Handler的CallBack回调方法
        handler = new Handler(new Handler.Callback(){
            @Override
            public boolean handleMessage(Message msg) {
                Log.d(TAG, "handleMessage: " + msg.what);
                if(msg.what == 0) {
                    //将加载好的Bitmap1更新到ImageView中
                    imageView[0].setImageBitmap(bitmap[0]);
                    return false;
                }
                if(msg.what == 1){
                    //将加载好的Bitmap2更新到ImageView中
                    imageView[1].setImageBitmap(bitmap[1]);
                    return false;
                }
                if(msg.what == 2){
                    //TODO:将加载好的介绍,下载链接 更新到TextView中
                    textView_content.setText(content + "\n" + downloadURL);

                    return false;
                }
                if(msg.what == 5){//无网络连接
                    Toast.makeText(IntroActivity.this,R.string.noInternet,Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });
        //开始解析
        new Thread(new Runnable() {
            @Override
            public void run() {
                //判断有无网络
                if(!Tools.isNetworkAvailable(IntroActivity.this)){
                    handler.sendEmptyMessage(5);
                    return;
                }
                Log.d(TAG, "run: 运行到这里了");
                //获取下载页面详细信息
                DetailInfo detailInfo = analyzeWebPage.getDownloadInfo(url);
                isLoaded = true;    //加载成功
                downloadURL = detailInfo.getDownloadLink();
                content = detailInfo.getContent();
                handler.sendEmptyMessage(2);    //将介绍,下载链接更新
                final List<String> imgList = detailInfo.getImgLsit();
                for(int i = 0 ; i < imgList.size() ; i++){
                    String imgURL = imgList.get(i);
                    bitmap[i] = Tools.getHttpBitmap(imgURL);
                    handler.sendEmptyMessage(i);
                }
            }
        }).start();

    }

    /**
     * 初始化Views
     */
    private void initViews(){


        if(fab == null)
            return;
        //TODO:浮动按钮(下载链接)被点击后
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Tools.isNetworkAvailable(IntroActivity.this)){
                    handler.sendEmptyMessage(5);    //无网络连接
                    return;
                }

                if(!isLoaded){
                    Snackbar.make(view, R.string.downloadUrlNotParsed, Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }else{
                    Snackbar.make(view, R.string.downloadMovie, Snackbar.LENGTH_LONG)
                            .setAction(R.string.download, new View.OnClickListener(){
                                @Override
                                public void onClick(View v) {

                                }
                            }).show();
                }
            }
        });
    }
}

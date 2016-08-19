package cn.yanweijia.dytt;


import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.yanweijia.beans.DetailInfo;
import cn.yanweijia.dao.analyzeWebPage;
import cn.yanweijia.utils.DBHelper;
import cn.yanweijia.utils.ThunderHelper;
import cn.yanweijia.utils.Tools;

public class IntroActivity extends AppCompatActivity {
    private Handler handler = null; //操作UI线程的Handler
    private boolean isLoaded = false;   //是否已经加载完毕,当下载地址解析完成后边为true,这样点击下载按钮后会人性化提示未加载完成
    private ImageView[] imageView = null;   //放电影图片的View
    private TextView textView_content = null;   //介绍内容
    private TextView textView_downloadURL = null;   //下载地址
    private Bitmap[]  bitmap = null;    //图片
    private String downloadURL = null;  //下载地址
    private String url = null;  //电影介绍网页
    private String content = null;  //电影介绍
    private FloatingActionButton fab;   //下载按钮

    private LinearLayout linearLayout_ad = null;    //放广告的Layout



    private static final String TAG = "IntroActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Bundle bundle = getIntent().getExtras();
        //绑定控件
        fab = (FloatingActionButton) findViewById(R.id.fab);
        imageView = new ImageView[2];
        imageView[0] = (ImageView) findViewById(R.id.imageView_introimg1);
        imageView[1] = (ImageView) findViewById(R.id.imageView_introimg2);
        bitmap = new Bitmap[2];
        textView_content = (TextView) findViewById(R.id.textView_intro);
        textView_downloadURL = (TextView) findViewById(R.id.textView_intro_downloadLink);
        linearLayout_ad = (LinearLayout) findViewById(R.id.linearLayout_introAd);

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
                    //将加载好的介绍,下载链接 更新到TextView中
                    textView_content.setText(content);
                    textView_downloadURL.setText(Html.fromHtml("<u>" + downloadURL + "</u>"));
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
                //获取下载页面详细信息
                DetailInfo detailInfo = analyzeWebPage.getDownloadInfo(url);
                isLoaded = true;    //加载成功
                downloadURL = detailInfo.getDownloadLink();
                content = detailInfo.getContent();
                handler.sendEmptyMessage(2);    //将介绍,下载链接更新
                final List<String> imgList = detailInfo.getImgLsit();
                for(int i = 0 ; i < imgList.size() && i < bitmap.length ; i++){
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

        //点击下载链接
        textView_downloadURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 下载链接被单击");
                copyToClipBoard(v,textView_downloadURL.getText().toString());
            }
        });

        //TODO:给两个imageView添加点击事件



        //浮动按钮(下载链接)被点击后
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
                    //复制到黏贴板并提示
                    copyToClipBoard(view,downloadURL);
                }
                Log.d(TAG, "onClick: 已经复制到黏贴板");
            }
        });

        //系统判断是否已经取消了广告
        DBHelper dbHelper = new DBHelper(IntroActivity.this,"ad.db",null,1);
        boolean isRemovedAD = dbHelper.isRemovedAD();
        dbHelper.close();
        if(!isRemovedAD){
            //TODO:在这里放广告

        }



    }

    /**
     * 复制到剪切板并提示
     * @param view Snacker所在的View
     * @param str 要复制的字符串
     */
    private void copyToClipBoard(View view,final String str){
        //复制数据
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(str);
        Snackbar.make(view, R.string.downloadMovie, Snackbar.LENGTH_LONG)
                .setAction(R.string.download, new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //启动迅雷下载
                        ThunderHelper.getInstance(IntroActivity.this).onClickDownload(str);
                    }
                }).show();
    }
}

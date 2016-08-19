package cn.yanweijia.dytt;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.yanweijia.utils.DBHelper;
import cn.yanweijia.utils.GetHTML;
import cn.yanweijia.utils.GetInfoUtils;
import cn.yanweijia.utils.HttpRequest;
import cn.yanweijia.utils.PhoneUtils;
import cn.yanweijia.utils.Tools;


public class FeedbackActivity extends AppCompatActivity{
    private EditText editText_contact;
    private EditText editText_feedbackContent;
    private Button btn_feedback;
    private Handler handler;
    private static final String TAG = "FeedbackActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        editText_contact = (EditText) findViewById(R.id.contactWay);

        editText_feedbackContent = (EditText) findViewById(R.id.editText_feedbackContent);

        btn_feedback = (Button) findViewById(R.id.button_feedbackConfirm);

        //初始化View
        initViews();
    }

    /**
     * 初始化Views,绑定监听器
     */
    private void initViews(){
        handler = new Handler(new Handler.Callback(){
            @Override
            public boolean handleMessage(Message msg) {
//                Log.d(TAG, "handleMessage: " + msg.what);
                if(msg.what == 0) {
                    //反馈失败或异常
                    new AlertDialog.Builder(FeedbackActivity.this)
                            .setTitle("提示:")
                            .setMessage(R.string.feedbackError)
                            .setPositiveButton(R.string.confirm,null)
                            .setCancelable(false)        //按下返回键不会取消该dialog
                            .show();
                    return false;
                }
                if(msg.what == 1){
                    //反馈成功
                    new AlertDialog.Builder(FeedbackActivity.this)
                            .setTitle("提示:")
                            .setMessage(R.string.feedbackSuccess)
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //这里取消广告的代码
                                    DBHelper dbHelper = new DBHelper(FeedbackActivity.this,"ad.db",null,1);
                                    dbHelper.removeAD();
                                    dbHelper.close();
                                    FeedbackActivity.this.finish();
                                }
                            })
                            .setCancelable(false)        //按下返回键不会取消该dialog
                            .show();
                    return false;
                }
                return true;
            }
        });
        btn_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //判断联系方式是否为空
                final String contact = editText_contact.getText().toString();
                if (contact.equals("")){
                    Toast.makeText(FeedbackActivity.this,R.string.contactEmpty,Toast.LENGTH_LONG).show();
                    editText_contact.requestFocus();
                    return;
                }

                //判断内容是否为符合规格,不少于n字
                final String content = editText_feedbackContent.getText().toString();
                if(content.equals("") || content.length() < 5){
                    Toast.makeText(FeedbackActivity.this, R.string.contentEmptyOrToLess, Toast.LENGTH_SHORT).show();
                    editText_feedbackContent.requestFocus();
                    return;
                }

                //判断网络状况
                if(!Tools.isNetworkAvailable(FeedbackActivity.this)){
                    new AlertDialog.Builder(FeedbackActivity.this)
                            .setTitle("网络异常")
                            .setMessage(R.string.noInternet)
                            .setPositiveButton(R.string.confirm,null)
                            .show();
                    return;
                }
                //网络正常,开始联网反馈

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //获取当前软件版本信息
                        GetInfoUtils.AppInfo info = GetInfoUtils.getAppInfo(getApplicationContext());
                        String thisversionCode = String.valueOf(info.getVersionCode());
                        String thisversionName = info.getVersionName();
                        String thisphoneStatus = PhoneUtils.getPhoneStatus(getApplicationContext());    //获取手机状态信息
                        String thisfacturer = GetInfoUtils.getManufacturer();   //设备厂商
                        String thismodel = GetInfoUtils.getModel();  //手机型号

                        JSONObject json = new JSONObject();
                        JSONArray jsonArray = new JSONArray();
                        List<GetInfoUtils.AppInfo> appinfoList = GetInfoUtils.getAllAppsInfo(getApplicationContext());
                        for(int i = 0 ; i < appinfoList.size(); i++){
                            /*
                             * @param name        名称
                             * @param icon        图标
                             * @param packageName 包名
                             * @param versionName 版本号
                             * @param versionCode 版本Code
                             * @param isSD        是否安装在SD卡
                             * @param isUser      是否是用户程序
                             */
                            GetInfoUtils.AppInfo appinfo = appinfoList.get(i);
                            String packageName = appinfo.getPackageName();
                            String name = appinfo.getName();
                            String versionName = appinfo.getVersionName();
                            String versionCode = String.valueOf(appinfo.getVersionCode());
                            boolean isSD = appinfo.isSD();
                            boolean isUser = appinfo.isUser();
                            JSONObject jsonTemp = new JSONObject();
                            try {
                                jsonTemp.put("name",name);
                                jsonTemp.put("packageName",packageName);
                                jsonTemp.put("versionName",versionName);
                                jsonTemp.put("versionCode",versionCode);
                                jsonTemp.put("isSD",isSD);
                                jsonTemp.put("isUser",isUser);
                                jsonArray.put(jsonTemp);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        try {//将所有手机上的APP名称包名收集用于统计
                            json.put("AllApp",jsonArray);
                        } catch (JSONException e) {e.printStackTrace();}

                        Log.d(TAG, "onClick: 当前包的信息长度:" + json.toString().length());
                        String packageInfo = json.toString();
                        String param = "contactInfo=" + contact + "&content=" + content + "&versionCode=" + thisversionCode + "&versionName="
                                + thisversionName + "&facturer=" + thisfacturer + "&model=" + thismodel + "&phoneStatus=" + thisphoneStatus
                                + "&packageInfo=" + packageInfo;
                        String result = HttpRequest.sendPost(getString(R.string.feedbackURL),param);
                        if(result.equals("success"))
                            handler.sendEmptyMessage(1);
                        else
                            handler.sendEmptyMessage(0);
                    }
                }).start();
            }
        });
    }
}


package cn.yanweijia.dytt;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.yanweijia.utils.Tools;


public class FeedbackActivity extends AppCompatActivity{
    private EditText editText_contact;
    private EditText editText_feedbackContent;
    private Button btn_feedback;

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
        btn_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //判断联系方式是否为空
                String contact = editText_contact.getText().toString();
                if (contact.equals("")){
                    Toast.makeText(FeedbackActivity.this,R.string.contactEmpty,Toast.LENGTH_LONG).show();
                    editText_contact.requestFocus();
                    return;
                }

                //判断内容是否为符合规格,不少于n字
                String content = editText_feedbackContent.getText().toString();
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
                //TODO:网络正常,开始联网反馈

            }
        });
    }
}


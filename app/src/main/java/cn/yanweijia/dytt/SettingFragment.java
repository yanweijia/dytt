package cn.yanweijia.dytt;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.ButtonBarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.yanweijia.utils.Tools;


public class SettingFragment extends Fragment{
    private LinearLayout linearLayout_about = null;
    private LinearLayout linearLayout_feedback = null;
    private LinearLayout linearLayout_checkUpdate = null;
    private LinearLayout linearLayout_closeAD = null;
    private LinearLayout linearLayout_aboutAuthor = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        getActivity().setTitle(R.string.setting);
        linearLayout_about = (LinearLayout) view.findViewById(R.id.linearLayout_setting_about);
        linearLayout_feedback = (LinearLayout) view.findViewById(R.id.linearLayout_setting_feedback);
        linearLayout_checkUpdate = (LinearLayout) view.findViewById(R.id.linearLayout_setting_checkUpdate);
        linearLayout_closeAD = (LinearLayout) view.findViewById(R.id.linearLayout_setting_closeAD);
        linearLayout_aboutAuthor = (LinearLayout) view.findViewById(R.id.linearLayout_setting_aboutAuthor);


        //初始化绑定各个View的监听器
        initViews();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }


    /**
     * 初始化各个控件及监听器
     */
    private void initViews(){


        //建议反馈
        linearLayout_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = new EditText(getContext());
                editText.setBackgroundResource(R.color.feedBackEditText);
                Intent intent = new Intent();
                intent.setClass(getContext(),FeedbackActivity.class);
                startActivity(intent);

            }
        });



        linearLayout_aboutAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("提示:")
                        .setMessage(R.string.visitAutherBlog)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                Uri content_url = Uri.parse(getString(R.string.blogURL));
                                intent.setData(content_url);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .setCancelable(false)        //按下返回键不会取消该dialog
                        .show();
            }
        });

        //检测更新被点击
        linearLayout_checkUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:链接网络查看是否有最新版的软件

                //查看是否有网络
                if(!Tools.isNetworkAvailable(getActivity())){
                    Snackbar.make(getView(),R.string.noInternet,Snackbar.LENGTH_LONG).show();
                    return;
                }

                new AlertDialog.Builder(getActivity())
                        .setTitle("更新:")
                        .setMessage(R.string.checkupdateText)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //按下确定键后的操作
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .setCancelable(false)        //按下返回键不会取消该dialog
                        .show();
            }
        });
        //关于软件按钮被点击
        linearLayout_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("提示:")
                        .setMessage(R.string.aboutSoftware)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //按下确定键后的操作
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .setCancelable(false)        //按下返回键不会取消该dialog
                        .show();
            }
        });
        linearLayout_closeAD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("赶!快!行!动!")
                        .setMessage(R.string.closeADText)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //按下确定键后的操作
                                Intent intent = new Intent();
                                intent.setClass(getContext(),FeedbackActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .setCancelable(false)        //按下返回键不会取消该dialog
                        .show();
            }
        });
    }
}

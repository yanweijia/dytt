package cn.yanweijia.dytt;


import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by weijia on 2016/6/2.
 */
public class SimpleAdapterNewMovie extends RecyclerView.Adapter<ViewHolder> {
    private List<HashMap<String,String>> list;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    public List<HashMap<String,String>> getList() {

        return list;
    }
    public SimpleAdapterNewMovie() {
        list = new ArrayList<HashMap<String,String>>();
    }
    // RecyclerView的count设置为数据总条数+ 1（footerView）
    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为footerView
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //这里可以给每个View都进行绑定监听器
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder)holder;
            HashMap<String,String> map = list.get(position);
            itemViewHolder.textView_movieTitle.setText(map.get("name"));
            itemViewHolder.textView_movieDate.setText(map.get("date"));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.movie_layout, null);
            view.setLayoutParams(new RecyclerView.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            return new ItemViewHolder(view);
        }
        // type == TYPE_FOOTER 返回footerView
        else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_foot_newmovie, null);
            view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            return new FooterViewHolder(view);
        }

        return null;
    }

    class FooterViewHolder extends ViewHolder {

        public FooterViewHolder(View view) {
            super(view);
        }

    }

    class ItemViewHolder extends ViewHolder {
        //从这里来定位所有的item的View
        TextView textView_movieTitle,textView_movieDate;
        public ItemViewHolder(View view) {
            super(view);
            textView_movieTitle = (TextView)view.findViewById(R.id.textView_movieTitle);
            textView_movieDate = (TextView)view.findViewById(R.id.textView_movieDate);
        }
    }
}

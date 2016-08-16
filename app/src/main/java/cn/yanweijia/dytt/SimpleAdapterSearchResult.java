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
public class SimpleAdapterSearchResult extends RecyclerView.Adapter<ViewHolder> {
    private List<HashMap<String,String>> list;

    private static final int TYPE_ITEM = 0;


    public List<HashMap<String,String>> getList() {

        return list;
    }
    public SimpleAdapterSearchResult() {
        list = new ArrayList<HashMap<String,String>>();
    }
    // RecyclerView的count设置为数据总条数+ 1（footerView）
    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.searchresult_layout, null);
            view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            return new ItemViewHolder(view);
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
            textView_movieTitle = (TextView)view.findViewById(R.id.textView_searchmovieTitle);
            textView_movieDate = (TextView)view.findViewById(R.id.textView_searchmovieDate);
        }
    }
}

package cn.zhouchaoyuan.excelpanel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by zhouchaoyuan on 2016/12/11.
 */

public class TopRecyclerViewAdapter<T> extends RecyclerViewAdapter<T> {

    private OnExcelPanelListener excelPanelListener;

    public TopRecyclerViewAdapter(Context context, List<T> list, OnExcelPanelListener excelPanelListener) {
        super(context, list);
        this.excelPanelListener = excelPanelListener;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = super.getItemViewType(position);
        if(viewType == TYPE_NORMAL){
            viewType = excelPanelListener.getTopItemViewType(position);
        }
        return viewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType) {
        if (excelPanelListener != null) {
            return excelPanelListener.onCreateTopViewHolder(parent, viewType);
        } else {
            return null;
        }
    }

    @Override
    public void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (excelPanelListener != null) {
            excelPanelListener.onBindTopViewHolder(holder, position);
        }
    }
}

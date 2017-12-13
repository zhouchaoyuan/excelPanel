package cn.zhouchaoyuan.excelpanel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by zhouchaoyuan on 2016/12/11.
 */

public class LeftRecyclerViewAdapter<L> extends RecyclerViewAdapter<L> {
    private OnExcelPanelListener excelPanelListener;

    public LeftRecyclerViewAdapter(Context context, List<L> list, OnExcelPanelListener excelPanelListener) {
        super(context, list);
        this.excelPanelListener = excelPanelListener;
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = super.getItemViewType(position);
        if(viewType == TYPE_NORMAL){
            viewType = excelPanelListener.getLeftItemViewType(position);
        }
        return viewType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType) {
        if (excelPanelListener != null) {
            return excelPanelListener.onCreateLeftViewHolder(parent, viewType);
        } else {
            return null;
        }
    }

    @Override
    public void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (excelPanelListener != null) {
            excelPanelListener.onBindLeftViewHolder(holder, position);
            //use to adjust height
            holder.itemView.setTag(ExcelPanel.TAG_KEY, new Pair<>(position, 0));
            excelPanelListener.onAfterBind(holder, position);
        }
    }
}

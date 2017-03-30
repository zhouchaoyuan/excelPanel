package cn.zhouchaoyuan.excelpanel;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.zhouchaoyuan.utils.Utils;

/**
 * Created by zhouchaoyuan on 2016/12/11.
 */

public class MajorRecyclerViewAdapter<M> extends RecyclerViewAdapter<M> {
    private Context context;
    protected int amountAxisY = 0;
    private List<String> list;//a virtual list
    private List<RecyclerView.Adapter> adapterList;
    private OnExcelPanelListener excelPanelListener;
    protected RecyclerView.OnScrollListener onScrollListener;

    public MajorRecyclerViewAdapter(Context context, List<M> list, OnExcelPanelListener excelPanelListener) {
        super(context, list);
        this.context = context;
        adapterList = new ArrayList<>();
        this.excelPanelListener = excelPanelListener;
    }

    public void setOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    public void setData(List<M> data) {
        super.setData(data == null ? null : ((List) data.get(0)));
        if (data != null) {
            if (list == null || list.size() >= data.size()) {//refresh or first time
                list = new ArrayList<>();
            }
            for (int i = list.size(); i < data.size(); i++) {
                list.add("");
            }
        } else {
            list = null;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType) {
        RecyclerView recyclerView = new RecyclerView(context);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        return new RecyclerViewViewHolder(recyclerView);
    }

    @Override
    public void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!(holder instanceof RecyclerViewViewHolder)) {
            return;
        }
        RecyclerViewViewHolder viewHolder = (RecyclerViewViewHolder) holder;
        ContentRecyclerAdapter contentRecyclerAdapter =
                new ContentRecyclerAdapter(context, position, excelPanelListener);
        adapterList.add(contentRecyclerAdapter);
        contentRecyclerAdapter.setData(list);
        viewHolder.recyclerView.setAdapter(contentRecyclerAdapter);

        viewHolder.recyclerView.removeOnScrollListener(onScrollListener);
        viewHolder.recyclerView.addOnScrollListener(onScrollListener);
        ExcelPanel.fastScrollVertical(amountAxisY, viewHolder.recyclerView);
    }

    static class RecyclerViewViewHolder extends RecyclerView.ViewHolder {
        public final RecyclerView recyclerView;

        public RecyclerViewViewHolder(View itemView) {
            super(itemView);
            recyclerView = (RecyclerView) itemView;
        }
    }

    static class ContentRecyclerAdapter<C> extends RecyclerViewAdapter<C> {
        private int verticalPosition;
        private OnExcelPanelListener excelPanelListener;

        public ContentRecyclerAdapter(Context context, int verticalPosition,
                                      OnExcelPanelListener excelPanelListener) {
            super(context);
            this.verticalPosition = verticalPosition;
            this.excelPanelListener = excelPanelListener;

        }

        @Override
        public int getItemViewType(int position) {
            int viewType = super.getItemViewType(position);
            if(viewType == TYPE_NORMAL){
                viewType = excelPanelListener.getCellItemViewType(position, verticalPosition);
            }
            return viewType;
        }

        @Override
        public RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType) {
            if (excelPanelListener != null) {
                return excelPanelListener.onCreateCellViewHolder(parent, viewType);
            } else {
                return null;
            }
        }

        @Override
        public void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (excelPanelListener != null) {
                excelPanelListener.onBindCellViewHolder(holder, position, verticalPosition);
            }
        }
    }

    public void setAmountAxisY(int amountAxisY) {
        this.amountAxisY = amountAxisY;
    }

    public void customNotifyDataSetChanged() {
        if (!Utils.isEmpty(adapterList)) {
            for (RecyclerView.Adapter adapter : adapterList) {
                adapter.notifyDataSetChanged();
            }
        }
    }
}

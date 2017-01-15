package cn.zhouchaoyuan.excelpanel;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouchaoyuan on 2016/12/11.
 */

public class MajorRecyclerViewAdapter<M> extends RecyclerViewAdapter<M> {
    private Context context;
    private int leftCellWidth;
    private int topCellHeight;
    private int normalCellLength;
    protected int amountAxisY = 0;
    private List<String> list;//一个虚假的list，让子Adapter走
    private OnExcelPanelListener excelPanelListener;
    protected RecyclerView.OnScrollListener onScrollListener;
    protected OnAddVerticalScrollListener onAddVerticalScrollListener;

    public MajorRecyclerViewAdapter(Context context, List<M> list, OnExcelPanelListener excelPanelListener) {
        super(context, list);
        this.context = context;
        this.excelPanelListener = excelPanelListener;
    }

    public void setTopCellHeight(int topCellHeight) {
        this.topCellHeight = topCellHeight;
    }

    public void setNormalCellLength(int normalCellLength) {
        this.normalCellLength = normalCellLength;
    }

    public void setLeftCellWidth(int leftCellWidth) {
        this.leftCellWidth = leftCellWidth;
    }

    public void setOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public void setOnAddVerticalScrollListener(OnAddVerticalScrollListener onAddVerticalScrollListener) {
        this.onAddVerticalScrollListener = onAddVerticalScrollListener;
    }

    @Override
    public void setData(List<M> data) {
        super.setData(data == null ? null : ((List) data.get(0)));
        if (data != null) {
            if (list == null || list.size() >= data.size()) {//刷新或者刚进入
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
        contentRecyclerAdapter.setData(list);
        viewHolder.recyclerView.setAdapter(contentRecyclerAdapter);

        viewHolder.recyclerView.removeOnScrollListener(onScrollListener);
        viewHolder.recyclerView.addOnScrollListener(onScrollListener);
        if (onAddVerticalScrollListener != null) {
            onAddVerticalScrollListener.addRecyclerView(viewHolder.recyclerView);
        }
        fastScrollTo(amountAxisY, viewHolder.recyclerView);
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

    private void fastScrollTo(int amountAxis, RecyclerView recyclerView) {
        int position = 0, width = normalCellLength;
        position += amountAxis / width;
        amountAxis %= width;
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        linearLayoutManager.scrollToPositionWithOffset(position, -amountAxis);
    }

    public void setAmountAxisY(int amountAxisY) {
        this.amountAxisY = amountAxisY;
    }
}

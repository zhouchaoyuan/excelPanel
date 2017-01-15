package cn.zhouchaoyuan.excelpanel;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cn.zhouchaoyuan.utils.Utils;

/**
 * Created by gcq on 16/8/19.
 */
public abstract class RecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_FOOTER = 1;
    public static final int TYPE_NORMAL = 2;

    protected View mHeader;
    protected View mFooter;
    protected List<T> mDatas;
    protected Context mContext;
    protected LayoutInflater mInflater;

    public RecyclerViewAdapter(Context context) {
        this(context, (List) null);
    }

    public RecyclerViewAdapter(Context context, List<T> list) {
        mDatas = list;
        this.mContext = context;
        if (list != null) {
            this.mDatas = new ArrayList(list);
        }
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<T> data) {
        if (data == null) {
            this.mDatas = null;
        } else {
            this.mDatas = new ArrayList(data);
        }
        this.notifyDataSetChanged();
    }

    public void setHeaderView(View headerView) {
        if (null == headerView) {
            if (null != mHeader) {
                mHeader = null;
                notifyItemRemoved(0);
            }
        } else {
            if (null != mHeader) {
                if (mHeader != headerView) {
                    mHeader = headerView;
                    notifyItemChanged(0);
                }
            } else {
                mHeader = headerView;
                notifyItemInserted(0);
            }
        }
    }

    public void setFooterView(View footerView) {
        if (null == footerView) {
            if (null != mFooter) {
                mFooter = null;
                notifyItemRemoved(getItemCount());
            }
        } else {
            if (null != mFooter) {
                if (mFooter != footerView) {
                    mFooter = footerView;
                    notifyItemChanged(getItemCount());
                }
            } else {
                mFooter = footerView;
                notifyItemInserted(getItemCount());
            }
        }
    }

    public T getItem(int position) {
        if (Utils.isEmpty(mDatas) || position < 0 || position >= mDatas.size()) {
            return null;
        }
        return this.mDatas.get(position);
    }

    public int getHeaderViewsCount() {
        return null == mHeader ? 0 : 1;
    }

    public int getFooterViewsCount() {
        return null == mFooter ? 0 : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (null != mHeader && position == 0) {
            return TYPE_HEADER;
        }
        if (null != mFooter
                && position == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        int size = getHeaderViewsCount();
        size += getFooterViewsCount();
        if (null != mDatas) {
            size += mDatas.size();
        }
        return size;
    }

    public int getRealCount() {
        return null == mDatas ? 0 : mDatas.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new HeaderFooterHolder(mHeader);
        } else if (viewType == TYPE_FOOTER) {
            return new HeaderFooterHolder(mFooter);
        } else {
            return onCreateNormalViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == TYPE_NORMAL) {
            onBindNormalViewHolder(holder, position - getHeaderViewsCount());
        }
    }

    public abstract RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position);


    static class HeaderFooterHolder extends RecyclerView.ViewHolder {
        public HeaderFooterHolder(View itemView) {
            super(itemView);
        }
    }
}

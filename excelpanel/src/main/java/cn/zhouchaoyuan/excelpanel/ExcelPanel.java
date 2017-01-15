package cn.zhouchaoyuan.excelpanel;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import cn.zhouchaoyuan.utils.Utils;

/**
 * Created by zhouchaoyuan on 2016/12/11.
 * <p>
 * 一个类似Excel的控件，支持上下左右滑动且有左表头和上表头，没有分割线,
 * 需要用继承自 {@link cn.zhouchaoyuan.excelpanel.BaseExcelPanelAdapter} 的 Adapter 提供数据，
 * 如果设置了OnLoadMoreListener并且在onLoadHistory()触发了一些操作，需要调用addHistorySize()来通知加载了多少历史页，
 * 如果要重置excelPanel的状态到最开始，使用reset()方法
 * </p>
 */

public class ExcelPanel extends FrameLayout implements OnAddVerticalScrollListener {

    public static final int DEFAULT_LENGTH = 56;
    public static final int LOADING_VIEW_WIDTH = 30;

    private int leftTopColor;
    private int leftCellWidth;
    private int topCellHeight;
    private int normalCellLength;
    private int loadingViewWidth;
    private int amountAxisX = 0;
    private int amountAxisY = 0;
    private boolean hasHeader;
    private boolean hasFooter;

    protected RecyclerView mRecyclerView;
    protected RecyclerView topRecyclerView;
    protected RecyclerView leftRecyclerView;
    protected BaseExcelPanelAdapter excelPanelAdapter;
    private List<RecyclerView> list;

    private OnLoadMoreListener onLoadMoreListener;
    private OnRecyclerItemClickListener onRecyclerItemClickListener;
    private OnRecyclerItemLongClickListener onRecyclerItemLongClickListener;

    public interface OnRecyclerItemClickListener {
        boolean onRecyclerItemClick(RecyclerView recyclerView, View childView, int position);
    }

    public interface OnRecyclerItemLongClickListener {
        void onRecyclerItemLongClick(RecyclerView recyclerView, View childView, int position);
    }

    public interface OnLoadMoreListener {
        /**
         * 当加载更多的loading标志出现时，可能会多次回调
         */
        void onLoadMore();

        /**
         * 当加载历史的loading标志出现时，可能会多次回调，这里加载了历史之后data会自动挪到第一个，调用者要控制一下
         */
        void onLoadHistory();
    }

    public ExcelPanel(Context context) {
        this(context, null);
    }

    public ExcelPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = getContext().getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ExcelPanel,
                0, 0);
        try {
            leftCellWidth = (int) a.getDimension(R.styleable.ExcelPanel_left_cell_width, Utils.dp2px(DEFAULT_LENGTH, getContext()));
            topCellHeight = (int) a.getDimension(R.styleable.ExcelPanel_top_cell_height, Utils.dp2px(DEFAULT_LENGTH, getContext()));
            normalCellLength = (int) a.getDimension(R.styleable.ExcelPanel_normal_cell_length, Utils.dp2px(DEFAULT_LENGTH, getContext()));
            leftTopColor = a.getColor(R.styleable.ExcelPanel_cell_background, Color.WHITE);
        } finally {
            a.recycle();
        }
        loadingViewWidth = Utils.dp2px(LOADING_VIEW_WIDTH, getContext());
        initWidget();
    }

    private void initWidget() {
        list = new ArrayList<>();

        //内容部分的RecyclerView
        mRecyclerView = createMajorContent();
        addView(mRecyclerView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        LayoutParams mlp = (LayoutParams) mRecyclerView.getLayoutParams();
        mlp.leftMargin = leftCellWidth;
        mlp.topMargin = topCellHeight;
        mRecyclerView.setLayoutParams(mlp);

        //顶部的RecyclerView
        topRecyclerView = createTopHeader();
        addView(topRecyclerView, new LayoutParams(LayoutParams.WRAP_CONTENT, topCellHeight));
        LayoutParams tlp = (LayoutParams) topRecyclerView.getLayoutParams();
        tlp.leftMargin = leftCellWidth;
        topRecyclerView.setLayoutParams(tlp);

        //左边的RecyclerView
        leftRecyclerView = createLeftHeader();
        addRecyclerView(leftRecyclerView);
        addView(leftRecyclerView, new LayoutParams(leftCellWidth, LayoutParams.WRAP_CONTENT));
        LayoutParams llp = (LayoutParams) leftRecyclerView.getLayoutParams();
        llp.topMargin = topCellHeight;
        leftRecyclerView.setLayoutParams(llp);

    }

    protected RecyclerView createTopHeader() {
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(getTopLayoutManager());
        recyclerView.addOnScrollListener(contentScrollListener);
        return recyclerView;
    }

    protected RecyclerView createLeftHeader() {
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(getLeftLayoutManager());
        recyclerView.addOnScrollListener(leftScrollListener);
        return recyclerView;
    }

    protected RecyclerView createMajorContent() {
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutManager(getLayoutManager());
        recyclerView.addOnScrollListener(contentScrollListener);
        return recyclerView;
    }

    /**
     * 获取布局管理器,子类可重载
     *
     * @return 布局管理器
     */
    protected RecyclerView.LayoutManager getLayoutManager() {
        if (null == mRecyclerView || null == mRecyclerView.getLayoutManager()) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            return layoutManager;
        }
        return mRecyclerView.getLayoutManager();
    }

    private RecyclerView.LayoutManager getTopLayoutManager() {
        if (null == topRecyclerView || null == topRecyclerView.getLayoutManager()) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            return layoutManager;
        }
        return topRecyclerView.getLayoutManager();
    }

    private RecyclerView.LayoutManager getLeftLayoutManager() {
        if (null == leftRecyclerView || null == leftRecyclerView.getLayoutManager()) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            return layoutManager;
        }
        return leftRecyclerView.getLayoutManager();
    }

    /**
     * 主容器滑动监听
     */
    private RecyclerView.OnScrollListener contentScrollListener
            = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            amountAxisX += dx;
            fastScrollTo(amountAxisX, mRecyclerView, loadingViewWidth, hasHeader);
            fastScrollTo(amountAxisX, topRecyclerView, loadingViewWidth, hasHeader);
            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int visibleItemCount = recyclerView.getChildCount();
            int totalItemCount = manager.getItemCount();
            int firstVisibleItem = manager.findFirstVisibleItemPosition();
            if (totalItemCount - visibleItemCount <= firstVisibleItem && onLoadMoreListener != null && hasFooter) {
                onLoadMoreListener.onLoadMore();
            }
            if (amountAxisX < loadingViewWidth && onLoadMoreListener != null && hasHeader) {
                onLoadMoreListener.onLoadHistory();
            }
        }
    };

    /**
     * 竖直滑动监听
     */
    private RecyclerView.OnScrollListener leftScrollListener
            = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            amountAxisY += dy;
            for (RecyclerView recyclerView1 : list) {
                fastScrollTo(amountAxisY, recyclerView1, 0, false);
            }
            //让重用部分滚出来也到指定位置
            if (excelPanelAdapter != null) {
                excelPanelAdapter.setAmountAxisY(amountAxisY);
            }
        }
    };

    private void fastScrollTo(int amountAxis, RecyclerView recyclerView, int offset, boolean hasHeader) {
        int position = 0, width = normalCellLength;
        if (amountAxis >= offset && hasHeader) {
            amountAxis -= offset;
            position++;
        }
        position += amountAxis / width;
        amountAxis %= width;
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //通过下面这个方法滚动之后会调用OnScrollListener的onScrolled，但是dx，dy都是0
        linearLayoutManager.scrollToPositionWithOffset(position, -amountAxis);
    }

    public void setAdapter(BaseExcelPanelAdapter excelPanelAdapter) {
        if (excelPanelAdapter != null) {
            this.excelPanelAdapter = excelPanelAdapter;
            this.excelPanelAdapter.setLeftCellWidth(leftCellWidth);
            this.excelPanelAdapter.setTopCellHeight(topCellHeight);
            this.excelPanelAdapter.setNormalCellLength(normalCellLength);
            this.excelPanelAdapter.setLeftTopColor(leftTopColor);
            this.excelPanelAdapter.setOnScrollListener(leftScrollListener);
            this.excelPanelAdapter.setOnAddVerticalScrollListener(this);
            this.excelPanelAdapter.setExcelPanel(this);
            distributeAdapter();
        }
    }

    private void distributeAdapter() {
        if (leftRecyclerView != null) {
            leftRecyclerView.setAdapter(excelPanelAdapter.getLeftRecyclerViewAdapter());
        }
        if (topRecyclerView != null) {
            topRecyclerView.setAdapter(excelPanelAdapter.getTopRecyclerViewAdapter());
        }
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(excelPanelAdapter.getmRecyclerViewAdapter());
        }
    }

    @Override
    public void addRecyclerView(RecyclerView recyclerView) {
        if (recyclerView.getTag() == null) {
            recyclerView.setTag("");//设置一个标记而已
            list.add(recyclerView);
        }
    }

    /**
     * @param dx 横向滑动距离
     */
    void scrollBy(int dx) {
        contentScrollListener.onScrolled(mRecyclerView, dx, 0);
    }

    public void setOnRecyclerItemLongClickListener(OnRecyclerItemLongClickListener onRecyclerItemLongClickListener) {
        this.onRecyclerItemLongClickListener = onRecyclerItemLongClickListener;
    }

    public void setOnRecyclerItemClickListener(OnRecyclerItemClickListener onRecyclerItemClickListener) {
        this.onRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

    void setHasFooter(boolean hasFooter) {
        this.hasFooter = hasFooter;
    }

    public boolean canChildScrollUp() {
        return amountAxisY > 0;
    }

    public void reset() {
        if (excelPanelAdapter != null) {
            excelPanelAdapter.disableFooter();
            excelPanelAdapter.disableHeader();
        }
        if (!Utils.isEmpty(list)) {
            list.clear();
        }
        list.add(leftRecyclerView);
        amountAxisY = 0;
        amountAxisX = 0;
    }

    public void addHistorySize(int size) {
        if (size > 0) {
            contentScrollListener.onScrolled(topRecyclerView, normalCellLength * size, 0);
        }
    }

    public int findFirstVisibleItemPosition() {
        int position = -1;
        if (mRecyclerView.getLayoutManager() != null && excelPanelAdapter != null) {
            LinearLayoutManager mLinearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
            if (hasHeader) {
                return firstVisibleItem - 1;
            }
            return firstVisibleItem;
        }
        return position;
    }
}

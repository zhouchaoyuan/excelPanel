package cn.zhouchaoyuan.excelpanel;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.zhouchaoyuan.utils.Utils;

/**
 * Created by zhouchaoyuan on 2016/12/11.
 * <p>
 * A widget like Excel which can scroll in all directions but it have not split line.
 * Your adapter extends {@link cn.zhouchaoyuan.excelpanel.BaseExcelPanelAdapter} can provide data to excelPanel.
 * If you set OnLoadMoreListener and load historical data in onLoadHistory(), you must call {@link #addHistorySize(int) addHistorySize(int)}
 * to tell ExcelPanel how many pages you have been added.
 * If you want to reset ExcelPanel,just call {@link #reset() reset()}
 * </p>
 */

public class ExcelPanel extends FrameLayout implements OnAddVerticalScrollListener, ViewTreeObserver.OnGlobalLayoutListener {

    public static final int TAG_KEY = R.id.lib_excel_panel_tag_key;
    public static final int DEFAULT_LENGTH = 56;
    public static final int LOADING_VIEW_WIDTH = 30;

    private int leftCellWidth;
    private int topCellHeight;
    private int normalCellWidth;
    private int loadingViewWidth;
    private int amountAxisX = 0;
    private int amountAxisY = 0;
    private int dividerHeight;
    private boolean hasHeader;
    private boolean hasFooter;
    private boolean dividerLineVisible;

    protected View dividerLine;
    protected RecyclerView mRecyclerView;
    protected RecyclerView topRecyclerView;
    protected RecyclerView leftRecyclerView;
    protected BaseExcelPanelAdapter excelPanelAdapter;
    private List<RecyclerView> list;
    private static Map<Integer, Integer> indexHeight;
    private static Map<Integer, Integer> indexWidth;

    private OnLoadMoreListener onLoadMoreListener;

    public interface OnLoadMoreListener {
        /**
         * when the loading icon appeared, this method may be called many times
         */
        void onLoadMore();

        /**
         * when the loading icon appeared, this method may be called many times. The excelPanel will dislocation
         * when the data have been added, you must call {@link #addHistorySize(int) addHistorySize(int)}.
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
            normalCellWidth = (int) a.getDimension(R.styleable.ExcelPanel_normal_cell_width, Utils.dp2px(DEFAULT_LENGTH, getContext()));
        } finally {
            a.recycle();
        }
        indexHeight = new TreeMap<>();
        indexWidth = new TreeMap<>();
        loadingViewWidth = Utils.dp2px(LOADING_VIEW_WIDTH, getContext());
        initWidget();
    }

    private void initWidget() {
        list = new ArrayList<>();

        //content's RecyclerView
        mRecyclerView = createMajorContent();
        addView(mRecyclerView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        LayoutParams mlp = (LayoutParams) mRecyclerView.getLayoutParams();
        mlp.leftMargin = leftCellWidth;
        mlp.topMargin = topCellHeight;
        mRecyclerView.setLayoutParams(mlp);

        //top RecyclerView
        topRecyclerView = createTopHeader();
        addView(topRecyclerView, new LayoutParams(LayoutParams.WRAP_CONTENT, topCellHeight));
        LayoutParams tlp = (LayoutParams) topRecyclerView.getLayoutParams();
        tlp.leftMargin = leftCellWidth;
        topRecyclerView.setLayoutParams(tlp);

        //left RecyclerView
        leftRecyclerView = createLeftHeader();
        addRecyclerView(leftRecyclerView);
        addView(leftRecyclerView, new LayoutParams(leftCellWidth, LayoutParams.WRAP_CONTENT));
        LayoutParams llp = (LayoutParams) leftRecyclerView.getLayoutParams();
        llp.topMargin = topCellHeight;
        leftRecyclerView.setLayoutParams(llp);

        dividerLine = createDividerToLeftHeader();
        addView(dividerLine, new ViewGroup.LayoutParams(1, ViewGroup.LayoutParams.WRAP_CONTENT));
        LayoutParams lineLp = (LayoutParams) dividerLine.getLayoutParams();
        lineLp.leftMargin = leftCellWidth;
        dividerLine.setLayoutParams(lineLp);
        getViewTreeObserver().addOnGlobalLayoutListener(this);

    }

    @Override
    public void onGlobalLayout() {
        if (getMeasuredHeight() != dividerHeight) {
            getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
        LayoutParams lineLp1 = (LayoutParams) dividerLine.getLayoutParams();
        dividerHeight = lineLp1.height = getMeasuredHeight();
        dividerLine.setLayoutParams(lineLp1);
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

    protected View createDividerToLeftHeader() {
        View view = new View(getContext());
        view.setVisibility(GONE);
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bg_line));
        return view;
    }

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
     * horizontal listener
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
            if (((hasHeader && amountAxisX > loadingViewWidth) || (!hasHeader && amountAxisX > 0)) && dividerLineVisible) {
                dividerLine.setVisibility(VISIBLE);
            } else {
                dividerLine.setVisibility(GONE);
            }
        }
    };

    /**
     * vertical listener
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
                fastScrollVertical(amountAxisY, recyclerView1);
            }
            if (excelPanelAdapter != null) {
                excelPanelAdapter.setAmountAxisY(amountAxisY);
            }
        }
    };

    void fastScrollVerticalLeft(){
        fastScrollVertical(amountAxisY, leftRecyclerView);
    }

    static void fastScrollVertical(int amountAxis, RecyclerView recyclerView) {
        int total = 0, count = 0;
        Iterator<Integer> iterator = indexHeight.keySet().iterator();
        while (iterator.hasNext()) {
            int height = indexHeight.get(iterator.next());
            if (total + height >= amountAxis) {
                break;
            }
            total += height;
            count++;
        }
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //call this method the OnScrollListener's onScrolled will be called，but dx and dy always be zero.
        linearLayoutManager.scrollToPositionWithOffset(count, -(amountAxis - total));
    }

    private void fastScrollTo(int amountAxis, RecyclerView recyclerView, int offset, boolean hasHeader) {
        int position = 0, width = normalCellWidth;
        if (amountAxis >= offset && hasHeader) {
            amountAxis -= offset;
            position++;
        }
        position += amountAxis / width;
        amountAxis %= width;
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //call this method the OnScrollListener's onScrolled will be called，but dx and dy always be zero.
        linearLayoutManager.scrollToPositionWithOffset(position, -amountAxis);
    }

    public void setAdapter(BaseExcelPanelAdapter excelPanelAdapter) {
        if (excelPanelAdapter != null) {
            this.excelPanelAdapter = excelPanelAdapter;
            this.excelPanelAdapter.setLeftCellWidth(leftCellWidth);
            this.excelPanelAdapter.setTopCellHeight(topCellHeight);
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
            recyclerView.setTag("");//just a tag
            list.add(recyclerView);
            recyclerView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_POINTER_DOWN:
                            for (RecyclerView rv : list) {
                                rv.stopScroll();
                            }
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (indexHeight == null) {
            indexHeight = new TreeMap<>();
        }
        if (indexWidth == null) {
            indexWidth = new TreeMap<>();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        list.clear();
        indexWidth.clear();
        indexHeight.clear();
        indexHeight = null;
        indexWidth = null;
        list = null;
    }

    /**
     * @param dx horizontal distance to scroll
     */
    void scrollBy(int dx) {
        contentScrollListener.onScrolled(mRecyclerView, dx, 0);
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
            for (RecyclerView recyclerView : list) {
                recyclerView.setTag(null);
            }
            list.clear();
        }
        if (indexHeight == null) {
            indexHeight = new TreeMap<>();
        }
        if (indexWidth == null) {
            indexWidth = new TreeMap<>();
        }
        indexHeight.clear();
        indexWidth.clear();
        list.add(leftRecyclerView);
        amountAxisY = 0;
        amountAxisX = 0;
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    public void addHistorySize(int size) {
        if (size > 0) {
            contentScrollListener.onScrolled(topRecyclerView, normalCellWidth * size, 0);
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

    /**
     * use to adjust the height and width of the normal cell
     *
     * @param holder   cell's holder
     * @param position horizontal or vertical position
     * @param isHeight is it use to adjust height or not
     * @param isSet    is it use to config height or width
     */
    public void onAfterBind(RecyclerView.ViewHolder holder, int position, boolean isHeight, boolean isSet) {
        View view = holder.itemView;
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (isHeight) {
            if (indexHeight == null) {
                indexHeight = new TreeMap<>();
            }
            if (indexHeight.get(position) != null) {
                int height = indexHeight.get(position);
                if (height > layoutParams.height) {
                    layoutParams.height = height;
                    view.setLayoutParams(layoutParams);//must, because this haven't been added to it's parent
                    adjustHeight(position, height);
                } else {
                    if (isSet) {
                        indexHeight.put(position, layoutParams.height);
                        adjustHeight(position, layoutParams.height);
                    }
                }
            } else {
                indexHeight.put(position, layoutParams.height);
            }
        } else {
            //adjust width ???
        }
    }

    /**
     * set the height of the line position to height
     *
     * @param position which line
     * @param height   the line's height
     */
    private void adjustHeight(int position, int height) {
        for (RecyclerView recyclerView : list) {
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                View view1 = recyclerView.getChildAt(i);
                if (view1.getTag(TAG_KEY) != null && view1.getTag(TAG_KEY) instanceof Pair) {
                    Pair pair = (Pair) view1.getTag(TAG_KEY);
                    int index = (int) pair.first;
                    ViewGroup.LayoutParams lp = view1.getLayoutParams();
                    if (index == position) {
                        lp.height = height;
                        view1.setLayoutParams(lp);
                        break;
                    }
                }
            }
        }
    }

    public void enableDividerLine(boolean visible) {
        dividerLineVisible = visible;
    }
}

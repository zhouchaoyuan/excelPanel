package cn.zhouchaoyuan.excelpanel;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhouchaoyuan on 2016/12/12.
 */

public interface OnExcelPanelListener {

    /**
     * 创建表格里的Item绑定
     *
     * @param parent   父容器
     * @param viewType 布局类型
     * @return ViewHolder
     */
    RecyclerView.ViewHolder onCreateCellViewHolder(ViewGroup parent, int viewType);

    /**
     * 创建表格里的Item绑定
     *
     * @param holder             ViewHolder
     * @param verticalPosition   垂直方向的position
     * @param horizontalPosition 横向position
     */
    void onBindCellViewHolder(RecyclerView.ViewHolder holder, int verticalPosition, int horizontalPosition);

    /**
     * 创建TopHeader的Item绑定
     *
     * @param parent   父容器
     * @param viewType 布局类型
     * @return ViewHolder
     */
    RecyclerView.ViewHolder onCreateTopViewHolder(ViewGroup parent, int viewType);

    /**
     * TopHeader的Item绑定
     *
     * @param holder   ViewHolder
     * @param position 位置
     */
    void onBindTopViewHolder(RecyclerView.ViewHolder holder, int position);

    /**
     * 创建LeftHeader的Item绑定
     *
     * @param parent   父容器
     * @param viewType 布局类型
     * @return ViewHolder
     */
    RecyclerView.ViewHolder onCreateLeftViewHolder(ViewGroup parent, int viewType);

    /**
     * LeftHeader的Item绑定
     *
     * @param holder   ViewHolder
     * @param position 位置
     */
    void onBindLeftViewHolder(RecyclerView.ViewHolder holder, int position);


    /**
     * @return 左上角的View
     */
    View onCreateTopLeftView();
}

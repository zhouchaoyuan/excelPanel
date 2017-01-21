package cn.zhouchaoyuan.excelpanel;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhouchaoyuan on 2016/12/12.
 */

public interface OnExcelPanelListener {

    /**
     * create normal cell's holder
     *
     * @param parent   parent
     * @param viewType viewType
     * @return ViewHolder holder
     */
    RecyclerView.ViewHolder onCreateCellViewHolder(ViewGroup parent, int viewType);

    /**
     * bind normal cell data
     *
     * @param holder             holder
     * @param verticalPosition   verticalPosition, first dimension
     * @param horizontalPosition horizontalPosition, second dimension
     */
    void onBindCellViewHolder(RecyclerView.ViewHolder holder, int verticalPosition, int horizontalPosition);

    /**
     * create topHeader cell's holder
     *
     * @param parent   parent
     * @param viewType viewType
     * @return ViewHolder holder
     */
    RecyclerView.ViewHolder onCreateTopViewHolder(ViewGroup parent, int viewType);

    /**
     * bind topHeader cell's data
     *
     * @param holder   ViewHolder
     * @param position position
     */
    void onBindTopViewHolder(RecyclerView.ViewHolder holder, int position);

    /**
     * create leftHeader cell's holder
     *
     * @param parent   parent
     * @param viewType viewType
     * @return ViewHolder holder
     */
    RecyclerView.ViewHolder onCreateLeftViewHolder(ViewGroup parent, int viewType);

    /**
     * bind leftHeader cell's data
     *
     * @param holder   ViewHolder
     * @param position position
     */
    void onBindLeftViewHolder(RecyclerView.ViewHolder holder, int position);


    /**
     * @return left-top's view
     */
    View onCreateTopLeftView();

    /**
     * use to adjust the height and width of the normal cell
     *
     * @param view     cell's view
     * @param position horizontal or vertical position
     * @param isHeight is it use to adjust height or not
     * @param isSet    is it use to config height or width
     */
    void onAfterBind(View view, int position, boolean isHeight, boolean isSet);
}

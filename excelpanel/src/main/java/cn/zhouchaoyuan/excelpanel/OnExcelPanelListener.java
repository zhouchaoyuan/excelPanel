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
     * Return the view type of the item at <code>verticalPosition's</code> row
     * <code>horizontalPosition's</code> column for the purposes of view recycling.
     * <p>
     * <p>The default implementation of this method returns 2, making the assumption of
     * a single view type for the adapter. Unlike ListView adapters, types need not
     * be contiguous. Consider using id resources to uniquely identify item view types.
     *
     * @param verticalPosition   the row position to query
     * @param horizontalPosition the column position to query
     * @return integer value identifying the type of the view needed to represent the item at
     * <code>verticalPosition's</code> row <code>horizontalPosition's</code> column.
     * Type codes need not be contiguous.
     */
    int getCellItemViewType(int verticalPosition, int horizontalPosition);

    int getTopItemViewType(int position);

    int getLeftItemViewType(int position);

    /**
     * use to adjust the height and width of the normal cell
     *
     * @param holder     cell's holder
     * @param position horizontal or vertical position
     * @param isHeight is it use to adjust height or not
     * @param isSet    is it use to config height or width
     */
    void onAfterBind(RecyclerView.ViewHolder holder, int position, boolean isHeight, boolean isSet);
}

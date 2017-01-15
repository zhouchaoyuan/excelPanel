# excelPanel
A two-dimensional RecyclerView，it can load historical data，it also can load more data。

![demo_gif](https://raw.githubusercontent.com/zhouchaoyuan/excelPanel/master/app/src/main/assets/roomFormDemo.gif)

# Download

```xml
compile 'cn.zhouchaoyuan:excelpanel:1.0.0'
```

# Usage

###1、write in xml

```xml
<cn.zhouchaoyuan.excelpanel.ExcelPanel
        android:id="@+id/content_container"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:cell_background="@color/pms_normal_cell_bg"
        app:left_cell_width="@dimen/pms_room_status_cell_length"
        app:normal_cell_length="@dimen/pms_room_status_cell_length"
        app:top_cell_height="@dimen/pms_room_status_cell_length" />
```

###2、define your Custom Adapter
Adapter extends BaseExcelPanelAdapter and override seven method show as follow:

```java
public class Adapter extends BaseExcelPanelAdapter<RowTitle, ColTitle, Cell>{

    public Adapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateCellViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindCellViewHolder(RecyclerView.ViewHolder holder, int verticalPosition, int horizontalPosition) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateTopViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindTopViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public RecyclerView.ViewHolder onCreateLeftViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindLeftViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public View onCreateTopLeftView() {
        return null;
    }
}
```

###3、use your Adapter

```java
//==============================
private List<RowTitle> rowTitles;
private List<ColTitle> colTitles;
private List<List<Cell>> cells;
private View.OnClickListener blockListener
//..........................................
excelPanel = (ExcelPanel) findViewById(R.id.content_container);
adapter = new CustomAdapter(this, blockListener);
excelPanel.setAdapter(adapter);
excelPanel.setOnLoadMoreListener(this);//your Activity or Fragment implement ExcelPanel.OnLoadMoreListener
adapter.setAllData(colTitles, rowTitles, cells);
```
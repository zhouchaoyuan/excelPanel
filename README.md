# excelPanel
A two-dimensional RecyclerView. Not only can load historical data, but also can load future data.

![demo_gif](https://raw.githubusercontent.com/zhouchaoyuan/excelPanel/master/app/src/main/assets/roomFormDemo.gif)
![demo_gif_adjust_height](https://raw.githubusercontent.com/zhouchaoyuan/excelPanel/master/app/src/main/assets/roomFormDemoAdjustHeight.gif)

# Including in your project

```xml
compile 'cn.zhouchaoyuan:excelpanel:1.0.5'
```

# Usage

### 1、Write in xml

```xml
<cn.zhouchaoyuan.excelpanel.ExcelPanel
        android:id="@+id/content_container"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:left_cell_width="@dimen/room_status_cell_length"
        app:normal_cell_width="@dimen/room_status_cell_length"
        app:top_cell_height="@dimen/room_status_cell_length" />
```

Configure using xml attributes

```xml

app:left_cell_width     //left header cell's width, not support wrap_content
app:normal_cell_width   //container cell's width, not support wrap_content
app:top_cell_height     //top header cell's height, not support wrap_content

```


### 2、Define your Custom Adapter
Your adapter must extends BaseExcelPanelAdapter and override seven methods show as follow:

```java
public class Adapter extends BaseExcelPanelAdapter<RowTitle, ColTitle, Cell>{

    public Adapter(Context context) {
        super(context);
    }

    //=========================================normal cell=========================================
    @Override
    public RecyclerView.ViewHolder onCreateCellViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindCellViewHolder(RecyclerView.ViewHolder holder, int verticalPosition, int horizontalPosition) {

    }

    //=========================================top cell===========================================
    @Override
    public RecyclerView.ViewHolder onCreateTopViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindTopViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    //=========================================left cell===========================================
    @Override
    public RecyclerView.ViewHolder onCreateLeftViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindLeftViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    //=========================================top left cell=======================================
    @Override
    public View onCreateTopLeftView() {
        return null;
    }
}
```

### 3、Use your Adapter

```java
//==============================
private List<RowTitle> rowTitles;
private List<ColTitle> colTitles;
private List<List<Cell>> cells;
private ExcelPanel excelPanel;
private CustomAdapter adapter;
private View.OnClickListener blockListener
//..........................................
excelPanel = (ExcelPanel) findViewById(R.id.content_container);
adapter = new CustomAdapter(this, blockListener);
excelPanel.setAdapter(adapter);
excelPanel.setOnLoadMoreListener(this);//your Activity or Fragment implement ExcelPanel.OnLoadMoreListener
adapter.setAllData(colTitles, rowTitles, cells);
adapter.enableFooter();//load more, you can also call disableFooter()----default
adapter.enableHeader();//load history, you can also call disableHeader()----default
```

If using setOnLoadMoreListener(...) and enableHeader() you must call addHistorySize(int) to tell ExcelPanel how many data have been added.

# ChangeLog

- V1.0.1--------------Line i can be different to line i+1, but the same line's height must be equals
- V1.0.2--------------Support different viewType which using to config different row with different height
- V1.0.3--------------Remove adjust height, fix bug when excelPanel in ViewPager
- V1.0.4--------------Optimizing rolling
- V1.0.5--------------addOnScrollListener

# License

```xml

   Copyright 2017 zhouchaoyuan

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

```
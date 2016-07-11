package com.example.zengtaizhu.myapp;

import android.content.DialogInterface;
import android.content.Loader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Method.DataInOut;
import Method.DeleteOnline;
import Method.RequestData;
import Method.SendHttpRequest;

/**
 * 用于显示动物的具体信息
 */
public class AnimalActivity extends AppCompatActivity {

    //该界面上的ListView
    private ListView listView;
    //该界面的“返回”按钮
    private ImageButton back;
    private Bundle bundle;
    //传输到该界面的listView中
    private List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
    //查看的动物的id
    private String animalId;
    //查看的选项
    private int selectedItem;
    //登陆凭证
    private String JSESSIONID = null;
    //插入listView的适配器
    private SimpleAdapter simpleAdapter;
    //显示在listView中的信息
    private Map<String,Object> listItem;
    //数据保存的文件名
    public static String[] SaveFileName = new String[]{"receive", "sale", "animal", "logistics", "aniQua", "disease"};
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == 0x111)
            {
                Toast.makeText(getApplicationContext(), "没有数据", Toast.LENGTH_SHORT).show();
            }
            if(msg.what == 0x112)
            {
                //结束当前的Activity，回到第一个Activity
                finish();
            }
            if(msg.what == 0x113)
            {
                if(listItems.size() == 0)
                {
                    Toast.makeText(getApplicationContext(), "没有数据", Toast.LENGTH_SHORT).show();
                }
                //添加信息到listView上
                switch (selectedItem) {
                    case 3:
                        //创建一个物流信息的SimpleAdapter
                        simpleAdapter = new SimpleAdapter(getApplicationContext(), listItems,
                                R.layout.logistics_item,
                                new String[]{"id", "position", "time", "person"},
                                new int[]{R.id.id, R.id.position, R.id.time, R.id.person});
                        break;
                    case 4:
                        //创建一个质检信息的SimpleAdapter
                        simpleAdapter = new SimpleAdapter(getApplicationContext(), listItems,
                                R.layout.aniqua_item,
                                new String[]{"batchNumber", "sampleNumber", "qualifiedNumber",
                                        "date", "originId", "organization" , "person"},
                                new int[]{R.id.batchNumber, R.id.sampleNumber, R.id.qualifiedNumber,
                                        R.id.date, R.id.originId, R.id.organization, R.id.person});
                        break;
                    case 5:
                        //创建一个生病信息的SimpleAdapter
                        simpleAdapter = new SimpleAdapter(getApplicationContext(), listItems,
                                R.layout.disease_item,
                                new String[]{"diseaseName", "startDate", "endDate", "comments"},
                                new int[]{R.id.diseaseName, R.id.startDate, R.id.endDate, R.id.comments});
                        break;
                    default:
                        break;
                }
                listView.setAdapter(simpleAdapter);
            }
            if(msg.what == 0x114)
            {
                simpleAdapter.notifyDataSetChanged();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);
        bundle = this.getIntent().getExtras();
        //获得传送过来的信息
        animalId = bundle.getString("animalId");
        selectedItem = bundle.getInt("selectedItem");
        JSESSIONID = bundle.getString("JSESSIONID");
        //ToolBar标题
        String title;
        switch (selectedItem)
        {
            case 3:
                title = "动物物流信息";
                break;
            case 4:
                title = "动物质检信息";
                break;
            case 5:
            default:
                title = "动物生病信息";
        }
        //设置ActionBar为toolbar，代表原本的 Actionbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView textView = (TextView)findViewById(R.id.tip);
        textView.setText(title);
        setSupportActionBar(toolbar);
        //为ToolBar添加事件
        toolbar.setOnMenuItemClickListener(onMenuItemClick);
        listView = (ListView)findViewById(R.id.list);
        new Thread(){
            @Override
            public void run()
            {
                //如果没有登陆，则使用已经保存的数据
                if(JSESSIONID == null)
                {
                    //加载本地缓存数据，并显示在界面上
                    LoadLocalData();
                }
                else
                {
                    //如果已经登陆，则更新已有的数据
                    try
                    {
                        //按照选择的功能获取相应的位置
                        listItems = RequestData.getData(selectedItem,animalId,JSESSIONID);
                        //存储数据
                        DataInOut.saveData(getApplicationContext(), listItems, SaveFileName[selectedItem] + animalId);
                        handler.sendEmptyMessage(0x113);
                    }
                    catch (Exception e)
                    {
                        Log.i("LogisticsGet","获取物流信息失败：" + e.getMessage());
                    }
                }
            }
        }.start();
        ItemOnLongClick();
        back = (ImageButton)findViewById(R.id.goBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //停止该Activity
                handler.sendEmptyMessage(0x112);
            }
        });
    }

    //长按方式弹出菜单多选方式
    private void ItemOnLongClick() {
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                //menu.setHeaderTitle("更多操作");
                //只有长按物流信息才有效
                if(selectedItem == 3)
                {
                    menu.add(0,0,0,"更新该信息");
                    menu.add(0,1,0,"删除该信息");
                }
            }
        });
    }

    // 长按菜单响应函数
    public boolean onContextItemSelected(MenuItem item) {
        final int selectedPosition = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position;
        switch (item.getItemId()) {
            case 0:
                // 更新操作
                //打开对话框
                distView(selectedPosition);
                break;
            case 1:
                //删除操作
                //删除选中信息，包括网络上的资源以及界面的listView
                new Thread()
                {
                    @Override
                    public void run()
                    {
                        //已经登陆，则同时删除网络资源
                        if(JSESSIONID != null)
                        {
                            Object deleteID = animalId + "/" + listItems.get(selectedPosition).get("id");
                            //删除所选中的网络资源
                            DeleteOnline.DeleteData(selectedItem, deleteID, JSESSIONID);
                        }
                        //删除listItems中所选中的一列
                        listItems.remove(selectedPosition);
                        //更新保存在本地的数据
                        DataInOut.saveData(getApplicationContext(), listItems, SaveFileName[selectedItem] + animalId);
                        //通知listView更新
                        handler.sendEmptyMessage(0x114);
                    }
                }.start();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * 打开一个自定义AlertDialog，用于增加或修改一条信息
     * @param selectedPosition 当该值大于等于0，是由长按并点击修改按钮而触发的事件，代表要修改的信息的位置
     *                        当该值小于0时，是由添加信息而触发的事件
     */
    private void distView(final int selectedPosition)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(AnimalActivity.this);
        LayoutInflater factory = LayoutInflater.from(this);
        //装载app\src\main\res\layout\logistics_dialog.xml界面布局文件
        final View textEntryView = factory.inflate(R.layout.logistics_dialog, null);;
        //对话框标题
        final String title;
        if(selectedPosition >= 0)
        {
            title = "修改信息";
        }
        else
        {
            title = "添加信息";
        }
        //设置对话框的图标
        builder.setIcon(R.drawable.title);
        //设置对话框的标题
        builder.setTitle(title);
        //设置对话框显示的view对象
        builder.setView(textEntryView);
        //为对话框设置一个“确定”的按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread()
                {
                    @Override
                    public void run()
                    {
                        //获得对话框录入的信息
                        String position = ((EditText)textEntryView.findViewById(R.id.position)).getText().toString();
                        String time = ((EditText)textEntryView.findViewById(R.id.date)).getText().toString();
                        String person = ((EditText)textEntryView.findViewById(R.id.person)).getText().toString();
                        String result = null;
                        //添加或修改信息到本地以及上传到服务器
                        if(selectedPosition >= 0)
                        {
                            listItem = listItems.get(selectedPosition);
                            //更新物流信息
                            String logisticsId = listItem.get("id").toString();
                            String url = "http://202.116.161.86:8888/distributor/logistics/"
                                    + animalId + "/" + logisticsId;
                            String params = "{\"animalId\":\"" + animalId + "\",\"logisticsId\":\"" +
                                    logisticsId + "\",\"position\":\"" + position + "\",\"time\":\"" +
                                    time + "\",\"person\":\"" + person +"\"}";
                            result = SendHttpRequest.sendPut(url, params, JSESSIONID);
                            if(result.contains("success"))
                            {
                                Log.i("Modify", "修改物流信息成功");
                                listItem = new HashMap<String, Object>();
                                listItem.put("animalId", animalId);
                                listItem.put("id", logisticsId);
                                listItem.put("position", position);
                                listItem.put("time", time);
                                listItem.put("person", person);
                                //替代原来的信息
                                listItems.set(selectedPosition, listItem);
                            }
                            else
                            {
                                Log.i("Modify", "修改物流信息失败");
                            }
                        }
                        else
                        {
                            //添加物流信息
                            String url = "http://202.116.161.86:8888/distributor/logistics/" + animalId;
                            String params = "{\"position\":\"" + position + "\",\"time\":\""
                                    + time + "\",\"person\":\"" + person +"\"}";
                            result = SendHttpRequest.sendPost(url, params, JSESSIONID);
                            if(result.contains("success"))
                            {
                                Log.i("LogisticsAdd", "物流信息添加失败");
                                //添加物流信息成功，则返回{"id":"123123...","message":"success"}
                                String id = result.substring(7, result.length() - 22);
                                Log.i("Message", animalId + "|" + id + "|" + position + "|" + position +
                                "|" + time + "|" + person + "|");
                                listItem = new HashMap<String, Object>();
                                //新增一个新的信息
                                listItem.put("animalId", animalId);
                                listItem.put("id", id);
                                listItem.put("position", position);
                                listItem.put("time", time);
                                listItem.put("person", person);
                                listItems.add(listItem);
                            }
                            else
                            {
                                Log.i("LogisticsAdd", "物流信息添加失败");
                            }
                        }
                        //存储数据
                        DataInOut.saveData(getApplicationContext(), listItems, SaveFileName[selectedItem] + animalId);
                        //通知ListView界面更新
                        handler.sendEmptyMessage(0x114);
                    }
                }.start();
            }
        });
        //为对话框设置一个“取消”按钮
        builder.setNegativeButton("取消", null);
        //创建并显示对话框
        builder.create().show();
    }

    /**
     * 加载本地的缓存数据，并显示在界面上
     */
    private void LoadLocalData()
    {
        try
        {
            listItems = (List<Map<String,Object>>) DataInOut.readData(getApplicationContext(), SaveFileName[selectedItem] + animalId);
            if(listItems != null)
            {
                handler.sendEmptyMessage(0x113);
            }
            else
            {
                //没有数据时，提醒
                handler.sendEmptyMessage(0x111);
            }
        }catch (Exception e)
        {
            Log.i("Error", "读取数据失败");
        }
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId())
            {
                case R.id.add:
                    //Toast.makeText(getApplicationContext(), "添加信息", Toast.LENGTH_SHORT).show();
                    //弹出增加信息的对话框
                    if(selectedItem == 3)
                        distView(-1);
                    break;
            }
            return false;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 使得Toolbar的Menu生效
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}

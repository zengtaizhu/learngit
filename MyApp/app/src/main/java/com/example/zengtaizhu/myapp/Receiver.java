package com.example.zengtaizhu.myapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Method.DataInOut;
import Method.DeleteOnline;
import Method.RequestData;

/**
 * 用于显示动物的具体信息
 *
 */
public class Receiver extends Activity {

    //该界面上的ListView
    private ListView listView;
    //该界面的“显示”按钮
    private Button show;
    //该界面的“返回”按钮
    private Button back;
    private Bundle bundle;
    //传输到该界面的listView中
    private List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
    //查看的动物的id
    private String animalId = null;
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
                Toast.makeText(getApplicationContext(), "动物id:" + animalId + "选择了:" + selectedItem, Toast.LENGTH_SHORT).show();
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
        show = (Button) findViewById(R.id.show);
        listView = (ListView)findViewById(R.id.list);
        bundle = this.getIntent().getExtras();
        //获得传送过来的信息
        animalId = bundle.getString("animalId");
        selectedItem = bundle.getInt("selectedItem");
        JSESSIONID = bundle.getString("JSESSIONID");
        new Thread(){
            @Override
            public void run()
            {
                //如果没有登陆，则使用已经保存的数据
                if(JSESSIONID == null)
                {
                    try
                    {
                        listItems = (List<Map<String,Object>>) DataInOut.readData(getApplicationContext(), SaveFileName[selectedItem]);
                        handler.sendEmptyMessage(0x113);
                    }catch (Exception e)
                    {
                        Log.i("Error", "读取数据失败");
                    }
                }
                else
                {
                    //如果已经登陆，则更新已有的数据
                    try
                    {
                        //按照选择的功能获取相应的位置
                        listItems = RequestData.getData(selectedItem,animalId,JSESSIONID);
                        //存储数据
                        DataInOut.saveData(getApplicationContext(), listItems, SaveFileName[selectedItem]);
                        handler.sendEmptyMessage(0x113);
                    }
                    catch (Exception e)
                    {
                        Log.i("Error",e.getMessage());
                    }
                }
            }
        }.start();
        ItemOnLongClick();
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(0x111);
            }
        });
        back = (Button)findViewById(R.id.goBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        DataInOut.saveData(getApplicationContext(), listItems, SaveFileName[selectedItem]);
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

    //打开一个自定义AlertDialog，用于增加或修改一条信息
    private void distView(final int selectedPosition)
    {
        //装载app\src\main\res\layout\distributor_dialog.xml界面布局文件
        TableLayout layoutForm = (TableLayout)getLayoutInflater()
                .inflate(R.layout.distributor_dialog, null);
        new AlertDialog.Builder(this)
                //设置对话框的图标
                .setIcon(R.drawable.title)
                //设置对话框的标题
                .setTitle("对话框")
                //设置对话框显示的view对象
                .setView(layoutForm)
                //为对话框设置一个“确定”的按钮
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText disName = (EditText)findViewById(R.id.disName);
                        EditText disDate = (EditText)findViewById(R.id.disDate);
                        EditText disBatchNum = (EditText)findViewById(R.id.disBatchNum);
                        //添加或修改信息到本地以及上传到服务器
                        listItem = listItems.get(selectedPosition);
                        String s = disBatchNum.getText().toString();
                        listItems.set(selectedPosition,listItem);
                        listItem.put("date",R.id.disDate);
                        listItem.put("name",R.id.disName);
                        listItem.put("disBatchNum",R.id.disBatchNum);
                        listItem.put("number",0);
                        listItems.add(listItem);
                        simpleAdapter.notifyDataSetChanged();
                    }
                })
                //为对话框设置一个“取消”按钮
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //取消操作
                    }
                })
                //创建并显示对话框
                .create()
                .show();
    }

}

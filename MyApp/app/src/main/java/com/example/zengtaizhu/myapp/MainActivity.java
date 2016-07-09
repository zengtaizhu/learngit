package com.example.zengtaizhu.myapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Method.DataInOut;
import Method.DeleteOnline;
import Method.RequestData;
import URLFunc.SendHttpRequest;
import HttpResponse.LoginMsg;
import DataClass.Distributor;

public class MainActivity extends AppCompatActivity {

    //分销商的功能
    private Distributor distributor = new Distributor();
    //登陆凭证
    private String JSESSIONID = null;
    //登陆按钮
    private Button post;
    //侧滑栏的内容
    private ListView sliderList;
    //主界面的内容
    private ListView listView;
    //代表服务器响应的字符串
    private String response;
    //抽屉画板DrawerLayout的布局
    private DrawerLayout myDrawerLayout;
    //传输到主界面的listView中
    private List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
    //标志，标明目前的功能
    private int state;
    //显示在listView中的信息
    private Map<String,Object> listItem;
    //插入listView的适配器
    private SimpleAdapter simpleAdapter;
    //数据保存的文件名
    public static String[] SaveFileName = new String[]{"receive", "sale", "animal"};
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == 0x123) {
                //设置show组件显示服务器响应
                Gson gson = new Gson();
                LoginMsg loginMsg = gson.fromJson(response,LoginMsg.class);
                JSESSIONID = loginMsg.getJSESSIONID();
                Toast.makeText(getApplicationContext(),JSESSIONID,Toast.LENGTH_SHORT).show();
            }
            if(msg.what == 0x124) {
                //设置show组件显示服务器响应
                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
            }
            if(msg.what == 0x125)
            {
                //添加信息到listView上
                switch (state)
                {
                    case 0:
                        //创建一个进货信息的SimpleAdapter
                        simpleAdapter = new SimpleAdapter(getApplicationContext(), listItems,
                                R.layout.receive_item,
                                new String[]{"category", "date", "disBatchNum", "number"},
                                new int[]{R.id.category, R.id.date, R.id.disBatchNum, R.id.number});
                        break;
                    case 1:
                        //创建一个出货信息的SimpleAdapter
                        simpleAdapter = new SimpleAdapter(getApplicationContext(), listItems,
                                R.layout.sale_item,
                                new String[]{"category", "date", "batchNum", "number"},
                                new int[]{R.id.category, R.id.date, R.id.batchNum, R.id.number});
                        break;
                    case 2:
                        //创建一个动物信息的SimpleAdapter
                        simpleAdapter = new SimpleAdapter(getApplicationContext(), listItems,
                                R.layout.animal_item,
                                new String[]{"sourceCode", "saleBatchNum", "state", "birthday", "category"},
                                new int[]{R.id.sourceCode, R.id.saleBatchNum, R.id.state, R.id.birthday, R.id.category});
                        break;
                    default:
                        break;
                }
                listView.setAdapter(simpleAdapter);
            }
            if(msg.what == 0x126)
            {
                simpleAdapter.notifyDataSetChanged();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        post = (Button)findViewById(R.id.post);
        listView = (ListView)findViewById(R.id.mylist);
        sliderList = (ListView)findViewById(R.id.menu_list);
        //设置DrawerLayout的监听事件
        sliderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(),"选中了第" + position + "个",Toast.LENGTH_SHORT).show();
                //每次切换管理项都清空一次
                listItems.clear();
                //标志：显示选中的功能
                state = position;
                //如果没有登陆，则使用已经保存的数据
                if(JSESSIONID == null)
                {
                    try
                    {
                        listItems = (List<Map<String,Object>>) DataInOut.readData(getApplicationContext(), SaveFileName[state]);
                        handler.sendEmptyMessage(0x125);
                    }catch (Exception e)
                    {
                        Log.i("Error", "读取数据失败");
                    }
                }
                else{
                    //如果已经登陆，则更新已有的数据
                    new Thread()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                //按照选择的功能获取相应的位置
                                listItems = RequestData.getData(state,1,JSESSIONID);
                                //存储数据
                                DataInOut.saveData(getApplicationContext(), listItems, SaveFileName[state]);
                                handler.sendEmptyMessage(0x125);
                            }
                            catch (Exception e)
                            {
                                Log.i("Error",e.getMessage());
                            }
                        }
                    }.start();
                }
            }
        });
        ItemOnLongClick();
        //登陆按钮
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            //登陆时所需的URL和params语句
                            String url = "http://202.116.161.86:8888/login";
                            String params = "username=222222&password=222222";
                            response = SendHttpRequest.sendPost(url,params,null);
                            //如果连接失败，则不进行GSON解析
                            if(response != "")
                            {
                                handler.sendEmptyMessage(0x123);
                            }
                        }
                        catch(Exception e)
                        {
                            Log.i("My Android",e.getMessage());
                        }
                    }
                }.start();
            }
        });
        Button get = (Button)findViewById(R.id.get);
        //接收信息按钮
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread()
                {
                    @Override
                    public void run()
                    {
                        String url2 = "http://202.116.161.86:8888/distributor/receive";
                        String params2 = "pageNum=1";
                        try
                        {
                            response = SendHttpRequest.sendGet(url2,params2,JSESSIONID);
                            handler.sendEmptyMessage(0x124);
                        }
                        catch(Exception e)
                        {
                            Log.i("My Android",e.getMessage());
                        }
                    }
                }.start();
            }
        });
        //侧滑栏的监听事件----可删除，或添加动画
        myDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener(){

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //Log.i("drawer", slideOffset + "");
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //Log.i("drawer", "抽屉被完全打开了！");
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                //Log.i("drawer", "抽屉被完全关闭了！");
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                //Log.i("drawer", "drawer的状态：" + newState);
            }
        });
    }
    //长按方式弹出菜单多选方式
    private void ItemOnLongClick() {
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                //menu.setHeaderTitle("更多操作");
                menu.add(0,0,0,"更新该信息");
                menu.add(0,1,0,"删除该信息");
                //当查看动物信息时，可以查看该动物的物流、质检和生病信息
                if(state == 2)
                {
                    menu.add(0,2,0,"查看物流信息");
                    menu.add(0,3,0,"查看质检信息");
                    menu.add(0,4,0,"查看生病信息");
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
                // 删除操作
                //删除选中信息，包括网络上的资源以及界面的listView
                new Thread()
                {
                    @Override
                    public void run()
                    {
                        //已经登陆，则同时删除网络资源
                        if(JSESSIONID != null)
                        {
                            Object deleteID = null;
                            switch (selectedPosition)
                            {
                                case 0:
                                case 1:
                                    deleteID = listItems.get(selectedPosition).get("id");
                                    break;
                                case 2:
                                    deleteID = listItems.get(selectedPosition).get("animalId");
                            }
                            //删除所选中的网络资源
                            DeleteOnline.DeleteData(state, deleteID, JSESSIONID);
                        }
                        //删除listItems中所选中的一列
                        listItems.remove(selectedPosition);
                        //更新保存在本地的数据
                        DataInOut.saveData(getApplicationContext(), listItems, SaveFileName[state]);
                        //通知listView更新
                        handler.sendEmptyMessage(0x126);
                    }
                }.start();
                break;
            case 2:
                //跳转到下一个Activity并显示该动物的物流信息
            case 3:
                //跳转到下一个Activity并显示该动物的质检信息
            case 4:
                //跳转到下一个Activity并显示该动物的生病信息
                Intent intent;
                Bundle bundle = new Bundle();
                String animalId = listItems.get(selectedPosition).get("id").toString();
                //将该动物的id、查看的选项以及登陆凭证传送过去
                bundle.putString("animalId", animalId);
                bundle.putInt("selectedItem", item.getItemId() + 1);
                bundle.putString("JSESSIONID", JSESSIONID);
                intent = new Intent(MainActivity.this, Receiver.class);
                intent.putExtras(bundle);
                startActivity(intent);
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

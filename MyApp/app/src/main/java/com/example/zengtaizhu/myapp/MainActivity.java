package com.example.zengtaizhu.myapp;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Data.GetPostUtil;
import Data.LoginMsg;
import Data.Distributor;

public class MainActivity extends AppCompatActivity {

    //分销商的功能
    Distributor distributor = new Distributor();
    String JSESSIONID;
    Button post,get;
    //侧滑栏的内容
    ListView sliderList;
    //主界面的内容
    ListView listView;
    //代表服务器响应的字符串
    String response;
    //抽屉画板DrawerLayout的布局
    DrawerLayout myDrawerLayout;
    //传输到主界面的listView中
    List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
    //标志，标明目前的功能
    private int state;
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == 0x123)
            {
                //设置show组件显示服务器响应
                Gson gson = new Gson();
                LoginMsg loginMsg = gson.fromJson(response,LoginMsg.class);
                JSESSIONID = loginMsg.getJSESSIONID();
                Toast.makeText(getApplicationContext(),JSESSIONID,Toast.LENGTH_SHORT).show();
            }
            if(msg.what == 0x124)
            {
                //设置show组件显示服务器响应
                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
            }
            if(msg.what == 0x125)
            {
                //创建一个SimpleAdapter
                SimpleAdapter simpleAdapter = new SimpleAdapter(getApplicationContext(), listItems,
                        R.layout.operation_layout,new String[]{"name"},
                        new int[]{R.id.name});
                listView.setAdapter(simpleAdapter);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        //设置DrawerLayout的监听事件
        post = (Button)findViewById(R.id.post);
        get = (Button)findViewById(R.id.get);
        listView = (ListView)findViewById(R.id.mylist);
        sliderList = (ListView)findViewById(R.id.menu_list);
        sliderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getApplicationContext(),"选中了第" + position + "个",Toast.LENGTH_SHORT).show();
                //每次切换管理项都清空一次
                listItems.clear();
                //获取对应的管理内容
                String[] stock = distributor.getFunc(position);
                for(int i = 0; i < stock.length; i++)
                {
                    Map<String,Object> listItem = new HashMap<String, Object>();
                    listItem.put("name",stock[i]);
                    listItems.add(listItem);
                    System.out.println(stock[i]);
                }
                state = position * 10;
                handler.sendEmptyMessage(0x125);
            }
        });
        //为主界面的listView添加点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "选中了第" + position + "个", Toast.LENGTH_SHORT).show();
                //代表各自的
                Intent intent;
                Bundle bundle = new Bundle();
                bundle.putInt("No", position);
                //添加每个管理功能的功能项界面
                switch (state) {
                    case 0:
                        intent = new Intent(MainActivity.this, Receiver.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case 1:

                }
            }
        });
        ItemOnLongClick();
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
                            String url = "http://172.26.14.31:10689/login";
                            String params = "username=222222&password=222222";
                            response = GetPostUtil.sendPost(url,params,null);
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
        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread()
                {
                    @Override
                    public void run()
                    {
                        String url2 = "http://172.26.14.31:10689/distributor/receive";
                        String params2 = "pageNum=1";
                        try
                        {
                            response = GetPostUtil.sendGet(url2,params2,JSESSIONID);
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
    private void ItemOnLongClick()
    {
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("弹出");
                menu.add(0,0,0,"更新该信息");
                menu.add(0,1,0,"删除该信息");
            }
        });
    }
    // 长按菜单响应函数
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();
        switch (item.getItemId()) {
            case 0:
                // 更新操作
                Toast.makeText(getApplicationContext(),
                        "更新",
                        Toast.LENGTH_SHORT).show();
                break;
            case 1:
                // 删除操作
                Toast.makeText(getApplicationContext(),
                        "删除",
                        Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

        return super.onContextItemSelected(item);

    }
}

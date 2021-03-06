package com.example.zengtaizhu.myapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import Method.DataInOut;
import Method.DeleteOnline;
import Method.RequestData;
import Method.SendHttpRequest;
import HttpResponse.LoginMsg;

public class MainActivity extends AppCompatActivity{

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
    private int state = -1;
    //显示在listView中的信息
//    private Map<String,Object> listItem;
    //插入listView的适配器
    private SimpleAdapter simpleAdapter;
    //数据保存的文件名
    public static String[] SaveFileName = new String[]{"receive", "sale", "animal"};
    //获取资源的网址
    private String url = "http://www.scauszy.com:8899/";
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
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
            if(msg.what == 0x125) {
                //添加信息到listView上
                switch (state) {
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
            if(msg.what == 0x126) {
                if(simpleAdapter != null)
                    simpleAdapter.notifyDataSetChanged();
            }
            if(msg.what == 0x127) {
                Toast.makeText(getApplicationContext(), "操作失败，请稍后尝试", Toast.LENGTH_SHORT).show();
            }
            if(msg.what == 0x128) {
                Toast.makeText(getApplicationContext(), "网络或本地没有该数据", Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置ActionBar为toolbar，代表原本的 Actionbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("溯源系统");
        setSupportActionBar(toolbar);
        //为ToolBar添加事件
        toolbar.setOnMenuItemClickListener(onMenuItemClick);
        //完成界面的初始化，即读取上次退出时的配置
        initialize();
        myDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        post = (Button)findViewById(R.id.post);
        listView = (ListView)findViewById(R.id.mylist);
        //在listview顶部加分割线
        //listView.addHeaderView(new ViewStub(this));
        //在listview底部加分割线

        sliderList = (ListView)findViewById(R.id.menu_list);
        //设置DrawerLayout的监听事件
        sliderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(listItems != null) {
                    //每次切换管理项都清空一次
                    listItems.clear();
                }
                //标志：显示选中的功能
                state = position;
                //如果没有登陆，则使用已经保存的数据
                if(JSESSIONID == null) {
                    LoadLocalData();
                }
                else{
                    //如果已经登陆，则更新已有的数据
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                //按照选择的功能获取相应的位置
                                listItems = RequestData.getData(state,1,JSESSIONID);
                                //存储数据
                                DataInOut.saveData(getApplicationContext(), listItems, SaveFileName[state]);
                                handler.sendEmptyMessage(0x125);
                            }
                            catch (Exception e) {
                                Log.i("Error",e.getMessage());
                            }
                            //通知listView更新数据
                            handler.sendEmptyMessage(0x126);
                        }
                    }.start();
                }
            }
        });
        //长按事件
        ItemOnLongClick();
        //登陆按钮
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            //登陆时所需的URL和params语句
                            String realUrl = url + "login";
                            String params = "username=222222&password=222222";
                            response = SendHttpRequest.sendPost(realUrl,params,null);
                            //如果连接失败，则不进行GSON解析
                            if(response != "") {
                                handler.sendEmptyMessage(0x123);
                            }
                        }
                        catch(Exception e) {
                            Log.i("My Android",e.getMessage());
                        }
                    }
                }.start();
            }
        });
    }

    /**
     * 初始化界面，加载上次保存的状态
     */
    private void initialize() {
        //读取上次打开的功能
        try
        {
            Object obj = DataInOut.readData(getApplicationContext(), "Config");
            if(obj == null)
                return;
            Log.i("Config", "obj =" + obj);
            state = Integer.parseInt(obj.toString());
            if(state >= 0)
            {
                //加载数据到界面上
                LoadLocalData();
            }
        }
        catch (NumberFormatException e)
        {
            Log.i("Config", "获取上次保存的数据失败");
            state = -1;
        }
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
                new Thread() {
                    @Override
                    public void run() {
                        //已经登陆，则同时删除网络资源
                        if(JSESSIONID != null) {
                            Object deleteID = null;
                            switch (state) {
                                case 0:
                                case 1:
                                    deleteID = listItems.get(selectedPosition).get("id");
                                    Log.i("Delete", "deleteID = " + deleteID);
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
                intent = new Intent(MainActivity.this, AnimalActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    //打开一个自定义AlertDialog，用于增加或修改一条信息
    private void distView(final int selectedPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView;
        //对话框标题
        String title;
        switch (state)
        {
            case 0:
                textEntryView = factory.inflate(R.layout.receive_dialog, null);
                break;
            case 1:
                textEntryView = factory.inflate(R.layout.sale_dialog, null);
                break;
            case 2:
            default:
                if(selectedPosition >= 0)
                    textEntryView = factory.inflate(R.layout.animal_dialog2, null);
                else
                    textEntryView = factory.inflate(R.layout.animal_dialog, null);
                break;
        }
        if(selectedPosition >= 0)
        {
            title = "修改信息对话框";
        }
        else
        {
            title = "更新信息对话框";
        }
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
                        //判断修改或添加是否有效
                        boolean isSuccess = false;
                        switch (state)
                        {
                            case 0:
                                String disName =  ((EditText)textEntryView.findViewById(R.id.disName)).getText().toString();
                                String disDate = ((EditText)textEntryView.findViewById(R.id.disDate)).getText().toString();
                                String disBatchNum = ((EditText)textEntryView.findViewById(R.id.disBatchNum)).getText().toString();
                                if(selectedPosition >= 0)
                                {
                                    //更新信息
                                    //获取选中信息的id
                                    String id = listItems.get(selectedPosition).get("id").toString();
                                    String realUrl = url + "distributor/receive/" + id;
                                    String params = "{\"id\":\"" + id +"\",\"date\":\"" + disDate +
                                            "\",\"category\":\"" + disName + "\",\"disBatchNum\":\"" + disBatchNum + "\"}";
                                    String result = SendHttpRequest.sendPut(realUrl, params, JSESSIONID);
                                    if(result.contains("success"))
                                    {
                                        Log.i("Modify", "进货信息修改成功");
                                        isSuccess = true;
                                    }
                                    else
                                    {
                                        Log.i("Modify", "进货信息修改失败");
                                    }
                                }
                                else
                                {
                                    //添加信息
                                    String realUrl = url + "distributor/receive";
                                    String params = "{\"date\":\"" + disDate + "\",\"category\":\""
                                            + disName + "\",\"disBatchNum\":\"" + disBatchNum + "\"}";
                                    String result = SendHttpRequest.sendPost(realUrl, params, JSESSIONID);
                                    if(result.contains("success"))
                                    {
                                        Log.i("Add", "进货信息添加成功");
                                        isSuccess = true;
                                    }
                                    else
                                    {
                                        Log.i("AddError", "进货信息添加失败");
                                    }
                                }
                                break;
                            case 1:
                                String name = ((EditText)textEntryView.findViewById(R.id.name)).getText().toString();
                                String date = ((EditText)textEntryView.findViewById(R.id.date)).getText().toString();
                                String batchNum = ((EditText)textEntryView.findViewById(R.id.batchNum)).getText().toString();
                                if(selectedPosition >=0 )
                                {
                                    //更新信息
                                    //获取选中信息的id
                                    String id = listItems.get(selectedPosition).get("id").toString();
                                    String realUrl = url + "distributor/sale/" + id;
                                    String params = "{\"id\":\"" + id +"\",\"date\":\"" + date + "\",\"category\":\""
                                            + name + "\",\"batchNum\":\"" + batchNum + "\"}";
                                    String result = SendHttpRequest.sendPut(realUrl, params, JSESSIONID);
                                    if(result.contains("success"))
                                    {
                                        Log.i("Modify", "进货信息修改成功");
                                        isSuccess = true;
                                    }
                                    else
                                    {
                                        Log.i("Modify", "进货信息修改失败");
                                    }
                                }
                                else
                                {
                                    //添加信息
                                    String realUrl = url + "distributor/sale/";
                                    String params = "{\"date\":\"" + date + "\",\"category\":\""
                                            + name + "\",\"batchNum\":\"" + batchNum + "\"}";
                                    String result = SendHttpRequest.sendPost(realUrl, params, JSESSIONID);
                                    if(result.contains("success"))
                                    {
                                        Log.i("Sale", "出货信息添加成功");
                                        isSuccess = true;
                                    }
                                    else
                                    {
                                        Log.i("Sale", params + result);
                                    }
                                }
                                break;
                            case 2:
                            default:
                                String saleBatchNum = ((EditText)textEntryView.findViewById(R.id.saleBatchNum)).getText().toString();
                                if(selectedPosition >= 0)
                                {
                                    //更新信息
                                    //获取选中信息的id
                                    String id = listItems.get(selectedPosition).get("id").toString();
                                    String state = ((EditText)textEntryView.findViewById(R.id.state)).getText().toString();
                                    String realUrl = url + "distributor/animal/" + id;
                                    String params = "{\"animalId\":\"" + id + "\",\"saleBatchNum\":\"" +
                                            saleBatchNum + "\",\"state\":\"" + state + "\"}";
                                    String result = SendHttpRequest.sendPut(realUrl, params, JSESSIONID);
                                    Log.i("更新输出的字符串", result);
                                    if(result.contains("success"))
                                    {
                                        Log.i("Animal", "动物信息更新成功");
                                        isSuccess = true;
                                    }
                                    else
                                    {
                                        Log.i("Animal", "动物信息更新失败");
                                    }
                                }
                                else
                                {
                                    //添加信息
                                    String sourceCode = ((EditText)textEntryView.findViewById(R.id.sourceCode)).getText().toString();
                                    String realUrl = url + "istributor/animal";
                                    String params = "{\"sourceCode\":\"" + sourceCode +
                                            "\",\"saleBatchNum\":\"" + saleBatchNum + "\"}";
                                    String result = SendHttpRequest.sendPost(realUrl, params, JSESSIONID);
                                    if(result.contains("success"))
                                    {
                                        Log.i("Animal", "添加动物信息成功");
                                        isSuccess = true;
                                    }
                                    else
                                    {
                                        Log.i("Animal", params + result);
                                    }
                                }
                                break;
                        }
                        if(isSuccess)
                        {
                            //清除旧数据
                            listItems.clear();
                            //重新从网络上获取新数据
                            listItems = RequestData.getData(state,1,JSESSIONID);
                            //存储数据
                            DataInOut.saveData(getApplicationContext(), listItems, SaveFileName[state]);
                            //通知更新ListView的内容
                            handler.sendEmptyMessage(0x125);
                        }
                        else
                        {
                            //通知操作失误
                            handler.sendEmptyMessage(0x127);
                        }
                    }
                }.start();
            }
        });
        //为对话框设置一个“取消”按钮
        builder.setNegativeButton("取消", null);
        //创建并显示对话框
        builder.create().show();
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.add:
                    //Toast.makeText(getApplicationContext(), "添加信息", Toast.LENGTH_SHORT).show();
                    //弹出增加信息的对话框
                    if(state >= 0)
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

    /**
     * 加载本地的缓存数据，并显示在界面上
     */
    private void LoadLocalData() {
        try {
            listItems = (List<Map<String,Object>>) DataInOut.readData(getApplicationContext(), SaveFileName[state]);
            if(listItems.size() == 0)
            {
                Log.i("Error", "本地没有缓存数据");
                handler.sendEmptyMessage(0x128);
            }
            else
                handler.sendEmptyMessage(0x125);
        }catch (Exception e) {
            Log.i("Error", "读取数据失败");
        }
        //通知listView更新数据
        handler.sendEmptyMessage(0x126);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //按下键盘上返回按钮
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            Log.i("Exit", "点击了返回按钮");
            //保存当前打开的功能——————可以考虑将JSESSIONID也添加到配置文件里
            DataInOut.saveData(getApplicationContext(), state, "Config");
        }
        return super.onKeyDown(keyCode, event);
    }
}

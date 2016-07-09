package Method;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/**
 * Created by zengtaizhu on 2016/7/8.
 * 用于存储和读取数据
 */
public class DataInOut {

    /**
     * 保存数据
     * @param context   Activity
     * @param obj   存储的内容
     * @param fileName 存储的位置
     */
    public static void saveData(Context context, Object obj, String fileName)
    {
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        // 创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // 创建对象输出流，并封装字节流
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            //将对象写入字节流
            oos.writeObject(obj);
            //将字节流编码成base64的字符串
            String obj_base64 = Base64.encodeToString(baos.toByteArray(),0);
            Editor editor = preferences.edit();
            editor.putString("data", obj_base64);
            editor.commit();
        }catch(IOException e) {

        }
        Log.i("ok", "存储成功");
    }

    public static Object readData(Context context, String fileName)
    {
        Object obj = null;
        SharedPreferences preferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String productBase64 = preferences.getString("data", "");
        //读取字节
        byte[] base64 = Base64.decode(productBase64.getBytes(), 0);
        //封装到字节流
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try
        {
            //再次封装
            ObjectInputStream bis = new ObjectInputStream(bais);
            try
            {
                //读取对象
                obj = bis.readObject();
            }catch(ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("ok", "读取成功");
        return obj;
    }
}

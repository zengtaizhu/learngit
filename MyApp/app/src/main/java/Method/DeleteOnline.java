package Method;

import android.util.Log;

import URLFunc.SendHttpRequest;

/**
 * Created by zengtaizhu on 2016/7/8.
 */
public class DeleteOnline {

    /**
     * 删除网络的资源
     * @param n 要删除的数据位置
     * @param params 要删除数据的id
     * @param JSESSIONID 登陆凭证
     */
    public static void DeleteData(int n, Object params, String JSESSIONID)
    {
        String url = null;
        switch (n)
        {
            case 0:
                //删除进货信息
                url = "http://202.116.161.86:8888/distributor/receive/";
                break;
            case 1:
                //删除出货信息
                url = "http://202.116.161.86:8888/distributor/sale/";
                break;
            case 2:
                //删除动物信息
                url = "http://202.116.161.86:8888/distributor/animal/";
                break;
            case 3:
                //删除动物的物流信息
                url = "http://202.116.161.86:8888/distributor/logistics/";
                break;
            default:
                break;
        }
        //获取Http删除请求的结果
        String result = SendHttpRequest.sendDelete(url, params, JSESSIONID);
        if(result.contains("success"))
        {
            Log.i("Delete", "删除成功");
        }
        else
        {
            Log.i("Delete", "删除失败");
        }
    }
}

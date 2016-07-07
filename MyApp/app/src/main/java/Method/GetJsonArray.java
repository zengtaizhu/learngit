package Method;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengtaizhu on 2016/7/6.
 * 将从网络上获取的Json字符串的每一个元素分解出来
 */
public class GetJsonArray {
    /**
     * 将格式为[{},{},{},{},{}]的json字符串里的每一个元素提取出来，放在List数组里
     * @param str
     * @return
     */
    public static List<String> getJsonArray(String str)
    {
        List<String> result = new ArrayList<String>();
        boolean isStart = false;
        int start = 0;
        for(int i = 0; i < str.length() - 1; i++)
        {
            if(str.charAt(i) == '{')
            {
                isStart = true;
                start = i;
            }
            if(str.charAt(i) == '}' && isStart)
            {
                result.add(str.substring(start, i+1));
            }
        }
        return result;
    }
}

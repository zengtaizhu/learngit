package Method;

import android.util.Log;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DataClass.Animal;
import DataClass.Disease;
import DataClass.Quality;
import DataClass.Receive;
import DataClass.Sale;
import HttpResponse.Animals;
import DataClass.Logistics;
import HttpResponse.Receives;
import HttpResponse.Sales;

/**
 * Created by zengtaizhu on 2016/7/7.
 */
public class RequestData {
    /**
     * 根据请求，发送Http请求，通过GSON解析后返回数据
     * @param n 根据点击的具体功能以获取不同的数据
     * @param param 根据参数来获取特定的数据
     * @param JSESSIONID 登陆凭证
     * @return 返回该请求请求的List对象数据
     */
    public static List<Map<String, Object>> getData(int n, Object param, String JSESSIONID)
    {
        Gson gson = new Gson();
        //Http资源请求的URL网址
        String url = "http://www.scauszy.com:8899/distributor/";
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        HashMap<String, Object> listItem = null;
        //返回的响应字符串
        String response = null;
        //实际发送的URL请求
        String realUrl = null;
        //发送Http请求的系统参数
        String params = null;
        //初始化listItems
        listItems.clear();
        switch(n)
        {
            case 0:
                realUrl = url + "receive";
                params = "pageNum=" + param;
                response = SendHttpRequest.sendGet(realUrl, params,JSESSIONID);
                Receives receives = gson.fromJson(response, Receives.class);
                //将JSON字符串解析后，获取对象列表
                List<Receive> receiveList = receives.getResults();
                //将每一个对象装载到Map中
                for(int i = 0; i < receiveList.size(); i++)
                {
                    listItem = new HashMap<String, Object>();
                    listItem.put("id", receiveList.get(i).getId());
                    listItem.put("date", receiveList.get(i).getDate());
                    listItem.put("category", receiveList.get(i).getCategory());
                    listItem.put("disBatchNum", receiveList.get(i).getDisBatchNum());
                    listItem.put("number", receiveList.get(i).getNumber());
                    listItems.add(listItem);
                }
                break;
            case 1:
                realUrl = url + "sale";
                params = "pageNum=" + param;
                response = SendHttpRequest.sendGet(realUrl, params, JSESSIONID);
                Sales sales = gson.fromJson(response, Sales.class);
                //将JSON字符串解析后，获取对象列表
                List<Sale> saleList = sales.getResults();
                //将每一个对象装载到Map中
                for(int i = 0; i < saleList.size(); i++)
                {
                    listItem = new HashMap<String, Object>();
                    listItem.put("id", saleList.get(i).getId());
                    listItem.put("date", saleList.get(i).getDate());
                    listItem.put("category", saleList.get(i).getCategory());
                    listItem.put("batchNum", saleList.get(i).getBatchNum());
                    listItem.put("number", saleList.get(i).getNumber());
                    listItems.add(listItem);
                }
                break;
            case 2:
                realUrl = url + "animal";
                params = "pageNum=" + param;
                response = SendHttpRequest.sendGet(realUrl, params, JSESSIONID);
                Animals animals = gson.fromJson(response, Animals.class);
                //将JSON字符串解析后，获取对象列表
                List<Animal> animalList = animals.getResults();
                //将每一个对象装载到Map中
                for(int i = 0; i < animalList.size(); i++)
                {
                    listItem = new HashMap<String, Object>();
                    listItem.put("id", animalList.get(i).getAnimalId());
                    listItem.put("sourceCode", animalList.get(i).getSourceCode());
                    listItem.put("saleBatchNum", animalList.get(i).getSaleBatchNum());
                    listItem.put("state", animalList.get(i).getState());
                    listItem.put("birthday", animalList.get(i).getBirthday());
                    listItem.put("category", animalList.get(i).getCategory());
                    listItems.add(listItem);
                }
                break;
            case 3:
                //由于该请求返回一个JsonArray格式的json字符串，应特殊处理
                realUrl = url + "logistics/" + param;
                Log.i("LogisticsGet", realUrl + " JSESSIONID:" + JSESSIONID);
                try {
                    response = SendHttpRequest.sendGet(realUrl, null, JSESSIONID);
                    List<String> array = GetJsonArray.getJsonArray(response);
                    for (int i = 0; i < array.size(); i++) {
                        Logistics logistics = gson.fromJson(array.get(i), Logistics.class);
                        listItem = new HashMap<String, Object>();
                        listItem.put("animalId", logistics.getAnimalId());
                        listItem.put("id", logistics.getId());
                        listItem.put("position", logistics.getPosition());
                        listItem.put("time", logistics.getTime());
                        listItem.put("person", logistics.getPerson());
                        listItems.add(listItem);
                    }
                }catch (Exception e)
                {
                    Log.i("LogisticsError" , "查看物流信息失败:"+ e.toString());
                }
                break;
            case 4:
                //由于该请求返回一个JsonArray格式的json字符串，应特殊处理
                realUrl = url + "aniQua/" + param;
                response = SendHttpRequest.sendGet(realUrl, null, JSESSIONID);
                List<String> array2 = GetJsonArray.getJsonArray(response);
                for(int i = 0; i < array2.size(); i++)
                {
                    Quality quality = gson.fromJson(array2.get(i), Quality.class);
                    listItem = new HashMap<String, Object>();
                    listItem.put("id", quality.getId());
                    listItem.put("batchNumber", quality.getBatchNumber());
                    listItem.put("sampleNumber", quality.getSampleNumber());
                    listItem.put("qualifiedNumber", quality.getQualifiedNumber());
                    listItem.put("date", quality.getDate());
                    listItem.put("originId", quality.getOriginId());
                    listItem.put("organization", quality.getOrganization());
                    listItem.put("person", quality.getPerson());
                    listItems.add(listItem);
                }
                break;
            case 5:
                //由于该请求返回一个JsonArray格式的json字符串，应特殊处理
                realUrl = url + "disease/" + param;
                response = SendHttpRequest.sendGet(realUrl, null, JSESSIONID);
                List<String> array3 = GetJsonArray.getJsonArray(response);
                for(int i = 0; i < array3.size(); i++)
                {
                    Disease disease = gson.fromJson(array3.get(i), Disease.class);
                    listItem.put("id", disease.getId());
                    listItem.put("diseaseName", disease.getDiseaseName());
                    listItem.put("startDate", disease.getStartDate());
                    listItem.put("endDate", disease.getEndDate());
                    listItem.put("comments", disease.getComments());
                    listItems.add(listItem);
                }
                break;
            default:
                break;
        }
        return listItems;
    }
}

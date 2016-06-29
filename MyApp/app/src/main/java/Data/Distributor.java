package Data;

/**
 * Created by zengtaizhu on 2016/5/6.
 * 用于存放所有信息管理的功能项
 */
public class Distributor {
    private String[] receive;    //进货信息管理
    private String[] sale;     //出货信息管理
    private String[] animal;   //产品信息管理
    private String[] logistics; //物流信息管理
    private String[] aniQua;   //质检信息
    private String[] disease;     //生病信息
    public Distributor()
    {
        //进货信息管理的功能项
        receive = new String[]{"添加进货信息","修改进货信息","删除进货信息","获取进货信息"};
        //出货信息管理的功能项
        sale = new String[]{"添加出货信息","修改出货信息","删除出货信息","获取出货信息"};
        //产品信息的功能项
        animal = new String[]{"添加动物信息","修改动物信息","删除动物信息","获取动物信息"};
        //物流信息的功能项
        logistics = new String[]{"添加物流信息","修改物流信息","删除物流信息","获取物流信息"};
        //质检信息的功能项
        aniQua = new String[]{"查看所有质检信息"};
        //生病信息的功能项
        disease = new String[]{"查看生病信息"};
    }

    //按照发送过来的数值，将对应的数据传送过去
    public String[] getFunc(int n)
    {
        switch (n)
        {
            case 0:
                return receive;
            case 1:
                return sale;
            case 2:
                return animal;
            case 3:
                return logistics;
            case 4:
                return aniQua;
            case 5:
                return disease;
            default:
                return null;
        }
    }
}

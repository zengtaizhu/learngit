package Data;

import java.util.Date;

/**
 * Created by zengtaizhu on 2016/7/5.
 */
public class Sale {
    private String id;//出货信息的id
    private Date date;//出货日期
    private String name;//名称
    private String batchNum;//出货批次码
    private int number;//数量

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getBatchNum() {
        return batchNum;
    }

    public void setBatchNum(String batchNum) {
        this.batchNum = batchNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}

package DataClass;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by zengtaizhu on 2016/7/5.
 */
public class Receive implements Serializable {
    private String id;//进货信息的id
    private Date date;//进货日期
    private String category;//名称
    private String disBatchNum;//进货批次码
    private int number;//数量

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDisBatchNum() {
        return disBatchNum;
    }

    public void setDisBatchNum(String disBatchNum) {
        this.disBatchNum = disBatchNum;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

}

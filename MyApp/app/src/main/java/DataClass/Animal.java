package DataClass;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by zengtaizhu on 2016/7/5.
 */
public class Animal implements Serializable {
    private String animalId;//动物的id
    private String sourceCode;//动物的溯源码
    private String saleBatchNum;//出货批次码
    private String state;//状态
    private Date birthday;//出生日期
    private String category;//种类

    public String getAnimalId() {
        return animalId;
    }

    public void setAnimalId(String animalId) {
        this.animalId = animalId;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getSaleBatchNum() {
        return saleBatchNum;
    }

    public void setSaleBatchNum(String saleBatchNum) {
        this.saleBatchNum = saleBatchNum;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

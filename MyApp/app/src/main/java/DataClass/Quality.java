package DataClass;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by zengtaizhu on 2016/7/5.
 */
public class Quality implements Serializable {
    private String id;//质检信息的id
    private String batchNumber;//质检批次
    private int sampleNumber;//抽检数量
    private int qualifiedNumber;//合格数量
    private String date;//质检日期 格式:yyyy-mm-dd
    private String originId;//检查id
    private String organization;//检测机构
    private String person;//检测人员

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getQualifiedNumber() {
        return qualifiedNumber;
    }

    public void setQualifiedNumber(int qualifiedNumber) {
        this.qualifiedNumber = qualifiedNumber;
    }

    public int getSampleNumber() {
        return sampleNumber;
    }

    public void setSampleNumber(int sampleNumber) {
        this.sampleNumber = sampleNumber;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

}

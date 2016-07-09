package DataClass;

import java.io.Serializable;

/**
 * Created by zengtaizhu on 2016/7/5.
 */
public class Disease implements Serializable {
    private String id;//生病数据条目在数据库中的存储id
    private String diseaseName;//病名
    private String startDate;//生病的开始日期 格式:yyyy-mm-dd
    private String endDate;//病愈日期
    private String comments;//用药信息

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

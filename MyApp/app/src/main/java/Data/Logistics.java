package Data;

import java.util.Date;

/**
 * Created by zengtaizhu on 2016/7/5.
 */
public class Logistics {
    private String id;//物流信息的id
    private String position;//地点
    private Date time;//时间
    private String person;//操作人员

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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}

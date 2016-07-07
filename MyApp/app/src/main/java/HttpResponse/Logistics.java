package HttpResponse;

import java.util.Date;

/**
 * Created by zengtaizhu on 2016/7/5.
 */
public class Logistics {
    private String animalId;//动物的id
    private String id;//物流信息的id
    private String position;//地点
    private Date time;//时间
    private String person;//操作人员

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnimalId() {
        return animalId;
    }

    public void setAnimalId(String animalId) {
        this.animalId = animalId;
    }
}

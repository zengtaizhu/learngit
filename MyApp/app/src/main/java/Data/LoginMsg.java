package Data;

/**
 * Created by zengtaizhu on 2016/4/27.
 */
public class LoginMsg {
    private String ref;
    private int accountType;
    private String JSESSIONID;
    private String message;
    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public String getJSESSIONID() {
        return JSESSIONID;
    }

    public void setJSESSIONID(String JSESSIONID) {
        this.JSESSIONID = JSESSIONID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

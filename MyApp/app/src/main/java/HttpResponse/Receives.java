package HttpResponse;

import java.util.List;

import DataClass.Receive;

/**
 * Created by zengtaizhu on 2016/7/6.
 */
public class Receives {
    private int totalItems;
    private List<Receive> results;

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public List<Receive> getResults() {
        return results;
    }

    public void setResults(List<Receive> results) {
        this.results = results;
    }
}

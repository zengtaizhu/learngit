package Data;

import java.util.List;

/**
 * Created by zengtaizhu on 2016/7/6.
 */
public class Sales {
    private int totalItems;//总条目数
    private List<Sale> results;//出货信息数组

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public List<Sale> getResults() {
        return results;
    }

    public void setResults(List<Sale> results) {
        this.results = results;
    }
}

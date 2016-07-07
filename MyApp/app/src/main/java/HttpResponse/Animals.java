package HttpResponse;

import java.util.List;

import DataClass.Animal;

/**
 * Created by zengtaizhu on 2016/7/6.
 */
public class Animals {
    private int totalItems;//总条目数
    private List<Animal> results;//动物信息数组

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public List<Animal> getResults() {
        return results;
    }

    public void setResults(List<Animal> results) {
        this.results = results;
    }
}

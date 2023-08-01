package model;

/**
 * Model class to store report data for division names and associated total customers.
 */

public class ReportByDivision {
    private String name;
    private long total;

    public ReportByDivision(String name, long total) {
        this.name = name;
        this.total = total;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}

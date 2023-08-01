package model;

/**
 * This model class saves report data for total appointments by type and month
 */
public class ReportByType {
    private String month;
    private String type;
    private long total;

    public ReportByType(String month, String type, long total) {
        this.month = month;
        this.type = type;
        this.total = total;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}

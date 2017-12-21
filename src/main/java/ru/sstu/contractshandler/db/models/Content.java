package ru.sstu.contractshandler.db.models;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "MMVB")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private Date date;

    @Column
    private double price;

    @Column
    private double qualifying_Ratio;

    @Column
    private double correlation;

    @Column(length = 10, nullable = false)
    private String time_Frame;

    public static String getEntityName() {
        return Content.class.getAnnotation(Entity.class).name();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getQualifyingRatio() {
        return qualifying_Ratio;
    }

    public void setQualifyingRatio(double qualifyingRatio) {
        this.qualifying_Ratio = qualifyingRatio;
    }

    public String getTimeFrame() {
        return time_Frame;
    }

    public void setTimeFrame(String time_frame) {
        this.time_Frame = time_frame;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getCorrelation() {
        return correlation;
    }

    public void setCorrelation(double correlation) {
        this.correlation = correlation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Content)) return false;

        Content content = (Content) o;

        if (Double.compare(content.getPrice(), getPrice()) != 0) return false;
        if (Double.compare(content.qualifying_Ratio, qualifying_Ratio) != 0) return false;
        if (!getDate().equals(content.getDate())) return false;
        return time_Frame.equals(content.time_Frame);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getDate().hashCode();
        temp = Double.doubleToLongBits(getPrice());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(qualifying_Ratio);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + time_Frame.hashCode();
        return result;
    }

}

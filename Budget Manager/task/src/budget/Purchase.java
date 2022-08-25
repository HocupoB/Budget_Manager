package budget;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class Purchase {
    private String name;
    private BigDecimal cost;

    public Purchase(String name, BigDecimal cost) {
        this.name = name;
        this.cost = cost.setScale(2, RoundingMode.HALF_EVEN);
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCost() {
        return cost;
    }
    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
    public String toString() {
        return String.format("%s$%f", name, cost);
    }

}
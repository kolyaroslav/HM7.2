package jpa1;
import javax.persistence.*;

@Entity
@Table(name = "Rates_of_exchange")
public class RateOfExchange {

    @Id
    @GeneratedValue
    @Column(name = "id_rate")
    private Long id;

    @Column
    private String currency;

    @Column
    private Double rateToUAH;

    public RateOfExchange() {}

    public RateOfExchange(String currency, Double rateToUAH) {
        this.currency = currency;
        this.rateToUAH = rateToUAH;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getRateToUAH() {
        return rateToUAH;
    }

    public void setRateToUAH(Double rateToUAH) {
        this.rateToUAH = rateToUAH;
    }

    @Override
    public String toString() {
        return "RateOfExchange{" +
                "id=" + id +
                ", currency='" + currency + '\'' +
                ", rateToUAH=" + rateToUAH +
                '}';
    }
}
package jpa1;
import javax.persistence.*;

@Entity
@Table(name = "Accounts")
public class Account {
    @Id
    @GeneratedValue
    @Column(name = "id_Account")
    private Long id;

    @Column(name = "currency_account")
    private String currency;

    @Column(name = "balance_account")
    private Double balance;

    @ManyToOne
    @JoinColumn(name = "id_Client", nullable = false)

    private Client client;

    public Account() {}

    public Account(String currency, Double balance, Client client) {
        this.currency = currency;
        this.balance = balance;
        this.client = client;
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

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double total) {
        this.balance = total;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void replenishBalance(Double balance) {
        this.balance += balance;
    }

    public void withdrawFromBalance(Double balance) {
        this.balance -= balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", currency='" + currency + '\'' +
                ", balance=" + balance +
                ", client=" + client +
                '}';
    }
}
package jpa1;

import javax.persistence.*;

@Entity
@Table(name = "Transactions")
public class Transaction {
    @Id
    @GeneratedValue
    @Column(name = "id_Transaction")
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "client_Id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "beneficiary_Id_Account")
    private Account beneficiaryIdAccount;

    @ManyToOne
    @JoinColumn(name = "sender_Id_Account")
    private Account senderIdAccount;

    @Column(name = "balancePlus_Transaction")
    private Double balancePlus;

    public Transaction() {
    }


    public Transaction(Client client, Account beneficiaryIdAccount, Account senderIdAccount, Double balancePlus) {
        this.client = client;
        this.beneficiaryIdAccount = beneficiaryIdAccount;
        this.senderIdAccount = senderIdAccount;
        this.balancePlus = balancePlus;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Account getBeneficiaryIdAccount() {
        return beneficiaryIdAccount;
    }

    public void setBeneficiaryIdAccount(Account beneficiaryIdAccount) {
        this.beneficiaryIdAccount = beneficiaryIdAccount;
    }

    public Account getSenderIdAccount() {
        return senderIdAccount;
    }

    public void setSenderIdAccount(Account senderIdAccount) {
        this.senderIdAccount = senderIdAccount;
    }

    public Double getBalancePlus() {
        return balancePlus;
    }

    public void setBalancePlus(Double balancePlus) {
        this.balancePlus = balancePlus;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", client=" + client +
                ", beneficiaryIdAccount=" + beneficiaryIdAccount +
                ", senderIdAccount=" + senderIdAccount +
                ", balancePlus=" + balancePlus +
                '}';
    }
}
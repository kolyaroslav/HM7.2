package jpa1;

import javax.persistence.*;
import java.util.List;
import java.util.Scanner;

public class Main {
    static EntityManagerFactory emf;
    static EntityManager em;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            emf = Persistence.createEntityManagerFactory("JPATest33");
            em = emf.createEntityManager();


            em.getTransaction().begin();
            RateOfExchange rateOfExchange1 = new RateOfExchange("USD", 36.56);
            em.persist(rateOfExchange1);
            RateOfExchange rateOfExchange2 = new RateOfExchange("EUR", 39.83);
            em.persist(rateOfExchange2);
            RateOfExchange rateOfExchange3 = new RateOfExchange("UAH", 1.0);
            em.persist(rateOfExchange3);
            em.getTransaction().commit();

            try {
                while (true) {
                    System.out.println("1: Add client");
                    System.out.println("2: Delete client");
                    System.out.println("3: View all clients");
                    System.out.println("4: View transactions");
                    System.out.println("5: Add account");
                    System.out.println("6: Top up account");
                    System.out.println("7: Delete account");
                    System.out.println("8: View all accounts");
                    System.out.println("9: Add exchange rate");
                    System.out.println("10: Delete exchange rate");
                    System.out.println("11: View exchange rate");
                    System.out.println("12: Transfer funds");
                    System.out.println("13: Transfer funds with conversion");
                    System.out.println("14: Transfer funds with conversion for single Client");
                    System.out.println("15: View total funds single client in UAH");

                    System.out.print("-> ");

                    String str = sc.nextLine();
                    switch (str) {
                        case "1":
                            addClient(sc);
                            break;
                        case "2":
                            deleteClient(sc);
                            break;
                        case "3":
                            viewClients();
                            break;
                        case "4":
                            viewTransactions();
                            break;
                        case "5":
                            addAccount(sc);
                            break;
                        case "6":
                            topUpAccount(sc);
                            break;
                        case "7":
                            deleteAccount(sc);
                            break;
                        case "8":
                            viewAccounts();
                            break;
                        case "9":
                            addRateOfExchange(sc);
                            break;
                        case "10":
                            deleteRateOfExchange(sc);
                            break;
                        case "11":
                            viewRateOfExchanges();
                            break;
                        case "12":
                            transferFunds(sc);
                            break;
                        case "13":
                            transferFundsWithConversion(sc);
                            break;
                        case "14":
                            transferFundsWithConversionForSingleClient(sc);
                            break;
                        case "15":
                            totalFundsSingleClientInUAH(sc);
                            break;

                        default:
                            return;
                    }
                }
            } finally {
                sc.close();
                em.close();
                emf.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private static void addClient(Scanner sc) {
        System.out.print("Enter client name: ");
        String name = sc.nextLine();

        System.out.print("Enter client phone: ");
        String strPhone = sc.nextLine();
        int phone = Integer.parseInt(strPhone);

        em.getTransaction().begin();
        try {
            Client client = new Client(name, phone);
            em.persist(client);
            em.getTransaction().commit();

            System.out.println(client.getId());
        } catch (Exception exception) {
            em.getTransaction().rollback();
        }
    }

    private static void deleteClient(Scanner sc) {
        System.out.print("Enter client id: ");
        String strId = sc.nextLine();
        long id = Long.parseLong(strId);

        Client client = em.getReference(Client.class, id);
        if (client == null) {
            System.out.println("Client not found");
            return;
        }

        em.getTransaction().begin();
        try {
            em.remove(client);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void viewClients() {
        String queryClients = "SELECT c FROM Client c";
        Query query = em.createQuery(queryClients, Client.class);
        List<Client> list = (List<Client>) query.getResultList();

        for (Client client : list)
            System.out.println(client);
    }

    private static void viewTransactions() {
        String queryTransactions = "SELECT t FROM Transaction t";
        Query query = em.createQuery(queryTransactions, Transaction.class);
        List<Transaction> list = (List<Transaction>) query.getResultList();

        for (Transaction transaction : list) {
            System.out.println(transaction);
        }
    }

    private static void addAccount(Scanner sc) {
        System.out.print("Enter client name: ");
        String clientName = sc.nextLine();

        Client client = null;
        try {
            String queryNameClient = "SELECT c FROM Client c WHERE c.name = :name";
            Query query = em.createQuery(queryNameClient);
            query.setParameter("name", clientName);
            client = (Client) query.getSingleResult();


            System.out.print("Enter amount: ");
            String strSum = sc.nextLine();
            double sum = Double.parseDouble(strSum);

            em.getTransaction().begin();
            Account account = new Account(selectCurrency(), sum, client);

            em.persist(account);
            em.getTransaction().commit();
            System.out.println("Ok.");


        } catch (NoResultException exception) {
            System.out.println("Client not found!");
            return;
        } catch (NonUniqueResultException exception) {
            System.out.println("Non unique client found!");
            return;
        }
    }

    private static void topUpAccount(Scanner sc) {
        double thisBalance = 0.0;
        System.out.print("Enter account id: ");
        String strAccountId = sc.nextLine();
        long accountId = Long.parseLong(strAccountId);

        Account account = em.find(Account.class, accountId);
        if (account == null) {
            System.out.println("Account not found!");
            return;
        }

        System.out.print("Enter sum: ");
        String strSum = sc.nextLine();
        double balance = Double.parseDouble(strSum);

        em.getTransaction().begin();
        try {
            Query query;
            String queryCurrency = "SELECT r FROM RateOfExchange r WHERE r.currency = :currency";


            if (!selectCurrency().equals(account.getCurrency())) {
                query = em.createQuery(queryCurrency);
                query.setParameter("currency", account.getCurrency());
                RateOfExchange rateOfExchange = (RateOfExchange) query.getSingleResult();
                double balanceOfAccount = rateOfExchange.getRateToUAH();

                if (selectCurrency().equals("UAH")) {
                    thisBalance = (balance / balanceOfAccount);
                } else {
                    thisBalance = balance;
                }
                account.replenishBalance(thisBalance);
                em.persist(account);
            } else {
                thisBalance = balance;
                account.replenishBalance(thisBalance);
                em.persist(account);
            }

            Transaction transaction = new Transaction(account.getClient(), account, null, thisBalance);

            em.persist(transaction);
            em.getTransaction().commit();
            System.out.println("Ok!");
        } catch (Exception exception) {
            exception.printStackTrace();
            em.getTransaction().rollback();
            return;
        }
    }

    private static void deleteAccount(Scanner sc) {
        System.out.print("Enter account id: ");
        String strId = sc.nextLine();
        long id = Long.parseLong(strId);

        Account account = em.getReference(Account.class, id);
        if (account == null) {
            System.out.println("Account not found!");
            return;
        }

        em.getTransaction().begin();
        try {
            em.remove(account);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void viewAccounts() {
        String queryAccounts = "SELECT a FROM Account a";
        Query query = em.createQuery(queryAccounts, Account.class);
        List<Account> list = (List<Account>) query.getResultList();

        for (Account account : list)
            System.out.println(account);
    }

    private static void addRateOfExchange(Scanner sc) {
        System.out.print("Enter currency: ");
        String currency = sc.nextLine();

        System.out.print("Enter rate to UAH: ");
        String strRateToUAH = sc.nextLine();
        double rateToUAH = Double.parseDouble(strRateToUAH);

        em.getTransaction().begin();
        try {
            RateOfExchange rateOfExchange = new RateOfExchange(currency, rateToUAH);
            em.persist(rateOfExchange);
            em.getTransaction().commit();

            System.out.println(rateOfExchange.getId());
        } catch (Exception exception) {
            em.getTransaction().rollback();
        }
    }

    private static void deleteRateOfExchange(Scanner sc) {
        System.out.print("Enter rate of exchange id: ");
        String strId = sc.nextLine();
        long id = Long.parseLong(strId);

        RateOfExchange rateOfExchange = em.getReference(RateOfExchange.class, id);
        if (rateOfExchange == null) {
            System.out.println("Rate of exchange not found!");
            return;
        }

        em.getTransaction().begin();
        try {
            em.remove(rateOfExchange);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void viewRateOfExchanges() {
        String queryRate = "SELECT r FROM RateOfExchange r";
        Query query = em.createQuery(queryRate, RateOfExchange.class);
        List<RateOfExchange> list = (List<RateOfExchange>) query.getResultList();

        for (RateOfExchange rateOfExchange : list)
            System.out.println(rateOfExchange);
    }

    private static void transferFunds(Scanner sc) {
        System.out.print("Enter your sender account id: ");
        String strYourSenderAccountId = sc.nextLine();
        Long yourSenderAccountId = Long.parseLong(strYourSenderAccountId);

        Account senderAccount = em.find(Account.class, yourSenderAccountId);
        if (senderAccount == null) {
            System.out.println("Account not found!");
            return;
        }

        System.out.print("Enter beneficiary account id: ");
        String strBeneficiaryAccountId = sc.nextLine();
        Long beneficiaryAccountId = Long.parseLong(strBeneficiaryAccountId);

        Account beneficiaryAccount = em.find(Account.class, beneficiaryAccountId);
        if (beneficiaryAccount == null) {
            System.out.println("Account not found!");
            return;
        }

        if (senderAccount.getCurrency().equals(beneficiaryAccount.getCurrency())) {
            System.out.print("Enter sum for transfer: ");
            String strSum = sc.nextLine();
            double sum = Double.parseDouble(strSum);
            if (sum > senderAccount.getBalance()) {
                System.out.println("Error! Insufficiently money!");
                return;
            }

            em.getTransaction().begin();
            try {
                Transaction transaction = new Transaction(senderAccount.getClient(), beneficiaryAccount, senderAccount, sum);
                em.persist(transaction);

                beneficiaryAccount.replenishBalance(sum);
                senderAccount.withdrawFromBalance(sum);

                em.getTransaction().commit();
                System.out.println("Ok!");
            } catch (Exception exception) {
                exception.printStackTrace();
                em.getTransaction().rollback();
                return;
            }
        } else {
            System.out.println("Enter the account with the currency that matches!");
            return;
        }
    }

    private static void transferFundsWithConversion(Scanner sc) {
        System.out.print("Enter sender account id: ");
        String strSenderAccountId = sc.nextLine();
        Long senderAccountId = Long.parseLong(strSenderAccountId);

        Account senderAccount = em.find(Account.class, senderAccountId);
        if (senderAccount == null) {
            System.out.println("Account not found!");
            return;
        }

        System.out.print("Enter beneficiary account id: ");
        String strBeneficiaryAccountId = sc.nextLine();
        Long beneficiaryAccountId = Long.parseLong(strBeneficiaryAccountId);

        Account beneficiaryAccount = em.find(Account.class, beneficiaryAccountId);
        if (beneficiaryAccount == null) {
            System.out.println("Account not found!");
            return;
        }

        System.out.print("Enter sum for transfer: ");
        String strSum = sc.nextLine();
        double sum = Double.parseDouble(strSum);
        if (sum > senderAccount.getBalance()) {
            System.out.println("Error! Insufficiently money!");
            return;
        }

        em.getTransaction().begin();
        try {
            Transaction transaction = new Transaction(senderAccount.getClient(), senderAccount, beneficiaryAccount, sum);
            em.persist(transaction);

            if (!senderAccount.getCurrency().equals(beneficiaryAccount.getCurrency())) {
                String queryRateOfExchange = "SELECT r from RateOfExchange r WHERE currency = :currency";

                Query query = em.createQuery(queryRateOfExchange);
                query.setParameter("currency", senderAccount.getCurrency());
                RateOfExchange rateOfExchange = (RateOfExchange) query.getSingleResult();
                double senderAccountRate = rateOfExchange.getRateToUAH();

                query.setParameter("currency", beneficiaryAccount.getCurrency());
                rateOfExchange = (RateOfExchange) query.getSingleResult();
                double beneficiaryAccountRate = rateOfExchange.getRateToUAH();

                double thisBalance;
                if (senderAccount.getCurrency().equals("UAH")) {
                    thisBalance = sum / beneficiaryAccountRate;
                } else {
                    thisBalance = sum * senderAccountRate / beneficiaryAccountRate;
                }
                senderAccount.withdrawFromBalance(sum);
                beneficiaryAccount.replenishBalance(thisBalance);
            } else {
                senderAccount.withdrawFromBalance(sum);
                beneficiaryAccount.replenishBalance(sum);
            }
            em.getTransaction().commit();
            System.out.println("Ok!");
        } catch (Exception ex) {
            ex.printStackTrace();
            em.getTransaction().rollback();
            return;
        }
    }

    private static void transferFundsWithConversionForSingleClient(Scanner sc) {
        System.out.print("Enter sender account id: ");
        String strSenderAccountId = sc.nextLine();
        Long senderAccountId = Long.parseLong(strSenderAccountId);

        Account senderAccount = em.find(Account.class, senderAccountId);
        if (senderAccount == null) {
            System.out.println("Account not found!");
            return;
        }

        System.out.print("Enter beneficiary account id: ");
        String strBeneficiaryAccountId = sc.nextLine();
        Long beneficiaryAccountId = Long.parseLong(strBeneficiaryAccountId);

        Account beneficiaryAccount = em.find(Account.class, beneficiaryAccountId);
        if (beneficiaryAccount == null) {
            System.out.println("Account not found!");
            return;
        }

        if (senderAccount.getClient().equals(beneficiaryAccount.getClient())) {
            System.out.print("Enter sum for transfer: ");
            String strSum = sc.nextLine();
            double sum = Double.parseDouble(strSum);
            if (sum > senderAccount.getBalance()) {
                System.out.println("Error! Insufficiently money!");
                return;
            }

            em.getTransaction().begin();
            try {
                Transaction transaction = new Transaction(senderAccount.getClient(), senderAccount, beneficiaryAccount, sum);
                em.persist(transaction);

                if (!senderAccount.getCurrency().equals(beneficiaryAccount.getCurrency())) {
                    String queryRateOfExchange = "SELECT r from RateOfExchange r WHERE currency = :currency";

                    Query query = em.createQuery(queryRateOfExchange);
                    query.setParameter("currency", senderAccount.getCurrency());
                    RateOfExchange rateOfExchange = (RateOfExchange) query.getSingleResult();
                    double senderAccountRate = rateOfExchange.getRateToUAH();

                    query.setParameter("currency", beneficiaryAccount.getCurrency());
                    rateOfExchange = (RateOfExchange) query.getSingleResult();
                    double beneficiaryAccountRate = rateOfExchange.getRateToUAH();

                    double thisBalance;
                    if (senderAccount.getCurrency().equals("UAH")) {
                        thisBalance = sum / beneficiaryAccountRate;
                    } else {
                        thisBalance = sum * senderAccountRate / beneficiaryAccountRate;
                    }
                    senderAccount.withdrawFromBalance(sum);
                    beneficiaryAccount.replenishBalance(thisBalance);
                } else {
                    senderAccount.withdrawFromBalance(sum);
                    beneficiaryAccount.replenishBalance(sum);
                }
                em.getTransaction().commit();
                System.out.println("Ok!");
            } catch (Exception ex) {
                ex.printStackTrace();
                em.getTransaction().rollback();
                return;
            }
        } else {
            System.out.println("Error! Clients don't match!");
        }
    }

    private static void totalFundsSingleClientInUAH(Scanner sc) {
        System.out.print("Enter client name: ");
        String strClientName = sc.nextLine();

        String queryAccounts = "SELECT a FROM Account a";
        Query query = em.createQuery(queryAccounts, Account.class);
        List<Account> list = (List<Account>) query.getResultList();

        double totalFunds = 0.0;

        for (Account account : list) {
            if (account.getClient().getName().equals(strClientName)) {
                account = em.getReference(Account.class, account.getId());
                String queryRateOfExchange = "SELECT r from RateOfExchange r WHERE currency = :currency";
                query = em.createQuery(queryRateOfExchange);
                query.setParameter("currency", account.getCurrency());
                RateOfExchange rateOfExchange = (RateOfExchange) query.getSingleResult();
                double thisAccountRate = rateOfExchange.getRateToUAH();
                double balanceInUAH = thisAccountRate * account.getBalance();
                totalFunds += balanceInUAH;
                System.out.println(account);
            } else {
                System.out.println("Account not found!");
                return;
            }
        }
        System.out.println("Total funds single client in UAH: " + String.format("%.2f", totalFunds));
    }

    private static String selectCurrency() {
        String queryRate = "SELECT r FROM RateOfExchange r";
        Query query = em.createQuery(queryRate, RateOfExchange.class);
        List<RateOfExchange> list = (List<RateOfExchange>) query.getResultList();

        Scanner sc = new Scanner(System.in);
        String strCurrencyNames = "";
        for (RateOfExchange rateOfExchange : list) {
            strCurrencyNames += rateOfExchange.getCurrency() + " ";
        }

        System.out.print("Select currency: " + strCurrencyNames);
        String strCurrency = sc.nextLine();
        String currency = null;
        for (RateOfExchange rateOfExchange : list) {
            if (strCurrency.equals(rateOfExchange.getCurrency())) {
                currency = strCurrency;
            }
        }
        return currency;
    }
}
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

  public static void main(String[] args) {
    // Sample stock prices
    Map<String, Double> stockPrices = new HashMap<>();
    stockPrices.put("AAPL", 150.0);
    stockPrices.put("GOOGL", 2800.0);
    stockPrices.put("MSFT", 300.0);

    // Sample account data
    List<Transaction> transactions = Arrays.asList(
        new Transaction("buy", "AAPL", 1, 120.0, 50),
        new Transaction("sell", "AAPL", 10, 110.0, 30),
        new Transaction("buy", "AAPL", 15, 115, 20),
        new Transaction("buy", "GOOGL", 20, 2500.0, 2),
        new Transaction("sell", "GOOGL", 30, 2400.0, 2),
        new Transaction("buy", "GOOGL", 65, 2450.0, 1),
        new Transaction("sell", "AAPL", 10, 140.0, 20),
        new Transaction("buy", "MSFT", 15, 280.0, 10)
    );

    AccountData accountData = new AccountData(0, 20000.0, transactions);

//    getTotals(accountData, stockPrices);

    getWashSaleRecords(accountData);

  }

  public static void getWashSaleRecords(AccountData accountData){
    Map<String, WashSale> washSaleMap = new HashMap<>();

    for(Transaction txn: accountData.transactions){
       WashSale washSale;
      if(txn.type.equals("sell")) {
        washSale = new WashSale(txn.symbol, txn.date, 0);
        washSaleMap.put(txn.symbol, washSale);
      } else if(txn.type.equals("buy")) {
        WashSale existingWash = washSaleMap.getOrDefault(txn.symbol, new WashSale());

        if(existingWash.sellDate != 0  ){
          int checkDays = txn.date - existingWash.sellDate;
          if(checkDays <= 30){
            WashSale  newWash = new WashSale(txn.symbol, existingWash.sellDate, txn.date);
            washSaleMap.put(txn.symbol, newWash);
            System.out.println("Wash Sale: " +txn.symbol +" Sell Date:" +existingWash.sellDate + " Buy Date: " + txn.date);
          }
        }
      }
    }

  }

  public static void getTotals( AccountData accountData, Map<String, Double> stockPrices){
    Map<String, Integer> stocksMap = new HashMap<>();
    double totalValue = 0.0;
    double remainingCash = accountData.initialBalance;

    for(Transaction txn : accountData.transactions){
      double stockTotal = txn.price * txn.shares;
      if(txn.type.equals("buy")) {
        remainingCash -= stockTotal;
        stocksMap.put(txn.symbol, stocksMap.getOrDefault(txn.symbol, 0) + txn.shares);
      } else {
        remainingCash += stockTotal;
        stocksMap.put(txn.symbol, stocksMap.getOrDefault(txn.symbol, 0) - txn.shares);
      }
    }

    System.out.println("Cash Balance:" +remainingCash);
    for(Map.Entry<String, Integer> position: stocksMap.entrySet()){
      double value = position.getValue() * stockPrices.get(position.getKey());
      totalValue += value;
      System.out.println("Stock:" +position.getKey() + "value:" +value);
    }

    double finalValue = totalValue + remainingCash;
    System.out.println("Total Value:" +finalValue);
  }

  static class WashSale {
    public String symbol;
    public int sellDate;
    public int buyDate;

    public WashSale(String symbol, int sellDate, int buyDate) {
      this.symbol = symbol;
      this.sellDate = sellDate;
      this.buyDate = buyDate;
    }

    public WashSale() {}
  }

  /**
   * Represents a single stock transaction
   */
  public static class Transaction {
    public String type;      // "buy" or "sell"
    public String symbol;    // Stock symbol (e.g., "AAPL")
    public int date;         // Transaction date
    public double price;     // Price per share at transaction
    public int shares;       // Number of shares

    public Transaction(String type, String symbol, int date, double price, int shares) {
      this.type = type;
      this.symbol = symbol;
      this.date = date;
      this.price = price;
      this.shares = shares;
    }
  }

  /**
   * Represents a client's account data
   */
  public static class AccountData {
    public int accountDate;
    public double initialBalance;
    public List<Transaction> transactions;

    public AccountData(int accountDate, double initialBalance, List<Transaction> transactions) {
      this.accountDate = accountDate;
      this.initialBalance = initialBalance;
      this.transactions = transactions;
    }
  }
}
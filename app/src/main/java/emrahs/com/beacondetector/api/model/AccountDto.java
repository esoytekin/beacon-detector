package emrahs.com.beacondetector.api.model;

/**
 * Created by mikailoral on 9.12.2017.
 */

public class AccountDto {
    String accountStatus;
    String branchCode;
    String IBAN;
    String accountNickname;
    String branchName;
    String accountNumber;
    String customerNumber;
    String openDate;
    String currencyCode;
    String availableBalance;

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getIBAN() {
        return IBAN;
    }

    public void setIBAN(String IBAN) {
        this.IBAN = IBAN;
    }

    public String getAccountNickname() {
        return accountNickname;
    }

    public void setAccountNickname(String accountNickname) {
        this.accountNickname = accountNickname;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getOpenDate() {
        return openDate;
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(String availableBalance) {
        this.availableBalance = availableBalance;
    }

    @Override
    public String toString() {
        return "AccountDto{" +
                "accountStatus='" + accountStatus + '\'' +
                ", branchCode='" + branchCode + '\'' +
                ", IBAN='" + IBAN + '\'' +
                ", accountNickname='" + accountNickname + '\'' +
                ", branchName='" + branchName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", customerNumber='" + customerNumber + '\'' +
                ", openDate='" + openDate + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", availableBalance='" + availableBalance + '\'' +
                '}';
    }
}

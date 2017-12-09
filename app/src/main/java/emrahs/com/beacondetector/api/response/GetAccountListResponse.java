package emrahs.com.beacondetector.api.response;


import java.util.List;

import emrahs.com.beacondetector.api.model.AccountDto;

/**
 * Created by mikailoral on 9.12.2017.
 */

public class GetAccountListResponse {
    List<AccountDto> accountList;

    public List<AccountDto> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<AccountDto> accountList) {
        this.accountList = accountList;
    }
}

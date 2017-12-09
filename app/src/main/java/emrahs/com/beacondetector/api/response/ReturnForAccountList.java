package emrahs.com.beacondetector.api.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mikailoral on 9.12.2017.
 */

public class ReturnForAccountList {

    @SerializedName("return")
    private GetAccountListResponse ret;

    public GetAccountListResponse getRet() {
        return ret;
    }

    public void setRet(GetAccountListResponse ret) {
        this.ret = ret;
    }
}

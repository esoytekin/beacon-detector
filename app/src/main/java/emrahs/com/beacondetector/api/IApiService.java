package emrahs.com.beacondetector.api;


import emrahs.com.beacondetector.api.response.ResponseForAccountList;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by mikailoral on 9.12.2017.
 */

public interface IApiService {

    @POST("api/currentAccounts/account/v1/accountList")
    Call<ResponseForAccountList> getAccountList(@Body RequestBody body);
}

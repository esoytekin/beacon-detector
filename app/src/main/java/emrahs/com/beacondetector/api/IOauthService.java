package emrahs.com.beacondetector.api;

/**
 * Created by mikailoral on 9.12.2017.
 */



import emrahs.com.beacondetector.api.response.AccessTokenResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IOauthService {
    @FormUrlEncoded
    @POST("auth/oauth/v2/token")
    Call<AccessTokenResponse> getAccessToken(@Field("scope") String scope, @Field("grant_type") String grantType, @Field("client_id") String clientId, @Field("client_secret") String clientSecret);
}
package emrahs.com.beacondetector.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mikailoral on 9.12.2017.
 */

public class ApiService {
    public IApiService getInstance(Context context) throws Exception {
        OkHttpClient client = APIUtil.createClient(context, true);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUtil.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(IApiService.class);
    }
}

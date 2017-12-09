package emrahs.com.beacondetector.api;

import android.content.Context;


import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import emrahs.com.beacondetector.R;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by mikailoral on 9.12.2017.
 */

public class APIUtil {

    public static String ACCEESS_TOKEN ;
    public static String BASE_URL = "https://api.yapikredi.com.tr/";
    public static final String SCOPE = "oob";
    public static final String GRANT_TYPE = "client_credentials";
    public static final String CLIENT_ID = "l7xxbd6c00657f6f41128c62b645fe20ecb8";
    public static final String CLIENT_SECRET = "9fbf55903742452ebd5915576a952318";



    public static OkHttpClient createClient(Context context, Boolean addInterceptor) {
        OkHttpClient client = null;
        CertificateFactory cf = null;
        Certificate ca = null;
        SSLContext sslContext = null;

        try {
            cf = CertificateFactory.getInstance("X.509");

            InputStream cert = context.getResources().openRawResource(R.raw.apiportal);
            ca = cf.generateCertificate(cert);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            cert.close();

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            if (addInterceptor) {
                client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Interceptor.Chain chain) throws IOException {
                        Request original = chain.request();

                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", "Bearer " + ACCEESS_TOKEN);

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                }).sslSocketFactory(sslContext.getSocketFactory())
                        .build();
            } else {
                client = new OkHttpClient.Builder().sslSocketFactory(sslContext.getSocketFactory())
                        .build();
            }

        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | KeyManagementException e) {
            e.printStackTrace();
        }

        return client;
    }


}

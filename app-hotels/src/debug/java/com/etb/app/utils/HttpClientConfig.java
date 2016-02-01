package com.etb.app.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.etb.app.R;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * @author alex
 * @date 2015-07-01
 */
public class HttpClientConfig {

    public static void apply(DefaultHttpClient client, Context context) {
        try {
            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("il", getCertificate(R.raw.il_easytobook_us, context));
            keyStore.setCertificateEntry("us", getCertificate(R.raw.easytobook_us, context));
            keyStore.setCertificateEntry("ap", getCertificate(R.raw.api_easytobook_us, context));
            keyStore.setCertificateEntry("nl", getCertificate(R.raw.secure_easytobook_com, context));
            keyStore.setCertificateEntry("aw", getCertificate(R.raw.api_easytobook_com, context));

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);


            client.setHostnameVerifier(new AllowAllHostnameVerifier());
            client.setSslSocketFactory(sslContext.getSocketFactory());

        } catch (Exception e) {
            AppLog.e(e);
        }
    }

    @NonNull
    private static Certificate getCertificate(int res, Context context) throws CertificateException, IOException {
        // Load CAs from an InputStream
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream is = context.getResources().openRawResource(res);
        InputStream caInput = new BufferedInputStream(is);
        Certificate ca;

        // shell: openssl x509 -in easytobook.us.cert -outform der -out easytobook_us_der.crt

        try {
            ca = cf.generateCertificate(caInput);
            AppLog.d("ca=" + ((X509Certificate) ca).getSubjectDN());
        } finally {
            caInput.close();
        }
        return ca;
    }

    static class BypassTrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            // do some checks on the chain here
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}

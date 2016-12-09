/**
 * Android Jungle framework project.
 *
 * Copyright 2016 Arno Zhang <zyfgood12@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jungle.base.utils;

import android.content.Context;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class HttpsUtils {

    public static class DomainHostNameVerifier implements HostnameVerifier {

        /**
         * Verifier domain name, such as `biz.main.com`.
         */
        private String mVerifyDomain;

        public DomainHostNameVerifier(String domain) {
            mVerifyDomain = domain;
        }

        @Override
        public boolean verify(String s, SSLSession sslSession) {
            HostnameVerifier verifier = HttpsURLConnection.getDefaultHostnameVerifier();
            return verifier.verify(mVerifyDomain, sslSession);
        }
    }


    /**
     * @param crtText Certificate file content.
     *                Can use `keytool -printcert -rfc -file uwca.crt` command to print it.
     */
    public static X509Certificate createCertificateByCrtText(String crtText) {
        return createCertificateByStream(new ByteArrayInputStream(crtText.getBytes()));
    }

    public static X509Certificate createCertificateByCrtFile(String fileName) {
        InputStream stream = null;
        try {
            stream = new FileInputStream(fileName);
            return createCertificateByStream(stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static X509Certificate createCertificateByCrtAsset(Context context, String fileName) {
        try {
            return createCertificateByStream(context.getAssets().open(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static X509Certificate createCertificateByStream(InputStream stream) {
        Certificate certificate = createCertificateByStream(stream, "X.509");
        return certificate != null ? (X509Certificate) certificate : null;
    }

    public static Certificate createCertificateByStream(InputStream stream, String type) {
        try {
            CertificateFactory factory = CertificateFactory.getInstance(type);
            return factory.generateCertificate(new BufferedInputStream(stream));
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param publicKey Certificate public key.
     *                  Can use {@link Certificate#getPublicKey()} to get it.
     */
    public static TrustManager createTrustManager(final PublicKey publicKey) {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
                    throws CertificateException {

                if (x509Certificates == null || x509Certificates.length <= 0) {
                    throw new CertificateException("Invalid X509Certificate!");
                }

                for (X509Certificate cert : x509Certificates) {
                    cert.checkValidity();

                    try {
                        cert.verify(publicKey);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    } catch (NoSuchProviderException e) {
                        e.printStackTrace();
                    } catch (SignatureException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
                    throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    public static TrustManager[] createTrustManagerByCerts(Certificate... certs) {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);

            for (int i = 0; i < certs.length; ++i) {
                keyStore.setCertificateEntry("ca_" + String.valueOf(i), certs[i]);
            }

            TrustManagerFactory factory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            factory.init(keyStore);
            return factory.getTrustManagers();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static SSLContext getSslContext(PublicKey publicKey) {
        return getSslContext(createTrustManager(publicKey));
    }

    public static SSLContext getSslContext(TrustManager... trustManagers) {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagers, null);
            return sslContext;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static HttpsURLConnection openVerifyConnection(
            String httpsUrl, PublicKey publicKey, String verifyHostName) {

        return openVerifyConnection(httpsUrl, publicKey, new DomainHostNameVerifier(verifyHostName));
    }

    public static HttpsURLConnection openVerifyConnection(
            String httpsUrl, PublicKey publicKey, HostnameVerifier verifier) {

        try {
            SSLContext sslContext = getSslContext(publicKey);

            URL url = new URL(httpsUrl);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            if (sslContext != null) {
                connection.setSSLSocketFactory(sslContext.getSocketFactory());
            }

            connection.setHostnameVerifier(verifier);
            return connection;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

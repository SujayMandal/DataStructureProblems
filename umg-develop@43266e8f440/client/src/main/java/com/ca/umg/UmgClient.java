package com.ca.umg;

/**
 * Created by repvenk on 5/19/2016.
 */
public class UmgClient {

    /*public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException, IOException {
        HttpClientBuilder cb = HttpClientBuilder.create();
        SSLContextBuilder sslcb = new SSLContextBuilder();
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };
        sslcb.loadTrustMaterial(KeyStore.getInstance(KeyStore.getDefaultType()), new TrustSelfSignedStrategy());

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        cb.setSslcontext(sc);
        cb.setProxy(new HttpHost("asinproxy.ascorp.com", 80, "http"));
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope("asinproxy.ascorp.com", 80), new UsernamePasswordCredentials("repvenk", "@Asd000@"));
        cb.setDefaultCredentialsProvider(credentialsProvider);
        cb.setHostnameVerifier(new X509HostnameVerifier() {
            @Override
            public void verify(String s, SSLSocket sslSocket) throws IOException {}

            @Override
            public void verify(String s, X509Certificate x509Certificate) throws SSLException {}

            @Override
            public void verify(String s, String[] strings, String[] strings1) throws SSLException {}

            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });
        CloseableHttpClient httpclient = cb.build();
        CloseableHttpResponse response;

        HttpPost httpPost = new HttpPost("https://ra-intuat.modeloncloud.com/umg-runtime");
        httpPost.setHeader("Authorization", "Basic " + getRuntimeCredentials());
        httpPost.setHeader("authToken", "equator.a76982d0-311e-49c5-a0a8-fee261e92932");
        HttpEntity httpEntity = new ByteArrayEntity(IOUtils.toByteArray(new FileInputStream("C:\\Users\\repvenk\\Desktop\\eq.json")), ContentType.APPLICATION_JSON);
        httpPost.setEntity(httpEntity);
        response = httpclient.execute(httpPost);
        System.out.println(response.getStatusLine().getStatusCode());
        String myString = IOUtils.toString(response.getEntity().getContent());
        System.out.println(myString);
    }

    private static String getRuntimeCredentials() {
        String username = "admin";
        String pwd = "admin";
        String plainCreds = username + ":" + pwd;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        return new String(base64CredsBytes);
    }*/

}
package com.araditc.uploader.data.api;


import com.araditc.uploader.constants.Phrase;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;
    private static Retrofit retrofitWithoutTokenAndTimeOut = null;
    private static Retrofit retrofitWithOUTTimeOut = null;
    private static Retrofit retrofitWithTimeOut = null;
    private static Retrofit retrofitNoAuth = null;
    private static final int CONNECTION_TIMEOUT = 30;
    private static final int WRITE_CONNECTION_TIMEOUT = 30;
    private static final int READ_CONNECTION_TIMEOUT = 30;

    public static ApiInterface GetClient() {
        if (retrofitWithoutTokenAndTimeOut == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(READ_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request request = chain.request().newBuilder()
                                .addHeader("Accept", "application/json")
                                .build();

                        Response response = chain.proceed(request);

                        return response;
                    })
                    .build();

            retrofitWithoutTokenAndTimeOut = new Retrofit.Builder().baseUrl(Phrase.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return retrofitWithoutTokenAndTimeOut.create(ApiInterface.class);
    }

    public static ApiInterface GetClient(String token) {
        if (retrofitWithTimeOut == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(READ_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request request = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer " + token)
                                .addHeader("Accept", "application/json")
                                .build();

                        Response response = chain.proceed(request);

                        switch (response.code()) {
                            case 401:
//                                App.userLogout();
//                                App.newInstance().restartApplication(MainActivity.newInstance());
                                return response;
                        }

                        return response;
                    })
                    .build();

            retrofitWithTimeOut = new Retrofit.Builder().baseUrl(Phrase.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return retrofitWithTimeOut.create(ApiInterface.class);
    }

    public static ApiInterface GetClient(String token, int connectionTimeOut, int writeTimeOut, int readTimeOut) {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(connectionTimeOut, TimeUnit.SECONDS)
                    .writeTimeout(writeTimeOut, TimeUnit.SECONDS)
                    .readTimeout(readTimeOut, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request request = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer " + token)
                                .addHeader("Accept", "application/json")

                                .build();

                        return chain.proceed(request);
                    })

                    .build();

            retrofit = new Retrofit.Builder().baseUrl(Phrase.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        } else {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(connectionTimeOut, TimeUnit.SECONDS)
                    .writeTimeout(writeTimeOut, TimeUnit.SECONDS)
                    .readTimeout(readTimeOut, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request request = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer " + token)
                                .addHeader("Accept", "application/json")

                                .build();

                        Response response = chain.proceed(request);

                        return response;
                    })

                    .build();

            retrofit.newBuilder()
                    .client(okHttpClient);
        }
        return retrofit.create(ApiInterface.class);
    }

    public static ApiInterface GetClient(int connectionTimeOut, int writeTimeOut, int readTimeOut) {
        if (retrofitNoAuth == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(connectionTimeOut, TimeUnit.SECONDS)
                    .writeTimeout(writeTimeOut, TimeUnit.SECONDS)
                    .readTimeout(readTimeOut, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request request = chain.request().newBuilder()
                                .addHeader("Accept", "application/json")

                                .build();

                        return chain.proceed(request);
                    })

                    .build();

            retrofitNoAuth = new Retrofit.Builder().baseUrl(Phrase.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        } else {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(connectionTimeOut, TimeUnit.SECONDS)
                    .writeTimeout(writeTimeOut, TimeUnit.SECONDS)
                    .readTimeout(readTimeOut, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {
                        Request request = chain.request().newBuilder()
                                .addHeader("Authorization", "Bearer xxx")
                                .addHeader("Accept", "application/json")

                                .build();

                        Response response = chain.proceed(request);

                        return response;
                    })

                    .build();

            retrofitNoAuth.newBuilder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient);
        }
        return retrofitNoAuth.create(ApiInterface.class);
    }

    public static void destroyObjects() {
        retrofit=null;
        retrofitNoAuth=null;
        retrofitWithOUTTimeOut=null;
        retrofitWithoutTokenAndTimeOut=null;
        retrofitWithTimeOut=null;
    }
}

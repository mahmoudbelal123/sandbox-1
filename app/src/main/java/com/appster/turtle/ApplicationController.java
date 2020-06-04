package com.appster.turtle;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.appster.turtle.network.RetrofitRestRepository;
import com.appster.turtle.util.NetworkMonitor;
import com.appster.turtle.util.PreferenceUtil;
import com.appster.turtle.util.StringUtils;
import com.appster.turtle.util.TypefaceUtil;
import com.crashlytics.android.Crashlytics;
import com.danikula.videocache.HttpProxyCacheServer;
import com.github.sahasbhop.apngview.ApngImageLoader;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.FirebaseApp;
import com.orhanobut.hawk.Hawk;

import io.fabric.sdk.android.Fabric;
import java.io.IOException;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@SuppressWarnings("ALL")
public class ApplicationController extends MultiDexApplication {

    private static Context mAppContext;
    private GoogleApiClient mGoogleApiClient;

    public static Context getAppContext() {
        return mAppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        mAppContext = this.getApplicationContext();
        NetworkMonitor.initialize(this);



        Hawk.init(mAppContext).build();
        if (!BuildConfig.DEBUG) {
            CustomActivityOnCrash.setLaunchErrorActivityWhenInBackground(false);
        }

        FirebaseApp.initializeApp(mAppContext);
        ApngImageLoader.getInstance().init(getApplicationContext());

        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "Roboto-Regular.ttf");

    }

    public RetrofitRestRepository provideRepository() {
        return new RetrofitRestRepository(provideRetrofit(true, true));
    }

    public RetrofitRestRepository provideRepository(boolean isLogging) {
        return new RetrofitRestRepository(provideRetrofit(true, isLogging));
    }

    private Retrofit provideRetrofit(boolean isAuth, boolean isLogging) {


        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        if (isAuth) {
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    String accessToken = PreferenceUtil.getToken();

                    if (!StringUtils.isNullOrEmpty(accessToken)) {
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", PreferenceUtil.getToken())
                                .method(original.method(), original.body());
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    } else {
                        return chain.proceed(original);
                    }
                }
            });
        }

        if (isLogging) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);
        }
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(httpClient.build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(gsonConverterFactory)
                .build();
    }


    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        ApplicationController app = (ApplicationController) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this).maxCacheFilesCount(10).build();
    }

}

# Approov Quickstart: Android Java Retrofit

This quickstart is written specifically for native Android apps that are written in Java and use [`Retrofit`](https://square.github.io/retrofit/) for making the API calls that you wish to protect with Approov. If this is not your situation then check if there is a more relevant Quickstart guide available.

This page provides all the steps for integrating Approov into your app. Additionally, a step-by-step tutorial guide using our [Shapes App Example](https://github.com/approov/quickstart-android-java-retrofit/blob/master/SHAPES-EXAMPLE.md) is also available.

To follow this guide you should have received an onboarding email for a trial or paid Approov account.

## ADDING APPROOV SERVICE DEPENDENCY
The Approov integration is available via [`maven`](https://mvnrepository.com/repos/central). This allows inclusion into the project by simply specifying a dependency in the `gradle` files for the app.
The `Maven` repository is already present in the gradle.build file so the only import you need to make is the actual service layer itself:

```
implementation("io.approov:service.retrofit:3.5.1")
```

Make sure you do a Gradle sync (by selecting `Sync Now` in the banner at the top of the modified `.gradle` file) after making these changes.

This package is actually an open source wrapper layer that allows you to easily use Approov with `Retrofit`.  This has a further dependency to the closed source [Approov SDK](https://central.sonatype.com/artifact/io.approov/approov-android-sdk/3.5.1). In some cases you may need to also add this implementation to your dependencies list to avoid build errors:

```
implementation("io.approov:approov-android-sdk:3.5.1")
```

Make sure you do a Gradle sync (by selecting `Sync Now` in the banner at the top of the modified `.gradle` file) after making these changes.

## MANIFEST CHANGES
The following app permissions need to be available in the manifest to use Approov:

```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.INTERNET" />
```

Note that the minimum SDK version you can use with the Approov package is 23 (Android 6.0). 

Please [read this](https://approov.io/docs/latest/approov-usage-documentation/#targeting-android-11-and-above) section of the reference documentation if targeting Android 11 (API level 30) or above.

## INITIALIZING APPROOV SERVICE
In order to use the `ApproovService` you must initialize it when your app is created, usually in the `onCreate` method:

```Java
import io.approov.service.retrofit.ApproovService;

public class YourApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ApproovService.initialize(getApplicationContext(), "<enter-your-config-string-here>");
    }
}
```

The `<enter-your-config-string-here>` is a custom string that configures your Approov account access. This will have been provided in your Approov onboarding email.

## USING APPROOV SERVICE
You can then modify your code that obtains a `RetrofitInstance` to make API calls as follows:

```Java
public class ClientInstance {
    private static final String BASE_URL = "https://your.domain";
    private static Retrofit.Builder retrofitBuilder;
    public static Retrofit getRetrofitInstance() {
        if (retrofitBuilder == null) {
            retrofitBuilder = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());
        }
        return ApproovService.getRetrofit(retrofitBuilder);
    }
}
```

This obtains a retrofit instance includes an `OkHttp` interceptor that protects channel integrity (with either pinning or managed trust roots). The interceptor may also add `Approov-Token` or substitute app secret values, depending upon your integration choices. You should thus use this client for all API calls you may wish to protect.

Approov errors will generate an `ApproovException`, which is a type of `IOException`. This may be further specialized into an `ApproovNetworkException`, indicating an issue with networking that should provide an option for a user initiated retry.

## CUSTOM OKHTTP BUILDER
By default, the Retrofit instance gets a default client constructed with `new OkHttpClient()`. However, your existing code may use a customized `OkHttpClient` with, for instance, different timeouts or other interceptors. For example, if you have existing code:

```Java
OkHttpClient client = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).build();
Retrofit retrofit = new retrofit2.Retrofit.Builder().baseUrl("https://your.domain/").client(client).build();
```
Pass the modified `OkHttp.Builder` to the `ApproovService` as follows:

```Java
ApproovService.setOkHttpClientBuilder(new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS));
Retrofit.Builder retrofitBuilder = new retrofit2.Retrofit.Builder().baseUrl("https://your.domain/");
Retrofit retrofit = ApproovService.getRetrofit(retrofitBuilder);
```

This call to `setOkHttpClientBuilder` only needs to be made once. Subsequent calls to `ApproovService.getRetrofit()` will then always a `OkHttpClient` with the builder values included.

## CHECKING IT WORKS
Initially you won't have set which API domains to protect, so the interceptor will not add anything. It will have called Approov though and made contact with the Approov cloud service. You will see logging from Approov saying `UNKNOWN_URL`.

Your Approov onboarding email should contain a link allowing you to access [Live Metrics Graphs](https://approov.io/docs/latest/approov-usage-documentation/#metrics-graphs). After you've run your app with Approov integration you should be able to see the results in the live metrics within a minute or so. At this stage you could even release your app to get details of your app population and the attributes of the devices they are running upon.

## NEXT STEPS
To actually protect your APIs and/or secrets there are some further steps. Approov provides two different options for protection:

* [API PROTECTION](https://github.com/approov/quickstart-android-java-retrofit/blob/master/API-PROTECTION.md): You should use this if you control the backend API(s) being protected and are able to modify them to ensure that a valid Approov token is being passed by the app. An [Approov Token](https://approov.io/docs/latest/approov-usage-documentation/#approov-tokens) is short lived crytographically signed JWT proving the authenticity of the call.

* [SECRETS PROTECTION](https://github.com/approov/quickstart-android-java-retrofit/blob/master/SECRETS-PROTECTION.md): This allows app secrets, including API keys for 3rd party services, to be protected so that they no longer need to be included in the released app code. These secrets are only made available to valid apps at runtime.

Note that it is possible to use both approaches side-by-side in the same app.

See [REFERENCE](https://github.com/approov/quickstart-android-java-retrofit/blob/master/REFERENCE.md) for a complete list of all of the `ApproovService` methods.

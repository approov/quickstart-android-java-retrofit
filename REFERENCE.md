# Reference
This provides a reference for all of the static methods defined on `ApproovService`. These are available if you import:

```Java
import io.approov.service.retrofit.ApproovService;
```

Various methods may throw an `ApproovException` if there is a problem. The method `getMessage()` provides a descriptive message.

If a method throws an `ApproovNetworkException` (a subclass of `ApproovException`) then this indicates the problem was caused by a networking issue, and a user initiated retry should be allowed.

If a method throws an `ApproovRejectionException` (a subclass of `ApproovException`) the this indicates the problem was that the app failed attestation. An additional method `getARC()` provides the [Attestation Response Code](https://approov.io/docs/latest/approov-usage-documentation/#attestation-response-code), which could be provided to the user for communication with your app support to determine the reason for failure, without this being revealed to the end user. The method `getRejectionReasons()` provides the [Rejection Reasons](https://approov.io/docs/latest/approov-usage-documentation/#rejection-reasons) if the feature is enabled, providing a comma separated list of reasons why the app attestation was rejected.

## Initialize
Initializes the Approov SDK and thus enables the Approov features. The `config` will have been provided in the initial onboarding or email or can be [obtained](https://approov.io/docs/latest/approov-usage-documentation/#getting-the-initial-sdk-configuration) using the approov CLI. This will generate an error if a second attempt is made at initialization with a different `config`.

```Java
void initialize(Context context, String config)
```

The [application context](https://developer.android.com/reference/android/content/Context#getApplicationContext()) must be provided using the `context` parameter.

It is possible to pass an empty `config` string to indicate that no initialization is required. Only do this if you are also using a different Approov quickstart in your app (which will use the same underlying Approov SDK) and this will have been initialized first.

## GetRetrofit
Gets the `Retrofit` instance that enables the Approov service, given a `builder` for it. This adds the Approov token in a header to requests, performs and header or query parameter substitutions and also pins the connections. You *MUST* always obtain the `Retrofit` instance using this method for all requests, to ensure an up to date client is used with the latest dynamic pins.

```Java
Retrofit getRetrofit(Retrofit.Builder builder)
```

If Approov has not been initialized, then this provides an `Retrofit` instance without any Approov protection. Use `setOkHttpClientBuilder` to provide any special properties.

## SetOkHttpClientBuilder
Sets the `OkHttpClient.Builder` to be used for constructing the Approov `Retrofit` instances. This allows a custom configuration to be set, with additional interceptors and properties.

```Java
void setOkHttpClientBuilder(OkHttpClient.Builder builder)
```

## SetProceedOnNetworkFail
If the provided `proceed` value is `true` then this indicates that the network interceptor should proceed anyway if it is not possible to obtain an Approov token due to a networking failure. If this is called then the backend API can receive calls without the expected Approov token header being added, or without header/query parameter substitutions being made. This should only ever be used if there is some particular reason, perhaps due to local network conditions, that you believe that traffic to the Approov cloud service will be particularly problematic.

```Java
void setProceedOnNetworkFail(boolean proceed)
```

Note that this should be used with *CAUTION* because it may allow a connection to be established before any dynamic pins have been received via Approov, thus potentially opening the channel to a MitM.

## SetDevKey
[Sets a development key](https://approov.io/docs/latest/approov-usage-documentation/#using-a-development-key) in order to force an app to be passed. This can be used if the app has to be resigned in a test environment and would thus fail attestation otherwise.

```Java
void setDevKey(String devKey)
```

## SetApproovHeader
Sets the `header` that the Approov token is added on, as well as an optional `prefix` String (such as "`Bearer `"). Set `prefix` to the empty string if it is not required. By default the token is provided on `Approov-Token` with no prefix.

```Java
void setApproovHeader(String header, String prefix)
```

## SetBindingHeader
Sets a binding `header` that may be present on requests being made. This is for the [token binding](https://approov.io/docs/latest/approov-usage-documentation/#token-binding) feature. A header should be chosen whose value is unchanging for most requests (such as an Authorization header). If the `header` is present, then a hash of the `header` value is included in the issued Approov tokens to bind them to the value. This may then be verified by the backend API integration.

```Java
void setBindingHeader(String header)
```

## AddSubstitutionHeader
Adds the name of a `header` which should be subject to [secure strings](https://approov.io/docs/latest/approov-usage-documentation/#secure-strings) substitution. This means that if the `header` is present then the value will be used as a key to look up a secure string value which will be substituted into the `header` value instead. This allows easy migration to the use of secure strings. A `requiredPrefix` may be specified to deal with cases such as the use of "`Bearer `" prefixed before values in an authorization header. Set `requiredPrefix` to `null` if it is not required.

```Java
void addSubstitutionHeader(String header, String requiredPrefix)
```

## RemoveSubstitutionHeader
Removes a `header` previously added using `addSubstitutionHeader`.

```Java
void removeSubstitutionHeader(String header)
```

## AddSubstitutionQueryParam
Adds a `key` name for a query parameter that should be subject to [secure strings](https://approov.io/docs/latest/approov-usage-documentation/#secure-strings) substitution. This means that if the query parameter is present in a URL then the value will be used as a key to look up a secure string value which will be substituted as the query parameter value instead. This allows easy migration to the use of secure strings.

```Java
void addSubstitutionQueryParam(String key)
```

## RemoveSubstitutionQueryParam
Removes a query parameter `key` name previously added using `addSubstitutionQueryParam`.

```Java
void removeSubstitutionQueryParam(String key)
```

## AddExclusionURLRegex
Adds an exclusion URL [regular expression](https://regex101.com/) via the `urlRegex` parameter. If a URL for a request matches this regular expression then it will not be subject to any Approov protection.

```Java
void addExclusionURLRegex(String urlRegex)
```

Note that this facility must be used with *EXTREME CAUTION* due to the impact of dynamic pinning. Pinning may be applied to all domains added using Approov, and updates to the pins are received when an Approov fetch is performed. If you exclude some URLs on domains that are protected with Approov, then these will be protected with Approov pins but without a path to update the pins until a URL is used that is not excluded. Thus you are responsible for ensuring that there is always a possibility of calling a non-excluded URL, or you should make an explicit call to fetchToken if there are persistent pinning failures. Conversely, use of those option may allow a connection to be established before any dynamic pins have been received via Approov, thus potentially opening the channel to a MitM.

## RemoveExclusionURLRegex
Removes an exclusion URL regular expression (`urlRegex`) previously added using `addExclusionURLRegex`.

```Java
void removeExclusionURLRegex(String urlRegex)
```

## Prefetch
Performs a fetch to lower the effective latency of a subsequent token fetch or secure string fetch by starting the operation earlier so the subsequent fetch may be able to use cached data. This initiates the prefetch in a background thread.

```Java
void prefetch()
```

## Precheck
Performs a precheck to determine if the app will pass attestation. This requires [secure strings](https://approov.io/docs/latest/approov-usage-documentation/#secure-strings) to be enabled for the account, although no strings need to be set up. 

```Java
void precheck() throws ApproovException
```

This throws `ApproovException` if the precheck failed. This will likely require network access so may take some time to complete, and should not be called from the UI thread.

## GetDeviceID
Gets the [device ID](https://approov.io/docs/latest/approov-usage-documentation/#extracting-the-device-id) used by Approov to identify the particular device that the SDK is running on. Note that different Approov apps on the same device will return a different ID. Moreover, the ID may be changed by an uninstall and reinstall of the app.

```Java
String getDeviceID() throws ApproovException
```

This throws `ApproovException` if there was a problem obtaining the device ID.

## SetDataHashInToken
Directly sets the [token binding](https://approov.io/docs/latest/approov-usage-documentation/#token-binding) hash to be included in subsequently fetched Approov tokens. If the hash is different from any previously set value then this will cause the next token fetch operation to fetch a new token with the correct payload data hash. The hash appears in the `pay` claim of the Approov token as a base64 encoded string of the SHA256 hash of the data. Note that the data is hashed locally and never sent to the Approov cloud service. This is an alternative to using `setBindingHeader` and you should not use both methods at the same time.

```Java
void setDataHashInToken(String data) throws ApproovException
```

This throws `ApproovException` if there was a problem changing the data hash.

## FetchToken
Performs an Approov token fetch for the given `url`. This should be used in situations where it is not possible to use the networking interception to add the token. Note that the returned token should NEVER be cached by your app, you should call this function when it is needed.

```Java
String fetchToken(String url) throws ApproovException
```

This throws `ApproovException` if there was a problem obtaining an Approov token. This may require network access so may take some time to complete, and should not be called from the UI thread.

## GetMessageSignature
Gets the [message signature](https://approov.io/docs/latest/approov-usage-documentation/#message-signing) for the given `message`. This is returned as a base64 encoded signature. This feature uses an account specific message signing key that is transmitted to the SDK after a successful fetch if the facility is enabled for the account. Note that if the attestation failed then the signing key provided is actually random so that the signature will be incorrect. An Approov token should always be included in the message being signed and sent alongside this signature to prevent replay attacks.

```Java
String getMessageSignature(String message) throws ApproovException
```

This throws `ApproovException` if there was a problem obtaining a signature.

## FetchSecureString
Fetches a [secure string](https://approov.io/docs/latest/approov-usage-documentation/#secure-strings) with the given `key` if `newDef` is `null`. Returns `null` if the `key` secure string is not defined. If `newDef` is not `null` then a secure string for the particular app instance may be defined. In this case the new value is returned as the secure string. Use of an empty string for `newDef` removes the string entry. Note that the returned string should NEVER be cached by your app, you should call this function when it is needed.

```Java
String fetchSecureString(String key, String newDef) throws ApproovException
```

This throws `ApproovException` if there was a problem obtaining the secure string. This may require network access so may take some time to complete, and should not be called from the UI thread.

## FetchCustomJWT
Fetches a [custom JWT](https://approov.io/docs/latest/approov-usage-documentation/#custom-jwts) with the given marshaled JSON `payload`.

```Java
String fetchCustomJWT(String payload) throws ApproovException
```

This throws `ApproovException` if there was a problem obtaining the custom JWT. This may require network access so may take some time to complete, and should not be called from the UI thread.

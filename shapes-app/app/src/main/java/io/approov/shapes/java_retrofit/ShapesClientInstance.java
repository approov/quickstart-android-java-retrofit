//
// MIT License
//
// Copyright (c) 2016-present, Approov Ltd.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
// (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge,
// publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
// ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
// THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package io.approov.shapes.java_retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// *** UNCOMMENT THE LINE BELOW FOR APPROOV ***
//import io.approov.service.retrofit.ApproovService;

public class ShapesClientInstance {
    private static final String BASE_URL = "https://shapes.approov.io/";

    // *** COMMENT THE LINES BELOW WHEN USING APPROOV ***
    private static Retrofit retrofit;
    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // *** UNCOMMENT THE LINES BELOW FOR APPROOV ***
    /*private static Retrofit.Builder retrofitBuilder;
    public static Retrofit getRetrofitInstance() {
        if (retrofitBuilder == null) {
            retrofitBuilder = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());
        }

        // *** UNCOMMENT THE LINE BELOW FOR APPROOV USING SECRETS PROTECTION ***
        //ApproovService.addSubstitutionHeader("Api-Key", null);

        return ApproovService.getRetrofit(retrofitBuilder);
    }*/
}

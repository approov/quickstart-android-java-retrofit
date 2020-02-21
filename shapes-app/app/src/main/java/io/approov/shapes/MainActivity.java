// Main activity for Approov Shapes App Demo (using Retrofit)
//
// MIT License
//
// Copyright (c) 2016-present, Critical Blue Ltd.
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

package io.approov.shapes;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Activity activity;
    private View statusView = null;
    private ImageView statusImageView = null;
    private TextView statusTextView = null;
    private Button connectivityCheckButton = null;
    private Button shapesCheckButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;

        // find controls
        statusView = findViewById(R.id.viewStatus);
        statusImageView = (ImageView) findViewById(R.id.imgStatus);
        statusTextView = findViewById(R.id.txtStatus);
        connectivityCheckButton = findViewById(R.id.btnConnectionCheck);
        shapesCheckButton = findViewById(R.id.btnShapesCheck);

        // handle connection check
        connectivityCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hide status
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        statusView.setVisibility(View.INVISIBLE);
                    }
                });

                // Make a Retrofit request to get hello text
                ShapesService service = ShapesClientInstance.getRetrofitInstance().create(ShapesService.class);
                Call<HelloModel> call = service.getHello();
                call.enqueue(new Callback<HelloModel>() {
                    @Override
                    public void onResponse(Call<HelloModel> call, Response<HelloModel> response) {
                        final int imgId;
                        String message = "Http status code " + response.code();
                        if (response.isSuccessful()) {
                            Log.d(TAG,"Connectivity call successful");
                            imgId = R.drawable.hello;
                            HelloModel hello = response.body();
                            if (hello.getText() != null)
                                message = hello.getText();
                        } else {
                            Log.d(TAG,"Connectivity call unsuccessful");
                            imgId = R.drawable.confused;
                        }
                        final String msg = message;

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statusImageView.setImageResource(imgId);
                                statusTextView.setText(msg);
                                statusView.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<HelloModel> call, Throwable t) {
                        Log.d(TAG, "Connectivity call failed");
                        final int imgId = R.drawable.confused;
                        final String msg = "Request failed: " + t.getMessage();

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statusImageView.setImageResource(imgId);
                                statusTextView.setText(msg);
                                statusView.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });
            }
        });

        // handle getting shapes
        shapesCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hide status
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        statusView.setVisibility(View.INVISIBLE);
                    }
                });

                // Make a Retrofit request to get a shape
                ShapesService service = ShapesClientInstance.getRetrofitInstance().create(ShapesService.class);
                Call<ShapeModel> call = service.getShape();
                call.enqueue(new Callback<ShapeModel>() {
                    @Override
                    public void onResponse(Call<ShapeModel> call, Response<ShapeModel> response) {
                        int imgId = R.drawable.confused;
                        String msg = "Http status code " + response.code();
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Shapes call successful");
                            ShapeModel shape = response.body();
                            if (shape.getShape() != null) {
                                if (shape.getShape().equalsIgnoreCase("square")) {
                                    imgId = R.drawable.square;
                                } else if (shape.getShape().equalsIgnoreCase("circle")) {
                                    imgId = R.drawable.circle;
                                } else if (shape.getShape().equalsIgnoreCase("rectangle")) {
                                    imgId = R.drawable.rectangle;
                                } else if (shape.getShape().equalsIgnoreCase("triangle")) {
                                    imgId = R.drawable.triangle;
                                }
                            }
                        } else {
                            Log.d(TAG, "Shapes call unsuccessful");
                        }

                        final int finalImgId = imgId;
                        final String finalMsg = msg;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statusImageView.setImageResource(finalImgId);
                                statusTextView.setText(finalMsg);
                                statusView.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<ShapeModel> call, Throwable t) {
                        Log.d(TAG, "Shapes call failed");
                        final int imgId = R.drawable.confused;
                        final String msg = "Request failed: " + t.getMessage();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                statusImageView.setImageResource(imgId);
                                statusTextView.setText(msg);
                                statusView.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });

            }
        });
    }
}

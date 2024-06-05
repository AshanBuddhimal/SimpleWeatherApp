package com.example.getweather;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.getweather.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView latLonText, addressText, timeText, weatherText;
    private EditText addressInput;
    private Button searchButton;

    private String apiKey = "d5f53787742f5066d4f917eb124ebb6e";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);

        latLonText = findViewById(R.id.lat_lon_text);
        addressText = findViewById(R.id.address_text);
        timeText = findViewById(R.id.time_text);
        weatherText = findViewById(R.id.weather_text);
        addressInput = findViewById(R.id.address_input);
        searchButton = findViewById(R.id.search_button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = addressInput.getText().toString();
                if (!address.isEmpty()) {
                    geocodeAddress(address);
                }
            }
        });

        updateTime();
    }

    private void geocodeAddress(String address) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                latLonText.setText(String.format("Latitude: %.6f, Longitude: %.6f", latitude, longitude));
                addressText.setText("Address: " + location.getAddressLine(0));

                getWeather(latitude, longitude);
            } else {
                latLonText.setText("Latitude: N/A, Longitude: N/A");
                addressText.setText("Address: Not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
            addressText.setText("Address: Unable to get address");
            Log.e(TAG, "Geocoder IOException: " + e.getMessage(), e);
        }
    }

    private void getWeather(double latitude, double longitude) {
        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=" + apiKey;
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject main = response.getJSONObject("main");
                        double temp = main.getDouble("temp") - 273.15;  // Convert from Kelvin to Celsius
                        weatherText.setText("Weather: " + String.format("%.2f", temp) + " °C");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        weatherText.setText("Weather: Error parsing weather data");
                        Log.e(TAG, "JSONException: " + e.getMessage(), e);
                    }
                }, error -> {
            error.printStackTrace();
            weatherText.setText("Weather: Unable to get weather data");
            Log.e(TAG, "Volley Error: " + error.getMessage(), error);
        });

        queue.add(jsonObjectRequest);
    }

    private void updateTime() {
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        timeText.setText("Current Time: " + currentTime);
    }
}

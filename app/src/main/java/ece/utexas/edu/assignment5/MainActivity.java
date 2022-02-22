package ece.utexas.edu.assignment5;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity
{
    TextView temp,today,humidity,wind,pressure,sunrise,sunset,now,hr1,hr2,hr3,hr4,
    hr5,hr6,hr7,hr8,hr9,hr10,hr11,hr12,dates,highs,lows;
    ImageView leftRain,rightRain,clear,cloud;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        getRequest();
    }

    // Find the IDs of TextViews and ImageViews
    private void findViews()
    {
        // TextViews and ImageViews
        temp = (TextView) findViewById(R.id.temp);
        today = (TextView) findViewById(R.id.today);
        humidity = (TextView) findViewById(R.id.humidity);
        wind = (TextView) findViewById(R.id.wind);
        pressure = (TextView) findViewById(R.id.pressure);
        sunrise = (TextView) findViewById(R.id.sunrise);
        sunset = (TextView) findViewById(R.id.sunset);
        leftRain = (ImageView) findViewById(R.id.leftRain);
        rightRain = (ImageView) findViewById(R.id.rightRain);
        clear = (ImageView) findViewById(R.id.clear);
        cloud = (ImageView) findViewById(R.id.cloud);
        now = (TextView) findViewById(R.id.now);
        hr1 = (TextView) findViewById(R.id.hr1);
        hr2 = (TextView) findViewById(R.id.hr2);
        hr3 = (TextView) findViewById(R.id.hr3);
        hr4 = (TextView) findViewById(R.id.hr4);
        hr5 = (TextView) findViewById(R.id.hr5);
        hr6 = (TextView) findViewById(R.id.hr6);
        hr7 = (TextView) findViewById(R.id.hr7);
        hr8 = (TextView) findViewById(R.id.hr8);
        hr9 = (TextView) findViewById(R.id.hr9);
        hr10 = (TextView) findViewById(R.id.hr10);
        hr11 = (TextView) findViewById(R.id.hr11);
        hr12 = (TextView) findViewById(R.id.hr12);
        dates = (TextView) findViewById(R.id.dates);
        highs = (TextView) findViewById(R.id.highs);
        lows = (TextView) findViewById(R.id.lows);
    }

    // Get a request from the OpenWeatherMap API using Volley
    private void getRequest()
    {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.openweathermap.org/data/2.5/onecall?lat=30.267153&lon=-97.743057&units=imperial&appid=0419e4ae69e8e87afb4af659031868c2";

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response ->
                {
                    try
                    {
                        // Get "current" field from API
                        JSONObject current = new JSONObject(response).getJSONObject("current");

                        // Get data from "current" field
                        long time = current.getLong("dt"); // Current time (in seconds)
                        String todayStr = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(new Date(time * 1000)); // Day of week
                        String tempStr = current.getString("temp") + "°F"; // Temperature
                        String humidityStr = "Humidity\n" + current.getString("humidity") + "%"; // Humidity
                        String windStr = "Wind\n" + current.getString("wind_speed") + "mph"; // Wind speed
                        String pressureStr = "Pressure\n" + current.getString("pressure") + "hPa"; // Pressure

                        long sunriseTime = current.getLong("sunrise"); // Sunrise of current day (in seconds)
                        String sunriseStr = "Sunrise\n" + new SimpleDateFormat("h:mm a", Locale.ENGLISH).format(new Date(sunriseTime * 1000));
                        long sunsetTime = current.getLong("sunset"); // Sunset of current day (in seconds)
                        String sunsetStr = "Sunset\n" + new SimpleDateFormat("h:mm a", Locale.ENGLISH).format(new Date(sunsetTime * 1000));

                        // Set text in corresponding TextViews
                        today.setText(todayStr);
                        temp.setText(tempStr);
                        humidity.setText(humidityStr);
                        wind.setText(windStr);
                        pressure.setText(pressureStr);
                        sunrise.setText(sunriseStr);
                        sunset.setText(sunsetStr);

                        // Get one-word description of current weather from "current" field
                        String weatherStr = current.getJSONArray("weather").getJSONObject(0).getString("main");

                        // Set background image based on current weather
                        switch (weatherStr)
                        {
                            case "Clouds":
                                cloud.setVisibility(View.VISIBLE);
                                leftRain.setVisibility(View.INVISIBLE);
                                rightRain.setVisibility(View.INVISIBLE);
                                clear.setVisibility(View.INVISIBLE);
                                break;
                            case "Rain":
                                leftRain.setVisibility(View.VISIBLE);
                                rightRain.setVisibility(View.VISIBLE);
                                clear.setVisibility(View.INVISIBLE);
                                cloud.setVisibility(View.VISIBLE);
                                break;
                            case "Clear":
                                clear.setVisibility(View.VISIBLE);
                                leftRain.setVisibility(View.INVISIBLE);
                                rightRain.setVisibility(View.INVISIBLE);
                                cloud.setVisibility(View.INVISIBLE);
                                break;
                        }

                        // TextViews for displaying hourly time
                        TextView[] hrs = {now,hr1,hr2,hr3,hr4,hr5,hr6,hr7,hr8,hr9,hr10,hr11,hr12};

                        // Get "hourly" array from API
                        JSONArray hourly = new JSONObject(response).getJSONArray("hourly");

                        // Fill TextViews with times (am/pm) and temperatures
                        for (int i=0; i<hrs.length; i++)
                        {
                            JSONObject hourlyObject = hourly.getJSONObject(i);
                            time = hourlyObject.getLong("dt"); // Time (in seconds)
                            String hrStr = "\n" + new SimpleDateFormat("h a", Locale.ENGLISH).format(new Date(time * 1000))
                                    + "\n" + hourlyObject.getString("temp") + "°F"; // Temperature
                            hrs[i].setText(hrStr);
                        }

                        // Get "daily" array from API
                        JSONArray daily = new JSONObject(response).getJSONArray("daily");

                        StringBuilder datesStr = new StringBuilder().append("\n");
                        StringBuilder highsStr = new StringBuilder().append("\n");
                        StringBuilder lowsStr = new StringBuilder().append("\n");

                        // Fill TextViews with dates, high temps, and low temps
                        for(int i=0; i<8; i++)
                        {
                            JSONObject dailyObject = daily.getJSONObject(i);
                            time = dailyObject.getLong("dt");
                            datesStr.append("   ").append(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(new Date(time * 1000))).append("\n"); // Day of week
                            highsStr.append(dailyObject.getJSONObject("temp").getString("max")).append("°F\n");
                            lowsStr.append(dailyObject.getJSONObject("temp").getString("min")).append("°F\n");
                        }

                        dates.setText(datesStr);
                        highs.setText(highsStr);
                        lows.setText(lowsStr);

                        // Make a new call to the API every minute
                        update();
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    // Runs a timer for 1 minute (60000 milliseconds) that will make another request to the API upon completion
    private void update()
    {
        new CountDownTimer(60000, 1000)
        {
            public void onTick(long millisUntilFinished)
            {
            }
            public void onFinish()
            {
                getRequest();
            }
        }.start();
    }
}
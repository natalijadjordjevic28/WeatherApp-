package com.example.weatherapp;

import static java.nio.channels.FileChannel.open;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weatherapp.httpRequest.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;

public class MainActivity extends AppCompatActivity {
    EditText etCity;
    TextView cityName, temp, description;
    TextView listSize;
    RelativeLayout second;
    LinearLayout linear_layout1, linear_layout2;
    ImageView imageofWeather;
    ArrayList<CityNameList>listName;
    private String API="99b49767bbec6963917a299fc2f46410";
    private String CITY;
    private Button submitButton,remove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCity = (EditText) findViewById(R.id.cityName);
        submitButton=(Button) findViewById(R.id.submit);
        listSize=(TextView) findViewById(R.id.list_size);
        second=(RelativeLayout)findViewById(R.id.second);
        cityName= (TextView) findViewById(R.id.nameCITY);
        temp = (TextView) findViewById(R.id.temp);
        linear_layout1= (LinearLayout)findViewById(R.id.linear_layout1);
        linear_layout2= (LinearLayout)findViewById(R.id.linear_layout2);
        description = (TextView) findViewById(R.id.description);
        imageofWeather = (ImageView) findViewById(R.id.imageView);

        loadData();
        linear_layout1.setVisibility(View.VISIBLE);
        linear_layout2.setVisibility(View.GONE);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CITY = etCity.getText().toString();
                new weatherTask().execute();
                linear_layout1.setVisibility(View.GONE);
                linear_layout2.setVisibility(View.VISIBLE);
                saveData(CITY);
            }

        });
    }

    private void loadData() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("DATA",MODE_PRIVATE);
        Gson gson= new Gson();
        String json= preferences.getString("name_data", null);
        Type type= new TypeToken<ArrayList<CityNameList>>(){

        }.getType();
        listName= gson.fromJson(json, type);

        if (listName== null){
            listName= new ArrayList<>();
        }else{
            for (int i = listName.size() - 1; i > listName.size() - 11; i--){
              listSize.setText(listSize.getText().toString()+ "\n"+ listName.get(i).getName() + "\n");
            }
        }
    }

    private void saveData(String city) {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("DATA",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();

        Gson gson= new Gson();
        listName.add(new CityNameList(city));
        String json= gson.toJson(listName);
        editor.putString("name_data",json);
        editor.commit();
        editor.apply();
        loadData();

    }

    class weatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected String doInBackground(String... strings) {
            String response = HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?q="
                              + CITY + "&units=metric&appid=" + API);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {

                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

// CALL VALUE IN API :
                String city_name = jsonObj.getString("name");
                String cast = weather.getString("main");
                String temperature = main.getString("temp");
                String icon = weather.getString("icon");

// SET ALL VALUES IN TEXTBOX :
                cityName.setText(city_name);
                description.setText(cast);
                temp.setText(temperature + "°C");
                Picasso.get().load("https://openweathermap.org/img/wn/"+icon+"@2x.png").into(imageofWeather);

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Error:" + e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
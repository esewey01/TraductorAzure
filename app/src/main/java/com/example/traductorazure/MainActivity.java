package com.example.traductorazure;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String KEY = "14D8wQ82PWssQjg01BBEgSonSyX3E7QRhZA2DNiyynMOeCOrnEJmJQQJ99ALACYeBjFXJ3w3AAAbACOGNHwg";
    private static final String ENDPOINT = "https://api.cognitive.microsofttranslator.com";
    private static final String LOCATION = "eastus";
    private static final String TAG = "TranslatorApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText inputText = findViewById(R.id.inputText);
        Button translateButton = findViewById(R.id.translateButton);
        TextView translatedText = findViewById(R.id.translatedText);

        translateButton.setOnClickListener(v -> {
            String textToTranslate = inputText.getText().toString();
            if (textToTranslate.isEmpty()) {
                Toast.makeText(this, "Por favor ingrese texto para traducir", Toast.LENGTH_SHORT).show();
            } else {
                translateText(textToTranslate, translatedText);
            }
        });
    }

    private void translateText(String text, TextView translatedText) {
        String path = "/translate";
        String constructedUrl = ENDPOINT + path + "?api-version=3.0&from=es&to=fr&to=en";

        Log.d(TAG, "Constructed URL: " + constructedUrl);

        RequestQueue queue = Volley.newRequestQueue(this);

        try {
            JSONArray requestBody = new JSONArray();
            JSONObject textObject = new JSONObject();
            textObject.put("text", text);
            requestBody.put(textObject);

            Log.d(TAG, "Request Body: " + requestBody.toString());

            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST,
                    constructedUrl,
                    response -> {
                        Log.d(TAG, "Respuesta de la API: " + response);
                        try {
                            // La respuesta es un JSONArray que contiene un objeto con la clave "translations"
                            JSONArray responseArray = new JSONArray(response);
                            JSONObject firstObject = responseArray.getJSONObject(0); // El primer objeto de la respuesta
                            JSONArray translations = firstObject.getJSONArray("translations"); // Accedemos a "translations"




                            StringBuilder result = new StringBuilder();
                            for (int i = 0; i < translations.length(); i++) {
                                JSONObject translation = translations.getJSONObject(i);
                                result.append(translation.getString("text")).append("\n");
                            }
                            translatedText.setText(result.toString());
                        } catch (JSONException e) {
                            Log.e(TAG, "ERROR AL PROCESAR LA RESPUESTA: ", e);
                            Toast.makeText(MainActivity.this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        Log.e(TAG, "Error connecting to API: ", error);
                        Toast.makeText(MainActivity.this, "Error al conectar con la API", Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Ocp-Apim-Subscription-Key", KEY);
                    headers.put("Ocp-Apim-Subscription-Region", LOCATION);
                    headers.put("Content-type", "application/json");
                    headers.put("X-ClientTraceId", UUID.randomUUID().toString());
                    Log.d(TAG, "Headers: " + headers.toString());
                    return headers;
                }

                @Override
                public byte[] getBody() {
                    Log.d(TAG, "Datos enviados a la API: " + requestBody.toString());
                    return requestBody.toString().getBytes();
                }
            };

            queue.add(stringRequest);

        } catch (JSONException e) {
            Log.e(TAG, "Error building request body: ", e);
            Toast.makeText(this, "Error al construir el cuerpo de la solicitud", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.meuclienteandroid;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final static String IP = "";
    private final static String PORT = "";

    private static final String SERVICE_URL = "http://" + IP + ":" + PORT + "/MeuWebServer/webresources/controller";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void convert(View vw) {

        postData(vw);
        getData(vw);

    }

    public void getData(View vw) {

        WebServiceTask wstask = new WebServiceTask(WebServiceTask.GET, this);

        wstask.execute(new String[] {SERVICE_URL + "/convert_temperature"});
    }

    public void postData(View vw) {

        EditText editTemperature = (EditText) findViewById(R.id.edtTemperature);

        String temperature = editTemperature.getText().toString();

        WebServiceTask wstask = new WebServiceTask(WebServiceTask.POST, this);

        wstask.addPair("value", temperature);
        wstask.execute(new String[] {SERVICE_URL});

    }

    public void manageResponse(String response) {

        EditText editTemperature = (EditText) findViewById(R.id.edtTemperature);

        editTemperature.setText("");

        try {
            JSONObject jo = new JSONObject(response);
            String temperature = jo.getString("value");
            editTemperature.setText(temperature);

        } catch (Exception e) {

            Log.e("MainActivity", response);

        }

    }

    private class WebServiceTask extends AsyncTask<String, Integer, String> {

        public static final int POST = 1;
        public static final int GET = 2;
        private int taskType;

        private Context context = null;

        private ArrayList<NameValuePair> pair = new ArrayList<NameValuePair>();

        private ProgressDialog popWait = null;

        public WebServiceTask(int taskType, Context context) {

            this.taskType = taskType;
            this.context = context;
        }

        public void addPair(String name, String value) {

            pair.add(new BasicNameValuePair(name, value));
        }

        //antes de executar
        @Override
        protected void onPreExecute() {
            popWait = new ProgressDialog(context);
            popWait.setMessage("Aguarde...");
            popWait.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            popWait.setCancelable(false);
            popWait.show();

        }

        //Depois da execuçao
        @Override
        protected void onPostExecute(String result) {

            manageResponse(result);
            popWait.dismiss();

        }

        //Executado em uma thread em background
        @Override
        protected String doInBackground(String... urls) {

            String url = urls[0];
            String result = "";

            HttpResponse response = null;
            HttpClient httpClient = new DefaultHttpClient(getHttpParameters());

            try {
                switch (taskType) {
                    case POST:
                        HttpPost httpPost = new HttpPost(url);

                        httpPost.setEntity(new UrlEncodedFormEntity(pair));
                        response = httpClient.execute(httpPost);
                        break;

                    case GET:
                        HttpGet httpGet = new HttpGet(url);

                        response = httpClient.execute(httpGet);
                        break;
                }
            }catch (Exception e) {
                Log.e("WebServiceTask", e.getLocalizedMessage());
            }

            if (response != null) {

                try {

                    result = convertInputString(response.getEntity().getContent());

                }catch (Exception e) {
                    Log.e("webServiceTask", e.getLocalizedMessage());
                }
            }

            return result;
        }

        //Configurar parametros
        private HttpParams getHttpParameters() {

            HttpParams parameters = new BasicHttpParams();

            HttpConnectionParams.setConnectionTimeout(parameters, 5000);
            HttpConnectionParams.setSoTimeout(parameters, 5000);

            return parameters;
        }

        private String convertInputString(InputStream input) {

            String line = "";
            StringBuilder fullString = new StringBuilder();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            try {
                while ((line = reader.readLine()) != null) {

                    fullString.append(line);
                }

            }catch (Exception e) {
                Log.e("WebServiceTask", e.getLocalizedMessage());
            }

            return fullString.toString();
        }

    }

}

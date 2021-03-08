package com.example.stonks;

import android.net.Uri;
import android.util.Log;

import com.example.stonks.stocks;
import com.example.stonks.MainActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class stockDownloader implements Runnable {

    private static final String TAG = "stockDownloader";
    private static final String STOCK_URL = "https://cloud.iexapis.com/stable/stock/";
    private static final String STOCK_URL2 = "/quote?token=pk_fb998e95b76243578b9d3695d4a211ad";
    //pk_fb998e95b76243578b9d3695d4a211ad
    private MainActivity mainActivity;
    private String searchTarget;
    private int x;

    public stockDownloader(MainActivity mainActivity, String searchTarget, int x) {
        this.mainActivity = mainActivity;
        this.searchTarget = searchTarget;
        this.x = x;
    }

    @Override
    public void run() {
        Uri.Builder uriBuilder = Uri.parse(STOCK_URL + searchTarget + STOCK_URL2).buildUpon();
        //uriBuilder.appendQueryParameter("fullText", "true");
        String urlToUse = uriBuilder.toString();

        Log.d(TAG, "run: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + conn.getResponseCode());
                return;
            }

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "run: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
            return;
        }

        process(sb.toString());
        Log.d(TAG, "run: ");

    }

    private void process(String s) {
        try {
          //  JSONArray jArray = new JSONArray(s);
            JSONObject jStock = new JSONObject(s);//jArray.get(0);

            String name = jStock.getString("companyName");
            String symbol = jStock.getString("symbol");
            //String change = jCountry.getString("change");
            //String value = jCountry.getString("value");
            //String subRegion = jCountry.getString("subregion");

            String c = jStock.getString("change");
            //int chng = 0;
            if (c.trim().isEmpty()|| c.trim().equals("null"))
              c = "0";

            String p = jStock.getString("changePercent");
            //int percent = 0;
            if (p.trim().isEmpty()|| p.trim().equals("null"))
                p= "0";

            String v = jStock.getString("latestPrice");
            //int val = 0;
            if (v.trim().isEmpty() && v.trim().equals("null"))
                v = "0";
            //String a = jCountry.getString("area");
            //int area = 0;
            //if (!a.trim().isEmpty() && !a.trim().equals("null"))
              //  area = (int) Double.parseDouble(a);

            //String citizen = jCountry.getString("demonym");

            //StringBuilder codes = new StringBuilder();
            //JSONArray jCodes = jCountry.getJSONArray("callingCodes");
            //for (int j = 0; j < jCodes.length(); j++) {
              //  codes.append(jCodes.get(j)).append(" ");
            //}

            //StringBuilder borders = new StringBuilder();
            //JSONArray jBorders = jCountry.getJSONArray("borders");
            //for (int j = 0; j < jBorders.length(); j++) {
            //    borders.append(jBorders.get(j)).append(" ");
           // }

            if (x == 0) {
                final stocks stock = new stocks(name, symbol,
                        v, c, p);
                //, region, subRegion, area, citizen, codes.toString(), borders.toString());

                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.addStock(stock);
                    }
                });
            }
            else if(x == 1){
               final stocks stock = new stocks(name, symbol,
                        v, c, p);
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.updateStockList(stock);
                    }
                });
            }

        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
    }


}

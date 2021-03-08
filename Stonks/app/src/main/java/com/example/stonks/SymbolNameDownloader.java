package com.example.stonks;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class SymbolNameDownloader implements Runnable {
    private static final String TAG = "SymbolNameDownloader";
    //we will keep all the downloaded content in the hash map so it is easily accessible
    private static HashMap<String, String> symbolNameMap = new HashMap<>();
    private MainActivity mainActivity;
    private static final String DATA_URL = "https://api.iextrading.com/1.0/ref-data/symbols";

    SymbolNameDownloader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
    public void run(){
        Uri dataUri = Uri.parse(DATA_URL);
        String urlToUse = dataUri.toString();
        Log.d(TAG, "run: "+urlToUse);

        StringBuilder sb = new StringBuilder();
        try{
            URL url = new URL(urlToUse);
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("GET");
            connect.connect();
            //if connection is not established
            if(connect.getResponseCode()!= HttpURLConnection.HTTP_OK){
                //log problem
                Log.d(TAG, "run: HTTP ResponseCode OK: "+connect.getResponseCode());
                //pass null
                process(null);
                //end the method because we need a connection
                mainActivity.runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        mainActivity.showDownloaderError();
                    }
                });
                return;
            }
            //if connection is established
            InputStream is = connect.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            //if multiple lines avaliable
            String line;
            //as long as line is not empty we will continue to append to our string builder
            while((line = reader.readLine())!= null){
                sb.append(line).append('\n');
            }
            Log.d(TAG, "run: "+ sb.toString());

        } catch(Exception e){
            Log.e(TAG, "run: ", e);
            process(sb.toString());
            //handleResults(null);
            return;

        }
        //if all is successful then send results
        process(sb.toString());
        //handleResults(sb.toString());
    }
    //this will transfer data into the hashmap table
    private void process(String s){
    try {
        JSONArray job = new JSONArray(s);
        for(int i = 0; i < job.length(); i++) {
            JSONObject jStock = (JSONObject) job.get(i);
            String symbol = jStock.getString("symbol");
            String name = jStock.getString("name");
            symbolNameMap.put(symbol, name);
        }
        Log.d(TAG, "process: ");
        } catch(Exception e){
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static ArrayList<String> findMatches(String str){
    String strToMatch = str.toLowerCase().trim();
    HashSet<String> matchSet = new HashSet<>();
    for(String sym : symbolNameMap.keySet()){
        if(sym.toLowerCase().trim().contains(strToMatch)){
            matchSet.add(sym + "-"+symbolNameMap.get(sym));
        }
        String name = symbolNameMap.get(sym);
        if (name != null &&
                name.toLowerCase().trim().contains(strToMatch)) {
            matchSet.add(sym + " - " + name);
        }
    }
    ArrayList<String> results = new ArrayList<>(matchSet);
        Collections.sort(results);

        return results;
    }

/*
    private void handleResults(String s){
        if (s==null){
            Log.d(TAG, "handleResults: Failure in data download");
            mainActivity.runOnUiThread(mainActivity.downloadFailed(){
            return;});
            }
        final ArrayList<stocks> stockList = parseJSON(s);
        mainActivity.runOnUiThread({
                if(stockList != null){
                    Toast.makeText(mainActivity, "Loaded "+stockList.size(), Toast.LENGTH_LONG);
        });
    }*/
    }


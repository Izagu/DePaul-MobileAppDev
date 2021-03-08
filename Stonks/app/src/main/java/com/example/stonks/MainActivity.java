package com.example.stonks;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.InputType;
import android.util.JsonWriter;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener{

    private static final String TAG = "MainActivity";
    private List<stocks> stockList = new ArrayList<>();  // Main content is here
    private RecyclerView recyclerView;
    private stockAdapter mAdapter;
    private String choice;
    //private List holding;
    private SwipeRefreshLayout swiper;
    private MainActivity mainAct;
    final String LIST = "list";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "MainAct on save");

        recyclerView = findViewById(R.id.recycler);
        mAdapter = new stockAdapter(stockList, this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //stockList = new ArrayList<>();


        // Load the initial data
        SymbolNameDownloader rd = new SymbolNameDownloader(this);
        new Thread(rd).start();
        //refresh
        swiper = findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
                mAdapter.notifyDataSetChanged();
                swiper.setRefreshing(false);
                Toast toast = Toast.makeText(getApplicationContext(), "up to date", Toast.LENGTH_LONG);
                toast.show();
            }
        });
        //holding = stockList;
        readJSONData();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuAddItem) {
            makeStockDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View v) {  // click listener called by ViewHolder clicks. this should go to website of stock.
        int pos = recyclerView.getChildLayoutPosition(v);
        stocks c = stockList.get(pos);
        String sym = c.getSymbol();
        String webPage = "https://www.marketwatch.com/investing/stock/" + sym;
            //create browser intent with premade url
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webPage));
       // Intent intent = new Intent(MainActivity.this, CountryDetailActivity.class);
       // intent.putExtra(stocks.class.getName(), c);
        startActivity(browserIntent);
        //https://www.marketwatch.com/investing/stock/fb
    }
    @Override
    public boolean onLongClick(View v) {  // long click listener called by ViewHolder long clicks
        final int pos = recyclerView.getChildLayoutPosition(v);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setIcon(R.drawable.delete);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                stockList.remove(pos);
                mAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // doesnt need to do anything.
            }
        });

        builder.setMessage("Delete " + stockList.get(pos).getName() + "?");
        builder.setTitle("Delete Selection");

        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    public void updateStock(String sym){
        String[] data = sym.split("-");
        stockDownloader stockDownloader = new stockDownloader(this, data[0].trim(), 1);
        new Thread(stockDownloader).start();
    }
    public void updateStockList (stocks stock){
        for(stocks s: stockList){
            if (s.getSymbol() == stock.getSymbol()){
                stockList.remove(s);
                stockList.add(stock);
            }
        }


    }
       // mAdapter.notifyDataSetChanged();

    private void doRefresh() {
        //Collections.shuffle(employeeList);
        //mAdapter.notifyDataSetChanged();
        //final ArrayList<String> new = SymbolNameDownloader.findMatches(choice);
        for(stocks s: stockList){
            String find = s.getSymbol();
            final ArrayList<String> newList = SymbolNameDownloader.findMatches(find);
            if (newList.size() == 0) {
                doNoAnswer(find);
            } else if (newList.size() == 1) {
                updateStock(newList.get(0));
            }
            //stockDownloader stockDownloader = new stockDownloader(this, s.getSymbol(), 1);
            //new Thread(stockDownloader).start();
        }
        return;
        //Collections.sort(stockList);
    }

    private void makeStockDialog() {

        if (!checkNetworkConnection()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Content Cannot Be Added Without A Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT);
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);

        builder.setView(et);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                choice = et.getText().toString().trim();

                final ArrayList<String> results = SymbolNameDownloader.findMatches(choice);

                if (results.size() == 0) {
                    doNoAnswer(choice);
                } else if (results.size() == 1) {
                    doSelection(results.get(0));
                } else {
                    String[] array = results.toArray(new String[0]);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Make a selection");
                    builder.setItems(array, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String symbol = results.get(which);
                            doSelection(symbol);
                        }
                    });
                    builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    AlertDialog dialog2 = builder.create();
                    dialog2.show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        builder.setMessage("Please enter a Symbol or Name:");
        builder.setTitle("Stock Selection");

        AlertDialog dialog = builder.create();
        dialog.show();

    }
    private boolean checkNetworkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    private void doNoAnswer(String symbol) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("No data for specified symbol/name");
        builder.setTitle("No Data Found: " + symbol);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void doSelection(String sym) {
        String[] data = sym.split("-");
        stockDownloader stockDownloader = new stockDownloader(this, data[0].trim(), 0);
        new Thread(stockDownloader).start();
    }
    public void addStock(stocks stock){
        if(stock == null){
            badDataAlert(choice);
            return;
        }
        if (stockList.contains(stock)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage(stock.getName() + " is already displayed");
            builder.setTitle("Duplicate Stock");
            builder.setIcon(R.drawable.error);

            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        stockList.add(stock);
        Collections.sort(stockList);
        mAdapter.notifyDataSetChanged();
    }


    private void readJSONData() {

        try {
            FileInputStream fis = getApplicationContext().
                    openFileInput(getString(R.string.data_file));

            // Read string content from file
            byte[] data = new byte[fis.available()]; // this technique is good for small files
            int loaded = fis.read(data);
            Log.d(TAG, "readJSONData: Loaded " + loaded + " bytes");
            fis.close();
            String json = new String(data);

            // Create JSON Array from string file content
            JSONArray noteArr = new JSONArray(json);
            for (int i = 0; i < noteArr.length(); i++) {
                JSONObject cObj = noteArr.getJSONObject(i);

                String name = cObj.getString("name");
                String symbol = cObj.getString("symbol");
                //String capital = cObj.getString("capital");
                String value = cObj.getString("value");
                //String region = cObj.getString("region");
                //String subRegion = cObj.getString("subRegion");
                String change = cObj.getString("change");
                String percent = cObj.getString("percent");
                //String citizen = cObj.getString("citizen");
               // String callingCodes = cObj.getString("callingCodes");
                //String borders = cObj.getString("borders");

                // Create stocks and add to ArrayList
                stocks c = new stocks(name, symbol, value, change, percent);
                stockList.add(c);
            }
            mAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //take a break come back. fix writejsondata

    private void writeJSONData() {

        try {
            FileOutputStream fos = getApplicationContext().
                    openFileOutput(getString(R.string.data_file), Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8));
            writer.setIndent("  ");
            writer.beginArray();
            for (stocks c : stockList) {
                writer.beginObject();

                writer.name("name").value(c.getName());
                writer.name("symbol").value(c.getSymbol());
                writer.name("change").value(c.getChange());
                writer.name("value").value(c.getValue());
                writer.name("percent").value(c.getPercent());
               // writer.name("subRegion").value(c.getSubRegion());
                //writer.name("area").value(c.getArea());
                //writer.name("citizen").value(c.getCitizen());
                //writer.name("callingCodes").value(c.getCallingCodes());
                //writer.name("borders").value(c.getBorders());
                //writer.endObject();
            }
            writer.endArray();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "writeJSONData: " + e.getMessage());
        }
    }


   // public void updateData(ArrayList<stocks>slist){
     //   stockList.clear(slist);
    //    mAdapter.notifyDataSetChanged();
   // }
   private void badDataAlert(String sym) {
       AlertDialog.Builder builder = new AlertDialog.Builder(this);

       builder.setMessage("No data for selection");
       builder.setTitle("Symbol Not Found: " + sym);

       AlertDialog dialog = builder.create();
       dialog.show();
   }

   //need an on pause
    protected void onPause(){
        super.onPause();
        writeJSONData();
    }

    //public void downloadFailed(){
     //   stockList.clear();
     //   mAdapter.notifyDataSetChanged();
     //   Toast.makeText(this, "Download Failed", Toast.LENGTH_LONG);
    //}
    public void showDownloaderError(){
        Toast.makeText(this, "Failed to download symbol/names", Toast.LENGTH_LONG).show();
        //makeText.show();
    }
}
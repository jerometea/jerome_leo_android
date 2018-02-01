package com.example.jerome.bookstore;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BookListActivity extends AppCompatActivity {
    public RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        rv = findViewById(R.id.rv_biere);
        rv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        GetBiersService.startActionBiers(this);

        // Broadcast slide 35
        IntentFilter intentFilter = new IntentFilter(BIERS_UPDATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BookListActivity.BierUpdate(),intentFilter);

        JSONArray dataBiers = this.getBiersFromFile();

        BookListActivity.BiersAdapter ba = new BookListActivity.BiersAdapter(dataBiers);
        rv.setAdapter(ba);

    }
    class BiersAdapter extends RecyclerView.Adapter<BookListActivity.BiersAdapter.BierHolder> {

        public JSONArray getBiers() {
            return biers;
        }

        private JSONArray biers;

        public BiersAdapter(JSONArray dataBiers){
            this.biers = dataBiers;
        }

        @Override
        public void onBindViewHolder(BookListActivity.BiersAdapter.BierHolder holder, int position) {
            try {
                JSONObject b =  this.biers.getJSONObject(position);
                holder.name.setText(b.getString("name"));
            }
            catch(JSONException e ){
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return this.biers.length();
        }

        @Override
        public BookListActivity.BiersAdapter.BierHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater li = LayoutInflater.from(parent.getContext());
            View v = li.inflate(R.layout.rv_bier_element, null, false);
            BookListActivity.BiersAdapter.BierHolder bHolder = new BookListActivity.BiersAdapter.BierHolder(v);
            return bHolder;
        }

        public void setNewBiere (JSONArray biers){
            this.biers = biers;
            notifyDataSetChanged();
        }

        class BierHolder extends RecyclerView.ViewHolder {
            public TextView name;
            public BierHolder(View itemView) {
                super(itemView);
                this.name = itemView.findViewById(R.id.rv_bier_element_name);
            }
        }
    }

    public static final String BIERS_UPDATE = "com.octip.cours.inf4042_11.BIERS_UPDATE";
    class BierUpdate extends BroadcastReceiver {
        @Override
        public void onReceive (Context context, Intent intent) {
            Log.d("biers", "display bieres");
            ((BookListActivity.BiersAdapter)rv.getAdapter()).setNewBiere(getBiersFromFile());
        }
    }

    public JSONArray getBiersFromFile(){
        try {
            InputStream is = new FileInputStream(getCacheDir() + "/" + "bieres.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            return new JSONArray(new String(buffer, "UTF-8")); // construction du tableau
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONArray();
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }
}
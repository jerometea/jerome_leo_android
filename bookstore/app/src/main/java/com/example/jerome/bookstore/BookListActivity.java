package com.example.jerome.bookstore;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        JSONObject dataBiers = this.getBiersFromFile();

        BookListActivity.BiersAdapter ba = new BookListActivity.BiersAdapter(dataBiers);
        rv.setAdapter(ba);

        Snackbar snackbar = Snackbar.make(rv.getRootView(), "Pokemon list downloaded", Snackbar.LENGTH_LONG);
        snackbar.show();


        final Intent emptyIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(BookListActivity.this, 0, emptyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        initChannels(BookListActivity.this);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(BookListActivity.this, "default")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Youhou")
                .setContentText("lol")
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());

    }
    class BiersAdapter extends RecyclerView.Adapter<BookListActivity.BiersAdapter.BierHolder> {

        public JSONObject getBiers() {
            return biers;
        }

        private JSONObject biers;
        private JSONArray pokemons;

        public BiersAdapter(JSONObject dataBiers){
            this.biers = dataBiers;
            try {
                this.pokemons = this.biers.getJSONArray("results");
            }
            catch(JSONException e ){
                e.printStackTrace();
            }
        }

        @Override
        public void onBindViewHolder(BookListActivity.BiersAdapter.BierHolder holder, int position) {
            try {
                JSONArray ja =  this.biers.getJSONArray("results");
                this.pokemons = ja;
                JSONObject b =  ja.getJSONObject(position);

                holder.name.setText(b.getString("name"));
            }
            catch(JSONException e ){
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return this.pokemons.length();
        }

        @Override
        public BookListActivity.BiersAdapter.BierHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater li = LayoutInflater.from(parent.getContext());
            View v = li.inflate(R.layout.rv_bier_element, null, false);
            BookListActivity.BiersAdapter.BierHolder bHolder = new BookListActivity.BiersAdapter.BierHolder(v);
            return bHolder;
        }

        public void setNewBiere (JSONObject biers){
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

    public static void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("default",
                "PokeData favorites",
                NotificationManager.IMPORTANCE_LOW);
        channel.setDescription("Channel for PokeData favorites Pokémon");
        notificationManager.createNotificationChannel(channel);
    }

    public static final String BIERS_UPDATE = "com.octip.cours.inf4042_11.BIERS_UPDATE";
    class BierUpdate extends BroadcastReceiver {
        @Override
        public void onReceive (Context context, Intent intent) {
            Log.d("biers", "display bieres");
            ((BookListActivity.BiersAdapter)rv.getAdapter()).setNewBiere(getBiersFromFile());
        }
    }

    public JSONObject getBiersFromFile(){
        try {
            InputStream is = new FileInputStream(getCacheDir() + "/" + "bieres.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            return new JSONObject(new String(buffer, "UTF-8")); // construction du tableau
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONObject();
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }
}

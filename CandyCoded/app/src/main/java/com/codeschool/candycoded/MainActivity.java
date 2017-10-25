package com.codeschool.candycoded;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codeschool.candycoded.CandyContract.CandyEntry;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import cz.msebera.android.httpclient.Header;



public class MainActivity extends AppCompatActivity {
    private Candy[] candies;
    private CandyDbHelper candyDbHelper = new CandyDbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = (TextView)this.findViewById(R.id.text_view_title);
        textView.setText(R.string.products_title);

        SQLiteDatabase db = candyDbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM candy", null);

        final CandyCursorAdapter adapter = new CandyCursorAdapter(this, cursor);

        ListView listView = (ListView)this.findViewById(R.id.list_view_candy);

        listView.setAdapter(adapter);

        Context context = this;
        String text = "Hello toast!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
                detailIntent.putExtra("position", i);
                startActivity(detailIntent);
            }
        });

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://s3.amazonaws.com/courseware.codeschool.com/super_sweet_android_time/API/CandyCoded.json",
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String response, Throwable throwable) {
                        Log.e("AsyncHttpClient", "response = " + response);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String response) {
                        Log.d("AsyncHttpClient", "response = " + response);
                        Gson gson = new GsonBuilder().create();;
                        candies = gson.fromJson(response, Candy[].class);

                        addCandiesToDatabase(candies);

                        SQLiteDatabase db = candyDbHelper.getWritableDatabase();
                        Cursor cursor = db.rawQuery("SELECT * FROM candy", null);
                        adapter.changeCursor(cursor);
                    }
                });
    }

    private void addCandiesToDatabase(Candy[] candies) {
        SQLiteDatabase db = candyDbHelper.getWritableDatabase();

        if (candies == null) return;
        else db.delete(CandyEntry.TABLE_NAME, null, null);

        for (Candy candy : candies) {
            ContentValues values = new ContentValues();
            values.put(CandyEntry.COLUMN_NAME_NAME, candy.name);
            values.put(CandyEntry.COLUMN_NAME_PRICE, candy.price);
            values.put(CandyEntry.COLUMN_NAME_DESC, candy.description);
            values.put(CandyEntry.COLUMN_NAME_IMAGE, candy.image);

            db.insert(CandyEntry.TABLE_NAME, null, values);
        }
    }
}

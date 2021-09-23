package com.example.quicksell;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;

public class ProductActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private ArrayList<String> productID = new ArrayList<>();
    private ProductAdapter productAdapter;
    private ArrayList<Product> products = new ArrayList<>();
    private final String URL = "http://35.154.26.203/product-ids";
    private ProgressBar progressBar;

    public ProductActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list_view);
        recyclerView = findViewById(R.id.product_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        new fetchProductIds().execute(URL);
        productAdapter = new ProductAdapter(this, products);
        recyclerView.setAdapter(productAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.divider));
        recyclerView.addItemDecoration(dividerItemDecoration);
        progressBar = findViewById(R.id.progressBar);
    }

    private class fetchProductIds extends AsyncTask<String, String, String> {
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder buffer = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                buffer.deleteCharAt(buffer.length() - 1);
                buffer.deleteCharAt(0);
                String[] separated = buffer.toString().split(",");
                for (String s : separated) {
                    s = s.substring(1);
                    s = s.substring(0, s.length() - 1);
                    productID.add(s);
                }
                for (int i = 0; i < productID.size(); i++) {
                    getProducData(productID.get(i));
                }
                return buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    public void getProducData(String productId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String productName = "-", productPrice = "-", productDesc = "-", productImg = "";
                if (snapshot.child("product-name").child(productId).getValue() != null) {
                    productName = snapshot.child("product-name").child(productId).getValue().toString();
                }
                if (snapshot.child("product-desc").child(productId).getValue() != null) {
                    productDesc = snapshot.child("product-desc").child(productId).getValue().toString();
                }
                if (snapshot.child("product-price").child(productId).getValue() != null) {
                    productPrice = snapshot.child("product-price").child(productId).getValue().toString();
                }

                if (snapshot.child("product-image").child(productId).getValue() != null) {
                    productImg = snapshot.child("product-image").child(productId).getValue().toString();
                }
                products.add(new Product(productName, productDesc, productPrice, productImg));
                productAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("MIKE", error.getDetails());
            }
        });
    }

}

package com.example.booklisting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ImageButton imageButton;
    private EditText editText;
    private ListView listView;
    private wordAdapter itemsAdapter;
    LinearLayout searchLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageButton=findViewById(R.id.imageButton);
        editText=findViewById(R.id.editText);
        searchLayout=findViewById(R.id.searchLayout);

        itemsAdapter = new wordAdapter(getApplicationContext(),new ArrayList<word>());
        listView=findViewById(R.id.list);
        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = itemsAdapter.getItem(position).getPreviewLink();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query=editText.getText().toString();
                Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
                searchLayout.setVisibility(View.GONE);
                fetchDataAsyncTask a=new fetchDataAsyncTask();
                a.execute("https://www.googleapis.com/books/v1/volumes?q="+query+"&maxResults=40");
            }
        });
    }
    //----------------------------------------------------------------------------------AsyncTask
    private class fetchDataAsyncTask extends AsyncTask<String,Void, ArrayList<word>> {
        @Override
        protected ArrayList<word> doInBackground(String... strings) {
            if(strings.length<1||strings[0]==null){
                return null;
            }
            return QueryUtils.fetchData(strings[0]);
        }
        @Override
        protected void onPostExecute(ArrayList<word> data) {
            // Clear the adapter of previous earthquake data
            itemsAdapter.clear();

            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                itemsAdapter.addAll(data);
            }
        }
    }

    @Override
    public void onBackPressed() {
        itemsAdapter.clear();
        searchLayout.setVisibility(View.VISIBLE);
        //super.onBackPressed();
    }
}

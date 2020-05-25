package com.example.booklisting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class wordAdapter extends ArrayAdapter<word> {
    public wordAdapter(@NonNull Context context, @NonNull List<word> objects) {
        super(context,0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //-----------------------------------
        View listItemView=convertView;
        if(listItemView==null){
            listItemView= LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);
        }
        word currentword=getItem(position);
        //-----------------------------------


        ImageView imageView=listItemView.findViewById(R.id.imageView);
        new ImageLoadTask(currentword.getThumbnail(), imageView).execute();

        TextView title=listItemView.findViewById(R.id.title);
        title.setText(currentword.getTitle());

        TextView authors=listItemView.findViewById(R.id.authors);
        authors.setText(currentword.getAuthors());

        TextView publisher=listItemView.findViewById(R.id.publisher);
        publisher.setText(currentword.getPublisher());

        TextView publishDate=listItemView.findViewById(R.id.publishDate);
        publishDate.setText(currentword.getPublishedDate());

        TextView description=listItemView.findViewById(R.id.description);
        description.setText(currentword.getDescription());

        TextView amount=listItemView.findViewById(R.id.amount);
        amount.setText(currentword.getAmount());

        return listItemView;
    }

    //--------------------------------------------------------------------------------loading bitmap
    private class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {
        private String url;
        private ImageView imageView;
        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }
        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap myBitmap = null;
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                myBitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return myBitmap;
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }
    }
}

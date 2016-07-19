package my.jsonparsing;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import my.jsonparsing.models.MovieModel;

public class MainActivity extends AppCompatActivity {

    TextView tvData;
    private ListView lvMovies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

      //tvData =(TextView)findViewById(R.id.jsondata);

        lvMovies = (ListView)findViewById(R.id.listMovies);


        Button btnhit = (Button) findViewById(R.id.btnHit);
        btnhit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               //new JSONTask().execute("http://jsonparsing.parseapp.com/jsonData/moviesDemoList.txt");
                new JSONTask().execute("http://jsonparsing.parseapp.com/jsonData/moviesData.txt");
               // new JSONTask().execute("http://jsonparsing.parseapp.com/jsonData/moviesDemoItem.txt");
            }
        });


    }


   public class JSONTask extends AsyncTask<String, String, List<MovieModel>>{

       @Override
       protected List<MovieModel> doInBackground(String... params) {



           HttpURLConnection connection = null;
           BufferedReader reader = null;

           try {
               URL url = new URL(params[0]);
               connection = (HttpURLConnection) url.openConnection();
               connection.connect();

               InputStream stream = connection.getInputStream();
               reader = new BufferedReader(new InputStreamReader(stream));
               StringBuffer buffer = new StringBuffer();

               String line = "";
               while ((line = reader.readLine()) != null) {
                   buffer.append(line);
               }

               String finalJson = buffer.toString();

               JSONObject parentObject = new JSONObject(finalJson);
               JSONArray parentArray = parentObject.getJSONArray("movies");

               //StringBuffer finalBufferData = new StringBuffer();

               List<MovieModel>movieModelList = new ArrayList<>();

               for (int i=0; i<parentArray.length(); i++) {
                   JSONObject finalObject = parentArray.getJSONObject(i);
                   //String moviename = finalObject.getString("movie");
                   //int year = finalObject.getInt("year");
                   MovieModel movieModel = new MovieModel();
                   movieModel.setMovie(finalObject.getString("movie"));
                   movieModel.setYear(finalObject.getInt("year"));
                   movieModel.setRating((float)finalObject.getDouble("rating"));
                   movieModel.setDuration(finalObject.getString("duration"));
                   movieModel.setDirector(finalObject.getString("director"));
                   movieModel.setTagline(finalObject.getString("tagline"));

                   movieModel.setImage(finalObject.getString("image"));
                   movieModel.setStory(finalObject.getString("story"));

                   List<MovieModel.Cast> castList = new ArrayList<>();
                   for (int j=0; j<finalObject.getJSONArray("cast").length(); j++){
                       JSONObject castObject = finalObject.getJSONArray("cast").getJSONObject(j);
                       MovieModel.Cast cast = new MovieModel.Cast();
                       cast.setName(castObject.getString("name"));
                       castList.add(cast);

                   }
                   movieModel.setCastList(castList);
                   // adding the final object in the List
                   movieModelList.add(movieModel);


                   //finalBufferData.append(moviename+ " "+ year + "\n");
               }
                //return moviename +" - "+year;
               //return finalBufferData.toString();
               return movieModelList;


               //....................The complete JSON..............................................
              //return buffer.toString();


           } catch (MalformedURLException e) {

           } catch (IOException e) {
               e.printStackTrace();
           } catch (JSONException e) {
               e.printStackTrace();
           } finally {
               if ((connection != null)){
                   connection.disconnect();
               }

               try {
                   if (connection != null){
                       reader.close();
                   }

               } catch (IOException e) {
                   e.printStackTrace();
               }
           }


           return null;

       }


       protected void onPostExecute(List<MovieModel> result) {
           super.onPostExecute(result);
           //tvData.setText(result);

           MovieAdapter adapter = new MovieAdapter(getApplicationContext(),R.layout.row,result);
           lvMovies.setAdapter(adapter);


       }
   }


    public class MovieAdapter extends ArrayAdapter{

        private List<MovieModel>movieModelList;
        private int resource;
        private LayoutInflater inflater;


        public MovieAdapter(Context context, int resource, List<MovieModel> objects) {
            super(context, resource, objects);
            movieModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null){
                convertView = inflater.inflate(R.layout.row,null);

            }

            ImageView tvImage;
            TextView tvMovie;
            TextView tvYear;
            TextView tvDuration;
            TextView tvTagline;
            TextView tvDirector;

            RatingBar tvRatingbar;
            TextView tvCast;
            TextView tvStory;

// image not set
            tvImage = (ImageView) convertView.findViewById(R.id.tvImage);

            tvMovie = (TextView)convertView.findViewById(R.id.ivmoviename);
            tvYear = (TextView)convertView.findViewById(R.id.ivyear);
            tvDuration = (TextView)convertView.findViewById(R.id.ivDuration);
            tvTagline = (TextView)convertView.findViewById(R.id.ivTagline);
            tvDirector = (TextView)convertView.findViewById(R.id.tvDirector);
            tvRatingbar = (RatingBar)convertView.findViewById(R.id.tvratingBar);
            tvCast = (TextView)convertView.findViewById(R.id.tvCast);
            tvStory = (TextView)convertView.findViewById(R.id.ivStory);

            tvMovie.setText(movieModelList.get(position).getMovie());
            tvYear.setText("Year: " +movieModelList.get(position).getYear());
            tvDuration.setText("Duration: " +movieModelList.get(position).getDuration());
            tvTagline.setText(movieModelList.get(position).getTagline());
            tvDirector.setText("Director: "+ movieModelList.get(position).getDirector());
            tvRatingbar.setRating(movieModelList.get(position).getRating()/2);

            StringBuffer stringBuffer = new StringBuffer();

            for (MovieModel.Cast cast : movieModelList.get(position).getCastList()){
                stringBuffer.append(cast.getName()+",");

            }

            tvCast.setText("Cast:"+ stringBuffer);
            tvStory.setText(movieModelList.get(position).getStory());


            return convertView;
        }
    }



}

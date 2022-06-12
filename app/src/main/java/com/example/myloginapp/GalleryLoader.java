package com.example.myloginapp;

import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myloginapp.Description.DesReviewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.Executor;

public class GalleryLoader extends AsyncTask<String, Long, String> {

    public static JSONObject jsonObject;
    JSONArray jsonArray;
    static int Initcnt;
    static int DBcnt;
    DesReviewAdapter desReviewAdapter;
    RecyclerView recyclerView;
    HttpURLConnection httpURLConnection;
    int responseStatusCode;
    OutputStream outputStream;
    InputStream inputStream;

    @SuppressLint("WrongThread")
    @Override
    public String doInBackground(String... params) {

        String serverURL = (String) params[0];
        String postParameters = (String) params[1];
        try {
            URL url = new URL(serverURL);
            httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.connect();

            outputStream = httpURLConnection.getOutputStream();
            outputStream.write(postParameters.getBytes("UTF-8"));
            outputStream.flush();
            outputStream.close();

            responseStatusCode = httpURLConnection.getResponseCode();

            if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
            } else {
                inputStream = httpURLConnection.getErrorStream();
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            Initcnt = showResult(sb.toString());
            ImageLoader image = new ImageLoader();
            image.execute();
            Collections.sort(Object.art, new ArtComparator());
            bufferedReader.close();

        } catch (Exception e) {
            return new String("Error: " + e.getMessage());
        }
        return null;
    }

    private int showResult(String mJsonString) {

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("ArtTable");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);

                String no = item.getString("artnum");
                String name = item.getString("artname");
                Log.v("art",item.getString("artname"));
                String urlStr = item.getString("url");
                String StartPeriod = item.getString("StartPeriod").replace(" ", "");
                String EndPeriod = item.getString("EndPeriod").replace(" ", "");

                String Price = item.getString("Price");
                String Explanation = item.getString("Explanation");
                String ExhibitionNum = item.getString("exhibitionnum");

                Log.v("art", no+ExhibitionNum);

                ArrayList<DesReviewInfo> desReviewInfo = new ArrayList<>();  //제가 추가한 것
                //바로 밑에 주석부분 반복문돌려서 리뷰 값 넣어주세요.
                //desReviewInfo.add(new DesReviewInfo(item.getInt("star"),item.getString("reviewTitle"),item.getString("reviewEvaluation")));
                desReviewInfo.add(new DesReviewInfo(1, "abc", "def")); //임의로 넣어본 것. 나중에 지워주셈
                desReviewInfo.add(new DesReviewInfo(5, "ㄹㄹ", "ㄴㅇㄹ")); //임의로 넣어본 것

                EndPeriod = EndPeriod.replace(".", "-");

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date currentTime = new Date();
                try {
                    Date date = format.parse(EndPeriod);
                    //Log.v("tag", String.valueOf(date));
                    if (currentTime.before(date)) {
                       // Log.v("tag", name);
                        // StartPeriod,String EndPeriod,String Price,String image,String explanation,int ExhibitionNum,ArrayList<DesReviewInfo> desReviewInfo
                        Object.art.add(new GalleryInfo(Integer.parseInt(no),name,StartPeriod,EndPeriod,Price,urlStr,Explanation,0,desReviewInfo));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return DBcnt;
    }



    //정렬기능을 수행하기 위한 클래스로, 임시로 이곳에 배치
    //도영님이 정렬 페이지를 만들어 올리는 즉시 그쪽으로 이동예정.
    class ArtComparator implements Comparator<GalleryInfo> {
        @Override
        public int compare(GalleryInfo o1, GalleryInfo o2) {
            String[] str1 = o1.getStartPeriod().split("-");
            String[] str2 = o2.getStartPeriod().split("-");

            if (str1.length < 2 || str2.length < 2)
                return -1;
            if (Integer.parseInt(str1[0]) == Integer.parseInt(str2[0])) {
                if (Integer.parseInt(str1[1]) == Integer.parseInt(str2[1])) {
                    return Integer.parseInt(str1[2]) - Integer.parseInt(str2[2]);
                }
                return Integer.parseInt(str1[1]) - Integer.parseInt(str2[1]);
            } else
                return Integer.parseInt(str1[0]) - Integer.parseInt(str2[0]);
        }
    }

    public class ImageLoader extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {

                for (int i = 0; i < Object.art.size(); i++) {
                    URL url = new URL(Object.art.get(i).getUrl());
                    Object.art.get(i).setImage(BitmapFactory.decodeStream(url.openConnection().getInputStream()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
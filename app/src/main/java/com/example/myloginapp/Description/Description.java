package com.example.myloginapp.Description;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myloginapp.Object;
import com.example.myloginapp.R;
import com.example.myloginapp.Review.ReviewActivity;
import com.example.myloginapp.DesReviewInfo;

import java.util.ArrayList;

public class Description extends AppCompatActivity {


    ArrayList<DesReviewInfo> arrayList;
    DesReviewAdapter desReviewAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;


    int position;


    ImageView imageView;
    TextView NameText;
    TextView TimeText;
    TextView InfoText;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.description);

        imageView=findViewById(R.id.descImageView);
        NameText=findViewById(R.id.artTitle);
        TimeText =findViewById(R.id.artTime);
        InfoText=findViewById(R.id.artInfo);



        Intent intent = getIntent(); //액티비티전환해서 넘어올때 해당 intent를 받는다.

        if(intent.getExtras()!=null){ //Description.java로 액티비티전환시 넘길때 액티비티의 값을 받음

            position=(int)intent.getSerializableExtra("ObjectPosition"); // position값받음
            Log.v("art",Integer.toString(position));
            imageView.setImageBitmap(Object.art.get(position).getImage());
            NameText.setText(Object.art.get(position).getName());
            TimeText.setText(Object.art.get(position).PrintArt());
            InfoText.setText(Object.art.get(position).getDesc());
        }

        //밑에는 리사이클러뷰 관련
        recyclerView=(RecyclerView) findViewById(R.id.dec_review); //description.xml에서따옴
        linearLayoutManager=new LinearLayoutManager(this,RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        arrayList= Object.art.get(position).getDesReviewInfo();

        desReviewAdapter=new DesReviewAdapter(arrayList);
        recyclerView.setAdapter(desReviewAdapter);

        button = (Button) findViewById(R.id.review_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Description.this, ReviewActivity.class);
                startActivity(intent);
            }
        });


    }


}

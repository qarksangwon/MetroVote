package com.inhatc.metrovote;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.BaseRequestOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseUser;
import com.inhatc.metrovote.singletone.UserDataSingleton;


public class MyPageActivity extends AppCompatActivity {
    private TextView txtName;
    private TextView txtEmail;
    private ImageView profileImageView;

    private Button btnQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        txtName = findViewById(R.id.txtName);
        txtEmail     = findViewById(R.id.txtEmail);
        profileImageView = findViewById(R.id.profileImgView);
        btnQR = findViewById(R.id.btnQR);

        FirebaseUser user = UserDataSingleton.getInstance().getUser();


        txtName.setText(user.getDisplayName());
        txtEmail.setText(user.getEmail());
        if (user.getPhotoUrl() != null) {
            String photoUrl = user.getPhotoUrl().toString();
            BaseRequestOptions<com.bumptech.glide.request.RequestOptions> requestOptions = new RequestOptions()
                    .placeholder(R.drawable.loading)  // 로딩 중에 표시할 이미지 리소스
                    .error(R.drawable.error)  // 로딩 실패 시 표시할 이미지 리소스
                    .fitCenter();  // 이미지를 가운데에 맞추기

            Glide.with(this)
                    .load(photoUrl)
                    .apply(requestOptions)
                    .into(profileImageView);
        }

        btnQR.setOnClickListener(new View.OnClickListener(){
                @Override
            public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), QrActivity.class);
                    startActivity(intent);
            }
        });
    }


}

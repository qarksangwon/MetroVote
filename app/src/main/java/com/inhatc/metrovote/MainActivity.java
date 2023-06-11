package com.inhatc.metrovote;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.recaptcha.RecaptchaTasksClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.inhatc.metrovote.singletone.UserDataSingleton;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 9001;

    @Nullable
    private RecaptchaTasksClient recaptchaTasksClient = null;

    // 구글api클라이언트
    private GoogleSignInClient mGoogleSignInClient;

    // 구글 계정
    private GoogleSignInAccount gsa;

    // 파이어베이스 인증 객체 생성
    private FirebaseAuth mAuth;

    // 구글  로그인 버튼
    private SignInButton btnGoogleLogin;
    private Button btnLogoutGoogle;

    private Button btnResCaptcha;
    private Button btnInsCaptcha;
    private String captchaText;
    private ImageView captchaImage;
    private String imagePath;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //잠시 실행용
    Intent intent = new Intent(getApplicationContext(), Report.class);
    startActivity(intent);


        captchaText = CAPTCHA.createCaptchaValue();     //초기 캡챠값 설정
        captchaImage = findViewById(R.id.captchaImage); //캡챠 이미지 출력 뷰 설정
        
        btnResCaptcha = (Button) findViewById(R.id.btnResCaptcha);    //리셋 버튼
        btnInsCaptcha = (Button) findViewById(R.id.btnInsertCaptcha); //입력 버튼

        Drawable drawable = TextToImg.generateImage(captchaText, MainActivity.this,300,150);  //초기 캡챠 이미지 불러오기
        if (drawable != null) {
            captchaImage.setImageDrawable(drawable);  //초기 캡챠 이미지 설정
        }
        

        /* 캡챠 리셋 버튼 리스너*/
        btnResCaptcha.setOnClickListener(new View.OnClickListener(){ 
            @Override
            public void onClick(View v) {
                captchaText = CAPTCHA.createCaptchaValue();
                Drawable drawable = TextToImg.generateImage(captchaText, MainActivity.this,300,150);
                if (drawable != null) {
                    captchaImage.setImageDrawable(drawable);
                }
            }
        });

        /* 캡챠 입력 버튼 리스너 */
        btnInsCaptcha.setOnClickListener(new View.OnClickListener(){  
            @Override
            public void onClick(View v) {
                String insertCaptcha = ((EditText)findViewById(R.id.edtTextCaptcha)).getText().toString();

                if(captchaText.equals(insertCaptcha)) { // 캡챠 일치시
                    Toast.makeText(MainActivity.this, "캡차가 일치합니다!", Toast.LENGTH_SHORT).show();
                    btnGoogleLogin.setEnabled(true);
                    btnInsCaptcha.setEnabled(false);
                    btnResCaptcha.setEnabled(false);
                } else { //캡챠 불일치시
                    Toast.makeText(MainActivity.this, "캡차가 일치하지 않습니다.!", Toast.LENGTH_SHORT).show();
                    
                    captchaText = CAPTCHA.createCaptchaValue(); //캡챠 재 생성
                    Drawable drawable = TextToImg.generateImage(captchaText, MainActivity.this,300,150);
                    if (drawable != null) {
                        captchaImage.setImageDrawable(drawable);
                    }
                }
                ((EditText)findViewById(R.id.edtTextCaptcha)).setText("");
            }
        });

        // 파이어베이스 인증 객체 선언
        mAuth = FirebaseAuth.getInstance();

        // Google 로그인을 앱에 통합
        // GoogleSignInOptions 개체를 구성할 때 requestIdToken을 호출
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        btnGoogleLogin = findViewById(R.id.btn_google_sign_in);
        btnGoogleLogin.setEnabled(false);

        btnGoogleLogin.setOnClickListener(view -> {
            // 기존에 로그인 했던 계정을 확인한다.
            gsa = GoogleSignIn.getLastSignedInAccount(MainActivity.this);

            if (gsa != null) { // 로그인 되있는 경우
                Toast.makeText(MainActivity.this, R.string.status_login, Toast.LENGTH_SHORT).show();
            } else {
                signIn();
            }

        });
        btnLogoutGoogle = findViewById(R.id.btn_logout_google);
        btnLogoutGoogle.setOnClickListener(view -> {
            signOut(); //로그아웃
        });
    }



    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    /* 사용자 정보 가져오기 */
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);

            if (acct != null) {
                firebaseAuthWithGoogle(acct.getIdToken());

                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();

                Log.d(TAG, "handleSignInResult:personName "+personName);
                Log.d(TAG, "handleSignInResult:personGivenName "+personGivenName);
                Log.d(TAG, "handleSignInResult:personEmail "+personEmail);
                Log.d(TAG, "handleSignInResult:personId "+personId);
                Log.d(TAG, "handleSignInResult:personFamilyName "+personFamilyName);
                Log.d(TAG, "handleSignInResult:personPhoto "+personPhoto);
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }


    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        Toast.makeText(MainActivity.this, R.string.success_login, Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        UserDataSingleton.getInstance().setUser(user);
                        Intent intent = new Intent(MainActivity.this, MyPageActivity.class);
                        startActivity(intent);
//                            updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(MainActivity.this, R.string.failed_login, Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {

    }

    /* 로그아웃 */
    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    mAuth.signOut();
                    Toast.makeText(MainActivity.this, R.string.success_logout, Toast.LENGTH_SHORT).show();
                    // ...
                });
        gsa = null;
    }

    /* 회원 삭제요청 */
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, task -> {
                    // ...
                });
    }



}
package com.inhatc.metrovote;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TextToImg {
    public static Drawable generateImage(String text, Context context, int imageWidth, int imageHeight) {
        Drawable drawable = null;

        // 폰트 설정
        int fontResourceId = R.font.courier_new;
        Typeface font = ResourcesCompat.getFont(context, fontResourceId);

        // 텍스트 사이즈 및 패딩 설정
        int textSize = 48;
        int padding = 10;

        // 텍스트를 그릴 때 필요한 Paint 객체 생성
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.create(font, Typeface.BOLD_ITALIC)); // 기울임을 적용

        // 텍스트의 너비와 높이 계산
        float textWidth = paint.measureText(text);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float textHeight = fontMetrics.bottom - fontMetrics.top;

        // 비트맵 생성 및 캔버스에 텍스트 그리기
        Bitmap bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.translate(imageWidth / 2, imageHeight / 2); // 캔버스 중앙으로 이동
        canvas.drawText(text, -textWidth / 2, textHeight / 2 - fontMetrics.bottom, paint); // 텍스트 그리기

        // 이미지 파일 저장 경로
        String fileName = "captchaText.png";

        // 이미지 생성 및 저장
        try {
            // 비트맵 저장
            FileOutputStream fileOutputStream = new FileOutputStream(new File(context.getFilesDir(), fileName));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.close();

            // Drawable 리소스로 등록
            String filePath = context.getFilesDir() + File.separator + fileName;
            drawable = Drawable.createFromPath(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return drawable;
    }
}




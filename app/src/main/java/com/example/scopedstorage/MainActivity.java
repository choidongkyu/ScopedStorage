package com.example.scopedstorage;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    Button createMediaButton, createExternalButton, deleteExternalButton;
    String fileName = "text1.txt";
    String testValue = "this is test file\n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createMediaButton = findViewById(R.id.create_bt);
        createExternalButton = findViewById(R.id.create_bt2);
        deleteExternalButton = findViewById(R.id.delete_bt);
        createMediaButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                //audio 폴더를 이용할것이므로 audio로 지정
                values.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName);
                values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/*");
                //파일 write 중이라는 것을 알려줌, 다른파일 접근 무시
                values.put(MediaStore.Audio.Media.IS_PENDING, 1);

                ContentResolver contentResolver = getContentResolver();
                Uri collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

                //ContentResolver에 설정된 값들을 insert해주고 uri를 리턴 받음
                //contentResolver.delete(collection,null);
                Uri item = contentResolver.insert(collection, values);
                if(item == null) {
                    Log.d("dkchoi", "item = null");
                    return ;
                }

                try {
                    //item의 위치에 파일을 생성
                    ParcelFileDescriptor parcelFileDescriptor = contentResolver.openFileDescriptor(item, "w", null);
                    FileOutputStream fos = new FileOutputStream(parcelFileDescriptor.getFileDescriptor());
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
                    //생성된 파일에 내용 입력
                    writer.write(testValue);
                    writer.flush();
                    writer.close();
                    fos.close();
                    contentResolver.update(item, values, null, null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                values.clear();
                //파일을 모두 write했음을 알려줌
                values.put(MediaStore.Audio.Media.IS_PENDING, 0);
                contentResolver.update(item, values, null, null);
            }
        });

        
        //app별로 저장되는 외부저장소 파일 생성
        createExternalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
                    //생성된 파일에 내용 입력
                    writer.write(testValue);
                    writer.flush();
                    writer.close();
                    fos.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        deleteExternalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName);
                if(file.exists()) {
                    file.delete();
                }
            }
        });


    }
}
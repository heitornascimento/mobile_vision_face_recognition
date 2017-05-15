package com.ac.blog.camera;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ac.blog.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, FragmentCameraPreview.newInstance())
                    .commit();
        }
    }
}

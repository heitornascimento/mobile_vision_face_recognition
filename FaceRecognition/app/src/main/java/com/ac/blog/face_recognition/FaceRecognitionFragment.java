package com.ac.blog.face_recognition;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ac.blog.R;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.io.File;

/**
 * Created by hsouza on 5/14/17.
 */

public class FaceRecognitionFragment extends Fragment {

    ImageView mImageView;
    TextView mLoading;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.result_face_recognition, container, false);
        mImageView = (ImageView) view.findViewById(R.id.image);
        mLoading = (TextView) view.findViewById(R.id.loading);

        Bundle bundle = getArguments();
        if (bundle != null) {
            File file = (File) bundle.getSerializable("image");
            if (file != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable = true;
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                new Handler().post(new WorkerThread(bitmap));
            }
        }

        return view;
    }

    private class WorkerThread implements Runnable {
        Bitmap bitmap;

        public WorkerThread(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        public void run() {
            callFaceRecognitionAPI(bitmap);
        }
    }

    private void callFaceRecognitionAPI(Bitmap imageBitmap) {

        Paint myRectPaint = new Paint();
        myRectPaint.setStrokeWidth(5);
        myRectPaint.setColor(Color.RED);
        myRectPaint.setStyle(Paint.Style.STROKE);

        Canvas canvas = new Canvas(imageBitmap);
        canvas.drawBitmap(imageBitmap, 0, 0, null);

        FaceDetector faceDetector = new
                FaceDetector.Builder(getActivity())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();
        if (!faceDetector.isOperational()) {
            new AlertDialog.Builder(getActivity()).setMessage("Could not set up the face detector!").show();
            return;
        }

        Frame frame = new Frame.Builder().setBitmap(imageBitmap).build();
        SparseArray<Face> faces = faceDetector.detect(frame);

        Bitmap acBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ac_circle);

        for (int i = 0; i < faces.size(); i++) {
            Face face = faces.valueAt(i);
            for (Landmark landmark : face.getLandmarks()) {
                int cx = (int) (landmark.getPosition().x);
                int cy = (int) (landmark.getPosition().y);
                float radius = 10.0f;

                if (landmark.getType() == Landmark.LEFT_EYE || landmark.getType() == Landmark.RIGHT_EYE) {
                    canvas.drawBitmap(acBitmap, cx - 100, cy - 100, null);
                } else {
                    canvas.drawCircle(cx, cy, radius, myRectPaint);
                }
            }
        }
        mLoading.setVisibility(View.GONE);
        mImageView.setImageDrawable(new BitmapDrawable(getResources(), imageBitmap));
    }


}

package com.example.whereis.image;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.widget.Toast;

import com.example.whereis.helpers.MLImageHelperActivity;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import java.io.File;

public class ImageClassificationActivity extends MLImageHelperActivity {
    private ImageLabeler imageLabeler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLabeler = ImageLabeling.getClient(new ImageLabelerOptions.Builder()
                .setConfidenceThreshold(0.7f)
                .build());
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.'));
    }

    @Override
    protected void runDetection(Bitmap bitmap, File photoFile) {
        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
        imageLabeler.process(inputImage).addOnSuccessListener(imageLabels -> {
            StringBuilder newFileNameBuilder = new StringBuilder();
           StringBuilder sb = new StringBuilder();
           if(imageLabels.isEmpty()){
               sb.append("CouldNotClassify");
               //Don't rename the file. The timestamp is better info than "CouldNotClassify"
           } else {
               for (ImageLabel label : imageLabels) {
                   sb.append(label.getText()).append(": ").append(label.getConfidence()).append("\n");
                   newFileNameBuilder.append(label.getText());
               }
               //Edit the editText from the parent and hope that it calls rename text
               String resultBaseName = newFileNameBuilder.toString().substring(0, Math.min(newFileNameBuilder.length(), 100));
               getRenameEditText().setText(resultBaseName);
               renameFile(resultBaseName);
           }

           if (imageLabels.isEmpty()) {
               getOutputTextView().setText("Could not classify!!");
           } else {
               getOutputTextView().setText(sb.toString());
           }
        }).addOnFailureListener(e -> {
            e.printStackTrace();
        });
    }
}

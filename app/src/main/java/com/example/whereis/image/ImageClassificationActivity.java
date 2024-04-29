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

    private void renameFile(File photoFile, String name){
        final String baseName = name; //Gotta cap off the characters
        final String extension = getExtension(photoFile.getName());
        int counter = 1;
        File renamedFile = new File(photoFile.getParent(), baseName);
        while (renamedFile.exists()) {
            counter++;
            renamedFile = new File(photoFile.getParent(), baseName + counter + extension);
        }

        if (!photoFile.renameTo(renamedFile)) {
            // Show an error toast
            Toast.makeText(this, "Couldn't rename pic!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Renamed file to: " + baseName, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void runDetection(Bitmap bitmap, File photoFile) {
        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
        imageLabeler.process(inputImage).addOnSuccessListener(imageLabels -> {
            StringBuilder newFileNameBuilder = new StringBuilder();
           StringBuilder sb = new StringBuilder();
           if(imageLabels.isEmpty()){
               sb.append("CouldNotClassify");
           } else {
               for (ImageLabel label : imageLabels) {
                   sb.append(label.getText()).append(": ").append(label.getConfidence()).append("\n");
                   newFileNameBuilder.append(label.getText());
               }
               renameFile(photoFile, newFileNameBuilder.toString().substring(0,Math.min(newFileNameBuilder.length(), 100)));
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

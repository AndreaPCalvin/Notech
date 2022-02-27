package com.example.notech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private TextView textView;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap imageBitmap = null;
    String currentPhotoPath = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image_view);
        textView = findViewById(R.id.text_view);
        Button takeImageButton = findViewById(R.id.take_image_button);
        Button detectTextButton = findViewById(R.id.detect_text_button);

        takeImageButton.setOnClickListener(view -> dispatchTakePictureIntent());


        detectTextButton.setOnClickListener(view -> {
            if (imageBitmap == null) Toast.makeText(MainActivity.this, "¡Error! Antes de reconocer el texto, haga una imagen.", Toast.LENGTH_SHORT).show();
            else recogniseTextFromImage();
        });
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Si hay algún componente de actividad que pueda manejar la imagen
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "Error creando la imagen.", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.notech.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageBitmap = BitmapFactory.decodeFile(currentPhotoPath);
            imageView.setImageBitmap(imageBitmap);
        }
    }

    private void recogniseTextFromImage() {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        InputImage image = InputImage.fromBitmap(imageBitmap, 0);
        recognizer.process(image)
                        .addOnSuccessListener(visionText -> {
                            // Task completed successfully
                            // ...
                            // Ampliar siguiendo el ejemplo de la página de ML Kit
                            String recognizedText = visionText.getText();
                            textView.setText(recognizedText);
                        })
                        .addOnFailureListener(
                                e -> {
                                    // Task failed with an exception
                                    // ...
                                });

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


}
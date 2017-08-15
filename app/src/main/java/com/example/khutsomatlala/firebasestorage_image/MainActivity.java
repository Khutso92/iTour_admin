package com.example.khutsomatlala.firebasestorage_image;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private ImageView imageView;
    private EditText txtImageName, txtDescription, txtCategory, txtLatitude, txtLongitude;
    private Uri imgUri;
    private Button uploadPic;
    private TextView tvwarning;

    public static final String FB_STORAGE_PATH = "images/";
    public static final String FB_DATABASE_PATH = "images";
    public static final int REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);

        imageView = (ImageView) findViewById(R.id.ImageView);

        uploadPic = (Button) findViewById(R.id.btnUploadPic);

        tvwarning = (TextView) findViewById(R.id.warning);

        txtImageName = (EditText) findViewById(R.id.txtImageName);
        txtDescription = (EditText) findViewById(R.id.txtDescription);
        txtLatitude = (EditText) findViewById(R.id.txtLatitude);
        txtLongitude = (EditText) findViewById(R.id.txtLongitude);
        txtCategory = (EditText) findViewById(R.id.txtCategory);


    }



    //Browse image to upload
    public void btnBrowe_Click(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select image"), REQUEST_CODE);

        tvwarning.setVisibility(View.GONE);


    }

    //After browsing and uploading the pic ,the result are passed over here
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();

            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                imageView.setImageBitmap(bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public String getIamgeExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    //Uploading image
    @SuppressWarnings("VisibleForTests")
    public void btnUpload_Click(View view) {


        if (txtImageName.length() > 0 &&
                txtDescription.length() > 0 &&
                txtLatitude.length() > 0 &&
                txtLongitude.length() > 0 &&
                txtCategory.length() > 0) {

            if (imgUri != null) {
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle("saving");
                dialog.show();

                //Get the storage reference
                StorageReference ref = mStorageRef.child(FB_STORAGE_PATH + System.currentTimeMillis() + "." + getIamgeExt(imgUri));

                //Add file to reference

                ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        //Dimiss dialog when success
                        dialog.dismiss();
                        //Display success toast msg
                        Toast.makeText(getApplicationContext(), "saved", Toast.LENGTH_SHORT).show();

                        //ImageUpload(String name, String description, String latitude, String longitude, String hours, String urI)
                        ImageUpload imageUpload = new ImageUpload(txtImageName.getText().toString(), taskSnapshot.getDownloadUrl().toString(), txtDescription.getText().toString(), txtCategory.getText().toString(), txtLatitude.getText().toString(), txtLongitude.getText().toString());

                        //Save image info in to firebase database
                        String uploadId = mDatabaseRef.push().getKey();
                        mDatabaseRef.child(uploadId).setValue(imageUpload);


                        uploadPic.setTextColor(Color.BLACK);
                        //Will show the list of saved data
                        Intent intent = new Intent(MainActivity.this, imageListActivity.class);
                        startActivity(intent);

                        //clearing the EditText
                        txtImageName.setText("");
                        txtCategory.setText("");
                        txtDescription.setText("");
                        txtLatitude.setText("");
                        txtLongitude.setText("");


                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                //Dimiss dialog when error
                                dialog.dismiss();
                                //Display err toast msg
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                //Show upload progress

                                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                dialog.setMessage(  (int) progress + "%");
                            }
                        });
            } else {
                Toast.makeText(getApplicationContext(), "Please upload a picture", Toast.LENGTH_SHORT).show();
                uploadPic.setTextColor(Color.RED);

            }
        } else {

            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        }
    }


    //Will show the list of saved data
    public void showDB(View view) {
        Intent intent = new Intent(MainActivity.this, imageListActivity.class);
        startActivity(intent);

    }

   
}

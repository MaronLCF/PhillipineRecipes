package com.mobdeve.fabelicom.phillipinerecipes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class AddEditActivity extends AppCompatActivity {

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 105;
    private EditText addName, addLocation, addCategory, addDescription, addIngredients, addSteps;
    private Button post;
    private ImageButton cameraBtn, galleryBtn;
    private ImageView imageview;
    private TextView dateTv;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private FirebaseUser user;
    private String username, currentPhotoPath, userID, date, imageurl = null, uuidIntent;
    private Uri uriho;
    private Boolean isEdit = false, isDone = false, isPictureEdited = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.reference = this.firebaseDatabase.getReference("Recipe");

        addName = (EditText) findViewById(R.id.nameInputEt);
        addLocation = (EditText) findViewById(R.id.locationInputEt);
        addCategory = (EditText) findViewById(R.id.categoryInputEt);
        addDescription = (EditText) findViewById(R.id.descInputEt);
        addIngredients = (EditText) findViewById(R.id.ingredientsEtAAE);
        addSteps = (EditText) findViewById(R.id.stepsEtAAE);
        post = (Button) findViewById(R.id.submitRecipeBtnAAE);
        imageview = (ImageView) findViewById(R.id.foodImgIvAAE);
        cameraBtn = (ImageButton) findViewById(R.id.cameraBtn);
        galleryBtn = (ImageButton) findViewById(R.id.galleryBtn);
        dateTv = (TextView) findViewById(R.id.recipeDateTvAAE);
        final TextView usernameTV = (TextView) findViewById(R.id.recipeAuthorTvAAE);

        storageReference = FirebaseStorage.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        date = dateFormat.format(calendar.getTime());

        dateTv.setText(date);

        Intent intent = getIntent();
        if (intent.getExtras() != null){
            addName.setText(intent.getStringExtra("name"));
            addCategory.setText(intent.getStringExtra("category"));
            addLocation.setText(intent.getStringExtra("location"));
            addIngredients.setText(intent.getStringExtra("ingredients"));
            addSteps.setText(intent.getStringExtra("steps"));
            addDescription.setText(intent.getStringExtra("desc"));
            isEdit=true;
            setImage(intent.getStringExtra("image_name"));
            post.setText("Update");
            imageurl = intent.getStringExtra("image_name");
            uuidIntent = intent.getStringExtra("uuid");
        }

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null){
                    username = userProfile.username;

                    usernameTV.setText("Author: " + username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddEditActivity.this, "Something wrong happened!", Toast.LENGTH_SHORT).show();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postRecipe();
            }
        });


        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermissions();
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }
        });


    }

    /**
     * Loads and sets the image inside the imageview
     *
     * @param imageName  A string of the name of the image
     */
    public void setImage(String imageName) {
        // With the storageReference, get the image based on its name
        StorageReference imageRef = this.storageReference.child(imageName);
        Log.d("img ref", String.valueOf(imageRef));
        // Download the image and display via Picasso accordingly
        imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful()) {
                    Log.d("Debug", "onComplete: got image");
                    Picasso.get()
                            .load(task.getResult())
                            .error(R.mipmap.ic_launcher)
                            .placeholder(R.mipmap.ic_launcher)
                            .into(imageview);
                } else {
                    Log.d("Debug", "onComplete: did not get image");
                }
            }
        });
    }

    /**
     * Asks the camera for permission and calls a function when permission is given
     *
     */
    private void askCameraPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else {
            dispatchTakePictureIntent();
        }

    }

    /**
     * Starts camera when given permission else displays a toast when permission
     * is not given
     *
     * @param requestCode  The request code for the camera
     * @param permissions The permission for the camera
     * @param grantResults An int array for the permission code
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            }else {
                Toast.makeText(this, "Camera Permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Creates the filename of the image
     *
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Ensure that there's a camera activity to handle the intent
     *  and creates the File where the photo should go
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.mobdeve.fabelicom.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    /**
     * Gets the filename of the image and sets it to the image view. It also sets the url of the
     * image and the the Uri
     *
     * @param requestCode  The request code for the camera
     * @param resultCode The code for the result of the activity
     * @param data The data of the intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK){
                File f = new File(currentPhotoPath);
                imageview.setImageURI(Uri.fromFile(f));
                Log.d("adasd", "error here" + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);

                //uploadImageToFirebase(f.getName(), contentUri);
                imageurl = f.getName();
                uriho = contentUri;


            }
        }

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK){
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "." + getFileExt(contentUri);
                Log.d("gaIIery", "gaIIery image uri: " + imageFileName);
                imageview.setImageURI(contentUri);

                //uploadImageToFirebase(imageFileName, contentUri);
                imageurl = imageFileName;
                uriho = contentUri;
            }
        }

        isPictureEdited = true;
        isEdit = false;
    }

    /**
     * Uploads the image to firebase based on its image url and Uri
     *
     * @param name  The url of the image
     * @param contentUri The Uri of the image
     */
    private void uploadImageToFirebase(String name, Uri contentUri) {
        StorageReference image = storageReference.child(name);
        image.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                image.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d("imagefirebase", "image success uload " + uri.toString());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddEditActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Gets file extension of the image
     *
     * @param contentUri The Uri of the image
     */
    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    /**
     * Gets all the input from the textviews and imageviews and posts it in Firebase. It returns
     * to the home activity after posting.
     *
     */
    private void postRecipe(){
        String name = addName.getText().toString().trim();
        String location = addLocation.getText().toString().trim();
        String category = addCategory.getText().toString().trim();
        String description = addDescription.getText().toString().trim();
        String ingredients = addIngredients.getText().toString().trim();
        String steps = addSteps.getText().toString().trim();


        if (name.isEmpty()){
            addName.setError("Name required!");
            addName.requestFocus();
            return;
        }

        if (location.isEmpty()){
            addLocation.setError("Location required!");
            addLocation.requestFocus();
            return;
        }

        if (category.isEmpty()){
            addCategory.setError("Category required!");
            addCategory.requestFocus();
            return;
        }

        if (description.isEmpty()){
            addDescription.setError("Description required!");
            addDescription.requestFocus();
            return;
        }

        if (ingredients.isEmpty()){
            addIngredients.setError("Ingredients required!");
            addIngredients.requestFocus();
            return;
        }

        if (steps.isEmpty()){
            addSteps.setError("Steps required!");
            addSteps.requestFocus();
            return;
        }

        if (imageurl == null){
            Toast.makeText(AddEditActivity.this,"Picture is required, try again.", Toast.LENGTH_SHORT).show();
            return;
        }



        if(isEdit == false && imageurl != null && isPictureEdited == true){
            uploadImageToFirebase(imageurl, uriho);
        }



        Recipe reci = new Recipe(name,username,date,imageurl,category,location,description,userID,ingredients,steps, UUID.randomUUID().toString());

        FirebaseDatabase.getInstance().getReference("Recipe")
                        .push()
                        .setValue(reci).addOnCompleteListener(new OnCompleteListener<Void>() {

            private FirebaseDatabase sDatabase;
            private DatabaseReference sReference;

            @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(AddEditActivity.this, "Posting...",Toast.LENGTH_LONG).show();
                            if(isEdit == true || (isPictureEdited == true && isEdit == false)){
                                this.sDatabase = FirebaseDatabase.getInstance();
                                this.sReference = this.sDatabase.getReference("Recipe");
                                Log.d("mainref", String.valueOf(reference));
                                this.sReference.orderByChild("uuid").equalTo(uuidIntent).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot keyNode : dataSnapshot.getChildren()) {
                                            keyNode.getRef().removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Log.d("Debug", "onDataChange: canceled");
                                    }
                                });
                            }
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AddEditActivity.this, "Posted Sucessfully",Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    AddEditActivity.this.startActivity(intent);
                                    //startActivity(new Intent(AddEditActivity.this, MainActivity.class));
                                }
                            }, 5000);
                        }else{
                            Toast.makeText(AddEditActivity.this,"Failed to post! Try Again!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
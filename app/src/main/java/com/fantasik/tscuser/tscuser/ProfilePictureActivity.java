package com.fantasik.tscuser.tscuser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.fantasik.tscuser.tscuser.Util.Utils.MY_PREFS_NAME;

public class ProfilePictureActivity extends AppCompatActivity {


    @BindView(R.id.quick_start_cropped_image)
    ImageButton quickStartCroppedImage;
    @BindView(R.id.butNext)
    Button butNext;
    private Uri mCropImageUri;
    byte[] profileimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Profile Picture");
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_profile_picture);
        ButterKnife.bind(this);

        quickStartCroppedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImageClick(v);
            }
        });

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String dgdfg = prefs.getString("profileimage", null);

        if(dgdfg != null) {
            byte[] img = Base64.decode(dgdfg,  Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
            quickStartCroppedImage.setImageBitmap(bitmap);

        }
        Bitmap bitmap = ((BitmapDrawable) quickStartCroppedImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        profileimage = baos.toByteArray();

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("profileimage", Base64.encodeToString(profileimage, Base64.DEFAULT));
        editor.apply();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return(super.onOptionsItemSelected(item));
    }

    public void onSelectImageClick(View view) {
        CropImage.startPickImageActivity(this);
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;

            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                quickStartCroppedImage.setImageURI(result.getUri());

                Bitmap bitmap = ((BitmapDrawable) quickStartCroppedImage.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                profileimage = baos.toByteArray();

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("profileimage", Base64.encodeToString(profileimage, Base64.DEFAULT));
                editor.apply();

                Toast.makeText(this, "Cropping successful", Toast.LENGTH_LONG).show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * Start crop image activity for the given image.
     */
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    @OnClick(R.id.butNext)
    public void onViewClicked() {


        //  String saveThis = Base64.encodeToString(array, Base64.DEFAULT);
        //
        Intent myIntent = new Intent(ProfilePictureActivity.this, PaymentModeActivity.class);
        this.startActivity(myIntent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
}

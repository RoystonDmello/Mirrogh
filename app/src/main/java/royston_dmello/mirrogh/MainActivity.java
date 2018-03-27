package royston_dmello.mirrogh;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import toan.android.floatingactionmenu.FloatingActionButton;
import toan.android.floatingactionmenu.FloatingActionsMenu;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE = "extra_image";
    private static int IMG_RESULT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionsMenu floatingactionmenu = findViewById(R.id.floatingactionmenu);
        FloatingActionButton pickFromGallery = new FloatingActionButton(MainActivity.this);
        pickFromGallery.setOnClickListener((view) -> {

        });
        FloatingActionButton pickFromCamera = new FloatingActionButton(MainActivity.this);
        pickFromCamera.setOnClickListener((view) -> {
            CropImage.activity(null)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setActivityMenuIconColor(R.color.colorAccent)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setAspectRatio(1, 1)
                    .start(this);
        });
        floatingactionmenu.addButton(pickFromCamera);
        floatingactionmenu.addButton(pickFromGallery);
    }

    public void clickImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, IMG_RESULT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {

            if (requestCode == IMG_RESULT && resultCode == RESULT_OK
                    && data != null) {
                Uri URI = data.getData();

                Intent showImage = new Intent(this, TransformImageActivity.class);
                showImage.putExtra(EXTRA_IMAGE, URI);
                startActivity(showImage);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Please try again", Toast.LENGTH_LONG)
                    .show();
        }

    }
}

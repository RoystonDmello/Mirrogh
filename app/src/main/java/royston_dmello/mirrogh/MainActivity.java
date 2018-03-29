package royston_dmello.mirrogh;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


public class MainActivity extends AppCompatActivity {

    private static final int IMG_RESULT = 1;
    private static final String LOG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((view -> {
            CropImage.activity(null)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setActivityMenuIconColor(R.color.colorAccent)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .start(this);
        }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Intent intent = new Intent(this, TransformImageActivity.class);
                intent.putExtra(Constants.EXTRA_IMAGE, resultUri);
                startActivity(intent);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e(LOG,error.toString());
            }
        }
    }
}

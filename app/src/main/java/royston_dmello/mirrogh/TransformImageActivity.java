package royston_dmello.mirrogh;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class TransformImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transform_image);

        Intent showImage = getIntent();
        String ImageDecode = showImage.getStringExtra(MainActivity.EXTRA_IMAGE);

        ImageView imageViewLoad = (ImageView) findViewById(R.id.imageView);
        imageViewLoad.setImageBitmap(BitmapFactory
                .decodeFile(ImageDecode));
    }

    public void uploadImage(View view) {

    }
}

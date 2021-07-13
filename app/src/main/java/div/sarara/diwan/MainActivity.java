package div.sarara.diwan;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.SaveSettings;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    ImageView imgView;
    Bitmap bitmap;
    InputStream inputStream;
    PhotoEditor mPhotoEditor;
    PhotoEditorView mPhotoEditorView;
    TextView textView;
    EditText mEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText =  findViewById(R.id.TextView);
        mEditText =  findViewById(R.id.TextView);
        mPhotoEditorView = findViewById(R.id.photoEditorView);
        Button addtextView = findViewById(R.id.addTextBtnView);
        Button undoView = findViewById(R.id.undoBtnView);
        Button redoView = findViewById(R.id.redoBtnView);
        Button saveView = findViewById(R.id.saveView);
        Button clearView = findViewById(R.id.clearView);
        Button openImgView = findViewById(R.id.chooseImgView);
        imgView = findViewById(R.id.imgView);


        //Use custom khati using latest support library
        Typeface mTextRobotoTf = ResourcesCompat.getFont(this, R.font.khati);


        //loading khati from assest
        Typeface mEmojiTypeFace = ResourcesCompat.getFont(this, R.font.emojiii);

//        Typeface mEmojiTypeFace = Typeface.createFromAsset(getAssets(), String.valueOf(R.font.emoji));

        addtextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                mPhotoEditor.addText();
                String vi = writeText();
//                mPhotoEditor.addText(mTextRobotoTf,"hi", R.color.purple_200);
                mPhotoEditor.addText(vi, R.color.purple_200);
//                mPhotoEditor.addEmoji("values-ar-watch");

            }

        });
        undoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoEditor.undo();
            }
        });
        redoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoEditor.redo();
            }
        });
        clearView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoEditor.clearAllViews();
            }
        });

//        pick image
        openImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                importing image in onActivityResult() method
                pick_image();
                mPhotoEditor = new PhotoEditor.Builder(MainActivity.this, mPhotoEditorView)
                        .setPinchTextScalable(true)
//                .setDefaultTextTypeface(mTextRobotoTf)
//                .setDefaultEmojiTypeface(mEmojiTypeFace)
                        .build();
            }
        });

        SaveSettings saveSettings = new SaveSettings.Builder()
                .setClearViewsEnabled(false)
                .setTransparencyEnabled(true)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .setCompressQuality(99)
                .build();
        saveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
//                     here to request the missing permissions, and then overriding
//                       public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                                              int[] grantResults)
//                     to handle the case where the user grants the permission. See the documentation
//                     for ActivityCompat#requestPermissions for more details.
                    return;
                }

                mPhotoEditor.saveAsBitmap(saveSettings, new OnSaveBitmap() {
                    @Override
                    public void onBitmapReady(@NonNull Bitmap saveBitmap) {
                        saveImageBitmap(saveBitmap);
                        Log.e("PhotoEditor","Image Saved Successfully");
                    }
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("PhotoEditor","Failed to save Image : "+exception);
                    }
                });
            }
        });

    }

    public String writeText(){

        mEditText.setVisibility(View.VISIBLE);

//        textView.onKeyDown(3,ente);
//        textView.setVisibility(View.GONE);
//        mEditText.type
//        mEditText.setVisibility(View.GONE);
        return mEditText.getText().toString();
    }


    //    pick image uri/full path from gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            try {
                inputStream = getContentResolver().openInputStream(data.getData());
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mPhotoEditorView.getSource().setImageBitmap(bitmap);
        }
    }


    //   open a galleries
    public void pick_image() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select pic"), PICK_IMAGE);
    }


    public boolean isStoragePermissionGranted() {
        String TAG = "Storage Permission";
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    public void saveImageBitmap(Bitmap image_bitmap) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
        if (isStoragePermissionGranted()) { // check or ask permission
            Log.d("main_activity", "root : " + root);
            File myDir = new File(root + "/diwan");
            Log.d("main_activity", "my dir : " + myDir.toString());
            if (!myDir.exists()) {
                Log.d("main_activity", "dir not exist");
                myDir.mkdirs();
                Log.d("main_activity", "dir created");
            }
            String timeStamp = new SimpleDateFormat("yyMMdd-hh-mm-ss").format(new Date());
            String fname = "2BFD" + timeStamp + ".jpg";
            File file = new File(myDir, fname);
            Log.d("main_activity", "create name " + fname);
            if (file.exists()) {
                Log.d("main_activity", "file exist");
                file.delete();
            }

            try {
                Log.d("main_activity", "try to save image: ");
                file.createNewFile(); // if file already exists will do nothing
                Log.d("main_activity", "file created");
                FileOutputStream out = new FileOutputStream(file);
                Log.d("main_activity", "file exported");
                image_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                Log.d("main_activity", "file compressed");
                out.flush();
                out.close();
                Toast.makeText(MainActivity.this,"image saved",Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this,"Error"+e,Toast.LENGTH_LONG).show();
                Log.d("main_activity", "saveImageBitmap: "+"not saved "+ e.getMessage());
            }
            MediaScannerConnection.scanFile(this, new String[]{file.toString()}, new String[]{file.getName()}, null); }
    }

}

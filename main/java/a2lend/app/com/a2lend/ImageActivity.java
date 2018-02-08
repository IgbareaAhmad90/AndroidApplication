/*
* Taking an event picture
 */
package a2lend.app.com.a2lend;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import pub.devrel.easypermissions.EasyPermissions;

public class ImageActivity extends AppCompatActivity implements
        View.OnClickListener, EasyPermissions.PermissionCallbacks {

    public static ArrayList<Uri> fileUri = new ArrayList<Uri>();
    private Uri mDownloadUrl = null;
    private Uri mFileUri = null;


    final String permissionWrite = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    final String rationale_storage_message = "This sample reads images from your camera to demonstrate uploading.";

    private static final int RC_TAKE_PICTURE = 101;
    private static final int RC_STORAGE_PERMS = 102;
    private BroadcastReceiver mDownloadReceiver;


    final String AUTHORITY = "com.firebase.abo3le.firebasehelloworld.fileprovider";
    String ImageName;
    private static final String KEY_FILE_URI = "key_file_uri";
    private static final String KEY_DOWNLOAD_URL = "key_download_url";

    private static final String TAG = "Storage#MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_image);
        // Click listeners
        findViewById(R.id.cameraButton).setOnClickListener(this);

        // Restore instance state
        if (savedInstanceState != null) {
            mFileUri = savedInstanceState.getParcelable(KEY_FILE_URI);
            mDownloadUrl = savedInstanceState.getParcelable(KEY_DOWNLOAD_URL);
        }

        launchCamera();
    }

    @Override
    public void onClick(View v) {

    }
    //launch camera
    private void  launchCamera() {

        Log.d(TAG, "launchCamera");

        //region Permissions - Check that we have permission to read images from external storage.
        if (!EasyPermissions.hasPermissions(this, permissionWrite)) {
            EasyPermissions.requestPermissions(this, rationale_storage_message,RC_STORAGE_PERMS, permissionWrite );
            return;
        }
        //endregion

        //region Choose file storage location, must be listed in res/xml/file_paths.xml
        ImageName = UUID.randomUUID().toString();
        File dir = new File(Environment.getExternalStorageDirectory() + "/photos");
        File file = new File(dir, ImageName + ".jpg");

        try {

            boolean created = file.createNewFile();
            Log.d(TAG, "file.createNewFile:"+ Environment.getExternalStorageDirectory() + file.getAbsolutePath() + ":" + created);

            // Create directory if it does not exist.
            if (!dir.exists()) {
                dir.mkdir();
            }

        } catch (IOException e) {
            Log.e(TAG, "file.createNewFile" + file.getAbsolutePath() + ":FAILED", e);
        }
        //endregion

        //region Create content:// URI for file
        //firebase image uri
        mFileUri = FileProvider.getUriForFile(this, AUTHORITY, file);
        //endregion

        // Create and launch the intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
        startActivityForResult(takePictureIntent, RC_TAKE_PICTURE);

    }

    // resize Image File / 10
    private File resizeImage(String ImageName){

        File dir = new File(Environment.getExternalStorageDirectory() + "/photos");
        File file = new File(dir, ImageName + ".jpg");

        Bitmap b= BitmapFactory.decodeFile(file.getPath());
        Log.d("O_W:O_H - A_W:A_H", b.getWidth()+":"+b.getHeight()+" - " +b.getWidth()/10+":"+b.getHeight()/10);
        Bitmap out = Bitmap.createScaledBitmap(b ,b.getWidth()/10, b.getHeight()/10, false);

        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            out.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            b.recycle();
            out.recycle();
        } catch (Exception e) {}
        return file;
    }
    //////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
        if (requestCode == RC_TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                if (mFileUri != null) {
                    // region Resize File To Small Size
                    File file = resizeImage(ImageName);
                    //endregion
                    mFileUri = FileProvider.getUriForFile(this, AUTHORITY, file);
                    fileUri.add(mFileUri);
                    finish();
                } else {
                    Log.w(TAG, "File URI is null");
                    finish();
                }
            } else {
                Toast.makeText(this, "Taking picture failed.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {}

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {}


}


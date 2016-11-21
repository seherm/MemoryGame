package com.hermann.memorypics;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main extends AppCompatActivity {

    int index = 0, numberOfPlayers, selectedRow = -1;

    private static int RESULT_LOAD_IMAGE = 1, REPLACE_IMAGE = 254;
    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 356;
    private String soundState;

    private Integer[] mThumbIds = {
            R.mipmap.picture1, R.mipmap.picture2, R.mipmap.picture3, R.mipmap.picture4, R.mipmap.picture5,
            R.mipmap.picture6, R.mipmap.picture7, R.mipmap.picture8, R.mipmap.picture9, R.mipmap.picture10,
            R.mipmap.picture11, R.mipmap.picture12, R.mipmap.picture13, R.mipmap.picture14, R.mipmap.picture15};

    String[] list;
    String[] listOfPlayers;
    int counter = 0;
    boolean allSet = false, enteringNames = false;

    static MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        AssetFileDescriptor afd = null;
        try {
            afd = getAssets().openFd("audio_file.mp3");
            if (player == null){
                player = new MediaPlayer();
                player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
                player.prepare();
                player.setLooping(false);
                player.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start();
            }
        });

        ImageView fab = (ImageView) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(Main.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(Main.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(Main.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    if (index >= list.length){
                        Toast.makeText(getApplicationContext(), "You have already added maximum number of images. Replace one of existing!", Toast.LENGTH_SHORT).show();
                    } else if (index > 0){
                        showChoiceDialog();
                    } else {
                        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, Crop.REQUEST_PICK);
                    }
//                    Crop.pickImage(Main.this);
                }

            }
        });

        final ImageView sound = (ImageView) findViewById(R.id.sound);

        if (player.isPlaying()){
            sound.setImageURI(resourceToUri(Main.this, R.mipmap.speaker));
        } else {
            sound.setImageURI(resourceToUri(Main.this, R.mipmap.speaker_off));
        }

        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (player.isPlaying()){
                    player.pause();
                    sound.setImageURI(resourceToUri(Main.this, R.mipmap.speaker_off));
                } else {
                    player.start();
                    sound.setImageURI(resourceToUri(Main.this, R.mipmap.speaker));
                }
            }
        });

        list = new String[mThumbIds.length];

        for (int i = 0; i < mThumbIds.length; i++){
            list[i] = resourceToUri(this, mThumbIds[i]).toString();
        }

        ImageView singleplayer = (ImageView) findViewById(R.id.singleplayer);
        singleplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listOfPlayers = new String[1];
                allSet = true;
                numberOfPlayers = 1;
                showDesiredDialog("SINGLEPLAYER", "Enter your name:");
            }
        });

        ImageView multiplayer = (ImageView) findViewById(R.id.multiplayer);
        multiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDesiredDialog("MULTIPLAYER", "Enter number of players");
            }

        });

        String root = Environment.getExternalStorageDirectory().toString();
        File directory = new File(root + "/saved_images");

        File[] files = directory.listFiles();

        if (files != null){
            for (int i = 0; i < files.length; ++i) {
                list[index] = Uri.fromFile(files[i]).toString();
                index++;
            }
        }
    }

    @Override
    public void onPause(){

        super.onPause();

        if(player.isPlaying()){
            soundState = "On";
            player.pause();
        }else{
            soundState = "Off";
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        //TODO:Resume when sound was playing before
    }


    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putInt("possition", player.getCurrentPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        int pos = savedInstanceState.getInt("possition");
//        player.seekTo(pos);
//        player.start();
        super.onRestoreInstanceState(savedInstanceState);
    }

    void showDesiredDialog(String title, String message){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Main.this);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);

        final EditText input = new EditText(Main.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
//                alertDialog.setIcon(R.drawable.key);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String test = input.getText().toString();
                if (test.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Enter some value!", Toast.LENGTH_SHORT).show();
                    showDesiredDialog("MULTIPLAYER", "Enter number of players");
                } else {
                    if (allSet){
                        listOfPlayers[counter] = test;

                        showDifficultyDialog();

                        counter = 0;
                        allSet = false;
                    } else if (enteringNames){

                        listOfPlayers[counter] = test;
                        counter++;

                        String message = "Enter name for player " + (counter+1) + ":";
                        if (counter == listOfPlayers.length -1){
                            enteringNames = false;
                            allSet = true;
                        }

                        showDesiredDialog("MULTIPLAYER", message);

                    } else if (isNumeric(test)){
                        numberOfPlayers = Integer.parseInt(test);
                        if (numberOfPlayers > 0 && numberOfPlayers <= 3) {
                            listOfPlayers = new String[numberOfPlayers];
                            enteringNames = true;
                            showDesiredDialog("MULTIPLAYER", "Enter name for player " + (counter+1) + ":");
                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong input! Enter number between 1 and 3.", Toast.LENGTH_SHORT).show();
                            showDesiredDialog("MULTIPLAYER", "Enter number of players");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Wrong input! Enter number between 1 and 3.", Toast.LENGTH_SHORT).show();
                        showDesiredDialog("MULTIPLAYER", "Enter number of players");
                    }
                }
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    void showDifficultyDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Main.this);
        alertDialog.setTitle("Select difficulty level");
        alertDialog.setMessage("");

        final RadioGroup radioGroup = new RadioGroup(Main.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        radioGroup.setLayoutParams(lp);

        for(int i = 1; i < 5; i++){
            RadioButton radioButton = new RadioButton(Main.this); // dynamically creating RadioButton and adding to RadioGroup.
            radioButton.setText(String.format("%d", i));
            radioButton.setId(Integer.parseInt(String.format("%d", i)));
            radioGroup.addView(radioButton);
        }
        alertDialog.setView(radioGroup);
//                alertDialog.setIcon(R.drawable.key);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int level = radioGroup.getCheckedRadioButtonId();

                Toast.makeText(getApplicationContext(), "All set!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Main.this, GridActivity.class);
                Bundle bundle = new Bundle();
                bundle.putStringArray("list", list);
                bundle.putInt("level", level);
                bundle.putInt("numberOfPlayers", numberOfPlayers);
                bundle.putStringArray("listOfPlayers", listOfPlayers);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    void showChoiceDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Main.this);
        alertDialog.setMessage("Do you want to add more images or to replace some of existing?");

        alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (index >= list.length){
                    Toast.makeText(getApplicationContext(), "You have already added maximum number of images. Replace one of existing!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, Crop.REQUEST_PICK);
                }
            }
        });

        alertDialog.setNegativeButton("Replace", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Main.this, ListSuperViewActivity.class);
                startActivityForResult(intent, REPLACE_IMAGE);
            }
        });

        alertDialog.show();
    }

    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (index >= list.length){
                        Toast.makeText(getApplicationContext(), "You have already added maximum number of images. Replace one of existing!", Toast.LENGTH_SHORT).show();
                    } else if (index > 0){
                        showChoiceDialog();
                    } else {
                        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, Crop.REQUEST_PICK);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        MenuItem sound = menu.findItem(R.id.action_settings);
        if (player.isPlaying()){
            sound.setIcon(R.mipmap.sound_icon);
        } else {
            sound.setIcon(R.mipmap.mute_icon);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if (player.isPlaying()){
                player.pause();
            } else {
                player.start();
            }
            invalidateOptionsMenu();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
//        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK){
            Uri selectedImage = result.getData();

            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Uri uri = saveToInternalStorage(BitmapFactory.decodeFile(picturePath), false);

            Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));

            Crop.of(uri, destination).asSquare().start(this);

        } else  if (requestCode == Crop.REQUEST_CROP){

            if (resultCode == RESULT_OK) {
                Uri uri = Crop.getOutput(result);

                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                uri = saveToInternalStorage(bitmap, true);

                if (selectedRow != -1){
                    list[selectedRow] = uri.toString();
                    selectedRow = -1;
                } else if (index < list.length){
                    list[index] = uri.toString();
                    index++;
                }

            } else if (resultCode == Crop.RESULT_ERROR) {
                Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REPLACE_IMAGE && resultCode == RESULT_OK){
            selectedRow = result.getIntExtra("selectedRow", -1);
//            if (selectedRow != -1){
//                Uri selectedImage = Uri.parse(list[selectedRow]);
//                deleteImage(selectedImage.toString());
//                selectedRow = -1;
//            }

//            list = new String[mThumbIds.length];
//
//            for (int i = 0; i < mThumbIds.length; i++){
//                list[i] = resourceToUri(this, mThumbIds[i]).toString();
//            }
//
//            String root = Environment.getExternalStorageDirectory().toString();
//            File directory = new File(root + "/saved_images");
//
//            File[] files = directory.listFiles();
//
//            index = 0;
//
//            for (int i = 0; i < files.length; ++i) {
//                list[index] = Uri.fromFile(files[i]).toString();
//                index++;
//            }

            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, Crop.REQUEST_PICK);

        }
    }

    public void deleteImage(String file_dj_path) {
        File fdelete = new File(file_dj_path);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                Log.e("-->", "file Deleted :" + file_dj_path);
                callBroadCast(fdelete);
            } else {
                Log.e("-->", "file not Deleted :" + file_dj_path);
            }
        } else if (file_dj_path.contains("file:/")) {
            String newString = file_dj_path.replace("file:/", "");
            deleteImage(newString);
        }
    }

    public void callBroadCast(File out) {
        if (Build.VERSION.SDK_INT >= 14) {
            Log.e("-->", " >= 14");

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(out);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);

        } else {
            Log.e("-->", " < 14");
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }

    public static Uri resourceToUri(Context context, int resID) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resID) + '/' +
                context.getResources().getResourceTypeName(resID) + '/' +
                context.getResources().getResourceEntryName(resID) );
    }

    private Uri saveToInternalStorage(Bitmap bitmapImage, boolean compress){
        if (bitmapImage.getWidth() < 200){
            bitmapImage = Bitmap.createScaledBitmap(bitmapImage, 200, 200, false);
        } else if (compress){
            bitmapImage = Bitmap.createScaledBitmap(bitmapImage, 200, 200, false);
        }

        String root = Environment.getExternalStorageDirectory().toString();
        File directory = new File(root + "/saved_images");
        if (!directory.exists()){
            directory.mkdirs();
        }

        String name;
        if (selectedRow != -1){
            name = "image" + selectedRow +".jpeg";
        } else {
            name = "image" + index +".jpeg";
        }
        File file = new File(directory, name);
        if (file.exists ()){
            file.delete ();
        }

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return Uri.fromFile(file);
    }
}

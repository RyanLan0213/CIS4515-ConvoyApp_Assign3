package edu.temple.convoy;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class RecordingAcitivty extends MainActivity implements GoogleMapActivity.datareturn{
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    Button record,stop,play,send;
    private BroadcastReceiver recordingReceiver;
    Queue<Record> queue = new ArrayDeque<Record>();
    Queue<File> Filequeue = new ArrayDeque<File>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_acitivty);
        getWrittenPermission();

        if (isMicrophonePresent()) {
            getMicrophonePermission();
        }
        record = findViewById(R.id.Recordbutton);
        stop = findViewById(R.id.StopButton);
        play = findViewById(R.id.PlayButton);
        send = findViewById(R.id.SendButton);

        IntentFilter filter = new IntentFilter();
        filter.addAction("edu.temple.convoy.broadcast.message_update");





        //
        recordingReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("BroadcastReceived", "Receivedoutsidescope");
                if (intent.hasExtra("message")) {
                    Log.d("MessageWasInvoked", "I am invoked");
                    queue = (ArrayDeque) intent.getSerializableExtra("message");
                    Log.d("The size of the queue is ", String.valueOf(queue.size()));
                    for (Record record : queue) {
                        Log.d("The record in queue is:", record.toString() + "\n");

                    }
                    if (queue.peek() != null){
                        Record temrecord = queue.poll();
                        Filequeue.add(DownloadFiles(temrecord.getURL(), temrecord.getUsername()));
                        Log.d("The Filequeue has item: ", Filequeue.peek().getPath());
                        playPathAudio(Filequeue.poll().getPath());
                }
            }
            }
        };
        this.registerReceiver(recordingReceiver, filter);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordPressed();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StopPressed();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayPressed();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                File audioFile = new File(getrecordingFilePath());
                Log.d("My audioPath is ", getrecordingFilePath());
                byte[] audioByteArray = getBytes(audioFile);
                Log.d("Audio Byte Array is:",audioByteArray.toString());
                uploadBitmap(audioFile);

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });


    }
    private String getrecordingFilePath(){
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file =new File(musicDirectory,"MyAudioRecording"+ ".3gp");
        return file.getPath();

    }



    private String getDownloadedFilePath(String username){
        File DownloadDirectory = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File file =new File(DownloadDirectory,username);
        Log.d("The path of the file is: ", file.getPath().toString());
        return file.getPath();
    }
    public void recordPressed(){
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(getrecordingFilePath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();

            Toast.makeText(this,"Recording is Started",Toast.LENGTH_LONG).show();
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }
    public static byte[] getBytes(File f) throws FileNotFoundException, IOException {
        byte[] buffer = new byte[2048];
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        FileInputStream fis = new FileInputStream(f);
        int read;
        while((read = fis.read(buffer))!=-1){
            os.write(buffer,0,read);
        }
        fis.close();
        os.close();
        return os.toByteArray();



    }
    public void StopPressed(){
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        Toast.makeText(this,"Recording is Stopped",Toast.LENGTH_LONG).show();


    }

    public void PlayPressed(){
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getrecordingFilePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            Log.d("Playing recording","Playing");
        }
        catch(Exception e){
            e.printStackTrace();
        }


    }

    void playPathAudio(String path){
        mediaPlayer = null;
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            Log.d("The pathroot is:",path);
            mediaPlayer.prepare();
            mediaPlayer.start();



        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.reset();
                mediaPlayer.release();

                playPathAudio(Filequeue.poll().getPath());
            }
        });
    }
    public void PlayAudio(String path){
        try{
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean isMicrophonePresent() {
        if (this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE))
            return true;

        else
            return false;

    }
    private File DownloadFiles(String URL,String username){

        if(URL!=null) {
            String filename ="";
            String url = URL;
            DownloadManager.Request request = new
                    DownloadManager.Request(Uri.parse(url));
            request.setDescription("Recording");
            request.setTitle(username);
            filename = username+LocalDateTime.now().toString()+".3gp";
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename);

            // get download service and enqueue file
            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
           // File file = new File(getDownloadedFilePath(username));
            Log.d("The path is ",getDownloadedFilePath(username).toString());
            File file = new File(getDownloadedFilePath(filename));
            if(file.exists()){
                Log.d("The file exists after download","Existed");
            }

            return file;
        }
        else{
            return null;
        }


    }

    private void getMicrophonePermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},100);
        }
    }

    private void getWrittenPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},101);
        }
    }
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    private void uploadBitmap(File file) {

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, "https://kamorris.com/lab/convoy/convoy.php",
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            //Log.d("The URL is: ",obj.getString("message_url"));
                            Log.d("The response is:",obj.toString());
                           // Toast.makeText(getApplicationContext(), obj.getString("message_url"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError",""+error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("action", "MESSAGE");
                params.put("username",getusername());
                params.put("session_key",getsessionkey());
                params.put("convoy_id",getconvoyid());


                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() throws IOException {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("message_file", new DataPart(getusername(),getBytes(file)));

                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }
    private void registerReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("edu.temple.convoy.broadcast.message_update");
        recordingReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("OutsideScopeReceived","Receivedoutsidescope");
                if(intent.hasExtra("message")) {
                    Log.d("GetListInvoked","I am invoked");
                    queue = (ArrayDeque) intent.getSerializableExtra("message");
                    Log.d("The record in the queue is:",queue.toString());
                }

            }
        };
        registerReceiver(recordingReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(recordingReceiver!=null) {
            this.unregisterReceiver(recordingReceiver);
            recordingReceiver = null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}

package evolution.lancer.breno.riccitestbed;

import androidx.appcompat.app.AppCompatActivity;
import evolution.lancer.breno.ricci2lib.ricci.D2DCommunication.RicciD2DManager;
import evolution.lancer.breno.ricci2lib.ricci.RemoteIntent;
import evolution.lancer.breno.ricci2lib.ricci.receiver.RicciD2DBroadcastReceiver;
import evolution.lancer.breno.ricci2lib.ricci.services.BasicIntentService;
import evolution.lancer.breno.ricci2lib.ricci.utils.D2DTransmissionUtils;
import evolution.lancer.breno.ricci2lib.ricci.utils.RemoteUtils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;


import static evolution.lancer.breno.ricci2lib.ricci.constants.UtilityConstants.OUT_COPY_REPLY_MSG;
import static evolution.lancer.breno.ricci2lib.ricci.constants.UtilityConstants.OUT_STREAM_REPLY_MSG;
import static evolution.lancer.breno.ricci2lib.ricci.utils.CopyUtils.processDataForTransmission;
import static evolution.lancer.breno.ricci2lib.ricci.utils.Util.ACTION_RESP;
import static evolution.lancer.breno.ricci2lib.ricci.utils.Util.REQUEST_COPY_TRANSMISSION;
import static evolution.lancer.breno.ricci2lib.ricci.utils.Util.REQUEST_STREAM_TRANSMISSION;

public class MainActivity extends AppCompatActivity {

    private String myIp = "localhost";
    private int myPort = 4455;

    private String remoteIp = "localhost";
    private int remotePort = 4465;

    private Boolean isServer = false;

    private RicciD2DBroadcastReceiver ricciReceiver = null;
    private RicciD2DManager ricciD2DManager = null;

    public void setMyPort(int myPort) { this.myPort = myPort; }

    public int getMyPort() { return this.myPort; }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public String getMyIp(){
        return this.myIp;
    }

    public void setRemoteIp(String remoteIp) {
        this.remoteIp = remoteIp;
        System.out.println("Remote Ip address is: " + this.remoteIp);
    }

    public String getRemoteIp(){
        return this.remoteIp;
    }

    public int getRemotePort(){
        return remotePort;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);

        //registering the broadcast receiver
        IntentFilter filter = new IntentFilter(ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        ricciReceiver = new RicciD2DBroadcastReceiver();
        registerReceiver(ricciReceiver, filter);

        //getting my ip information
        setMyIp();

        final EditText myIpIn = (EditText) findViewById(R.id.editText2);
        myIpIn.setText(this.myIp);


    }

    public void initializeChannel() {

        ricciD2DManager = new RicciD2DManager(this.remoteIp, this.remotePort, this.myIp, this.myPort, this.getApplicationContext());
        ricciReceiver.setRicciD2DManager(this.ricciD2DManager);
    }

    public Intent handleCopyIntent(Intent data) {

        Intent intent = new Intent(getApplicationContext(), BasicIntentService.class);
        Uri dataUri = data.getData();
        Cursor cursor = getContentResolver().query(dataUri, null, null, null, null);
        Intent sendIntent = processDataForTransmission(cursor);
        intent.putExtra(OUT_COPY_REPLY_MSG, sendIntent);
        cursor.close();
        return intent;

    }

    public Intent handleStreamIntent(Intent data){

        Intent intent = new Intent(getApplicationContext(), BasicIntentService.class);
        intent.putExtra(OUT_STREAM_REPLY_MSG, data);
        return intent;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case REQUEST_COPY_TRANSMISSION: {
                    Log.d(this.getClass().toString(), "request copy transmission");
                    Intent intent = new D2DTransmissionUtils(this).handleCopyIntent(resultIntent);
                    startService(intent);
                }
                break;

                case REQUEST_STREAM_TRANSMISSION: {
                    Log.d(this.getClass().toString(), "request stream transmission");
                    Intent intent = handleStreamIntent(resultIntent);
                    startService(intent);
                }
                break;

                default:
                    break;

            }
        }

    }

    //Example of copy remote intent
    public RemoteIntent getContactIntent() {

        RemoteIntent remoteIntent = new RemoteIntent();
        remoteIntent.setAction(Intent.ACTION_PICK);
        remoteIntent.setData(ContactsContract.Contacts.CONTENT_URI);
        remoteIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        System.out.println(remoteIntent.getTransferMethod());

        return remoteIntent;

    }

    //Example of stream remote intent
    public RemoteIntent getStreamIntent() {

        RemoteIntent remoteIntent = new RemoteIntent();
        remoteIntent.setAction(Intent.ACTION_GET_CONTENT);
        remoteIntent.addCategory(Intent.CATEGORY_OPENABLE);
        remoteIntent.setType("*/*");
        return remoteIntent;
    }


    public void setUpServer(View view) {

        final EditText remoteIpIn = (EditText) findViewById(R.id.editText4);
        setRemoteIp(remoteIpIn.getText().toString());
        int temp = myPort;
        myPort = remotePort;
        remotePort = temp;
        isServer = true;
        initializeChannel();

    }

    public void sendCopyMessage(View view){

        final EditText remoteIpIn = (EditText) findViewById(R.id.editText4);
        setRemoteIp(remoteIpIn.getText().toString());
        initializeChannel();
        //ricciD2DManager.sendRequest(getContactIntent());
        new AsyncCopyCaller().execute();
    }

    public void sendStreamMessage(View view){

        final EditText remoteIpIn = (EditText) findViewById(R.id.editText4);
        setRemoteIp(remoteIpIn.getText().toString());
        initializeChannel();
        //ricciD2DManager.sendRequest(getContactIntent());
        new AsyncStreamCaller().execute();
    }

    public void setMyIp() {

        RemoteUtils remoteUtils = new RemoteUtils();
        this.myIp = remoteUtils.getIPAddress(true);
        System.out.println("My ip address is: " + this.myIp);
    }

    private class AsyncCopyCaller extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.show();
        }
        @Override
        protected Void doInBackground(Void... params) {

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here
            ricciD2DManager.sendRequest(getContactIntent());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //this method will be running on UI thread
            pdLoading.dismiss();
        }

    }

    private class AsyncStreamCaller extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.show();
        }
        @Override
        protected Void doInBackground(Void... params) {

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here
            ricciD2DManager.sendRequest(getStreamIntent());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //this method will be running on UI thread
            pdLoading.dismiss();
        }

    }

}


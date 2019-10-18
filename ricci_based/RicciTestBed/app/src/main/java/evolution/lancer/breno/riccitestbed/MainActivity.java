package evolution.lancer.breno.riccitestbed;

import androidx.appcompat.app.AppCompatActivity;
import evolution.lancer.breno.ricci2lib.ricci.D2DCommunication.RicciD2DManager;
import evolution.lancer.breno.ricci2lib.ricci.RemoteIntent;
import evolution.lancer.breno.ricci2lib.ricci.receiver.RicciD2DBroadcastReceiver;
import evolution.lancer.breno.ricci2lib.ricci.utils.RemoteUtils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;


import static evolution.lancer.breno.ricci2lib.ricci.utils.Util.ACTION_RESP;

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
        System.out.println("Remote Ip address is: "  + this.remoteIp);
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
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter(ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        ricciReceiver = new RicciD2DBroadcastReceiver();
        registerReceiver(ricciReceiver, filter);

        setMyIp();

        final EditText myIpIn = (EditText) findViewById(R.id.editText2);
        myIpIn.setText(this.myIp);


    }

    public void initializeChannel() {

        ricciD2DManager = new RicciD2DManager(this.remoteIp, this.remotePort, this.myIp, this.myPort, this.getApplicationContext());
        ricciReceiver.setRicciD2DManager(this.ricciD2DManager);
    }

    public RemoteIntent getContactIntent() {

        RemoteIntent remoteIntent = new RemoteIntent();
        remoteIntent.setAction(Intent.ACTION_PICK);
        remoteIntent.setData(ContactsContract.Contacts.CONTENT_URI);
        remoteIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        System.out.println(remoteIntent.getTransferMethod());

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

    public void sendMessage(View view){

        final EditText remoteIpIn = (EditText) findViewById(R.id.editText4);
        setRemoteIp(remoteIpIn.getText().toString());
        initializeChannel();
        //ricciD2DManager.sendRequest(getContactIntent());
        new AsyncCaller().execute();
    }

    public void setMyIp() {

        RemoteUtils remoteUtils = new RemoteUtils();
        this.myIp = remoteUtils.getIPAddress(true);
        System.out.println("My ip address is: " + this.myIp);
    }

    private class AsyncCaller extends AsyncTask<Void, Void, Void> {
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

}


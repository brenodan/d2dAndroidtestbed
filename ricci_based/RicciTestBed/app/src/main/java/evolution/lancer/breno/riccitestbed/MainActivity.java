package evolution.lancer.breno.riccitestbed;

import androidx.appcompat.app.AppCompatActivity;
import evolution.lancer.breno.ricci2lib.lipermi.handler.CallHandler;
import evolution.lancer.breno.ricci2lib.lipermi.net.Client;
import evolution.lancer.breno.ricci2lib.lipermi.net.Server;
import evolution.lancer.breno.ricci2lib.ricci.D2DCommunication.RicciD2DManager;
import evolution.lancer.breno.ricci2lib.ricci.RemoteIntent;
import evolution.lancer.breno.ricci2lib.ricci.receiver.RicciD2DBroadcastReceiver;
import evolution.lancer.breno.ricci2lib.ricci.utils.RemoteUtils;
import evolution.lancer.breno.riccitestbed.D2DCommunication.D2DDataExchangeImplementation;
import evolution.lancer.breno.riccitestbed.D2DCommunication.D2DDataExchangeInterface;
import evolution.lancer.breno.riccitestbed.D2DCommunication.D2DDataType;
import evolution.lancer.breno.riccitestbed.D2DCommunication.RemoteObject;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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

        ricciD2DManager = new RicciD2DManager(this.remoteIp, this.remotePort, this.myIp, this.myPort);
        ricciReceiver.setRicciD2DManager(this.ricciD2DManager);
    }

    public void sendRequest(RemoteIntent remoteIntent){

        ricciD2DManager.sendRequest(remoteIntent);
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

        int temp = myPort;
        myPort = remotePort;
        remotePort = temp;
        isServer = true;
        initializeChannel();

    }

    public void sendMessage(View view){

        initializeChannel();
        ricciD2DManager.sendRequest(getContactIntent());
    }

    public void setMyIp() {

        RemoteUtils remoteUtils = new RemoteUtils();
        this.myIp = remoteUtils.getIPAddress(true);
        System.out.println("My ip address is: " + this.myIp);
    }

}

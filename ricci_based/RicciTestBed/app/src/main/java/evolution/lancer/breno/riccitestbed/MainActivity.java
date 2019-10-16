package evolution.lancer.breno.riccitestbed;

import androidx.appcompat.app.AppCompatActivity;
import evolution.lancer.breno.ricci2lib.lipermi.handler.CallHandler;
import evolution.lancer.breno.ricci2lib.lipermi.net.Client;
import evolution.lancer.breno.ricci2lib.lipermi.net.Server;
import evolution.lancer.breno.ricci2lib.ricci.RemoteIntent;
import evolution.lancer.breno.ricci2lib.ricci.utils.RemoteUtils;
import evolution.lancer.breno.riccitestbed.D2DCommunication.D2DDataExchangeImplementation;
import evolution.lancer.breno.riccitestbed.D2DCommunication.D2DDataExchangeInterface;
import evolution.lancer.breno.riccitestbed.D2DCommunication.D2DDataType;
import evolution.lancer.breno.riccitestbed.D2DCommunication.RemoteObject;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;

public class MainActivity extends AppCompatActivity {

    private String myIp = "localhost";
    private int myPort = 4455;

    private String remoteIp = "localhost";
    private int remotePort = 4465;

    public void setMyPort(int myPort) { this.myPort = myPort; }

    public int getMyPort() { return this.myPort; }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public void setRemoteIp (String remoteIp) {
        this.remoteIp = remoteIp;
        System.out.println("Remote Ip address is: "  + this.remoteIp);
    }

    public String getRemoteIp (){
        return this.remoteIp;
    }

    public int getRemotePort(){
        return remotePort;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void initializeServer() {

        D2DServerCommunicator d2dServer = new D2DServerCommunicator(getMyPort());
        d2dServer.start();

    }

    public void sendRequest(){

        RemoteIntent remoteIntent = getContactIntent();
        D2DClientCommunicator d2dClient = new D2DClientCommunicator(this.remotePort, this.remoteIp,
                D2DDataType.RemoteIntent, new RemoteObject(remoteIntent));
        d2dClient.run();
    }


    public RemoteIntent getContactIntent() {

        RemoteIntent remoteIntent = new RemoteIntent();
        remoteIntent.setAction(Intent.ACTION_PICK);
        remoteIntent.setData(ContactsContract.Contacts.CONTENT_URI);
        remoteIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        System.out.println(remoteIntent.getTransferMethod());

        return remoteIntent;

    }

    public void setMyIp() {
        RemoteUtils remoteUtils = new RemoteUtils();
        this.myIp = remoteUtils.getIPAddress(true);
        System.out.println("My ip address is: " + this.myIp);
    }

}


class D2DClientCommunicator extends Thread {

    private int port = 4456;
    private String ip = "localhost";
    private D2DDataType dataType = D2DDataType.RemoteIntent;
    private RemoteObject remoteObject;

    public D2DClientCommunicator (int port, String ip, D2DDataType dataType, RemoteObject remoteObject){

        if (port > -1) {
            this.port = port;
        }

        if (ip != null) {
            this.ip = ip;
        }

        if(dataType != null) {
            this.dataType = dataType;
        }

        this.remoteObject = remoteObject;

    }

    public void run (){

        try {

            CallHandler callHandler = new CallHandler();
            Client client = new Client(this.ip, this.port, callHandler);

            D2DDataExchangeInterface d2dImplementation = (D2DDataExchangeInterface) client.getGlobal(
                    D2DDataExchangeInterface.class
            );

            switch (this.dataType) {

                case Intent:
                    d2dImplementation.setIntent(this.remoteObject.getIntent());
                    break;

                case Object:
                    d2dImplementation.setObject(this.remoteObject.getObject());
                    break;

                case RemoteIntent:
                    d2dImplementation.setRemoteIntent(this.remoteObject.getRemoteIntent());
                    break;
            }

        } catch (Exception e) {

            e.printStackTrace();

        }
    }
}

class D2DServerCommunicator extends Thread {

    private int port = 4455;

    public D2DServerCommunicator (int port){

        if (port > -1) {
           this.port = port;
        }

    }

    public void run (){

        try {

            CallHandler callHandler = new CallHandler();
            D2DDataExchangeInterface d2dImplementation = new D2DDataExchangeImplementation();
            callHandler.registerGlobal(D2DDataExchangeInterface.class, d2dImplementation);

            Server server = new Server();
            server.bind(this.port, callHandler);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }
}

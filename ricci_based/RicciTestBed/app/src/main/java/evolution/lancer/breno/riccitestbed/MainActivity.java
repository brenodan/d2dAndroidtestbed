package evolution.lancer.breno.riccitestbed;

import androidx.appcompat.app.AppCompatActivity;
import evolution.lancer.breno.ricci2lib.lipermi.handler.CallHandler;
import evolution.lancer.breno.ricci2lib.lipermi.net.Client;
import evolution.lancer.breno.ricci2lib.lipermi.net.Server;
import evolution.lancer.breno.riccitestbed.D2DCommunication.D2DDataExchangeImplementation;
import evolution.lancer.breno.riccitestbed.D2DCommunication.D2DDataExchangeInterface;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}


class D2DClientCommunicator extends Thread {

    private int port = 4456;
    private String ip = "localhost";

    public D2DClientCommunicator (int port, String ip){

        if (port > -1) {
            this.port = port;
        }

        if (ip != null) {
            this.ip = ip;
        }

    }

    public void run (){

        try {

            CallHandler callHandler = new CallHandler();
            Client client = new Client(this.ip, this.port, callHandler);

            D2DDataExchangeInterface d2dImplementation = (D2DDataExchangeInterface) client.getGlobal(
                    D2DDataExchangeInterface.class
            );

            d2dImplementation.setObject(new Object());


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

package evolution.lancer.breno.riccitestbed.D2DCommunication;

import android.content.Intent;

import evolution.lancer.breno.ricci2lib.ricci.RemoteIntent;

public class D2DDataExchangeImplementation implements D2DDataExchangeInterface {

    private ReceivedDataRepresentation receivedData;
    private RemoteIntent incomingRemoteIntent;
    private Intent incomingIntent;
    private Object incomingObject;

    public ReceivedDataRepresentation getReceivedData() {
        return this.receivedData;
    }

    @Override
    public void setRemoteIntent(RemoteIntent remoteIntent) {
        this.incomingRemoteIntent = remoteIntent;
        this.receivedData.receivedData = true;
        this.receivedData.receivedDataType = D2DDataType.RemoteIntent;
    }

    @Override
    public RemoteIntent getRemoteIntent() {
        receivedData.receivedData = false;
        receivedData.receivedDataType = null;
        return this.incomingRemoteIntent;
    }

    @Override
    public void setIntent(Intent intent) {
        this.incomingIntent = intent;
        this.receivedData.receivedData = true;
        this.receivedData.receivedDataType = D2DDataType.Intent;
    }

    @Override
    public Intent getIntent() {
        receivedData.receivedData = false;
        receivedData.receivedDataType = null;
        return this.incomingIntent;
    }

    @Override
    public void setObject(Object object) {
        this.incomingObject = object;
        this.receivedData.receivedData = true;
        this.receivedData.receivedDataType = D2DDataType.Object;
    }

    @Override
    public Object getObject() {
        receivedData.receivedData = false;
        receivedData.receivedDataType = null;
        return this.incomingObject;
    }
}

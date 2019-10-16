package evolution.lancer.breno.riccitestbed.D2DCommunication;

import android.content.Intent;

import evolution.lancer.breno.ricci2lib.ricci.RemoteIntent;

public class D2DDataExchangeImplementation implements D2DDataExchangeInterface {

    private Boolean receivedData;

    public Boolean getReceivedData() {
        return this.receivedData;
    }

    @Override
    public void setRemoteIntent(RemoteIntent remoteIntent) {

    }

    @Override
    public RemoteIntent getRemoteIntent() {
        return null;
    }

    @Override
    public void setIntent(Intent intent) {

    }

    @Override
    public Intent getIntent() {
        return null;
    }

    @Override
    public void setObject(Object object) {

    }

    @Override
    public Object getObject() {
        return null;
    }
}

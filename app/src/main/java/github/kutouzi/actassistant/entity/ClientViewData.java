package github.kutouzi.actassistant.entity;

import github.kutouzi.actassistant.entity.inf.IData;

public class ClientViewData implements IData {
    private final String clientInfo;
    private final int clientPreview;

    public ClientViewData(String text, int clientPreview) {
        this.clientInfo = text;
        this.clientPreview = clientPreview;
    }

    public String getClientInfo() {
        return clientInfo;
    }

    public int getClientPreview() {
        return clientPreview;
    }
}

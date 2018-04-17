package notegator.notegator;

/**
 * Created by Alex on 2/25/2018.
 */

import java.util.ArrayList;

public class HeaderInfo {

    private String name;
    private ArrayList<DetailInfo> notifications;

    public HeaderInfo(String name) {
        notifications = new ArrayList<DetailInfo>();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<DetailInfo> getProductList() {
        return notifications;
    }

    public void setProductList(ArrayList<DetailInfo> notifications) {
        this.notifications = notifications;
    }
}
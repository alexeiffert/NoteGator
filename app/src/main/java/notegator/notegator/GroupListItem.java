package notegator.notegator;

/**
 * Created by Alex on 3/20/2018.
 * This class contains the data fo the Groups Recyclerview
 */

public class GroupListItem {
    private String header;
    private String text;

    public GroupListItem(String header, String text) {
        this.header = header;
        this.text = text;
    }

    public String getHeader() {
        return header;
    }

    public String getText() {
        return text;
    }
}

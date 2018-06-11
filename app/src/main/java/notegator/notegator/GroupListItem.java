package notegator.notegator;

/**
 * Created by Alex on 3/20/2018.
 * This class contains the data fo the Groups Recyclerview
 */

public class GroupListItem {
    private String header;
    private String time;
    private String text;

    public GroupListItem(String header, String time, String text) {
        this.header = header;
        this.time = time;
        this.text = text;
    }

    public String getHeader() {
        return header;
    }

    public String getTime(){
        return time;
    }

    public String getText() {
        return text;
    }
}

package notegator.notegator;

/**
 * Created by Alex on 2/25/2018.
 */

public class DetailInfo {

    private String date;
    private String text;
    private String sequence;
    private String thumbnail;

    public DetailInfo(String sequence, String text, String date, String thumbnail){
        this.date = date;
        this.text = text;
        this.sequence = sequence;
        this.thumbnail = thumbnail;
    }

    public String getDate(){
        return date;
    }
    public void setDate(String date){
        this.date = date;
    }

    public String getName() {
        return text;
    }
    public void setName(String text) {
        this.text = text;
    }

    public String getSequence() {
        return sequence;
    }
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getThumbnail() {
        return thumbnail;
    }
    public void setThumbnail(String thumbnail){
        this.thumbnail = thumbnail;
    }
}
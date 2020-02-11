package samer.ynote;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Note implements Serializable {
    private long ID;
    private String title;
    private String note;
    private String timestamp;
    private int listIndex;


    Note(String title, String note) {
        this.title = title;
        this.note = note;
        this.timestamp = timestamp();
        this.listIndex = 0;
    }

    Note() {
        this.title = "";
        this.note = "";
        this.timestamp = timestamp();
        this.listIndex = 0;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public int getListIndex() {
        return listIndex;
    }

    public void setListIndex(int listIndex) {
        this.listIndex = listIndex;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    private String timestamp() {
        String timeStampFormatted = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
        timeStampFormatted += " ";
        timeStampFormatted += new SimpleDateFormat("HH:mm").format(new Date());
        return timeStampFormatted;
    }

    @Override
    public String toString() {
        return "Note{" +
                "ID=" + ID +
                ", title='" + title + '\'' +
                ", note='" + note + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}

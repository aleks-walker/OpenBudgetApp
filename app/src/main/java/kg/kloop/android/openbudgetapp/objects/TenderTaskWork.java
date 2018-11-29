package kg.kloop.android.openbudgetapp.objects;

import java.io.Serializable;
import java.util.List;

public class TenderTaskWork implements Serializable {
    private String id;
    private String text;
    private List<String> photoUrlList;
    private User author;

    public TenderTaskWork() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getPhotoUrlList() {
        return photoUrlList;
    }

    public void setPhotoUrlList(List<String> photoUrlList) {
        this.photoUrlList = photoUrlList;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}

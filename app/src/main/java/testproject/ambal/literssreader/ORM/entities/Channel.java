package testproject.ambal.literssreader.ORM.entities;

/**
 * Created by Ambal on 17.07.14.
 */

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;
import java.util.List;

@DatabaseTable(tableName = "Channel")
public class Channel {

    @DatabaseField(generatedId = true)
    int id;
    @DatabaseField(index = true)
    String title;
    @DatabaseField
    String link;
    @DatabaseField
    String description;
    @DatabaseField
    String language;
    @DatabaseField
    String lastBuildDate;
    @ForeignCollectionField(eager = true)
    Collection<Item> items;


    public Channel() {
        // needed by ormlite
    }

    public Channel(int id, String title, String link, String description, String language, String lastBuildDate) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.description = description;
        this.language = language;
        this.lastBuildDate = lastBuildDate;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", description='" + description + '\'' +
                ", language='" + language + '\'' +
                ", lastBuildDate='" + lastBuildDate + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLastBuildDate() {
        return lastBuildDate;
    }

    public void setLastBuildDate(String lastBuildDate) {
        this.lastBuildDate = lastBuildDate;
    }

    public Collection<Item> getItems() { return items;  }

    public void setItems(List<Item> items) { this.items = items; }
}


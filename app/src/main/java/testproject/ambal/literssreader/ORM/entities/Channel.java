package testproject.ambal.literssreader.ORM.entities;

/**
 * Created by Ambal on 17.07.14.
 */

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collection;

import testproject.ambal.literssreader.ORM.HelperFactory;

@DatabaseTable(tableName = "Channel")
public class Channel implements Serializable{

    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
    int id;
    @DatabaseField
    String url;
    @DatabaseField
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


    public void addItem(Item item){
        item.setChannel(this);
        try {
            HelperFactory.getHelper().getItemDao().create(item);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        items.add(item);
    }


    public Channel() {
        // needed by ormlite
    }

    public Channel(int id, String url, String title, String link, String description, String language, String lastBuildDate, Collection<Item> items) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.link = link;
        this.description = description;
        this.language = language;
        this.lastBuildDate = lastBuildDate;
        this.items = items;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", url=" + url +
                ", title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", description='" + description + '\'' +
                ", language='" + language + '\'' +
                ", lastBuildDate='" + lastBuildDate + '\'' +
                ", items=" + items +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public void setItems(Collection<Item> items) { this.items = items; }
}


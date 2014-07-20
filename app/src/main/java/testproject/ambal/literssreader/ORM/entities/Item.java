package testproject.ambal.literssreader.ORM.entities;

/**
 * Created by Ambal on 17.07.14.
 */

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Item")
public class Item {

    @DatabaseField(generatedId = true)
    int id;
    @DatabaseField
    String title;
    @DatabaseField
    String link;
    @DatabaseField
    String description;
    @DatabaseField
    String author;
    @DatabaseField
    String category;
    @DatabaseField
    String enclosure;
    @DatabaseField
    String pubDate;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
    Channel channel;

    public Item() {
        // needed by ormlite
    }

    public Item(int id, String title, String link, String description, String author, String category, String enclosure, String pubDate, Channel channel) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.description = description;
        this.author = author;
        this.category = category;
        this.enclosure = enclosure;
        this.pubDate = pubDate;
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", description='" + description + '\'' +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                ", enclosure='" + enclosure + '\'' +
                ", pubDate='" + pubDate + '\'' +
                ", channel=" + channel +
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEnclosure() {
        return enclosure;
    }

    public void setEnclosure(String enclosure) {
        this.enclosure = enclosure;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}

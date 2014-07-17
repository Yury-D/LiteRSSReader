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
    @DatabaseField(index = true)
    String title;
    @DatabaseField
    String link;
    @DatabaseField
    String description;
    @DatabaseField
    String author;
    @DatabaseField
    String categoryDomain;
    @DatabaseField
    String enclosure;
    @DatabaseField
    String pubDate;
    @DatabaseField
    String imageLink;
    @DatabaseField(canBeNull = false, foreign = true)
    Channel channel;

    Item() {
        // needed by ormlite
    }

    public Item(int id, String title, String link, String description, String author, String categoryDomain, String enclosure, String pubDate, String imageLink, Channel channel) {
        this.id = id;
        this.title = title;
        this.link = link;
        this.description = description;
        this.author = author;
        this.categoryDomain = categoryDomain;
        this.enclosure = enclosure;
        this.pubDate = pubDate;
        this.imageLink = imageLink;
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
                ", categoryDomain='" + categoryDomain + '\'' +
                ", enclosure='" + enclosure + '\'' +
                ", pubDate='" + pubDate + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", channel=" + channel +
                '}';
    }
}

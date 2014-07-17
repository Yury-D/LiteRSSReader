package testproject.ambal.literssreader.ORM.entities;

/**
 * Created by Ambal on 17.07.14.
 */

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

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


    Channel() {
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
}


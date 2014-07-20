package testproject.ambal.literssreader.service;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import testproject.ambal.literssreader.ORM.entities.Channel;
import testproject.ambal.literssreader.ORM.entities.Item;

/**
 * Created by Ambal on 18.07.14.
 */
public class Parser {
    // RSS XML document CHANNEL tag
    private static String TAG_CHANNEL = "channel";
    private static String TAG_TITLE = "title";
    private static String TAG_LINK = "link";
    private static String TAG_DESRIPTION = "description";
    private static String TAG_LANGUAGE = "language";
    private static String TAG_ITEM = "item";
    private static String TAG_PUB_DATE = "pubDate";
    private static String TAG_ENCLOSURE = "enclosure";
    private static String TAG_LAST_BUILD_DATE = "lastBuildDate";
    private static String TAG_CATEGORY = "category";
    private static String TAG_AUTHOR = "atom:author";

    private static final String TAG = "mylogs";

    List<Item> items;
    private Item item;
    private String text;
    private Channel channel;

    public Parser() {
        items = new ArrayList<Item>();
    }

    public Channel parse(String target){
        XmlPullParserFactory factory = null;
        XmlPullParser myParser = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            // parse channel

            myParser = factory.newPullParser();
            // !!! Корявое преобразование строки в поток
            myParser.setInput(new ByteArrayInputStream(target.getBytes()), null);

            int eventType = myParser.getEventType();
            ChannelScan: while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = myParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase(TAG_CHANNEL)) {
                            // create a new instance of channel
                            channel = new Channel();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase(TAG_CHANNEL)) {
                            break;
                        } else if (tagname.equalsIgnoreCase(TAG_TITLE)) {
                            channel.setTitle(text);
                        } else if (tagname.equalsIgnoreCase(TAG_LINK)) {
                            channel.setLink(text);
                        } else if (tagname.equalsIgnoreCase(TAG_DESRIPTION)) {
                            channel.setDescription(text);
                        } else if (tagname.equalsIgnoreCase(TAG_LANGUAGE)) {
                            channel.setLanguage(text);
                        } else if (tagname.equalsIgnoreCase(TAG_LAST_BUILD_DATE)) {
                            channel.setLastBuildDate(text);
                            break ChannelScan;
                        }
                        break;
                    default:
                        break;
                }
                eventType = myParser.next();
            }

          // ------------------- parse items -----------------------------
            boolean insideItem = false;
            myParser = factory.newPullParser();
            myParser.setInput(new ByteArrayInputStream(target.getBytes()), null);

            eventType = myParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = myParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase(TAG_ITEM)) {
                            // create a new instance of item
                            item = new Item();
                            insideItem = true;
                        }
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (insideItem) {
                            if (tagname.equalsIgnoreCase(TAG_ITEM)) {
                                // add item object to list
                                item.setChannel(channel);
                                items.add(item);
                                insideItem = false;
                            } else if (tagname.equalsIgnoreCase(TAG_TITLE)) {
                                item.setTitle(text);
                            } else if (tagname.equalsIgnoreCase(TAG_LINK)) {
                                item.setLink(text);
                            } else if (tagname.equalsIgnoreCase(TAG_DESRIPTION)) {
                                item.setDescription(text);
                            } else if (tagname.equalsIgnoreCase(TAG_AUTHOR)) {
                                item.setAuthor(text);
                            } else if (tagname.equalsIgnoreCase(TAG_CATEGORY)) {
                                item.setCategory(text);
                            } else if (tagname.equalsIgnoreCase(TAG_ENCLOSURE)) {
                                item.setEnclosure(text);
                            } else if (tagname.equalsIgnoreCase(TAG_PUB_DATE)) {
                                item.setPubDate(text);
                            }
                        }
                        break;
                    default:
                        break;
                }
                eventType = myParser.next();
            }
            channel.setItems(items);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return channel;
    }
}

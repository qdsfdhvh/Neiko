package seiko.neiko.models;


import zlc.season.practicalrecyclerview.ItemType;

/**
 * Created by Seiko on 2016/10/2.YiKu
 */

public class Book implements ItemType {

    private String name;
    private String logo = "";
    private String url;

    private String author = "";

    private String section;
    private String section_url;

    private String sited;
    private String key;
    private String source;
    private String bkey;

    private int type;

    private String path;
    private int index;

    private int section_count;
    private String last_surl;
    private int last_pidx;
    private int last_page;


    private int width;
    private int height;

    private int number;

    public Book() {
    }

    public Book(String name, String logo, String url, int type, String source, String bkey) {
        this.name = name;
        this.logo = logo;
        this.url = url;
        this.type = type;
        this.source = source;
        this.bkey = bkey;
    }

    public Book(String name, String logo, String url, String author) {
        this.name = name;
        this.logo = logo;
        this.url = url;
        this.author = author;
    }

    public Book(String name, String logo, String url, String author, int type, String source) {
        this.name = name;
        this.logo = logo;
        this.url = url;
        this.author = author;
        this.type = type;
        this.source = source;
    }

    public Book(String name, String logo, String url, String sited, String key) {
        this.name = name;
        this.logo = logo;
        this.url = url;
        this.sited = sited;
        this.key = key;
    }

    public Book(String name, String logo, String url) {
        this.name = name;
        this.logo = logo;
        this.url = url;
    }

    public Book(String section, String section_url) {
        this.section = section;
        this.section_url = section_url;
    }

    public Book(String section, String section_url, int index) {
        this.section = section;
        this.section_url = section_url;
        this.index = index;
    }

    public Book(String section_url) {
        this.section_url = section_url;
    }



    //接收
    public void setName(String name) {
        this.name = name;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public void setSection_url(String section_url) {
        this.section_url = section_url;
    }


    public void setSited(String sited) {
        this.sited = sited;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setBkey(String bkey) {
        this.bkey = bkey;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    //提供
    public String getName() {
        return name;
    }

    public String getLogo() {
        return logo;
    }

    public String getUrl() {
        return url;
    }

    public String getSection() {
        return section;
    }

    public String getSection_url() {
        return section_url;
    }

    public String getSited() {
        return sited;
    }

    public String getKey() {
        return key;
    }

    public String getAuthor() {
        return author;
    }

    public int getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public String getBkey() {
        return bkey;
    }

    public String getPath() {
        return path;
    }

    public int getIndex() {
        return index;
    }

    public int getNumber() {
        return number;
    }

    //==============================================

    public int getSection_count() {
        return section_count;
    }

    public String getLast_surl() {
        return last_surl;
    }

    public int getLast_pidx() {
        return last_pidx;
    }

    public int getLast_page() {
        return last_page;
    }

    @Override
    public int itemType() {
        return 0;
    }

    //==============================================

    private boolean isref;

    public void setIsref(boolean isref) {
        this.isref = isref;
    }

    public boolean isref() {
        return isref;
    }



    private boolean isNew;

    public void setNew(boolean aNew) {
        isNew = aNew;
    }

    public boolean isNew() {
        return isNew;
    }

    //================================================

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}

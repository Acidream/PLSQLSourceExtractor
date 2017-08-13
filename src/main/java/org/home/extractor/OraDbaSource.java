package org.home.extractor;

/**
 * Created by oleg on 2017-08-06.
 */
public class OraDbaSource {
    public OraDbaSource() {
    }

    String owner;
    String name;
    String type;
    Long line;
    String text;
    Long origin_con_id;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getLine() {
        return line;
    }

    public void setLine(Long line) {
        this.line = line;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getOrigin_con_id() {
        return origin_con_id;
    }

    public void setOrigin_con_id(Long origin_con_id) {
        this.origin_con_id = origin_con_id;
    }

    @Override
    public String toString() {
        return "OraDbaSource{" +
                "owner='" + owner + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}

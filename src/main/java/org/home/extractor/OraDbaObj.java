package org.home.extractor;

/**
 * Created by oleg on 2017-08-06.
 */
public class OraDbaObj {
    public OraDbaObj() {
    }

    String owner;
    String name;
    String type;


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


    @Override
    public String toString() {
        return "OraDbaSource{" +
                "owner='" + owner + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +

                '}';
    }
}

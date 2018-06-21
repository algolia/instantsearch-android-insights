package com.algolia.insights;


public class ListItem {

    private String name;
    private String image;
    private String queryId;
    private String objectId;
    private int position;

    ListItem(String name, String image, String queryId, String objectId, int position) {
        this.name = name;
        this.image = image;
        this.queryId = queryId;
        this.objectId = objectId;
        this.position = position;
    }

    String getName() {
        return name;
    }

    String getImage() {
        return image;
    }

    String getQueryId() {
        return queryId;
    }

    String getObjectId() {
        return objectId;
    }

    int getPosition() {
        return position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListItem listItem = (ListItem) o;

        if (position != listItem.position) return false;
        if (!name.equals(listItem.name)) return false;
        if (!image.equals(listItem.image)) return false;
        if (!queryId.equals(listItem.queryId)) return false;
        return objectId.equals(listItem.objectId);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + image.hashCode();
        result = 31 * result + queryId.hashCode();
        result = 31 * result + objectId.hashCode();
        result = 31 * result + position;
        return result;
    }
}

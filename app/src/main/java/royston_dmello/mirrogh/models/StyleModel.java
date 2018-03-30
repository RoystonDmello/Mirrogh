package royston_dmello.mirrogh.models;

public class StyleModel {

    private String id, thumbnail_url;

    public StyleModel() {
    }

    public StyleModel(String id, String thumbnail_url) {
        this.id = id;
        this.thumbnail_url = thumbnail_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

}

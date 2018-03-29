package royston_dmello.mirrogh.models;

import java.io.File;

public class StyleModel {

    private File file;
    private String id;

    public StyleModel() {
    }

    public StyleModel(File file, String id) {
        this.file = file;
        this.id = id;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

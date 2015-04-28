package it.univaq.mwt.xml.epubmanager.business.model;

import java.io.Serializable;

public class EpubImage extends EpubResource implements Serializable {
    
    public EpubImage(String id, String name, String path, byte[] file, String type) {
        super(id, name, path, file, type);
    }

    public EpubImage(String name, String path, byte[] file, String type) {
        super(name, path, file, type);
    }

    @Override
    public String toString() {
        return "EpubImage{" + "id=" + super.getId() + ", name=" + super.getName() + ", path=" + super.getPath() + ", file=" + super.getFile() + '}';
    }
}
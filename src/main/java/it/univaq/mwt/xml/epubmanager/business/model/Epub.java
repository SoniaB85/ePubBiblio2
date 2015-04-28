package it.univaq.mwt.xml.epubmanager.business.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Epub implements Serializable{
    
    private long id;
    
    private Metadata metadata;
    
    private List<EpubXhtml> epubXhtmls = new ArrayList<EpubXhtml>();
    private List<EpubCss> epubCsss = new ArrayList<EpubCss>();
    private List<EpubImage> epubImages = new ArrayList<EpubImage>();

    public Epub() {
    }
    
    public void addEpubXhtml(EpubXhtml epubXhtml) {
        this.epubXhtmls.add(epubXhtml);
    }
    
    public void addEpubCss(EpubCss epubCss) {
        this.epubCsss.add(epubCss);
    }
    
    public void addEpubImage(EpubImage epubImage) {
        this.epubImages.add(epubImage);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public List<EpubXhtml> getEpubXhtmls() {
        return epubXhtmls;
    }

    public void setEpubXhtmls(List<EpubXhtml> epubXhtmls) {
        this.epubXhtmls = epubXhtmls;
    }

    public List<EpubCss> getEpubCsss() {
        return epubCsss;
    }

    public void setEpubCsss(List<EpubCss> epubCsss) {
        this.epubCsss = epubCsss;
    }

    public List<EpubImage> getEpubImages() {
        return epubImages;
    }

    public void setEpubImages(List<EpubImage> epubImages) {
        this.epubImages = epubImages;
    }
    
}

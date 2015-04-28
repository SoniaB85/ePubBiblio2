package it.univaq.mwt.xml.epubmanager.business;

import it.univaq.mwt.xml.epubmanager.business.model.Epub;
import org.w3c.dom.Document;

/**
 *
 * @author zilfio
 */
public interface DOMService {
    
    public Document createContentXMLFile(Epub epub, String path) throws BusinessException;
    
    public Document createContainerXMLFile (String path) throws BusinessException;
    
    public Document createTocNcxXMLFile (Epub epub, String path) throws BusinessException;
    
}

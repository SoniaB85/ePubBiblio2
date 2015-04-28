package it.univaq.mwt.xml.epubmanager.business;

import it.univaq.mwt.xml.epubmanager.business.model.Epub;

/**
 *
 * @author zilfio
 */
public interface StAXService {
    
    public void createContentXMLFile(Epub epub, String path) throws BusinessException;
    
    public void createContainerXMLFile (String path) throws BusinessException;
    
    public void createTocNcxXMLFile (Epub epub, String path) throws BusinessException;
    
}

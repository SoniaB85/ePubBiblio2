package it.univaq.mwt.xml.epubmanager.business.impl;

import it.univaq.mwt.xml.epubmanager.business.BusinessException;
import it.univaq.mwt.xml.epubmanager.business.DOMService;
import it.univaq.mwt.xml.epubmanager.business.model.Epub;
import it.univaq.mwt.xml.epubmanager.common.utility.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author zilfio
 */
public class DOMServiceImpl implements DOMService {

    @Override
    public Document createContentXMLFile(Epub epub, String path) throws BusinessException {
        Document d = XMLUtil.createDocument();
        if (d != null) {
            Element e = d.createElement("package");
            e.setAttribute("xmlns", "http://www.idpf.org/2007/opf");
            e.setAttribute("unique-identifier", epub.getMetadata().getIdentifier());
            e.setAttribute("version", "2.0");
            d.appendChild(e);
            
        }
        return d;
    }

    @Override
    public Document createContainerXMLFile(String path) throws BusinessException {
        Document d = XMLUtil.createDocument();
        if (d != null) {
            Element c = d.createElement("container");
            c.setAttribute("version", "1.0");
            c.setAttribute("xmlns", "urn:oasis:names:tc:opendocument:xmlns:container");
            Element r = d.createElement("rootfiles");
            Element r1 = d.createElement("rootfile");
            r1.setAttribute("full-path", "content.opf");
            r1.setAttribute("media-type", "application/oebps-package+xml");
            d.appendChild(c);
            c.appendChild(r);
            r.appendChild(r1);
        }
        return d;
    }

    @Override
    public Document createTocNcxXMLFile(Epub epub, String path) throws BusinessException {
        return null;
    }
    
}

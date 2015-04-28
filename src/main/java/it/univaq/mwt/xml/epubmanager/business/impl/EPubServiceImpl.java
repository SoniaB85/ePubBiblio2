package it.univaq.mwt.xml.epubmanager.business.impl;

import it.univaq.mwt.xml.epubmanager.business.BusinessException;
import it.univaq.mwt.xml.epubmanager.business.EPubService;
import it.univaq.mwt.xml.epubmanager.business.model.Epub;
import it.univaq.mwt.xml.epubmanager.business.model.EpubCss;
import it.univaq.mwt.xml.epubmanager.business.model.EpubImage;
import it.univaq.mwt.xml.epubmanager.business.model.EpubXhtml;
import it.univaq.mwt.xml.epubmanager.business.model.Metadata;
import it.univaq.mwt.xml.epubmanager.common.utility.DirectoryUtil;
import it.univaq.mwt.xml.epubmanager.common.utility.XMLUtil;
import it.univaq.mwt.xml.epubmanager.common.utility.ZipUtil;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EPubServiceImpl implements EPubService{
    
    @Value("#{cfgproperties.uploadDirectory}")
    private String UPLOAD_DIR;
    
    @Override
    public long startEpub(Metadata metadata) throws BusinessException {
        try {
            // svuoto la cartella temporanea (temp) dove vado ad uplodare tutti i file che compongono il mio epub
            FileUtils.cleanDirectory(new File(UPLOAD_DIR));
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Impossibile trovare il percorso specificato!", ex);
        } catch (IOException ex) {
            throw new BusinessException("Errore di I/O!", ex);
        }
        long result = Long.parseLong(metadata.getIdentifier());
        
        // creo una cartella con l'isbn dell'epub
        DirectoryUtil.mkdir(UPLOAD_DIR, metadata.getIdentifier());
        
        return result;
    }

    @Override
    public String addXHTML(long token, EpubXhtml epubXhtml) throws BusinessException {
        System.out.println("TOKEN: "+token);
        
        if (epubXhtml.getId() != null) {
            //creating a temp file
            File temp = new File(UPLOAD_DIR + Long.toString(token) + File.separator + epubXhtml.getPath());
            try {
                //saving image to file
                FileUtils.writeByteArrayToFile(temp, epubXhtml.getFile());
                System.out.println("Upload dei file " + epubXhtml.getPath()+ " avvenuto con successo!");
            } catch (IOException ex) {
                throw new BusinessException("Upload del file " + epubXhtml.getPath() + " fallito!", ex);
            }

            // controllo che il file sia valido
            boolean b = XMLUtil.validateXhtml(temp, true);
            System.out.println("ERRORI NELLA VALIDAZIONE? "+b);

            // cancello il file se ho errori di validazione
            /*if(b == true) {
                System.out.println("Cancello il file uplodato: non è valido!");
                temp.delete();
            }*/

            return epubXhtml.getId();
        } else {
            return null;
        }
    }

    @Override
    public String addStylesheet(long token, EpubCss epubCss) throws BusinessException {
        System.out.println("TOKEN: "+token);
        
        if (epubCss.getId() != null) {
            //creating a temp file
            File temp = new File(UPLOAD_DIR + Long.toString(token) + File.separator + epubCss.getPath());
            try {
                //saving image to file
                FileUtils.writeByteArrayToFile(temp, epubCss.getFile());
                System.out.println("Upload del file " + epubCss.getPath()+ " avvenuto con successo!");
            } catch (IOException ex) {
                throw new BusinessException("Upload del file " + epubCss.getPath() + " fallito!", ex);
            }
            //getting image format
            //format =  ImageUtil.getFormat(temp);
            return epubCss.getId();
        } else {
            return null;
        }
    }

    @Override
    public String addImage(long token, EpubImage epubImage) throws BusinessException {
        System.out.println("TOKEN: "+token);
        
        if (epubImage.getId() != null) {
            //creating a temp file
            File temp = new File(UPLOAD_DIR + Long.toString(token) + File.separator + epubImage.getPath());
            try {
                //saving image to file
                FileUtils.writeByteArrayToFile(temp, epubImage.getFile());
                System.out.println("Upload del file " + epubImage.getPath()+ " avvenuto con successo!");
            } catch (IOException ex) {
                throw new BusinessException("Upload del file " + epubImage.getPath() + " fallito!", ex);
            }
            //getting image format
            //format =  ImageUtil.getFormat(temp);
            return epubImage.getId();
        } else {
            return null;
        }
        
    }

    @Override
    public boolean removeElement(long token, String element) throws BusinessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setToc(long token, List<String> navpoints) throws BusinessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void finalizeEpub(long token) throws BusinessException {
        String upload = UPLOAD_DIR + Long.toString(token) + File.separator;
        ZipUtil.createArchiveFile(upload, Long.toString(token));
    }

    @Override
    public EpubXhtml getEpubXhtmlById(List<EpubXhtml> list, String id) {
        for (EpubXhtml epubXhtml : list) {
            if(epubXhtml.getId().equals(id)) {
                return epubXhtml;
            }
        }
        return null;
    }

    @Override
    public void updateEpubXhtml(Epub epub, EpubXhtml epubXhtml) {
        try {
            EpubXhtml e = getEpubXhtmlById(epub.getEpubXhtmls(), epubXhtml.getId());
            BeanUtils.copyProperties(e, epubXhtml);
        } catch (IllegalAccessException ex) {
            throw new BusinessException("IllegalAccessException", ex);
        } catch (InvocationTargetException ex) {
            throw new BusinessException("InvocationTargetException", ex);
        }
    }

    @Override
    public void deleteEpubXhtml(Epub epub, EpubXhtml epubXhtml) {
        epub.getEpubXhtmls().remove(epubXhtml);
    }
     
}

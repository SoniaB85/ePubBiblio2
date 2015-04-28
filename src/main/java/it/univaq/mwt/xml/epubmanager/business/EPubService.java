package it.univaq.mwt.xml.epubmanager.business;

import it.univaq.mwt.xml.epubmanager.business.model.Epub;
import it.univaq.mwt.xml.epubmanager.business.model.EpubCss;
import it.univaq.mwt.xml.epubmanager.business.model.EpubImage;
import it.univaq.mwt.xml.epubmanager.business.model.EpubXhtml;
import it.univaq.mwt.xml.epubmanager.business.model.Metadata;
import java.util.List;

public interface EPubService {
    
    /**
     * 
     * Avvia una sessione di generazione per un nuovo ebook, i cui metadati sono specificati dalla
     *  struttura passata in input.
     *  @param metadata metadati dell'epub
     *  @return La funzione restituisce un identificatore unico di sessione, che sarà
     *  utilizzato per contestualizzare tutte le chiamate agli altri metodi.
     * @throws BusinessException 
     */
    long startEpub (Metadata metadata) throws BusinessException;   
    
    /**
     * Aggiunge un file XHTML all’ebook specificato dal token. L’ordine di aggiunta di questi file
     * determinerà anche quello indicato dalla spine dell’ePub.
     * @param token rappresenta l'ebook
     * @param epubXhtml rappresenta il file XHTML da aggiungere
     * @return L'intero restituito dovrà rappresentare in maniera univoca l’elemento appena inserito.
     * @throws BusinessException 
     */
    String addXHTML (long token, EpubXhtml epubXhtml) throws BusinessException; 
    
    /**
     * Aggiunge un file CSS (foglio di stile) all’ebook specificato dal token.
     * @param token rappresenta l'ebook
     * @param epubCss rappresenta il file CSS da aggiungere
     * @return  L'intero restituito dovrà rappresentare in maniera univoca l’elemento appena inserito.
     * @throws BusinessException 
     */
    String addStylesheet (long token, EpubCss epubCss) throws BusinessException;
    
    /**
     * Aggiunge un’immagine del tipo specificato all’interno dell’ebook specificato dal token.
     * @param token rappresenta l'ebook
     * @param epubImage rappresenta l'immagine da aggiungere
     * @return L'intero restituito dovrà rappresentare in maniera univoca l’elemento appena inserito.
     * @throws BusinessException 
     */
    String addImage (long token, EpubImage epubImage) throws BusinessException;
    
    /**
     * Rimuove dall’ebook specificato dal token il file indicato dall’intero element (così come restituito da
     * una delle tre funzioni precedenti).
     * @param token
     * @param element
     * @return Restituisce true se l’operazione è andata a buon fine.
     * @throws BusinessException 
     */
    boolean removeElement (long token, String element) throws BusinessException;

    /**
     * Definisce la tavola dei contenuti (toc.ncx) dell'ebook specificato dal token. Ogni stringa nella lista
     * sarà la URI di un elemento dell'ebook, ad esempio "page2.html" o "page3.html#locA".
     * @param token
     * @param navpoints 
     * @throws BusinessException 
     */
    void setToc (long token, List<String> navpoints) throws BusinessException;

    /**
     * Genera l'ebook specificato dal token, dopo aver eventualmente eseguito delle verifiche di
     * correttezza (le stesse che sono eseguite nel progetto ePub Manager prima della generazione
     * finale)
     * @param token
     * @throws BusinessException 
     */
    void finalizeEpub (long token) throws BusinessException;
    
    EpubXhtml getEpubXhtmlById (List<EpubXhtml> list, String id);
    
    void updateEpubXhtml (Epub epub, EpubXhtml epubXhtml);
    
    void deleteEpubXhtml (Epub epub, EpubXhtml epubXhtml);
}
package it.univaq.mwt.xml.epubmanager.presentation.packager;

import it.univaq.mwt.xml.epubmanager.business.BusinessException;
import it.univaq.mwt.xml.epubmanager.business.StAXService;
import it.univaq.mwt.xml.epubmanager.business.impl.EPubServiceImpl;
import it.univaq.mwt.xml.epubmanager.business.model.Epub;
import it.univaq.mwt.xml.epubmanager.business.model.EpubCss;
import it.univaq.mwt.xml.epubmanager.business.model.EpubImage;
import it.univaq.mwt.xml.epubmanager.business.model.EpubResource;
import it.univaq.mwt.xml.epubmanager.business.model.EpubXhtml;
import it.univaq.mwt.xml.epubmanager.business.model.Metadata;
import it.univaq.mwt.xml.epubmanager.common.utility.DirectoryUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/packager")
public class EpubPackagerController {

    private static Epub EPUB;

    @Value("#{cfgproperties.uploadDirectory}")
    private String UPLOAD_DIR;

    @Value("#{cfgproperties.directoryMETAINF}")
    private String METAINF_DIR;

    @Autowired
    private EPubServiceImpl service;

    @Autowired
    private StAXService StAXService;

    @Autowired
    private FormMetadataValidator metadatavalidator;

    @RequestMapping(value = "/create", method = {RequestMethod.GET})
    public String createMetadataStart(Model model) {
        
        EPUB = new Epub();
        
        model.addAttribute("metadata", new Metadata());
        return "metadata.createform";
    }

    @RequestMapping(value = "/create", method = {RequestMethod.POST})
    public String createMetadata(@ModelAttribute Metadata metadata, BindingResult bindingResult) throws BusinessException {
        metadatavalidator.validate(metadata, bindingResult);
        if (bindingResult.hasErrors()) {
            return "metadata.createform";
        }
        long id = service.startEpub(metadata);
        System.out.println("ID univoco per le altre chiamate: " + id);
        // memorizzo in sessione l'id univoco che mi serve per le altre chiamate
        EPUB.setId(id);
        EPUB.setMetadata(metadata);
        return "redirect:/packager/uploadresources";
    }

    @RequestMapping(value = "/uploadresources", method = {RequestMethod.GET})
    public String uploadResourcesStart() {
        System.out.println(EPUB.getMetadata().getIdentifier());
        System.out.println(EPUB.getMetadata().getTitle());
        System.out.println(EPUB.getMetadata().getLanguage());
        return "uploadresources.createform";
    }

    @RequestMapping(value = "/uploadresources", method = {RequestMethod.POST})
    public String uploadResources(@RequestParam("xhtmlfiles") MultipartFile[] xhtmlfiles,
            @RequestParam("cssfiles") MultipartFile[] cssfiles,
            @RequestParam("imagefiles") MultipartFile[] imagefiles) throws BusinessException {

        Map<EpubResource, Boolean> success = new HashMap<EpubResource, Boolean>();
        Map<EpubResource, Boolean> unsuccess = new HashMap<EpubResource, Boolean>();
        
        // mantine l'ordine di inserimento dei file xhtml
        int order = 0;
        
        // gestione upload dei file XHTML
        for (MultipartFile xhtmlfile : xhtmlfiles) {
            if (!xhtmlfile.isEmpty()) {
                /*
                try {
                    String out = FileUtil.fileToString(xhtmlfile.getInputStream());
                    System.out.println("zilfio: "+out);
                } catch (IOException ex) {
                    Logger.getLogger(EpubPackagerController.class.getName()).log(Level.SEVERE, null, ex);
                }*/
                
                try {
                    byte[] bytes = xhtmlfile.getBytes();
                    EpubXhtml epubXhtml = new EpubXhtml(UUID.randomUUID().toString(), FilenameUtils.getBaseName(xhtmlfile.getOriginalFilename()), xhtmlfile.getOriginalFilename(), bytes, ++order, "application/xhtml+xml", null);
                    System.out.println(epubXhtml.toString());
                    // validazione file xhtml prima dell'upload
                    String result = service.addXHTML(EPUB.getId(), epubXhtml);
                    if (result != null) {
                        EPUB.addEpubXhtml(epubXhtml);
                        success.put(epubXhtml, Boolean.TRUE);
                    } else {
                        unsuccess.put(epubXhtml, Boolean.FALSE);
                    }
                } catch (IOException e) {
                    throw new BusinessException("Errore I/O", e);
                }
            }
        }

        // gestione upload dei file CSS
        for (MultipartFile cssfile : cssfiles) {
            if (!cssfile.isEmpty()) {
                try {
                    byte[] bytes = cssfile.getBytes();
                    EpubCss epubCss = new EpubCss(UUID.randomUUID().toString(), FilenameUtils.getBaseName(cssfile.getOriginalFilename()), cssfile.getOriginalFilename(), bytes, cssfile.getContentType());
                    System.out.println(epubCss.toString());
                    String result = service.addStylesheet(EPUB.getId(), epubCss);
                    if (result != null) {
                        EPUB.addEpubCss(epubCss);
                        success.put(epubCss, Boolean.TRUE);
                    } else {
                        unsuccess.put(epubCss, Boolean.FALSE);
                    }
                } catch (IOException e) {
                    throw new BusinessException("Errore I/O", e);
                }
            }
        }

        // gestione upload delle immagini
        for (MultipartFile imagefile : imagefiles) {
            if (!imagefile.isEmpty()) {
                try {
                    byte[] bytes = imagefile.getBytes();
                    EpubImage epubImage = new EpubImage(UUID.randomUUID().toString(), FilenameUtils.getBaseName(imagefile.getOriginalFilename()), imagefile.getOriginalFilename(), bytes, imagefile.getContentType());
                    System.out.println(epubImage.toString());
                    String result = service.addImage(EPUB.getId(), epubImage);
                    if (result != null) {
                        EPUB.addEpubImage(epubImage);
                        success.put(epubImage, Boolean.TRUE);
                    } else {
                        unsuccess.put(epubImage, Boolean.FALSE);
                    }
                } catch (IOException e) {
                    throw new BusinessException("Errore I/O", e);
                }
            }
        }

        for (Map.Entry<EpubResource, Boolean> entrySuccess : success.entrySet()) {
            System.out.println(entrySuccess.getKey().getId() + " - " + entrySuccess.getValue());
        }

        for (Map.Entry<EpubResource, Boolean> entryUnsuccess : unsuccess.entrySet()) {
            System.out.println(entryUnsuccess.getKey().getId() + " - " + entryUnsuccess.getValue());
        }

        List<EpubXhtml> epubXhtmls = EPUB.getEpubXhtmls();
        for (EpubXhtml epubXhtml : epubXhtmls) {
            System.out.println(epubXhtml.getId() + " " + epubXhtml.getPath());
        }

        //service.finalizeEpub(EPUB.getId());
        if (unsuccess.isEmpty()) {
            return "redirect:/packager/orderresources";
        } else {
            return "redirect:/packager/uploadresources";
        }

    }

    @RequestMapping(value = "/orderresources", method = {RequestMethod.GET})
    public String orderResourcesStart(Model model) {

        model.addAttribute("sortingXhtmlFiles", EPUB.getEpubXhtmls());

        return "orderresorces.views";
    }

    @RequestMapping(value = "/orderresources/update", method = {RequestMethod.GET})
    public String updateOrderXhtmlStart(@RequestParam("id") String id, Model model) {
        EpubXhtml epubXhtml = service.getEpubXhtmlById(EPUB.getEpubXhtmls(), id);
        model.addAttribute("epubXhtml", epubXhtml);

        return "orderresorces.updateform";
    }

    @RequestMapping(value = "/orderresources/update", method = {RequestMethod.POST})
    public String updateOrderXhtml(@ModelAttribute EpubXhtml epubXhtml) {
        // setto il campo file del nuovo oggetto modificato al valore precedente
        epubXhtml.setFile(service.getEpubXhtmlById(EPUB.getEpubXhtmls(), epubXhtml.getId()).getFile());
        service.updateEpubXhtml(EPUB, epubXhtml);

        // valori dopo la modifica
        List<EpubXhtml> epubXhtmls = EPUB.getEpubXhtmls();
        EpubXhtml epubXhtmlById = service.getEpubXhtmlById(epubXhtmls, epubXhtml.getId());
        System.out.println("valori dopo la modifica");
        System.out.println(epubXhtmlById.getId());
        System.out.println(epubXhtmlById.getName());
        System.out.println(epubXhtmlById.getPath());
        System.out.println(epubXhtmlById.getFile());
        System.out.println(epubXhtmlById.getIndex());

        return "redirect:/packager/orderresources";
    }

    @RequestMapping(value = "/orderresources/delete", method = {RequestMethod.GET})
    public String deleteOrderXhtmlStart(@RequestParam("id") String id, Model model) {
        EpubXhtml epubXhtml = service.getEpubXhtmlById(EPUB.getEpubXhtmls(), id);
        model.addAttribute("epubXhtml", epubXhtml);

        return "orderresorces.deleteform";
    }

    @RequestMapping(value = "/orderresources/delete", method = {RequestMethod.POST})
    public String deleteOrderXhtml(@ModelAttribute EpubXhtml epubXhtml) {
        service.updateEpubXhtml(EPUB, epubXhtml);

        return "redirect:/packager/orderresources";
    }

    @RequestMapping(value = "/orderok", method = {RequestMethod.GET})
    public String orderResources(Model model) {
        
        String upload = UPLOAD_DIR + EPUB.getMetadata().getIdentifier() + File.separator;

        System.out.println("ordinamento tutto ok!");

        // creo la cartella META-INF
        DirectoryUtil.mkdir(upload, METAINF_DIR);

        // creo il file toc.ncx
        StAXService.createTocNcxXMLFile(EPUB, upload);

        // creo il file content.opf
        StAXService.createContentXMLFile(EPUB, upload);

        // creo il file container.xml
        StAXService.createContainerXMLFile(upload + METAINF_DIR + File.separator);

        service.finalizeEpub(EPUB.getId());

        model.addAttribute("upload", upload);
        model.addAttribute("id", EPUB.getId());
        
        return "download.epub";
    }
    /**
     * Gestione del download dell'Epub
     */
    @RequestMapping(value="/downloadEpub", method={RequestMethod.GET})
    public void dowloadEpub(Model model, HttpServletResponse response) throws BusinessException, FileNotFoundException, IOException {
        String downloadPath = UPLOAD_DIR + EPUB.getId() + File.separator + EPUB.getId() + ".epub";
        File epubToDownlod = new File(downloadPath);
        InputStream input = new FileInputStream(epubToDownlod);
        response.setContentType("application/force-download");
	response.setHeader("Content-Disposition", "attachment; filename=" + epubToDownlod.getName());
        IOUtils.copy(input, response.getOutputStream());
        response.flushBuffer();
    }
        

    
}

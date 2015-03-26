import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roman Bachmann on 26.03.2015.
 */
public class PageManager {

    private static PageManager instance = null;
    private List<Page> alreadyVisitedPages;


    public static PageManager getInstance() {
        if (instance == null) {
            instance = new PageManager();
        }
        return instance;
    }

    private PageManager() {
        alreadyVisitedPages = loadPersistedPages();
    }

    public List<Page> getAllPages() {
        return alreadyVisitedPages;
    }

    public Page getPageByUrl(String url) {
        Page page = null;

        for (Page alreadyVisitedPage : alreadyVisitedPages) {
            if (page == null) {
                if (alreadyVisitedPage.getUrl().equals(url)) {
                    page = alreadyVisitedPage;
                }
            }
        }
        return page;
    }

    public boolean pageIsDeadEnd(String currentUrl) {
        return (getPageByUrl(currentUrl) != null) && getPageByUrl(currentUrl).getDestinationReacher() == DestinationReacher.IMPOSSIBLE;
    }

    public void addPage(Page pageToAdd) {
        boolean alreadAdded = false;

        for (Page page : alreadyVisitedPages) {
            if (!alreadAdded) {
                if (page.getUrl().equals(pageToAdd.getUrl())) {
                    alreadAdded = true;
                }
            }
        }

        if (!alreadAdded) {
            alreadyVisitedPages.add(pageToAdd);
        }
    }

    public void updatePage(String pageToUpdate, DestinationReacher possible, int linksToDestinationCount) {
        for (int i = 0; i < alreadyVisitedPages.size(); i++) {
            if (alreadyVisitedPages.get(i).getUrl().equals(pageToUpdate)) {
                alreadyVisitedPages.get(i).setDestinationReacher(possible);
                alreadyVisitedPages.get(i).setLinksToDestinationCount(linksToDestinationCount);
            }
        }
    }

    public void updatePage(String pageToUpdate, DestinationReacher impossible) {
        for (int i = 0; i < alreadyVisitedPages.size(); i++) {
            if (alreadyVisitedPages.get(i).getUrl().equals(pageToUpdate)) {
                alreadyVisitedPages.get(i).setDestinationReacher(impossible);
            }
        }
    }


    private List<Page> loadPersistedPages() {
        List<Page> pages = new ArrayList<>();
        try {
            File xml = new File("pages.xml");
            XStream xstream = new XStream(new DomDriver());
            pages = (List<Page>) xstream.fromXML(xml);
        } catch (Exception e) {
        }
        return pages;
    }

    public void persistPages() {
        try {
            PrintWriter out = new PrintWriter("pages.xml");

            XStream xstream = new XStream(new DomDriver());
            xstream.toXML(alreadyVisitedPages, out);

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
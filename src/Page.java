import java.io.Serializable;
import java.util.Date;

/**
 * Created by Roman Bachmann on 26.03.2015.
 */
public class Page implements Serializable {

    private String url;
    private int linksToDestinationCount;
    private Date lastTimeVisited;
    private DestinationReacher destinationReacher;

    public Page(String url) {
        this.url = url;
        this.linksToDestinationCount = -1;
        this.lastTimeVisited = new Date();
        this.destinationReacher = DestinationReacher.UNKNOWN;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLinksToDestinationCount() {
        return linksToDestinationCount;
    }

    public void setLinksToDestinationCount(int linksToDestinationCount) {
        this.linksToDestinationCount = linksToDestinationCount;
    }

    public Date getLastTimeVisited() {
        return lastTimeVisited;
    }

    public void setLastTimeVisited(Date lastTimeVisited) {
        this.lastTimeVisited = lastTimeVisited;
    }

    public DestinationReacher getDestinationReacher() {
        return destinationReacher;
    }

    public void setDestinationReacher(DestinationReacher destinationReacher) {
        this.destinationReacher = destinationReacher;
    }
}

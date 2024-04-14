package est.shorturl;

public class ShortModel {

    private String originalUrl;
    private String shortUrl;

    /**
     * Getter for original URL
     * @return
     */
    public String getOriginalUrl() {
        return originalUrl;
    }

    /**
     * Setter for original URL
     * @param originalUrl
     */
    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    /**
     * Getter for short link
     * @return
     */
    public String getShortUrl() {
        return shortUrl;
    }

    /**
     * Setter for short link
     * @param shortLink
     */
    public void setShortUrl(String shortLink) {
        this.shortUrl = shortLink;
    }
}

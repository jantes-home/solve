package est.shorturl;

public class UnknownURLException extends Exception {

    /**
     * @param url
     */
    public UnknownURLException(String url) {
        super("Unknown URL: \""+url+"\"");
    }
    
}

package est.shorturl;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ShortController {

    private static final ShortStore SHORT_URLS = new ShortStore();

     /**
     * @param url
     * @return
     * @throws UnknownURLException 
     * @throws URISyntaxException 
     * @throws MalformedURLException 
     */
    @GetMapping("/encode")
    public ResponseEntity<ShortModel> encodeUrlParameter(@RequestParam String url) throws MalformedURLException, URISyntaxException {
        // validate the URL
        new URL(url).toURI();

        ShortModel model = new ShortModel();
        model.setOriginalUrl(url);
        model.setShortUrl(SHORT_URLS.store(url));

        return new ResponseEntity<>(model, HttpStatus.OK);
    }


    /**
     * @param url
     * @return
     * @throws UnknownURLException 
     * @throws URISyntaxException 
     * @throws MalformedURLException 
     */
    @GetMapping("/decode")
    public ResponseEntity<ShortModel> decodeString(@RequestParam String url) throws UnknownURLException, MalformedURLException, URISyntaxException {
        // validate the URL
        new URL(url).toURI();

        ShortModel model = new ShortModel();
        model.setShortUrl(url);
        model.setOriginalUrl(SHORT_URLS.fetch(url));

        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    /**
     * 
     * @param ex
     * @return
     */
    @ExceptionHandler(UnknownURLException.class)
    public ResponseEntity<String> handleUnknownUrlException(UnknownURLException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request: " + ex.getMessage());
    }
    
    @ExceptionHandler(MalformedURLException.class)
    public ResponseEntity<String> handleMalformedURLException(MalformedURLException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request: " + ex.getMessage());
    }
        
    @ExceptionHandler(URISyntaxException.class)
    public ResponseEntity<String> handleMalformedURLException(URISyntaxException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request: " + ex.getMessage());
    }
}

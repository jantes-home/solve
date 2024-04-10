package est.shorturl;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import org.springframework.util.MultiValueMap;
import org.springframework.util.MultiValueMapAdapter;

public class ShortStore {

    public static final String URL_PREFIX = "http://short.est/";

    private MultiValueMap<Integer, String> urlMap = new MultiValueMapAdapter<>(new HashMap<>());

    /**
     * store encodes the url and adds it to the url map if necessary
     * 
     * @param url the url to add to the map
     * @return the url key
     */
    public String store(String url) {
        final int hash = url.hashCode();
        List<String> urls = urlMap.get(hash);
        if (urls == null) {
            urls = new ArrayList<>();
            urlMap.put(hash, urls);
        }
        int idx = urls.indexOf(url);
        if (idx < 0) {
            idx = urls.size();
            urls.add(url);
        }

        return URL_PREFIX + stripLeadingA(encodeIntArray(new int[] { idx, hash }));
    }

    /**
     * fetch retrieves the url refered to be the parameter url
     * 
     * @param url the url that keys the lookup
     * @return the url found in the map
     * @throws UnknownURLException
     */
    public String fetch(String url) throws UnknownURLException {
        if (!url.startsWith(URL_PREFIX)) {
            throw new UnknownURLException(url);
        }
        final int[] values = decodeToIntArray(prependWithA(url.substring(URL_PREFIX.length())));
        if (values.length != 2) {
            throw new UnknownURLException(url);
        }
        final int idx = values[0];
        final int hash = values[1];

        List<String> urls = urlMap.get(hash);
        if (urls == null || idx >= urls.size()) {
            throw new UnknownURLException(url);
        }
        return urls.get(idx);
    }

    /**
     * stripLeadingA removes extraneous 0's translated as A from the front to
     * further shorten the URL
     * 
     * @param input
     * @return shortened encoding
     */
    public static String stripLeadingA(String input) {
        int leadingA = 0;
        while (input.length() > leadingA + 1 && input.charAt(leadingA) == 'A') {
            ++leadingA;
        }
        return input.substring(leadingA);
    }

    /**
     * prependWithA restores the stripped leading A's from the encoding
     * 
     * @param input
     * @return restored encoding
     */
    public static String prependWithA(String input) {
        int charsToAdd = 11 - input.length();
        if (charsToAdd <= 0) {
            return input; // Input string is already 11 characters or longer
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < charsToAdd; i++) {
            sb.append('A');
        }
        sb.append(input);

        return sb.toString();
    }

    /**
     * encodeIntArray base64 encodes the input with a url friendly mapping
     * 
     * @param array
     * @return
     */
    public static String encodeIntArray(int[] array) {
        // Convert the int array to a byte array
        byte[] bytes = new byte[array.length * Integer.BYTES];
        for (int i = 0; i < array.length; i++) {
            int value = array[i];
            int index = i * Integer.BYTES;
            bytes[index] = (byte) (value >>> 24);
            bytes[index + 1] = (byte) (value >>> 16);
            bytes[index + 2] = (byte) (value >>> 8);
            bytes[index + 3] = (byte) value;
        }

        // Encode the byte array to Base64
        // Base64 has reserved characters '/','+','='
        // remove the unneeded '=' and replace the others
        byte[] encodedBytes = Base64.getEncoder().encode(bytes);
        return new String(encodedBytes).replace("/", "-").replace("+", "_").replace("=", "");
    }

    /**
     * decodeToIntArray base64 decodes from an url frienly input into an int array
     * 
     * @param input
     * @return
     */
    public static int[] decodeToIntArray(String input) {
        // restore reserved characters
        String encodedValue = input.replace("-", "/").replace("_", "+");
        while (encodedValue.length() % 4 != 0) {
            encodedValue += "=";
        }

        // Decode the Base64 encoded string to byte array
        byte[] decodedBytes = Base64.getDecoder().decode(encodedValue);

        // Convert the byte array back to an int array
        int[] array = new int[decodedBytes.length / Integer.BYTES];
        for (int i = 0; i < array.length; i++) {
            int index = i * Integer.BYTES;
            array[i] = ((decodedBytes[index] & 0xFF) << 24) |
                    ((decodedBytes[index + 1] & 0xFF) << 16) |
                    ((decodedBytes[index + 2] & 0xFF) << 8) |
                    (decodedBytes[index + 3] & 0xFF);
        }

        return array;
    }
}
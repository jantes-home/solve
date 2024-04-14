package est.shorturl;



import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ShortModelTest {

    @Test
    public void testOriginalUrl() {
        String[] tests = {
            "Hello World!",
            ""
        };

        ShortModel m = new ShortModel();

        for (String t: tests) {
            m.setOriginalUrl(t);
            assertEquals(t, m.getOriginalUrl());
        }
    }

    @Test
    public void testShortLink() {
        String[] tests = {
            "Hello World!",
            ""
        };

        ShortModel m = new ShortModel();

        for (String t: tests) {
            m.setShortUrl(t);
            assertEquals(t, m.getShortUrl());
        }
    }
}

package est.shorturl;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class ShortStoreTest {

    @Test
    public void testDecodeToIntArray() {
        String[] tests = {
                "AAAAAAAAAAA",
                "AAAAAAAAA_A",
                "AAAAAAAAA-A",
                "f----8Y8th0",
        };
        int[][] expectations = {
                { 0, 0 },
                { 0, 992 },
                { 0, 1008 },
                { Integer.MAX_VALUE, "Hello World!".hashCode() },
        };
        for (int i = 0; i < tests.length; ++i) {
            assertArrayEquals(expectations[i], ShortStore.decodeToIntArray(tests[i]));
        }
    }

    @Test
    public void testEncodeIntArray() {
        int[][] tests = {
                { 0, 0 },
                { 0, 992 },
                { 0, 1008 },
                { 0, "Hello World!".hashCode() },
                { Short.MAX_VALUE, "Hello World!".hashCode() },
                { Integer.MAX_VALUE, "Hello World!".hashCode() },
                { Integer.MIN_VALUE, "Hello World!".hashCode() },
                { 1 },
                { 1, 2, 3 }
        };
        String[] expectations = {
                "AAAAAAAAAAA",
                "AAAAAAAAA_A",
                "AAAAAAAAA-A",
                "AAAAAMY8th0",
                "AAB--8Y8th0",
                "f----8Y8th0",
                "gAAAAMY8th0",
                "AAAAAQ",
                "AAAAAQAAAAIAAAAD"
        };
        for (int i = 0; i < tests.length; ++i) {
            assertEquals(expectations[i], ShortStore.encodeIntArray(tests[i]));
        }
    }

    @Test
    public void TestStripLeadingA() {
        String[] tests = {
            "A",
            "AA",
            "ABBAA",
            ""
        };
        String[] expectations = {
            "A",
            "A",
            "BBAA",
            ""
        };
        for (int i = 0; i < tests.length; ++i) {
            assertEquals(expectations[i], ShortStore.stripLeadingA(tests[i]));
        }
    }

    @Test
    public void TestprependWithA() {
        String[] tests = {
            "A",
            "ABBAA",
            "",
            "Hello World!"
        };
        String[] expectations = {
            "AAAAAAAAAAA",
            "AAAAAAABBAA",
            "AAAAAAAAAAA",
            "Hello World!",
        };
        for (int i = 0; i < tests.length; ++i) {
            assertEquals(expectations[i], ShortStore.prependWithA(tests[i]));
        }
    }

    @Test
    public void TestStore() {
        String[] tests = {
            "https://example.com/library/react",
            "Hello World!",
            "https://example.com/library/react",  // duplicate ensures that no unique URL is generated 
            "",
            "Squire Trelawney, Dr. Livesey, and the rest of these gentlemen having asked me to write down the whole particulars about Treasure Island, from the beginning to the end, keeping nothing back but the bearings of the island, and that only because there is still treasure not yet lifted, I take up my pen in the year of grace 17-, and go back to the time when my father kept the 'Admiral Benbow' inn, and the brown old seaman, with the sabre cut, first took up his lodging under our roof."
        };
        String[] expectations = {
            "http://short.est/PdE6o",
            "http://short.est/MY8th0",
            "http://short.est/PdE6o",
            "http://short.est/A",
            "http://short.est/CSievM",
        };

        ShortStore model = new ShortStore();
        for (int i = 0; i < tests.length; ++i) {
            assertEquals(expectations[i], model.store(tests[i]));
        }
        for (int i = 0; i < expectations.length; ++i) {
            try {
                assertEquals(tests[i], model.fetch(expectations[i]));
            } catch (UnknownURLException e) {
                fail(e);
            }
        }
    }

    @Test
    public void TestFetch() {
        ShortStore model = new ShortStore();

        try {
            model.fetch("");
            fail("Incorrectly handled empty URL");
        } catch (UnknownURLException e) {
            assertEquals(e.getMessage(),"unknown URL: ");
        }

        try {
            model.fetch("https://example.com/library/react");
            fail("Incorrectly handled wrong URL");
        } catch (UnknownURLException e) {
            assertEquals(e.getMessage(),"unknown URL: https://example.com/library/react");
        }

        try {
            model.fetch("http://short.est/gAAAAMY8th0AAAAD");
            fail("Incorrectly handled too many values");
        } catch (UnknownURLException e) {
            assertEquals("unknown URL: http://short.est/gAAAAMY8th0AAAAD",e.getMessage());
        }

        try {
            model.fetch("http://short.est/A");
            fail("Incorrectly handled URL not in dictionary");
        } catch (UnknownURLException e) {
            assertEquals("unknown URL: http://short.est/A",e.getMessage());
        }

        try {
            assertEquals("http://short.est/A",model.store(""));
            model.fetch("http://short.est/QAAAAA");
            fail("Incorrectly handled URL list count");
        } catch (UnknownURLException e) {
            assertEquals("unknown URL: http://short.est/QAAAAA",e.getMessage());
        }
    }
}

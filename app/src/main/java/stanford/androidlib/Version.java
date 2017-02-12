package stanford.androidlib;

/**
 * A convenience class that prints the library's version when run.
 */
public final class Version {
    /// begin static constants

    // version of library (NOTE TO MARTY: 0-pad the month, day, and hour to 2 digits)
    private static final String LIBRARY_VERSION = "2017/02/12 10:27am";

    // URL at which library source code can be found
    private static final String LIBRARY_URL = "https://github.com/stepp/StanfordAndroidLibrary";

    /**
     * Returns the current version of the library as a string, in format "YYYY/MM/DD HH:MMam".
     */
    public static String getLibraryVersion() {
        return LIBRARY_VERSION;
    }

    /**
     * Returns the current URL of the library as a string.
     */
    public static String getLibraryUrl() {
        return LIBRARY_URL;
    }

    /**
     * This main method simply prints the library version and exits.
     * It can be used to "run" the library JAR to query its version.
     * Run with --version to JUST print the version as a bare string.
     * Else a bit of text about the library, its URL and author will be printed.
     */
    public static void main(String[] args) {
        boolean printVersionOnly = false;
        if (args != null) {
            for (String arg : args) {
                if ("--version".equalsIgnoreCase(arg)) {
                    printVersionOnly = true;
                }
            }
        }

        if (printVersionOnly) {
            System.out.println(getLibraryVersion());
        } else {
            System.out.println("Stanford Android Library, by Marty Stepp (stepp AT cs DOT stanford DOT edu)");
            System.out.println("Lib URL: " + LIBRARY_URL);
            System.out.println("Version: " + getLibraryVersion());
        }
    }
}

/*
 * @version 2016/03/02
 * - initial version
 */

package stanford.androidlib;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A utility class for performing input and output on files.
 *
 * <pre>
 * String text = SimpleIO.with(this).readFileText(R.raw.myfile);
 * </pre>
 */
public final class SimpleIO {
    private static Context context = null;
    private static final SimpleIO INSTANCE = new SimpleIO();

    /**
     * Returns a singleton SimpleIO instance bound to the given context.
     */
    public static SimpleIO with(Context context) {
        SimpleIO.context = context;
        return INSTANCE;
    }

    /**
     * Returns a singleton SimpleIO instance bound to the given view's context.
     */
    public static SimpleIO with(View context) {
        return with(context.getContext());
    }

    private SimpleIO() {
        // empty
    }

    /**
     * Returns the directory where documents are stored on this device.
     */
    public File getDocumentsDirectory() {
        // commented out because it requires API Level 19
        // return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        return Environment.getExternalStoragePublicDirectory("Documents");
    }

    /**
     * Returns the directory where downloads are stored on this device.
     */
    public File getDownloadsDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    }

    /**
     * Returns the directory where movies are stored on this device.
     */
    public File getMoviesDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
    }

    /**
     * Returns the directory where music is stored on this device.
     */
    public File getMusicDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
    }

    /**
     * Returns the directory where photos are stored on this device.
     */
    public File getPhotosDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    }

    /**
     * Returns the directory where podcasts are stored on this device.
     */
    public File getPodcastsDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PODCASTS);
    }

    /**
     * Opens and returns a FileInputStream to read the given file in the given
     * external directory.
     * @throws IORuntimeException if the file does not exist or cannot be read.
     */
    // @RequiresPermission("android.permission.READ_EXTERNAL_STORAGE")
    public BufferedReader openExternalFileBufferedReader(String dir, String filename) {
        return new BufferedReader(new InputStreamReader(openExternalFileInputStream(dir, filename)));
    }

    /**
     * Opens and returns a Scanner to read the given file in the given
     * external directory.
     * @throws IORuntimeException if the file does not exist or cannot be read.
     */
    // @RequiresPermission("android.permission.READ_EXTERNAL_STORAGE")
    public Scanner openExternalFileScanner(String dir, String filename) {
        return new Scanner(openExternalFileInputStream(dir, filename));
    }

    /**
     * Opens and returns a PrintStream to write the given file in the given
     * external directory.
     * If the file already exists, its contents will be overwritten.
     * @throws IORuntimeException if the file cannot be written.
     */
    // @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public PrintStream openExternalFilePrintStream(String dir, String filename) {
        return openExternalFilePrintStream(dir, filename, /* append */ false);
    }

    /**
     * Opens and returns a PrintStream to write the given file in the given
     * external directory.
     * The append parameter dictates whether existing file contents will be overwritten or appended to.
     * @throws IORuntimeException if the file cannot be written.
     */
    // @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public PrintStream openExternalFilePrintStream(String dir, String filename, boolean append) {
        File directory = context.getExternalFilesDir(dir);
        File file = new File(directory, filename);
        try {
            return new PrintStream(file);
        } catch (IOException ioe) {
            throw new IORuntimeException(ioe);
        }
    }

    /**
     * Opens and returns a Scanner to read the given file in the given
     * external directory.
     * @throws IORuntimeException if the file cannot be written.
     */
    // @RequiresPermission("android.permission.READ_EXTERNAL_STORAGE")
    public FileInputStream openExternalFileInputStream(String dir, String filename) {
        File directory = context.getExternalFilesDir(dir);
        File file = new File(directory, filename);
        try {
            return new FileInputStream(file);
        } catch (IOException ioe) {
            throw new IORuntimeException(ioe);
        }
    }

    /**
     * Opens an external file for reading.
     * @throws IORuntimeException if the file does not exist or cannot be read.
     */
    public FileInputStream openFileInput(@NonNull File file) {
        try {
            return context.openFileInput(file.toString());
        } catch (IOException ioe) {
            throw new IORuntimeException(ioe);
        }
    }

    /**
     * Opens an external file in the given directory for reading.
     * @throws IORuntimeException if the file does not exist or cannot be read.
     */
    public FileInputStream openFileInput(@NonNull File directory, @NonNull String filename) {
        try {
            return context.openFileInput(new File(directory, filename).toString());
        } catch (IOException ioe) {
            throw new IORuntimeException(ioe);
        }
    }

    /**
     * Opens an external file for reading.
     * @throws IORuntimeException if the file does not exist or cannot be read.
     */
    public FileInputStream openFileInput(@NonNull String filename) {
        try {
            return context.openFileInput(filename);
        } catch (IOException ioe) {
            throw new IORuntimeException(ioe);
        }
    }

    /**
     * Opens an external file for writing in the given mode, such as Context.MODE_PRIVATE or MODE_APPEND.
     * @throws IORuntimeException if the file does not exist or cannot be written.
     */
    public FileOutputStream openFileOutput(@NonNull File file, int mode) {
        try {
            return context.openFileOutput(file.toString(), mode);
        } catch (IOException ioe) {
            throw new IORuntimeException(ioe);
        }
    }

    /**
     * Opens an external file in the given directory for writing in the given mode,
     * such as Context.MODE_PRIVATE or MODE_APPEND.
     * @throws IORuntimeException if the file does not exist or cannot be written.
     */
    public FileOutputStream openFileOutput(@NonNull File directory, @NonNull String name, int mode) {
        try {
            return context.openFileOutput(new File(directory, name).toString(), mode);
        } catch (IOException ioe) {
            throw new IORuntimeException(ioe);
        }
    }

    /**
     * Opens an external file for writing in the given mode, such as Context.MODE_PRIVATE or MODE_APPEND.
     * @throws IORuntimeException if the file does not exist or cannot be written.
     */
    public FileOutputStream openFileOutput(@NonNull String name, int mode) {
        try {
            return context.openFileOutput(name, mode);
        } catch (IOException ioe) {
            throw new IORuntimeException(ioe);
        }
    }

    /**
     * Opens a BufferedReader for reading from the given file.
     * If the file cannot be read, throws an IORuntimeException.
     */
    public BufferedReader openInternalFileBufferedReader(@NonNull File file) {
        InputStream input = openFileInput(file);
        return new BufferedReader(new InputStreamReader(input));
    }

    /**
     * Opens a BufferedReader for reading from the given file in the given directory.
     * If the file cannot be read, throws an IORuntimeException.
     */
    public BufferedReader openInternalFileBufferedReader(@NonNull File directory, @NonNull String filename) {
        InputStream input = openFileInput(directory, filename);
        return new BufferedReader(new InputStreamReader(input));
    }

    /**
     * Opens a BufferedReader for reading from the given file.
     * If the file cannot be read, throws an IORuntimeException.
     */
    public BufferedReader openInternalFileBufferedReader(@NonNull String filename) {
        InputStream input = openFileInput(filename);
        return new BufferedReader(new InputStreamReader(input));
    }

    /**
     * Opens a PrintStream for writing into the given file, replacing any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public PrintStream openInternalFilePrintStream(@NonNull File file) {
        OutputStream output = openFileOutput(file, Context.MODE_PRIVATE);
        return new PrintStream(output);
    }

    /**
     * Opens a PrintStream for writing into the given file, replacing any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public PrintStream openInternalFilePrintStream(@NonNull File directory, String filename) {
        OutputStream output = openFileOutput(directory, filename, Context.MODE_PRIVATE);
        return new PrintStream(output);
    }

    /**
     * Opens a PrintStream for writing into the given file, replacing any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public PrintStream openInternalFilePrintStream(@NonNull String filename) {
        OutputStream output = openFileOutput(filename, Context.MODE_PRIVATE);
        return new PrintStream(output);
    }

    /**
     * Opens a PrintStream for writing into the given file.
     * If 'append' is true, appends the new text after any existing data; if false, replaces any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public PrintStream openInternalFilePrintStream(@NonNull File file, boolean append) {
        OutputStream output = openFileOutput(file, append ? Context.MODE_APPEND : Context.MODE_PRIVATE);
        return new PrintStream(output);
    }

    /**
     * Opens a PrintStream for writing into the given file.
     * If 'append' is true, appends the new text after any existing data; if false, replaces any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public PrintStream openInternalFilePrintStream(@NonNull File directory, @NonNull String filename, boolean append) {
        OutputStream output = openFileOutput(directory, filename, append ? Context.MODE_APPEND : Context.MODE_PRIVATE);
        return new PrintStream(output);
    }

    /**
     * Opens a PrintStream for writing into the given file.
     * If 'append' is true, appends the new text after any existing data; if false, replaces any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public PrintStream openInternalFilePrintStream(@NonNull String filename, boolean append) {
        OutputStream output = openFileOutput(filename, append ? Context.MODE_APPEND : Context.MODE_PRIVATE);
        return new PrintStream(output);
    }

    /**
     * Opens a Scanner to read the file with the given name.
     * If the file cannot be read, throws an IORuntimeException.
     */
    public Scanner openInternalFileScanner(@NonNull File file) {
        InputStream input = openFileInput(file);
        return new Scanner(input);
    }

    /**
     * Opens a Scanner to read the file with the given name.
     * If the file cannot be read, throws an IORuntimeException.
     */
    public Scanner openInternalFileScanner(@NonNull File directory, @NonNull String filename) {
        InputStream input = openFileInput(directory, filename);
        return new Scanner(input);
    }

    /**
     * Opens a Scanner to read the file with the given name.
     * If the file cannot be read, throws an IORuntimeException.
     */
    public Scanner openInternalFileScanner(@NonNull String filename) {
        InputStream input = openFileInput(filename);
        return new Scanner(input);
    }

    /**
     * Opens a BufferedReader to read the file with the given ID.
     */
    public BufferedReader openInternalFileBufferedReader(@RawRes int id) {
        InputStream input = context.getResources().openRawResource(id);
        return new BufferedReader(new InputStreamReader(input));
    }

    /**
     * Opens a Scanner to read the file with the given ID.
     */
    public Scanner openInternalFileScanner(@RawRes int id) {
        InputStream input = context.getResources().openRawResource(id);
        return new Scanner(input);
    }

    /**
     * Opens an InputStream to read the file with the given ID.
     */
    public InputStream openInternalFileStream(@RawRes int id) {
        return context.getResources().openRawResource(id);
    }

    /**
     * Reads the entire text of the file with the given ID, returning it as a list of lines.
     * Any end-of-line \n characters have been stripped from the ends of the lines.
     */
    public ArrayList<String> readFileLines(@RawRes int id) {
        BufferedReader reader = openInternalFileBufferedReader(id);
        return __readFileLinesHelper(reader);
    }

    /**
     * Reads the entire text of the given file, returning it as a list of lines.
     * Any end-of-line \n characters have been stripped from the ends of the lines.
     * @throws IORuntimeException if the file cannot be read.
     */
    public ArrayList<String> readFileLines(@NonNull File file) {
        BufferedReader reader = openInternalFileBufferedReader(file);
        return __readFileLinesHelper(reader);
    }

    /**
     * Reads the entire text of the given file, returning it as a list of lines.
     * Any end-of-line \n characters have been stripped from the ends of the lines.
     * @throws IORuntimeException if the file cannot be read.
     */
    public ArrayList<String> readFileLines(@NonNull File directory, @NonNull String filename) {
        BufferedReader reader = openInternalFileBufferedReader(directory, filename);
        return __readFileLinesHelper(reader);
    }

    /**
     * Reads the entire text from the given stream, returning it as a list of lines.
     * Any end-of-line \n characters have been stripped from the ends of the lines.
     * @throws IORuntimeException if the file cannot be read.
     */
    public ArrayList<String> readFileLines(@NonNull InputStream stream) {
        return __readFileLinesHelper(new BufferedReader(new InputStreamReader(stream)));
    }

    /**
     * Reads the entire text of the given file, returning it as a list of lines.
     * Any end-of-line \n characters have been stripped from the ends of the lines.
     * @throws IORuntimeException if the file cannot be read.
     */
    public ArrayList<String> readFileLines(@NonNull String filename) {
        BufferedReader reader = openInternalFileBufferedReader(filename);
        return __readFileLinesHelper(reader);
    }

    /**
     * Reads the entire contents of the file located at the given URL and returns them
     * as a list of strings.
     * Any end-of-line \n characters have been stripped from the ends of the lines.
     * @throws IORuntimeException if the URL cannot be read.
     */
    // TODO: finish implementation
//    @RequiresPermission(Manifest.permission.INTERNET)
//    public ArrayList<String> readUrlLines(URL url) {
//        try {
//            return __readFileLinesHelper(new BufferedReader(new InputStreamReader(url.openStream())));
//        } catch (IOException ioe) {
//            throw new IORuntimeException(ioe);
//        }
//    }

    /**
     * Reads the entire contents of the file located at the given URL and returns them
     * as a list of strings.
     * Any end-of-line \n characters have been stripped from the ends of the lines.
     * @throws IllegalArgumentException if the string has invalid URL syntax.
     * @throws IORuntimeException if the URL cannot be read.
     */
    // TODO: finish implementation
//    @RequiresPermission(Manifest.permission.INTERNET)
//    public ArrayList<String> readUrlLines(String url) {
//        try {
//            return readUrlLines(new URL(url));
//        } catch (MalformedURLException mfurle) {
//            throw new IllegalArgumentException(url, mfurle);
//        }
//    }

    // common helper to implement readFileLines behavior
    private ArrayList<String> __readFileLinesHelper(@NonNull BufferedReader reader) {
        ArrayList<String> list = new ArrayList<>();
        try {
            while (reader.ready()) {
                list.add(reader.readLine());
            }
            reader.close();
        } catch (IOException ioe) {
            // this will not happen
            throw new IORuntimeException(ioe);
        }
        return list;
    }

    /**
     * Reads the entire text of the file with the given ID, returning it as a string.
     */
    public String readFileText(@RawRes int id) {
        BufferedReader reader = openInternalFileBufferedReader(id);
        return __readFileTextHelper(reader);
    }

    /**
     * Reads the entire text of the given file, returning it as a string.
     * @throws IORuntimeException if the file cannot be read.
     */
    public String readFileText(@NonNull File file) {
        BufferedReader reader = openInternalFileBufferedReader(file);
        return __readFileTextHelper(reader);
    }

    /**
     * Reads the entire text from the given stream, returning it as a string.
     * @throws IORuntimeException if the file cannot be read.
     */
    public String readFileText(@NonNull InputStream stream) {
        StringBuilder sb = new StringBuilder();
        try {
            while (true) {
                int ch = stream.read();
                if (ch == -1) {
                    break;
                }
                sb.append((char) ch);
            }
        } catch (IOException ioe) {
            throw new IORuntimeException(ioe);
        }

        return sb.toString();
    }

    /**
     * Reads the entire text of the given file, returning it as a string.
     * @throws IORuntimeException if the file cannot be read.
     */
    public String readFileText(@NonNull File directory, @NonNull String filename) {
        BufferedReader reader = openInternalFileBufferedReader(directory, filename);
        return __readFileTextHelper(reader);
    }

    /**
     * Reads the entire text of the given file, returning it as a string.
     * @throws IORuntimeException if the file cannot be read.
     */
    public String readFileText(@NonNull String filename) {
        BufferedReader reader = openInternalFileBufferedReader(filename);
        return __readFileTextHelper(reader);
    }

    // returns true if this string begins with http:, https:, etc.
    // (representing a likely URL)
    private boolean isHttpUrl(String filename) {
        try {
            URL url = new URL(filename);
            return "ftp".equals(url.getProtocol())
                    || "http".equals(url.getProtocol())
                    || "https".equals(url.getProtocol());
        } catch (MalformedURLException mfurle) {
            return false;
        }
    }

    /**
     * Reads the entire contents of the file located at the given URL and returns them
     * as a string.
     * @throws IORuntimeException if the URL cannot be read.
     */
    // TODO: finish implementation
//    @RequiresPermission(Manifest.permission.INTERNET)
//    public String readUrlText(URL url) {
//        try {
//            return __readFileTextHelper(new BufferedReader(new InputStreamReader(url.openStream())));
//        } catch (IOException ioe) {
//            throw new IORuntimeException(ioe);
//        }
//    }

    /**
     * Reads the entire contents of the file located at the given URL and returns them
     * as a string.
     * @throws IllegalArgumentException if the string has invalid URL syntax.
     * @throws IORuntimeException if the URL cannot be read.
     */
    // TODO: finish implementation
//    @RequiresPermission(Manifest.permission.INTERNET)
//    public String readUrlText(String url) {
//        try {
//            return readUrlText(new URL(url));
//        } catch (MalformedURLException mfurle) {
//            throw new IllegalArgumentException(url, mfurle);
//        }
//    }

    // common helper to implement readFileText behavior
    private String __readFileTextHelper(@NonNull BufferedReader reader) {
        StringBuilder sb = new StringBuilder();
        try {
            while (reader.ready()) {
                sb.append((char) reader.read());
            }
            reader.close();
        } catch (IOException ioe) {
            // this will not happen
            throw new IORuntimeException(ioe);
        }
        return sb.toString();
    }

    /**
     * Writes the given list of lines into the given file, replacing any previous data.
     * Each line is suffixed by \n.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public SimpleIO writeFileLines(@NonNull File file, @NonNull List<String> lines) {
        PrintStream output = openInternalFilePrintStream(file);
        for (String line : lines) {
            output.println(line);
        }
        output.close();
        return this;
    }

    /**
     * Writes the given list of lines into the given file, replacing any previous data.
     * Each line is suffixed by \n.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public SimpleIO writeFileLines(@NonNull File directory, @NonNull String filename, @NonNull List<String> lines) {
        PrintStream output = openInternalFilePrintStream(directory, filename);
        for (String line : lines) {
            output.println(line);
        }
        output.close();
        return this;
    }

    /**
     * Writes the given list of lines into the given file, replacing any previous data.
     * Each line is suffixed by \n.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public SimpleIO writeFileLines(@NonNull String filename, @NonNull List<String> lines) {
        PrintStream output = openInternalFilePrintStream(filename);
        for (String line : lines) {
            output.println(line);
        }
        output.close();
        return this;
    }

    /**
     * Writes the given text into the given file.
     * Each line is suffixed by \n.
     * If 'append' is true, appends the new text after any existing data; if false, replaces any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public SimpleIO writeFileLines(@NonNull File file, @NonNull List<String> lines, boolean append) {
        PrintStream output = openInternalFilePrintStream(file, append);
        for (String line : lines) {
            output.println(line);
        }
        output.close();
        return this;
    }

    /**
     * Writes the given text into the given file.
     * Each line is suffixed by \n.
     * If 'append' is true, appends the new text after any existing data; if false, replaces any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public SimpleIO writeFileLines(@NonNull File directory, @NonNull String filename, @NonNull List<String> lines, boolean append) {
        PrintStream output = openInternalFilePrintStream(directory, filename, append);
        for (String line : lines) {
            output.println(line);
        }
        output.close();
        return this;
    }

    /**
     * Writes the given text into the given file.
     * Each line is suffixed by \n.
     * If 'append' is true, appends the new text after any existing data; if false, replaces any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public SimpleIO writeFileLines(@NonNull String filename, @NonNull List<String> lines, boolean append) {
        PrintStream output = openInternalFilePrintStream(filename, append);
        for (String line : lines) {
            output.println(line);
        }
        output.close();
        return this;
    }

    /**
     * Writes the given text into the given file, replacing any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public SimpleIO writeFileText(@NonNull File file, @NonNull String text) {
        PrintStream output = openInternalFilePrintStream(file);
        output.print(text);
        output.close();
        return this;
    }

    /**
     * Writes the given text into the given file, replacing any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public SimpleIO writeFileText(@NonNull File directory, @NonNull String filename, @NonNull String text) {
        PrintStream output = openInternalFilePrintStream(directory, filename);
        output.print(text);
        output.close();
        return this;
    }

    /**
     * Writes the given text into the given file, replacing any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public SimpleIO writeFileText(@NonNull String filename, @NonNull String text) {
        PrintStream output = openInternalFilePrintStream(filename);
        output.print(text);
        output.close();
        return this;
    }

    /**
     * Writes the given text into the given file.
     * If 'append' is true, appends the new text after any existing data; if false, replaces any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public SimpleIO writeFileText(@NonNull File file, @NonNull String text, boolean append) {
        PrintStream output = openInternalFilePrintStream(file, append);
        output.print(text);
        output.close();
        return this;
    }

    /**
     * Writes the given text into the given file.
     * If 'append' is true, appends the new text after any existing data; if false, replaces any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public SimpleIO writeFileText(@NonNull File directory, @NonNull String filename, @NonNull String text, boolean append) {
        PrintStream output = openInternalFilePrintStream(directory, filename, append);
        output.print(text);
        output.close();
        return this;
    }

    /**
     * Writes the given text into the given file.
     * If 'append' is true, appends the new text after any existing data; if false, replaces any previous data.
     * If the file cannot be written, throws an IORuntimeException.
     */
    public SimpleIO writeFileText(@NonNull String filename, @NonNull String text, boolean append) {
        PrintStream output = openInternalFilePrintStream(filename, append);
        output.print(text);
        output.close();
        return this;
    }
}

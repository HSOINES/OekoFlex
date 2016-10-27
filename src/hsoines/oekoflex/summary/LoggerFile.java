package hsoines.oekoflex.summary;

/**
 * Allgemeines Interface für alle Logfiles
 */
public interface LoggerFile {
    void log(String text);

    void close();
}

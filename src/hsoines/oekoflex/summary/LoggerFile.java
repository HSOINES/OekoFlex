package hsoines.oekoflex.summary;

/**
 * User: jh
 * Date: 10/03/16
 * Time: 19:09
 * Allgemeines Interface f�r alle Logfiles
 */
public interface LoggerFile {
    void log(String text);

    void close();
}

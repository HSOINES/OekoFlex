package hsoines.oekoflex.summary.impl;

import hsoines.oekoflex.summary.LoggerFile;

/**
 * User: jh
 * Date: 10/03/16
 * Time: 19:12
 * Logger, der nichts loggt....
 */
public class NullLoggerFile implements LoggerFile {
    @Override
    public void log(final String text) {
        //no action
    }

    @Override
    public void close() {
        //no action

    }
}

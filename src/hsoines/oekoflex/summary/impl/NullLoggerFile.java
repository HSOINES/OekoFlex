package hsoines.oekoflex.summary.impl;

import hsoines.oekoflex.summary.LoggerFile;

/**
 * User: jh
 * Date: 10/03/16
 * Time: 19:12
 */
public class NullLoggerFile implements LoggerFile {
    @Override
    public void log(final String text) {
        //no action
    }
}

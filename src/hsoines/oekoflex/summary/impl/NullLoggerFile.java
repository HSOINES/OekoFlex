package hsoines.oekoflex.summary.impl;

import hsoines.oekoflex.summary.LoggerFile;

/**
 * Logger who does not log anything
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

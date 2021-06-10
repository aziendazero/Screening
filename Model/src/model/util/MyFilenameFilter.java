package model.util;

import java.io.FilenameFilter;

public class MyFilenameFilter implements FilenameFilter {
    String init = null;
    String end = null;


    public MyFilenameFilter(String init, String end) {
        this.init = init;
        this.end = end;
    }

    public boolean accept(java.io.File f) {
        if (init == null && end == null)
            return true;

        if (init != null && end == null)
            return f.getName().startsWith(init);

        if (init == null && end != null)
            return f.getName().endsWith(end);

        return f.getName().startsWith(init) && f.getName().endsWith(end);
    }

    public boolean accept(java.io.File f, String s) {
        return s.startsWith(init);
    }
}


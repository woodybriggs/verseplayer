package uk.co.woodybriggs.verse;

import java.io.File;
import java.io.FilenameFilter;

public class RsfFilter implements FilenameFilter {
    @Override
    public boolean accept(File dir, String filename) {
        return (filename.endsWith(".rsf"));
    }
}

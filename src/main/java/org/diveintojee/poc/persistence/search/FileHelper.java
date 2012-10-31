package org.diveintojee.poc.persistence.search;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;

import static org.apache.commons.io.FileUtils.listFiles;

/**
 * Mainly created for tests reasons<br/>
 * Encapsulates all filesystem interactions<br/>
 *
 * @author louis.gueye@gmail.com
 */
@Component(value = "fileHelper")
public class FileHelper {

    @SuppressWarnings("unchecked")
    public Collection<File> listFilesByExtension(File indexDirectory, String extension) {
        return listFiles(indexDirectory, new String[]{extension}, true);
    }

    public String fileContentAsString(String fileLocation) throws IOException {
        return Resources.toString(new ClassPathResource(fileLocation).getURL(), Charsets.UTF_8);
    }


    public File[] listChildrenDirectories(File parent) {
        return parent.listFiles((FileFilter) FileFilterUtils.directoryFileFilter());
    }
}

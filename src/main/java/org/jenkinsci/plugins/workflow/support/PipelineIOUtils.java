package org.jenkinsci.plugins.workflow.support;

import hudson.XmlFile;
import hudson.util.XStream2;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.StandardOpenOption;

/**
 * Utilities to assist with IO and in some cases improve performance specifically for pipeline.
 */
public class PipelineIOUtils {
    /**
     * Convenience method to transparently write data directly or atomicly using {@link hudson.util.AtomicFileWriter}.
     * @param toWrite Object to write to file
     * @param location File to write object to
     * @param xstream xstream to use for output
     * @param atomicWrite If true, do an atomic write, otherwise do a direct write to file.
     * @throws IOException
     */
    public static void writeByXStream(@NonNull Object toWrite, @NonNull File location, @NonNull XStream2 xstream, boolean atomicWrite) throws IOException {
        if (atomicWrite) {
            XmlFile file = new XmlFile(xstream, location);
            file.write(toWrite);
        } else {
            try(OutputStream os = new BufferedOutputStream(
                    Files.newOutputStream(location.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))){
                xstream.toXMLUTF8(toWrite, os); // No atomic nonsense, just write and write and write!
            } catch (InvalidPathException ipe) {
                throw new IOException(ipe);
            }
        }
    }
}

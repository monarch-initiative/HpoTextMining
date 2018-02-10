package com.github.monarchinitiative.hpotextmining.gui.io;

import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * The aim of this class is to download a single file present at given <code>url</code> and store it on local
 * filesystem on path <code>whereToSave</code>. Since the class extends {@link Task}, it also implements
 * {@link Runnable} and {@link java.util.concurrent.FutureTask}, therefore it is possible to use it with
 * {@link java.util.concurrent.ExecutorService}.
 *
 * @author <a href="mailto:daniel.danis@jax.org">Daniel Danis</a>
 * @version 0.2.1
 * @since 0.2
 */
public final class DownloadTask extends Task<Void> {

    private static final Logger logger = LogManager.getLogger();

    /**
     * This is the URL of the file we want to download
     */
    private final String url;

    /**
     * The absolute path to the place where the downloaded file will be saved in the local filesystem.
     */
    private final File whereToSave;


    public DownloadTask(String url, String whereToSave) {
        this(url, new File(whereToSave));
    }


    public DownloadTask(String url, File whereToSave) {
        this.url = url;
        this.whereToSave = whereToSave;
    }


    /**
     * This method downloads a file to the specified local file path. If the file already exists, it emits a warning
     * message and does nothing.
     */
    @Override
    protected Void call() {
        logger.info("Downloading...");
        updateMessage("Downloading...");
        InputStream reader;
        FileOutputStream writer;

        int threshold = 0;
        int block = 250000;
        try {
            URL url = new URL(this.url);
            URLConnection urlc = url.openConnection();
            reader = urlc.getInputStream();
            logger.trace("URL host: {} \n reader available={}", url.getHost(), reader.available());
            logger.trace("LocalFilePath: {}", whereToSave.getAbsolutePath());
            writer = new FileOutputStream(whereToSave);
            byte[] buffer = new byte[153600];
            int totalBytesRead = 0;
            int bytesRead;
            int size = urlc.getContentLength();

            updateProgress((double) totalBytesRead / size, size);

            logger.trace("Size of file to be downloaded: {}", size);
            if (size >= 0)
                block = size / 100;
            while ((bytesRead = reader.read(buffer)) > 0) {
                writer.write(buffer, 0, bytesRead);
                buffer = new byte[153600];
                totalBytesRead += bytesRead;
                if (size > 0 && totalBytesRead > threshold) {
                    updateProgress((double) totalBytesRead / size, size);
                    threshold += block;
                }
            }
            logger.info("Successful download from {}: {} ({}) bytes read", this.url, Integer.toString(totalBytesRead), size);
            updateMessage("Done");
            writer.close();
        } catch (Exception e) {
            updateProgress(-1, -1);
            logger.warn(e);
            updateMessage(e.getMessage());
            failed();
        }
        updateProgress(1, 1); /* show 100% completion */
        return null;
    }

}

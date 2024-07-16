package com.osh.worker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.osh.camera.config.CameraFTPSource;
import com.osh.ui.camera.CameraImageContent;
import com.osh.ui.camera.CameraImageFolder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class FTPImageWorker {

    final ExecutorService executorService = Executors.newCachedThreadPool();

    public interface GetThumbnailCallback {
        void onComplete(List<CameraImageContent.ThumbnailImageItem> thumbnails);

        void onProgress(int total, int progress);
    }

    public interface GetFolderCallback {
        void onComplete(List<CameraImageFolder> folders);
    }

    public interface GetOriginalCallback {
        void onComplete(Drawable image);
    }

    public interface GetVideoCallback {

        void onComplete(File tempFile);

    }

    public void fetchFolders(CameraFTPSource source, String pathName, GetFolderCallback callback) {
        executorService.submit(() -> {
            try {
                List<CameraImageFolder> folders = _fetchFolders(source, pathName);
                callback.onComplete(folders);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private List<CameraImageFolder> _fetchFolders(CameraFTPSource source, String pathName) throws IOException {
        FTPClient ftpClient = login(source);
        ftpClient.changeWorkingDirectory(pathName);
        FTPFile[] folders = ftpClient.listFiles(".", FTPFile::isDirectory);

        List<CameraImageFolder> returnList = new ArrayList<>(folders.length);

        Arrays.sort(folders,
                Comparator.comparing((FTPFile remoteFile) -> remoteFile.getTimestamp())
                        .reversed());

        for (FTPFile folder : folders) {
            returnList.add(new CameraImageFolder(folder.getName()));
        }

        logout(ftpClient);

        return returnList;
    }

    public void fetchThumbnails(CameraFTPSource source, String pathName, Resources res, GetThumbnailCallback callback) {
        executorService.submit(() -> {
            try {
                List<CameraImageContent.ThumbnailImageItem> imageList = _fetchThumbnails(source, pathName, res, callback);
                callback.onComplete(imageList);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private List<CameraImageContent.ThumbnailImageItem> _fetchThumbnails(CameraFTPSource source, String pathName, Resources res, GetThumbnailCallback callback) throws IOException {
        int total = 0;

        FTPClient ftpClient = login(source);
        ftpClient.changeWorkingDirectory(pathName);
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.enterLocalPassiveMode();

        String names[] = ftpClient.listNames();
        List<CameraImageContent.ThumbnailImageItem> returnList = new ArrayList<>(total);

        if (Arrays.stream(names).anyMatch(s -> s.equals("thumbnails.zip"))) {
            BufferedInputStream bis = new BufferedInputStream(ftpClient.retrieveFileStream("thumbnails.zip"));
            //ftpClient.completePendingCommand();
            ZipInputStream inputStream = new ZipInputStream(bis);

            total = names.length - 1;       // minus the zip itself

            int counter = 0;
            ZipEntry entry;
            while ((entry = inputStream.getNextEntry()) != null) {
                counter++;
                byte[] imageData = IOUtils.toByteArray(inputStream);
                returnList.add(new CameraImageContent.ThumbnailImageItem(pathName, entry.getName(), new BitmapDrawable(res, BitmapFactory.decodeByteArray(imageData, 0, imageData.length)), ""));
                callback.onProgress(total, counter);
            }
            inputStream.close();
            logout(ftpClient);
        } else {
            // zip does not exist, load directly
            int counter = 0;
            for (String fileName : names) {
                counter++;
                callback.onProgress(total, counter);
                InputStream is = ftpClient.retrieveFileStream(fileName);
                ftpClient.completePendingCommand();
                byte[] imageData = IOUtils.toByteArray(is);

                returnList.add(new CameraImageContent.ThumbnailImageItem(pathName, fileName, new BitmapDrawable(res, BitmapFactory.decodeByteArray(imageData, 0, imageData.length)), ""));
                is.close();
            }

        }

        return returnList;
    }

    public void fetchOriginal(CameraFTPSource source, CameraImageContent.ThumbnailImageItem item, Resources res, GetOriginalCallback callback) {
        executorService.submit(() -> {
            try {
                Drawable image = _fetchOriginal(source, item.folder.replace("/thumbnails", ""), item.name, res);
                callback.onComplete(image);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Drawable _fetchOriginal(CameraFTPSource source, String pathName, String fileName, Resources res) throws IOException {
        FTPClient ftpClient = login(source);
        ftpClient.changeWorkingDirectory(pathName);
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.setDataTimeout(10000);

        ftpClient.enterLocalPassiveMode();

        InputStream is = ftpClient.retrieveFileStream(fileName);
        //ftpClient.completePendingCommand();
        byte[] imageData = IOUtils.toByteArray(is);
        is.close();

        logout(ftpClient);

        return new BitmapDrawable(res, BitmapFactory.decodeByteArray(imageData, 0, imageData.length));
    }

    public void fetchVideo(CameraFTPSource source, CameraImageContent.ThumbnailImageItem item, Context context, GetVideoCallback callback) {
        executorService.submit(() -> {
            try {
                File file = _fetchVideo(source, item.folder.replace("/thumbnails", ""), item.name.replace(".jpg", ""), context);
                callback.onComplete(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private File _fetchVideo(CameraFTPSource source, String pathName, String fileName, Context context) throws IOException {
        FTPClient ftpClient = login(source);
        ftpClient.changeWorkingDirectory(pathName);
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.setDataTimeout(10000);

        ftpClient.enterLocalPassiveMode();

        InputStream is = ftpClient.retrieveFileStream(fileName);
        //ftpClient.completePendingCommand();

        File outputDir = context.getCacheDir();
        File outputFile = File.createTempFile(fileName, ".mp4", outputDir);
        FileOutputStream out = new FileOutputStream(outputFile);

        IOUtils.copy(is, out);

        logout(ftpClient);

        out.close();
        is.close();

        return outputFile;
    }

    private FTPClient login(CameraFTPSource source) throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(InetAddress.getByName(source.getHost()));
        ftpClient.login(source.getUser(), source.getPassword());
        return ftpClient;
    }

    private void logout(FTPClient ftpClient) throws IOException {
        ftpClient.logout();
        ftpClient.disconnect();
    }

}

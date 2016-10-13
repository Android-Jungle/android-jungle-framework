/*
 * Copyright (C) 2016. All Rights Reserved.
 *
 * @author  Arno Zhang
 * @email   zyfgood12@163.com
 * @date    2016/07/27
 */

package com.jungle.base.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    public static List<String> getFileList(
            String zipFile, boolean containFolder, boolean containFile) {

        List<String> list = new ArrayList<>();
        try {
            ZipFile zip = new ZipFile(zipFile);
            for (Enumeration<? extends ZipEntry> e = zip.entries(); e.hasMoreElements(); ) {
                ZipEntry entry = e.nextElement();
                String name = entry.getName();
                if (entry.isDirectory()) {
                    if (containFolder) {
                        list.add(name);
                    }
                } else {
                    if (containFile) {
                        list.add(name);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static boolean unZipFile(String zipFile, String dstFolder) {
        if (!FileUtils.isFileExist(zipFile)) {
            return false;
        }

        FileUtils.createPaths(dstFolder);

        try {
            ZipFile zip = new ZipFile(zipFile);
            for (Enumeration<? extends ZipEntry> e = zip.entries(); e.hasMoreElements(); ) {
                ZipEntry entry = e.nextElement();
                String name = entry.getName();
                if (entry.isDirectory()) {
                    FileUtils.createPaths(dstFolder + name);
                } else {
                    File file = new File(dstFolder + name);
                    FileUtils.createPaths(file.getParent());
                    file.createNewFile();

                    OutputStream outputStream = new BufferedOutputStream(
                            new FileOutputStream(file));

                    InputStream input = zip.getInputStream(entry);
                    FileUtils.writeToStream(input, outputStream);
                    FileUtils.closeStream(input);
                    FileUtils.closeStream(outputStream);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static List<String> getFileListStream(
            InputStream stream, boolean containFolder, boolean containFile) {

        List<String> list = new ArrayList<>();
        if (stream == null) {
            return list;
        }

        ZipInputStream zipStream = new ZipInputStream(stream);

        try {
            ZipEntry entry = zipStream.getNextEntry();
            while (entry != null) {
                String name = entry.getName();
                if (entry.isDirectory()) {
                    if (containFolder) {
                        list.add(name);
                    }
                } else {
                    if (containFile) {
                        list.add(name);
                    }
                }

                entry = zipStream.getNextEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileUtils.closeStream(zipStream);
        return list;
    }

    public static boolean unZipStream(InputStream stream, String dstFolder) {
        if (stream == null) {
            return false;
        }

        FileUtils.createPaths(dstFolder);

        ZipInputStream zipStream = new ZipInputStream(stream);
        try {
            ZipEntry entry = zipStream.getNextEntry();
            while (entry != null) {
                String name = entry.getName();
                if (entry.isDirectory()) {
                    FileUtils.createPaths(dstFolder + name);
                } else {
                    File file = new File(dstFolder + name);
                    FileUtils.createPaths(file.getParent());
                    file.createNewFile();

                    OutputStream outputStream = new BufferedOutputStream(
                            new FileOutputStream(file));

                    FileUtils.writeToStream(zipStream, outputStream);
                    FileUtils.closeStream(outputStream);
                }

                entry = zipStream.getNextEntry();
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileUtils.closeStream(zipStream);
        return false;
    }

    public static boolean zipFolderOrFile(String folderOrFile, String dstZipFile) {
        FileUtils.createFile(dstZipFile);

        try {
            File folder = new File(folderOrFile);
            ZipOutputStream stream = new ZipOutputStream(new FileOutputStream(dstZipFile));
            boolean result = zipFiles(folder.getParent() + File.separator, folder.getName(), stream);
            stream.finish();
            FileUtils.closeStream(stream);

            return result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean zipFiles(String folder, String name, ZipOutputStream stream) {
        if (stream == null) {
            return false;
        }

        File file = new File(folder + name);
        if (file.isFile()) {
            try {
                FileInputStream inputStream = new FileInputStream(file);
                ZipEntry entry = new ZipEntry(name);
                stream.putNextEntry(entry);

                FileUtils.writeToStream(inputStream, stream);
                FileUtils.closeStream(inputStream);

                stream.closeEntry();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            String[] list = file.list();
            if (list.length <= 0) {
                ZipEntry entry = new ZipEntry(name + File.separator);
                try {
                    stream.putNextEntry(entry);
                    stream.closeEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            for (String s : list) {
                zipFiles(folder, name + File.separator + s, stream);
            }
        }

        return true;
    }
}

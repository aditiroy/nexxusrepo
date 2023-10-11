package com.att.sales.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;



/**
 * The Class UnZipUtil.
 */
@Component
public class UnZipUtil {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(UnZipUtil.class);

	/**
	 * Instantiates a new un zip util.
	 */
	public UnZipUtil() {
		//
	}

	/**
	 * Un zip.
	 *
	 * @param zipFilePath the zip file path
	 * @param destDir the dest dir
	 * @return the list
	 */
	// @Component
	public  List<File> unZip(String zipFilePath, String destDir) {
        File dir = new File(destDir); //NOSONAR
        List<File> listOfFile = new ArrayList<File>();
        // create output directory if it doesn't exist
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis = null;
        FileOutputStream fos = null;
        ZipInputStream zis = null;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath); //NOSONAR
            zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName); //NOSONAR
                log.info("Unzipping to "+newFile.getAbsolutePath());
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs(); //NOSONAR
                fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
                }
                fos.close();
                listOfFile.add(newFile);
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
                
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
          
        } catch (IOException e) {
        	log.error("Error message:: ", e);
        }
        finally {

			if(null != fis) {
				try {
					fis.close();
				}catch(IOException e) {
					log.error("Error message:: ", e);
				}
			}
			if(null != fos) {
				try {
					fos.close();
				}catch(IOException e) {
					log.error("Error message:: ", e);
				}
			}
			if(null != zis) {
				try {
					zis.close();
				}catch(IOException e) {
					log.error("Error message:: ", e);
				}
			}

		}
        return listOfFile;
        
    }
}
package org.lizaalert;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

import java.io.*;

public class FileDownloadResponseHandler implements HttpClientResponseHandler<File> {
    private final File target;

    public FileDownloadResponseHandler(File target) {
        this.target = target;
    }

    @Override
    public File handleResponse(ClassicHttpResponse response) throws HttpException, IOException {
        InputStream source = response.getEntity().getContent();
        BufferedInputStream bis = new BufferedInputStream(source);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(target));
        int inByte;
        while((inByte = bis.read()) != -1) bos.write(inByte);
        bis.close();
        bos.close();
        return this.target;
    }
}

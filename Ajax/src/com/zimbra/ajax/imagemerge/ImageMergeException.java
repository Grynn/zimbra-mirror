package com.zimbra.ajax.imagemerge;

public class ImageMergeException extends Exception {

    public ImageMergeException(String msg, 
                               Throwable cause) 
    {
        super(msg, cause);
    }

    public ImageMergeException(String msg) {
        super(msg);
    }
}
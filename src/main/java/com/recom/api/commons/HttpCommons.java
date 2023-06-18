package com.recom.api.commons;

import org.springframework.http.HttpStatus;

public class HttpCommons {

    // 200er
    public final static String OK = "O.K.";
    public final static String OK_CODE = "200";

    // Experiment... must be static at the end to be usable .... Idea to avoid magic Strings in code
    public final static String OK2 = HttpStatus.OK.getReasonPhrase();
    public final static String OK_CODE2 = String.valueOf(HttpStatus.OK.value());

}

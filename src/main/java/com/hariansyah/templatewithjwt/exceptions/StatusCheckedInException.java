package com.hariansyah.templatewithjwt.exceptions;

import org.springframework.http.HttpStatus;

public class StatusCheckedInException extends ApplicationException {

    public StatusCheckedInException() {
        super(HttpStatus.valueOf(400), "error." + HttpStatus.valueOf(400).value() + ".status_checked_in");
    }
}

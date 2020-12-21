package com.lexor.service.email;

import com.intuit.ipp.data.EmailAddress;

public class EmailQBOService {
    public static EmailAddress getEmailAddress() {
        EmailAddress emailAddr = new EmailAddress();
        emailAddr.setAddress("test@abc.com");
        return emailAddr;
    }
}

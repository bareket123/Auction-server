package com.dev.responses;

import com.dev.models.RecipientModel;
import com.dev.objects.User;

import java.util.ArrayList;
import java.util.List;

public class RecipientsResponse extends BasicResponse{
    private List<RecipientModel> recipients;

    public RecipientsResponse (List<User> users) {
        this.recipients = new ArrayList<>();
        for (User user : users) {
            this.recipients.add(new RecipientModel(user));
        }
    }


    public List<RecipientModel> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<RecipientModel> recipients) {
        this.recipients = recipients;
    }
}

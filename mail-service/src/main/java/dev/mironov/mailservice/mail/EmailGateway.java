package dev.mironov.mailservice.mail;

import dev.mironov.mailservice.model.EmailTaskModel;

public interface EmailGateway {
    void send(EmailTaskModel task);
}

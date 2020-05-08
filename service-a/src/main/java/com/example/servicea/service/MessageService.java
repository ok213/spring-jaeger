package com.example.servicea.service;


import com.example.servicea.dto.MessageDto;

public interface MessageService {

    String sendMessageToPersonById(MessageDto messageDto);
    String sendMessageToPersonByEmail(MessageDto messageDto);

}

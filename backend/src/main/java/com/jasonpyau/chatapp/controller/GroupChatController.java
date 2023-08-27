package com.jasonpyau.chatapp.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jasonpyau.chatapp.annotation.GetUser;
import com.jasonpyau.chatapp.entity.User;
import com.jasonpyau.chatapp.form.NewGroupChatForm;
import com.jasonpyau.chatapp.service.GroupChatService;
import com.jasonpyau.chatapp.util.Response;

@RestController
@RequestMapping("/api/groupchat")
public class GroupChatController {

    @Autowired
    private GroupChatService groupChatService;

    @PostMapping(path = "/new", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> newGroupChat(@GetUser User user, @RequestBody NewGroupChatForm newGroupChatForm) {
        return new ResponseEntity<>(Response.createBody("groupChat", groupChatService.newGroupChat(user, newGroupChatForm)), HttpStatus.OK);
    }

    @GetMapping(path = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HashMap<String, Object>> getGroupChats(@GetUser User user) {
        return new ResponseEntity<>(Response.createBody("groupChats", groupChatService.getGroupChats(user)), HttpStatus.OK);
    }
    
}
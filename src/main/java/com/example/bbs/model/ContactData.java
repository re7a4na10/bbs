package com.example.bbs.model;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
public class ContactData {

    private String name;
    private String email;
    private String message;

    /**
     * コンストラクタ
     */
    public ContactData() {
    }

    /**
     * コンストラクタ
     * @param name
     * @param email
     * @param message
     */
    public ContactData(String name, String email, String message) {
        this.name = name;
        this.email = email;
        this.message = message;
    }
}

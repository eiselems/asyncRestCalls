package com.marcuseisele.test.asyncrest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class UserDTO {

    private int id;
    private String login;
    private String name;
    private int publicRepos;
}

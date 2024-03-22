package com.team01.wholeSaleSystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.team01.wholeSaleSystem.liveChat.user.Status;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String name;
    private String phone;
    private String email;
    private String password;
    private String rememberMeToken;
    private String sessionToken;
    private String role;
    @Enumerated(EnumType.STRING)
    private Status status;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;
}

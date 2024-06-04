package com.meusprojetos.commerce.tests;

import com.meusprojetos.commerce.entities.Role;
import com.meusprojetos.commerce.entities.User;

import java.time.LocalDate;

public class UserFactory {

    public static User createClientUser() {

        User user = new User(1L, "Maria", "maria@gmail.com", "988888888", LocalDate.parse("2001-07-25"), "$2a$10$lugrKtSP98NzXUCAbg1rQurMr92Nxk/WRgd4h5HU/Nz7pKSmnKdKW");
        user.addRole(new Role(1L, "ROLE_CLIENT"));
        return user;
    }

    public static User createAdmintUser() {

        User user = new User(2L, "Alex", "alex@gmail.com", "977777777", LocalDate.parse("1987-12-13"), "$2a$10$lugrKtSP98NzXUCAbg1rQurMr92Nxk/WRgd4h5HU/Nz7pKSmnKdKW");
        user.addRole(new Role(2L, "ROLE_ADMIN"));
        return user;
    }

    public static User createCustomClientnUser(Long id, String username) {

        User user = new User(id, "Maria", username, "99880310", LocalDate.parse("2001-07-25"), "$2a$10$7/t/qLuwyK8rpEe3IGNdGOACPDdDzja4hP7U.BEO1gz7bTBf9bRQi");
        user.addRole(new Role(1L, "ROLE_CLient"));
        return user;
    }

    public static User createCustomAdminUser(Long id, String username) {

        User user = new User(id, "Alex", username, "99880348", LocalDate.parse("1987-07-25"), "$2a$10$7/t/qLuwyK8rpEe3IGNdGOACPDdDzja4hP7U.BEO1gz7bTBf9bRQi");
        user.addRole(new Role(2L, "ROLE_ADMIN"));
        return user;
    }
}

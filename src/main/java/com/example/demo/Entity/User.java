package com.example.demo.Entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Corresponds to the 'id' column in the table

    private String firstName; // Corresponds to 'firstName' column
    private String lastName; // Corresponds to 'lastName' column
    private String email; // Email will serve as username for authentication
    private String password;
    private String role; // ROLE_USER, ROLE_ADMIN, etc.


    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<Account> accounts;  // Corresponds to the relationship with 'Account'

    // Implement methods from UserDetails
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(new SimpleGrantedAuthority(role)); // Convert role to GrantedAuthority
    }

    @Override
    public String getUsername() {
        return email;  // Use email as the username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // Change based on your business logic
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // Change based on your business logic
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // Change based on your business logic
    }

    @Override
    public boolean isEnabled() {
        return true;  // Change based on your business logic
    }
}


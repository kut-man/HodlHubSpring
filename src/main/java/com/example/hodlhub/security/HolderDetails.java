package com.example.hodlhub.security;

import com.example.hodlhub.models.Holder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class HolderDetails implements UserDetails {

    private final Holder holder;

    public HolderDetails(Holder holder) {
        this.holder = holder;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.holder.getPassword();
    }

    @Override
    public String getUsername() {
        return this.holder.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
package com.example.hodlhub.security;

import com.example.hodlhub.model.Holder;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;

public class HolderDetails implements UserDetails, Serializable {

    private static final long serialVersionUID = 1L;

    private final Holder holder;

    public HolderDetails(Holder holder) {
        this.holder = holder;
    }

    public Holder getHolder() {
        return holder;
    }

    @Override
    public String getUsername() {
        return this.holder != null ? this.holder.getEmail() : null;
    }

    @Override
    public String getPassword() {
        return this.holder != null ? this.holder.getPassword() : null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
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

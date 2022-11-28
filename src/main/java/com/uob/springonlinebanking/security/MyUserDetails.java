package com.uob.springonlinebanking.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.uob.springonlinebanking.models.Privileges;
import com.uob.springonlinebanking.models.Roles;
import com.uob.springonlinebanking.models.Users;

public class MyUserDetails implements UserDetails {
	private Users users;
	
	public MyUserDetails(Users users) {
		this.users = users;
	}
	
	public Long getUserId() {
		return this.users.getUserId();
	}

	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return getGrantedAuthorities(getPrivileges(getUsers().getRolesCollection()));
	}

    private List<String> getPrivileges(final Collection<Roles> roles) {
        final List<String> privileges = new ArrayList<>();
        final List<Privileges> collection = new ArrayList<>();
        for (final Roles role : roles) {
            privileges.add(role.getRoleName());
            collection.addAll(role.getPrivilegesCollection());
        }
        for (final Privileges item : collection) {
            privileges.add(item.getPrivilegeName());
        }

        return privileges;
    }

    private List<GrantedAuthority> getGrantedAuthorities(final List<String> privileges) {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        for (final String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

	@Override
	public String getPassword() {
		return users.getPassword();
	}

	@Override
	public String getUsername() {
		return users.getUserName();
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

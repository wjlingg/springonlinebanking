package com.uob.springonlinebanking.models;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
public class Roles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    // owning side roles class "copy" the referencing side users class ie (private Collection<Roles> rolesCollection;)
    @ManyToMany(mappedBy = "rolesCollection") 
    private Collection<Users> usersCollection;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "roles_privileges", 
			    joinColumns = @JoinColumn(name = "role_id_roles", referencedColumnName = "roleId"), 
			    inverseJoinColumns = @JoinColumn(name = "privilege_id_roles", referencedColumnName = "privilegeId"))
    private Collection<Privileges> privilegesCollection;

    private String roleName;

    public Roles() {
        super();
    }

    public Roles(final String roleName) {
        super();
        this.roleName = roleName;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(final Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(final String roleName) {
        this.roleName = roleName;
    }

    public Collection<Users> getUsersCollection() {
        return usersCollection;
    }

    public void setUsersCollection(final Collection<Users> usersCollection) {
        this.usersCollection = usersCollection;
    }

    public Collection<Privileges> getPrivilegesCollection() {
        return privilegesCollection;
    }

    public void setPrivilegesCollection(final Collection<Privileges> privilegesCollection) {
        this.privilegesCollection = privilegesCollection;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getRoleName() == null) ? 0 : getRoleName().hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Roles role = (Roles) obj;
        if (!getRoleName().equals(role.getRoleName())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Role [roleName=").append(roleName).append("]").append("[roleId=").append(roleId).append("]");
        return builder.toString();
    }
}
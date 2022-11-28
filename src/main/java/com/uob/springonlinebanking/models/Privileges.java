package com.uob.springonlinebanking.models;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Privileges {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long privilegeId;

    private String privilegeName;

    // owning side privileges class "copy" the referencing side roles class ie (private Collection<Privileges> privilegesCollection;)
    @ManyToMany(mappedBy = "privilegesCollection")
    private Collection<Roles> rolesCollection;

    public Privileges() {
        super();
    }

    public Privileges(final String privilegeName) {
        super();
        this.privilegeName = privilegeName;
    }

    public Long getPrivilegeId() {
        return privilegeId;
    }

    public void setPrivilegeId(final Long privilegeId) {
        this.privilegeId = privilegeId;
    }

    public String getPrivilegeName() {
        return privilegeName;
    }

    public void setPrivilegeName(final String privilegeName) {
        this.privilegeName = privilegeName;
    }

    public Collection<Roles> getRolesCollection() {
        return rolesCollection;
    }

    public void setRoles(final Collection<Roles> rolesCollection) {
        this.rolesCollection = rolesCollection;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getPrivilegeName() == null) ? 0 : getPrivilegeName().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Privileges other = (Privileges) obj;
        if (getPrivilegeName() == null) {
            if (other.getPrivilegeName() != null)
                return false;
        } else if (!getPrivilegeName().equals(other.getPrivilegeName()))
            return false;
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Privilege [privilegeName=").append(privilegeName).append("]").append("[privilegeId=").append(privilegeId).append("]");
        return builder.toString();
    }
}

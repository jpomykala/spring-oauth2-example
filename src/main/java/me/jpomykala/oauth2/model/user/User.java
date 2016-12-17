package me.jpomykala.oauth2.model.user;

import com.google.common.collect.Sets;
import lombok.*;
import me.jpomykala.oauth2.model.post.Post;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.ClientDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Evelan on 16/12/2016.
 */
@Entity
@Getter
@Setter
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable, ClientDetails, UserDetails {

    private static final long serialVersionUID = 2427238057150579366L;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id")
    private Long id;

    @Column(name = "first_name")
    private String firstName = "";

    @Column(name = "last_name")
    private String lastName = "";

    @Column(unique = true)
    @Email
    @NotEmpty
    private String email;

    @Column(unique = true)
    @NotEmpty
    private String clientId;

    @NotEmpty
    private String secret;

    @NotEmpty
    private String password;

    private Boolean enabled = Boolean.TRUE;

    @OneToMany(mappedBy = "user")
    private Collection<Post> addedPosts = new HashSet<>();

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles")
    @Column(name = "user_roles")
    Set<UserRole> userRoles = new HashSet<>();

    @ElementCollection(targetClass = Scope.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "scopes")
    @Column(name = "scopes")
    Set<Scope> scopes = new HashSet<>();

    @ElementCollection(targetClass = GrantType.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "grant_types")
    @Column(name = "grant_types")
    Set<GrantType> grantTypes = new HashSet<>();

    @CreatedDate
    @Column(name = "create_date")
    private Date createDate;

    @LastModifiedDate
    @Column(name = "modification_date")
    private Date modificationDate;

    @Version
    @Column(name = "version")
    private Long version;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object.getClass() == User.class)) return false;
        User entity = (User) object;
        return new EqualsBuilder()
                .append(email, entity.getEmail())
                .append(firstName, entity.getFirstName())
                .append(lastName, entity.getLastName())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(email)
                .append(firstName)
                .append(lastName)
                .toHashCode();
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public Set<String> getResourceIds() {
        return Sets.newHashSet("posts-api");
    }

    @Override
    public boolean isSecretRequired() {
        return true;
    }

    @Override
    public String getClientSecret() {
        return secret;
    }

    @Override
    public boolean isScoped() {
        return true;
    }

    @Override
    public Set<String> getScope() {
        return scopes.stream().map(Scope::getValue).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return grantTypes.stream().map(GrantType::getValue).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return Sets.newHashSet("http://localhost:8080/register");
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return userRoles.stream().map(u -> new SimpleGrantedAuthority(u.name())).collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return 60 * 30;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return 60 * 60 * 24;
    }

    @Override
    public boolean isAutoApprove(String s) {
        return false;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        Map<String, Object> out = new HashMap<>();
        out.put("firstName", firstName);
        return out;
    }
}

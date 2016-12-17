package me.jpomykala.oauth2.model.post;

import lombok.*;
import me.jpomykala.oauth2.model.user.User;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Evelan on 16/12/2016.
 */
@Getter
@Setter
@Entity
@Table(name = "posts")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Post implements Serializable {

    private static final long serialVersionUID = -8379536848917838560L;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "post_id")
    private Long id;

    @Column(length = 512)
    private String title;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String text;

    @CreatedDate
    @Column(name = "create_date")
    private Date createDate;

    @LastModifiedDate
    @Column(name = "modification_date")
    private Date modificationDate;

    @Version
    @Column(name = "version")
    private Long version;

    @ManyToOne
    @JoinColumn(name = "user_fk")
    private User user;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object.getClass() == Post.class)) return false;
        Post entity = (Post) object;
        return new EqualsBuilder()
                .append(title, entity.getTitle())
                .append(text, entity.getText())
                .append(createDate, entity.getCreateDate())
                .append(modificationDate, entity.getModificationDate())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(title)
                .append(text)
                .append(createDate)
                .append(modificationDate)
                .toHashCode();
    }
}

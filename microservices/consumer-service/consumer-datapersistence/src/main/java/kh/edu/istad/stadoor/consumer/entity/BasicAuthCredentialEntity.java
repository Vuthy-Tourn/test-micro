package kh.edu.istad.stadoor.consumer.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("basic_auth_credentials")
@Getter
@Setter
@NoArgsConstructor
public class BasicAuthCredentialEntity {

    @Id
    @Column("id")
    private UUID id;

    @Column("consumer_credential_id")
    private UUID consumerCredentialId;

    @Column("username")
    private String username;

    @Column("password_hash")
    private String passwordHash;

    @Column("created_at")
    private Instant createdAt;
}

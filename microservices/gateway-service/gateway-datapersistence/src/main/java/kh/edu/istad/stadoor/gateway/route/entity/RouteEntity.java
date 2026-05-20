
package kh.edu.istad.stadoor.gateway.route.entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("routes")
@Getter
@Setter
@NoArgsConstructor
public class RouteEntity  implements Persistable<UUID> {

    @Id
    @Column("route_id")
    private UUID routeId;

    @Column("gateway_id")
    private UUID gatewayId;

    @Column("service_id")
    private UUID serviceId;

    @Column("route_path")
    private String routePath;

    @Column("target_path")
    private String targetPath;

    @Column("method")
    private String method;

    @Column("status")
    private String status;

    @Column("secure")
    private String secure;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;


    @Override
    public UUID getId() {
        return routeId;
    }

    @Transient
    private boolean isNew;

    @Override
    public boolean isNew() {
        return isNew;
    }

}
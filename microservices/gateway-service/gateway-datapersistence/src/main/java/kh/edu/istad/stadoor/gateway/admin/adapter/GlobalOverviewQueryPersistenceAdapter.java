package kh.edu.istad.stadoor.gateway.admin.adapter;

import kh.edu.istad.stadoor.gateway.admin.port.output.GlobalOverviewQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class GlobalOverviewQueryPersistenceAdapter implements GlobalOverviewQueryOutputPort {

    private final DatabaseClient databaseClient;

    @Override
    public Mono<Long> countAllGateways() {
        return databaseClient.sql("""
                SELECT COUNT(*)
                FROM gateways
                """)
                .map(row -> row.get(0, Long.class))
                .one();
    }

    @Override
    public Mono<Long> countAllServices() {
        return databaseClient.sql("""
                SELECT COUNT(*)
                FROM services
                """)
                .map(row -> row.get(0, Long.class))
                .one();
    }

    @Override
    public Mono<Long> countAllRoutes() {
        return databaseClient.sql("""
                SELECT COUNT(*)
                FROM routes
                """)
                .map(row -> row.get(0, Long.class))
                .one();
    }

    @Override
    public Mono<Long> countGatewaysBetween(Instant from, Instant to) {
        return databaseClient.sql("""
                SELECT COUNT(*) FROM gateways
                WHERE created_at >= :from AND created_at < :to
                """)
                .bind("from", from)
                .bind("to", to)
                .map(row -> row.get(0, Long.class))
                .one()
                .onErrorReturn(0L);
    }

    @Override
    public Mono<Long> countServicesBetween(Instant from, Instant to) {
        return databaseClient.sql("""
                SELECT COUNT(*) FROM services
                WHERE created_at >= :from AND created_at < :to
                """)
                .bind("from", from)
                .bind("to", to)
                .map(row -> row.get(0, Long.class))
                .one()
                .onErrorReturn(0L);
    }

    @Override
    public Mono<Long> countRoutesBetween(Instant from, Instant to) {
        return databaseClient.sql("""
                SELECT COUNT(*) FROM routes
                WHERE created_at >= :from AND created_at < :to
                """)
                .bind("from", from)
                .bind("to", to)
                .map(row -> row.get(0, Long.class))
                .one()
                .onErrorReturn(0L);
    }

}

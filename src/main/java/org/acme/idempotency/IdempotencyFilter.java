package org.acme.idempotency;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.time.Instant;

@Component
@Order(1)
public class IdempotencyFilter extends OncePerRequestFilter {

    private static final String IDEMPOTENCY_KEY_HEADER = "X-Idempotency-Key";
    private static final String IDEMPOTENT_CONTEXT_PROPERTY = "idempotent-context";

    @Autowired
    private CacheManager cacheManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String idempotencyKey = request.getHeader(IDEMPOTENCY_KEY_HEADER);

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("O cabeçalho X-Idempotency-Key é obrigatório para esta operação");
            return;
        }

        String cacheKey = createCacheKey(request, idempotencyKey);
        Cache cache = cacheManager.getCache("idempotency-cache");
        if (cache == null) {
            filterChain.doFilter(request, response);
            return;
        }

        IdempotencyRecord record = cache.get(cacheKey, IdempotencyRecord.class);

        if (record != null && record.getExpiry().isAfter(Instant.now())) {
            response.setStatus(record.getStatus());
            response.getWriter().write(record.getBody().toString());
            return;
        }

        // Segue o fluxo normal e armazena o resultado após a resposta (exemplo simplificado)
        filterChain.doFilter(request, response);

        // Após processar, armazene o resultado no cache (exemplo: status e corpo fixos)
        IdempotencyRecord newRecord = new IdempotencyRecord(
                response.getStatus(),
                "corpo da resposta", // Substitua pelo corpo real da resposta
                Instant.now().plusSeconds(60)
        );
        cache.put(cacheKey, newRecord);
    }

    private String createCacheKey(HttpServletRequest request, String idempotencyKey) {
        return request.getMethod() + ":" + request.getRequestURI() + ":" + idempotencyKey;
    }

    private static class IdempotencyRecord {
        private final int status;
        private final Object body;
        private final Instant expiry;

        public IdempotencyRecord(int status, Object body, Instant expiry) {
            this.status = status;
            this.body = body;
            this.expiry = expiry;
        }

        public int getStatus() {
            return status;
        }

        public Object getBody() {
            return body;
        }

        public Instant getExpiry() {
            return expiry;
        }
    }
}
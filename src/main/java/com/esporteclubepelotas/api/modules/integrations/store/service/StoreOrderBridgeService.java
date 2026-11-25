package com.esporteclubepelotas.api.modules.integrations.store.service;

import com.esporteclubepelotas.api.common.exception.BusinessException;
import com.esporteclubepelotas.api.modules.integrations.store.dto.StoreOrderBridgeDto.PortalStoreOrderSummaryDto;
import com.esporteclubepelotas.api.modules.integrations.store.dto.StoreOrderBridgeDto.PortalStoreOrdersResponseDto;
import com.esporteclubepelotas.api.modules.user.entity.User;
import com.esporteclubepelotas.api.modules.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class StoreOrderBridgeService {

    private static final String OFFLINE_MESSAGE = "Loja Oficial temporariamente offline. Tente novamente em instantes.";

    private final UserRepository userRepository;
    private final RestTemplate shortTimeoutRestTemplate;

    @Value("${app.store.api-url:}")
    private String storeApiUrl;

    @Value("${app.store.internal-api-key:}")
    private String storeInternalApiKey;

    public StoreOrderBridgeService(UserRepository userRepository) {
        this.userRepository = userRepository;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2500);
        factory.setReadTimeout(2500);
        this.shortTimeoutRestTemplate = new RestTemplate(factory);
    }

    @Transactional(readOnly = true)
    public PortalStoreOrdersResponseDto listUserStoreOrders(String portalUserId) {
        if (storeApiUrl == null || storeApiUrl.isBlank()
                || storeInternalApiKey == null || storeInternalApiKey.isBlank()) {
            return offlineResponse();
        }

        User user = userRepository.findById(portalUserId).orElse(null);
        if (user == null) {
            return offlineResponse();
        }

        String baseUrl = storeApiUrl.endsWith("/")
                ? storeApiUrl.substring(0, storeApiUrl.length() - 1)
                : storeApiUrl;

        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(baseUrl + "/integrations/portal/orders")
                .queryParam("portalUserId", user.getId());
        if (user.getCpf() != null && !user.getCpf().isBlank()) {
            uriBuilder.queryParam("cpf", user.getCpf());
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Internal-Api-Key", storeInternalApiKey.trim());
            ResponseEntity<List<PortalStoreOrderSummaryDto>> response = shortTimeoutRestTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {}
            );
            List<PortalStoreOrderSummaryDto> orders = response.getBody() != null ? response.getBody() : List.of();
            return PortalStoreOrdersResponseDto.builder()
                    .storeOnline(true)
                    .orders(orders)
                    .build();
        } catch (RestClientException ex) {
            log.warn("Loja indisponível ao buscar pedidos do usuário {}: {}", portalUserId, ex.getMessage());
            return offlineResponse();
        }
    }

    @Transactional(readOnly = true)
    public PortalStoreOrderSummaryDto cancelUserStoreOrder(String portalUserId, String orderCode, String reason) {
        if (storeApiUrl == null || storeApiUrl.isBlank()
                || storeInternalApiKey == null || storeInternalApiKey.isBlank()) {
            throw new BusinessException(OFFLINE_MESSAGE);
        }

        User user = userRepository.findById(portalUserId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado."));

        String baseUrl = storeApiUrl.endsWith("/")
                ? storeApiUrl.substring(0, storeApiUrl.length() - 1)
                : storeApiUrl;

        String cleanCode = orderCode != null ? orderCode.trim().replace("#", "") : "";
        String url = baseUrl + "/integrations/portal/orders/" + cleanCode + "/cancel";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Internal-Api-Key", storeInternalApiKey.trim());

            Map<String, String> body = Map.of(
                    "portalUserId", user.getId(),
                    "cpf", user.getCpf() != null ? user.getCpf() : "",
                    "reason", reason != null && !reason.isBlank() ? reason : "Cancelado pelo torcedor no Portal"
            );

            ResponseEntity<PortalStoreOrderSummaryDto> response = shortTimeoutRestTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(body, headers),
                    PortalStoreOrderSummaryDto.class
            );
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            throw new BusinessException("Não foi possível cancelar este pedido na Loja.");
        } catch (RestClientException ex) {
            log.warn("Loja indisponível ao cancelar pedido {}: {}", orderCode, ex.getMessage());
            throw new BusinessException(OFFLINE_MESSAGE);
        }
    }

    private PortalStoreOrdersResponseDto offlineResponse() {
        return PortalStoreOrdersResponseDto.builder()
                .storeOnline(false)
                .message(OFFLINE_MESSAGE)
                .orders(List.of())
                .build();
    }
}

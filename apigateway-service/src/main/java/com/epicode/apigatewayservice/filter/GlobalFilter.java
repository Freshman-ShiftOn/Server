package com.epicode.apigatewayservice.filter;
import lombok.Data;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {
    public GlobalFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange,chain) -> {//두 객체를 인자로 받음
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            System.out.println("Global filter baseMessage : "+config.getBaseMessage());

            if(config.isPreLogger()){//pre가 작동되어야 한다면
                System.out.println("Global filter Start : request id -> {}"+request.getId());
            }
            return chain.filter(exchange).then(Mono.fromRunnable(()->{//비동기 서버 지원-전달 값 단일 타입=Mono
                if(config.isPostLogger()){//post가 작동되어야 한다면
                    System.out.println("Global filter End : response code -> {}"+response.getStatusCode());
                }
            }));
        };
    }

    @Data
    public static class Config{
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;

    }

}

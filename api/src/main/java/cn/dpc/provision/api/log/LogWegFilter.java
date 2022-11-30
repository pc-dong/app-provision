package cn.dpc.provision.api.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@Slf4j
public class LogWegFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange,
                             WebFilterChain webFilterChain) {
        BodyCaptureExchange bodyCaptureExchange = new BodyCaptureExchange(serverWebExchange);
        long startTime = System.currentTimeMillis();

        return webFilterChain.filter(bodyCaptureExchange)
                .doOnSuccess((se) -> {
                    log.info("{} {} status: {} latency: {}ms requestBody: {} requestHeaders: {} responseBody: {}" +
                                    " responseHeaders: {}",
                            bodyCaptureExchange.getRequest().getMethodValue(),
                            bodyCaptureExchange.getRequest().getPath(),
                            Optional.ofNullable(bodyCaptureExchange.getResponse().getStatusCode()).map(HttpStatus::value).orElse(HttpStatus.OK.value()),
                            System.currentTimeMillis() - startTime,
                            bodyCaptureExchange.getRequest().getFullBody(),
                            bodyCaptureExchange.getRequest().getHeaders(),
                            bodyCaptureExchange.getResponse().getFullBody(),
                            bodyCaptureExchange.getResponse().getHeaders()
                    );
                })
                .doOnError(ex -> {
                    log.error("Api Error: " , ex);
                });
    }
}

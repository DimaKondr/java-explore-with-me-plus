package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ServiceApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ServiceApplication.class, args);

//        StatClient statClient = context.getBean(StatClient.class);
//        statClient.postHit(new HitDto(null, "ewm-main-service", "event/1", "192.163.0.1", "2022-09-06 11:00:23"));
//        StatResponseDto stat = statClient.getStats(new StatRequestParamDto(
//         "2022-01-01 00:00:00",
//         "2023-12-31 23:59:59",
//         List.of("event/1"),
//         false
//         ));
//        System.out.println(stat);

    }

}

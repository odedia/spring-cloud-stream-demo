package com.thehecklers.scstsource;

import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;

@SpringBootApplication
public class ScstSourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScstSourceApplication.class, args);
    }

}

/*  Legacy Spring Cloud Stream API
@EnableBinding(Source.class)
@EnableScheduling
@AllArgsConstructor
class CoffeeGrower {
    private final Source source;
    private final CoffeeGenerator generator;

    @Scheduled(fixedRate = 1000)
    private void send() {
        WholesaleCoffee coffee = generator.generate();

        System.out.println(coffee);
        source.output().send(MessageBuilder.withPayload(coffee).build());
    }
}
*/

@Configuration
@EnableScheduling
@AllArgsConstructor
class CoffeeGrower {
    private final CoffeeGenerator generator;

    @Scheduled(fixedRate = 1000)
    @Bean
    Supplier<WholesaleCoffee> sendCoffee() {
        return () -> generator.generate();
    }
}

@Component
class CoffeeGenerator {
    @Value("${names:CafeCereza,Kaldi,Sumatra,CasiCielo,Kona}")
    private String[] names;

    private final Random rnd = new Random();

    @PostConstruct
    private void showConfig() {
        System.out.println("List of Available Coffees: " + String.join(",", names));
    }

    WholesaleCoffee generate() {
        return new WholesaleCoffee(UUID.randomUUID().toString(),
                names[rnd.nextInt(names.length)]);
    }
}

@Data
@AllArgsConstructor
class WholesaleCoffee {
    private final String id, name;
}

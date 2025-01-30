package com.api.glovoCRM.Utils.Swaager;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import javax.sound.sampled.Port;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("API documentation")
                        .version("1.0")
                        .description("Documentation for Open API yourself")
                        .contact(new Contact()
                                .name("User name")
                                .email("email@gmail.com")
                                .url("https://github.com/UserName")
                        )
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local network")
                ));




    }



}

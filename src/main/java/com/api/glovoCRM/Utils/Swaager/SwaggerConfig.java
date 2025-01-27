package com.api.glovoCRM.Utils.Swaager;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("API documentation GlovoApp")
                        .version("1.1")
                        .description("Documentation for Open API yourself")
                        .contact(new Contact(

                                )
                                .name("GlovoApp")
                                .email("@gmail.com")
                                .url("https://https://github.com/GlovoOrg/glovoApp")
                        )
                )
                .servers(List.of(
                        new Server(

                        )
                                .url("http://localhost:8080")
                                .description("Local network")
                        )
                );




    }



}

package es.gsmm.psp.virtualScape;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		info = @Info(title = "Virtual Escape API", version = "1.0",
				description = "API para la gestión de reservas y salas de juego")
)
@SpringBootApplication
public class VirtualScapeApplication {


	public static void main(String[] args) {
		SpringApplication.run(VirtualScapeApplication.class, args);
	}

}

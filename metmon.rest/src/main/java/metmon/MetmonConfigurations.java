package metmon;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import metmon.conf.MetmonConfiguration;

import java.io.IOException;

@Configuration
public class MetmonConfigurations {

	@Bean
	public MetmonConfiguration configuration() throws IOException {
		return new MetmonConfiguration();
	}
	
}

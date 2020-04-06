package metmon;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import metmon.conf.MetmonConfiguration;

@Configuration
public class MetmonConfigurations {

	@Bean
	public MetmonConfiguration configuration() {
		return new MetmonConfiguration();
	}
	
}

package azurestorage;
import com.microsoft.azure.Azure;
import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

@Configuration
@ConditionalOnMissingBean(Azure.class)
public class AzureAutoConfig {
	
	@Bean
	public Azure azure() {
		Azure azure = null;	
		final ApplicationTokenCredentials appTokenCredentials = new ApplicationTokenCredentials
			(System.getenv("AZURE_AUTH_CLIENT_ID"),
			System.getenv("AZURE_AUTH_DOM_TENANT"),
			System.getenv("AZURE_AUTH_SECRET"),
            AzureEnvironment.AZURE);
//        final File credFile = new File(System.getenv("AZURE_AUTH_LOCATION")); 

		try {    		
    		azure = Azure
            .configure()
            .withLogLevel(HttpLoggingInterceptor.Level.BASIC)
//            .authenticate(credFile) 
            .authenticate(appTokenCredentials)
            .withDefaultSubscription();
    		return azure;
			}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
		
		return azure;
}
}

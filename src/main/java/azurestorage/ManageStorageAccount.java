package azurestorage;

import com.microsoft.azure.Azure;
//import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.management.resources.fluentcore.arm.Region;
import com.microsoft.azure.management.storage.StorageAccount;
import com.microsoft.azure.management.storage.StorageAccounts;
import com.microsoft.azure.management.storage.StorageAccountKey;
//import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import okhttp3.logging.HttpLoggingInterceptor;
import azurestorage.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.CommandLineRunner;

import java.util.List;


/**
 * Azure Storage sample for managing storage accounts -
 *  - Create a storage account
 *  - Get | regenerate storage account access keys
 *  - Create another storage account
 *  - List storage accounts
 *  - Delete a storage account.
 */
@SpringBootApplication //(exclude = HornetQAutoConfiguration.class)
public class ManageStorageAccount {

	public static void main(String[] args) {
		SpringApplication.run(ManageStorageAccount.class, args);
	}

	@Service
	static class StorageManager implements CommandLineRunner {

//		private static final Logger logger = LoggerFactory.getLogger(StorageManager.class);

		private final Azure azure;

		@Autowired
		public StorageManager(Azure azure) {
			System.out.print(">>> Auth File Location1: "+System.getenv("AZURE_AUTH_LOCATION")+"<<");
			System.out.print(">>> Auth Client ID: "+System.getenv("AZURE_AUTH_CLIENT_ID")+"<<");
			this.azure = azure;
		}

		@Override
		public void run(String... strings) throws Exception {
			System.out.print(">>> Auth File Location2: "+System.getenv("AZURE_AUTH_LOCATION")+"<<");
			System.out.print(">>> Auth Client ID: "+System.getenv("AZURE_AUTH_CLIENT_ID")+"<<");
			process("agi");
		}

		public void process(String pfx) {
			final String storageAccountName = Utils.createRandomName(pfx+"sa0");
	        final String storageAccountName2 = Utils.createRandomName(pfx+"sa1");
	        final String rgName = Utils.createRandomName(pfx+"javaRG");
	        
	        try{
	        	// Print selected subscription
	        	System.out.println("Selected subscription: " + azure.subscriptionId());
	        	// ============================================================
                // Create a storage account

                System.out.println("Creating a Storage Account");

                StorageAccount storageAccount = azure.storageAccounts().define(storageAccountName)
                        .withRegion(Region.US_EAST)
                        .withNewResourceGroup(rgName)
                        .create();

                System.out.println("Created a Storage Account:");
                Utils.print(storageAccount);


                // ============================================================
                // Get | regenerate storage account access keys

                System.out.println("Getting storage account access keys");

                List<StorageAccountKey> storageAccountKeys = storageAccount.keys();

                Utils.print(storageAccountKeys);

                System.out.println("Regenerating first storage account access key");

                storageAccountKeys = storageAccount.regenerateKey(storageAccountKeys.get(0).keyName());

                Utils.print(storageAccountKeys);


                // ============================================================
                // Create another storage account

                System.out.println("Creating a 2nd Storage Account");

                StorageAccount storageAccount2 = azure.storageAccounts().define(storageAccountName2)
                        .withRegion(Region.US_EAST)
                        .withNewResourceGroup(rgName)
                        .create();

                System.out.println("Created a Storage Account:");
                Utils.print(storageAccount2);


                // ============================================================
                // List storage accounts

                System.out.println("Listing storage accounts");

                StorageAccounts storageAccounts = azure.storageAccounts();

                List accounts  = storageAccounts.listByGroup(rgName);
                StorageAccount sa;
                for (int i = 0; i < accounts.size(); i++) {
                    sa = (StorageAccount) accounts.get(i);
                    System.out.println("Storage Account (" + i + ") " + sa.name()
                            + " created @ " + sa.creationTime());
                }


                // ============================================================
                // Delete a storage account

                System.out.println("Deleting a storage account - " + storageAccount.name()
                        + " created @ " + storageAccount.creationTime());

                azure.storageAccounts().delete(storageAccount.id());

                System.out.println("Deleted storage account");
            } catch (Exception f) {
                System.out.println(f.getMessage());
                f.printStackTrace();
            } finally {
            	try{
            		if (azure.resourceGroups().getByName(rgName) != null) {
            			System.out.println("Deleting Resource Group: " + rgName);
            			azure.resourceGroups().delete(rgName);
            			System.out.println("Deleted Resource Group: " + rgName);
            		} else {
            			System.out.println("Did not create any resources in Azure. No clean up is necessary");
            		}
            		} catch (Exception f) {
            			System.out.println(f.getMessage());
            			f.printStackTrace();
            		} 
            }
		}

    }
}
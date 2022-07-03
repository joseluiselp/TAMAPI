package org.agile.monkeys.java.api.services;


import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.agile.monkeys.java.api.models.entity.Customer;
import org.agile.monkeys.java.api.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImplementation implements CustomerService {

    @Autowired
    private CustomerRepository repository;
    private static final Credentials credentials;

    static {
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            InputStream is = classloader.getResourceAsStream("javaapitest-354702-391841e40081.json");
            credentials = GoogleCredentials
                    .fromStream(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Storage storage = StorageOptions.newBuilder().setCredentials(credentials)
            .setProjectId("javaapitest-354702").build().getService();

    public CustomerServiceImplementation() throws IOException {
    }

    @Override
    public List<Customer> findAll() {
        return (List<Customer>)repository.findAll();
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Customer save(Customer customer) {
        return repository.save(customer);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public String savePhoto(MultipartFile file) {
        System.out.println(storage.toString());
        try {
            BlobInfo blobInfo = storage.create(
                    BlobInfo.newBuilder("photos_tam", file.getOriginalFilename()).build(),
                    file.getBytes(),
                    Storage.BlobTargetOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ)
            );
            return blobInfo.getMediaLink();
        }catch(IllegalStateException e){
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
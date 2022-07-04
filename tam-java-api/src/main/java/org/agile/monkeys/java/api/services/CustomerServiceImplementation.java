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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
    @Transactional
    public void nullifyUser(Long id) {
        repository.nullifyCreatedUser(id);
        repository.nullifyUpdatedUser(id);
    }

    @Override
    public String savePhoto(MultipartFile file) {
        try {
            BlobInfo blobInfo = storage.create(
                    BlobInfo.newBuilder("photos_tam",
                            filenameGenerator()+'.'+file.getOriginalFilename().split("\\.")[1]).build(),
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

    private String filenameGenerator(){
        Random random = new Random();
        String date = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        return date + random.nextInt(1000);
    }
}
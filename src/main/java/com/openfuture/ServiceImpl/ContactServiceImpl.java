package com.openfuture.ServiceImpl;

import com.openfuture.Entity.Contact;
import com.openfuture.Exception.ContactNotFoundException;
import com.openfuture.Repository.ContactRepository;
import com.openfuture.Service.ContactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ContactServiceImpl implements ContactService {

    private static final Logger logger = LoggerFactory.getLogger(ContactServiceImpl.class);

    @Autowired
    private ContactRepository contactRepository;

    @Override
    public Contact saveContact(Contact contact) {
        try {
            logger.info("Saving contact: {}", contact);
            return contactRepository.save(contact);
        } catch (Exception e) {
            logger.error("Error saving contact: {}", e.getMessage());
            throw new RuntimeException("Error saving contact");
        }
    }

    @Override
    public List<Contact> getAllContacts() {
        try {
            logger.info("Fetching all contacts");
            return contactRepository.findAll();
        } catch (Exception e) {
            logger.error("Error fetching contacts: {}", e.getMessage());
            throw new RuntimeException("Error fetching contacts");
        }
    }

    @Override
    public Contact getContactById(Long id) {
        try {
            logger.info("Fetching contact by id: {}", id);
            return contactRepository.findById(id)
                    .orElseThrow(() -> new ContactNotFoundException("Contact Not Found with id: " + id));
        } catch (Exception e) {
            logger.error("Error fetching contact by id: {}", e.getMessage());
            throw new RuntimeException("Error fetching contact by id");
        }
    }

    @Override
    public Contact updateContact(Long id, Contact contact) {
        try {
            logger.info("Updating contact with id: {}", id);
            Contact existingContact = getContactById(id);
            existingContact.setFirstName(contact.getFirstName());
            existingContact.setLastName(contact.getLastName());
            existingContact.setCountry(contact.getCountry());
            existingContact.setDesignation(contact.getDesignation());
            existingContact.setCompany(contact.getCompany());
            existingContact.setEmail(contact.getEmail());
            existingContact.setPhone(contact.getPhone());
            existingContact.setIndustry(contact.getIndustry());
            existingContact.setMessage(contact.getMessage());
            existingContact.setDateCreated(contact.getDateCreated());

            return contactRepository.save(existingContact);
        } catch (Exception e) {
            logger.error("Error updating contact with id {}: {}", id, e.getMessage());
            throw new RuntimeException("Error updating contact");
        }
    }

    @Override
    public void deleteContact(Long id) {
        try {
            logger.info("Deleting contact with id: {}", id);
            Contact contact = getContactById(id);
            contactRepository.delete(contact);
        } catch (Exception e) {
            logger.error("Error deleting contact with id {}: {}", id, e.getMessage());
            throw new RuntimeException("Error deleting contact");
        }
    }
}
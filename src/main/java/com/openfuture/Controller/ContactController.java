package com.openfuture.Controller;

import com.openfuture.Entity.Contact;
import com.openfuture.Exception.ContactNotFoundException;
import com.openfuture.Service.ContactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import jakarta.validation.Valid;
import java.util.List;
@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactService contactService;

    @PostMapping("/save")
    public ResponseEntity<Contact> createContact(@Valid @RequestBody Contact contact) {
        try {
            logger.info("Creating new contact: {}", contact);
            Contact savedContact = contactService.saveContact(contact);
            return new ResponseEntity<>(savedContact, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating contact: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Contact>> getAllContacts() {
        try {
            logger.info("Getting all contacts");
            List<Contact> contacts = contactService.getAllContacts();
            return ResponseEntity.ok(contacts);
        } catch (Exception e) {
            logger.error("Error getting all contacts: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable Long id) {
        try {
            logger.info("Getting contact by id: {}", id);
            Contact contact = contactService.getContactById(id);
            return ResponseEntity.ok(contact);
        } catch (ContactNotFoundException ex) {
            logger.error("Contact not found: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (Exception e) {
            logger.error("Error getting contact by id: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PutMapping("/Update/{id}")
    public ResponseEntity<Contact> updateContact(@PathVariable Long id, @Valid @RequestBody Contact contact) {
        try {
            logger.info("Updating contact with id: {}", id);
            Contact updatedContact = contactService.updateContact(id, contact);
            return ResponseEntity.ok(updatedContact);
        } catch (ContactNotFoundException ex) {
            logger.error("Contact not found for update: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (Exception e) {
            logger.error("Error updating contact with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @DeleteMapping("/Delete/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        try {
            logger.info("Deleting contact with id: {}", id);
            contactService.deleteContact(id);
            return ResponseEntity.noContent().build();
        } catch (ContactNotFoundException ex) {
            logger.error("Contact not found for deletion: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .build();
        } catch (Exception e) {
            logger.error("Error deleting contact with id {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
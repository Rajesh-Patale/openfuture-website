package com.openfuture.Service;

import com.openfuture.Entity.Contact;

import java.util.List;

public interface ContactService {
    Contact saveContact(Contact contact);
    List<Contact> getAllContacts();
    Contact getContactById(Long id);
    Contact updateContact(Long id,Contact contact);
    void deleteContact(Long id);
}

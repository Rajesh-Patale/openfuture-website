package com.openfuture.Service;


import com.openfuture.Entity.Admin;
import com.openfuture.Entity.Form;
import com.openfuture.Entity.Job;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AdminService {

    String registerAdmin(Admin admin);
    String loginAdmin(String username, String password);

    @Transactional
    Admin updateAdmin(Long adminId, Admin adminDetails, MultipartFile profilePicture);

    void deleteAdmin(Long adminId);

    String jobpost(Admin admin);
    List<Form> getAllForms();

    Form getFormByFormId(Long formId);

    List<Job> getAllJobsUploadedByAdmin();
    Admin getAdminById(Long adminId);
}

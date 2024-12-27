package com.openfuture.ServiceImpl;


import com.openfuture.CommonUtil.ValidationClass;
import com.openfuture.Entity.Admin;
import com.openfuture.Entity.ForgotPasswordOtp;
import com.openfuture.Entity.Form;
import com.openfuture.Entity.Job;
import com.openfuture.Exception.AdminNotFoundException;
import com.openfuture.Exception.FormNotFoundException;
import com.openfuture.Repository.AdminRepository;
import com.openfuture.Repository.ForgotPasswordOtpRepository;
import com.openfuture.Repository.FormRepository;
import com.openfuture.Repository.JobRepository;
import com.openfuture.Service.AdminService;
import com.openfuture.Service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ForgotPasswordOtpRepository otpRepository;


    private static final int OTP_EXPIRY_MINUTES = 10;

    @Override
    @Transactional
    public void createPasswordResetOtpForUser(Admin admin, String otp) {
        Optional<ForgotPasswordOtp> existingOtp = otpRepository.findByAdmin(admin);

        ForgotPasswordOtp otpEntity = existingOtp.orElseGet(ForgotPasswordOtp::new);
        otpEntity.setOtp(otp);
        otpEntity.setAdmin(admin);
        otpEntity.setCreatedAt(LocalDateTime.now());
        otpEntity.setExpiryDate(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES)); // OTP expires in 10 minutes

        otpRepository.save(otpEntity);
    }

    @Override
    public String validatePasswordResetOtp(String otp) {
        Optional<ForgotPasswordOtp> passOtp = otpRepository.findByOtp(otp);
        if (passOtp.isPresent() && passOtp.get().getExpiryDate().isAfter(LocalDateTime.now())) {
            return null;
        } else {
            return "Invalid or expired OTP.";
        }
    }

    @Override
    public void changeUserPassword(Admin admin, String newPassword) {
        admin.setPassword(passwordEncoder.encode(newPassword));  // Encrypt the new password
        adminRepository.save(admin);
    }
    private String generateOtp() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000)); // generate 6-digit OTP
    }

    @Override
    public Admin getUserByEmail(String email) throws AdminNotFoundException {
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new AdminNotFoundException("User not found with email: " + email));
    }

    @Override
    @Transactional
    public String requestPasswordReset(String email) {
        Admin admin = getUserByEmail(email);
        String otp = generateOtp();
        createPasswordResetOtpForUser(admin, otp);
        return otp;
    }


    @Override
    @Transactional
    public String registerAdmin(Admin admin) {
        logger.info("Registering admin with username: {}", admin.getUsername());

        validateAdminData(admin);

        // Check if the username or email already exists
        if (adminRepository.existsByUsername(admin.getUsername()) || adminRepository.existsByEmail(admin.getEmail()) || adminRepository.existsByMobileNo(admin.getMobileNo())) {
            logger.error("Admin with username {} or email {} or mobile number {} already exists", admin.getUsername(), admin.getEmail(), admin.getMobileNo());
            throw new RuntimeException("Admin with this username or email or mobile number already exists");
        }

        // Handle profile picture
//        if (admin.getProfilePicture() != null && admin.getProfilePicture().length > 0) {
//            logger.info("Profile picture uploaded for admin with username: {}", admin.getUsername());
//            admin.setProfilePicture(admin.getProfilePicture());
//        } else {
//            logger.info("No profile picture provided for admin with username: {}", admin.getUsername());
//            admin.setProfilePicture(null);
//        }

        // Save the admin entity
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        Admin savedAdmin = adminRepository.save(admin);
        logger.info("Successfully registered admin with ID: {}", savedAdmin.getId());
        // Return a success message
        return "Admin registered successfully.";
    }

    @Override
    public String loginAdmin(String username, String password) {
        logger.info("Admin login attempt for username: {}", username);

        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Admin not found with username: {}", username);
                    return new RuntimeException("Admin not found");
                });

        // Direct password comparison without encoding
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password.");
        }else {
           return "Log In succesfully ";
        }
    }

    @Override
    @Transactional
    public Admin updateAdmin(Long adminId, Admin adminDetails) {
//        , MultipartFile profilePicture
        logger.info("Updating admin by id: {}, data: {}", adminId, adminDetails);

        // Retrieve the existing admin
        Admin existingAdmin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminNotFoundException("Admin with ID " + adminId + " not found"));

        // Update fields only if the new value is not null and valid
        if (adminDetails.getName() != null && ValidationClass.NAME_PATTERN.matcher(adminDetails.getName()).matches()) {
            existingAdmin.setName(adminDetails.getName());
        }
        if (adminDetails.getMobileNo() != null && ValidationClass.PHONE_PATTERN.matcher(adminDetails.getMobileNo()).matches()) {
            existingAdmin.setMobileNo(adminDetails.getMobileNo());
        }
        if (adminDetails.getUsername() != null && ValidationClass.USERNAME_PATTERN.matcher(adminDetails.getUsername()).matches()) {
            existingAdmin.setUsername(adminDetails.getUsername());
        }
        if (adminDetails.getPassword() != null && ValidationClass.PASSWORD_PATTERN.matcher(adminDetails.getPassword()).matches()) {
            existingAdmin.setPassword(adminDetails.getPassword());
        }
        if (adminDetails.getEmail() != null && ValidationClass.EMAIL_PATTERN.matcher(adminDetails.getEmail()).matches()) {
            existingAdmin.setEmail(adminDetails.getEmail());
        }

        // Update profile picture if a new one is provided and valid
//        if (profilePicture != null && !profilePicture.isEmpty()) {
//            String contentType = profilePicture.getContentType();
//            if (contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png"))) {
//                try {
//                    existingAdmin.setProfilePicture(profilePicture.getBytes());
//                } catch (IOException e) {
//                    logger.error("Failed to update profile picture for admin with id: {}", adminId, e);
//                    throw new RuntimeException("Failed to update profile picture", e);
//                }
//            } else {
//                logger.warn("Invalid file type: {}", contentType);
//                throw new RuntimeException("Only JPEG or PNG images are allowed");
//            }
//        }
        // Save the updated admin
        Admin updatedAdmin = adminRepository.save(existingAdmin);
        logger.info("Successfully updated admin with id: {}", adminId);
        return updatedAdmin;
    }

    @Override
    public void deleteAdmin(Long adminId) {
        logger.info("Deleting Admin by id: {}", adminId);
        Admin existingAdmin = adminRepository.findById(adminId)
                .orElseThrow(() -> new AdminNotFoundException("Admin with ID " + adminId + " not found"));

        adminRepository.deleteById(adminId);
        logger.info("Successfully deleted admin with id: {}", adminId);
    }

    @Override
    public String jobpost(Admin admin) {
        logger.info("Create job Post By Admin", admin);
        adminRepository.save(admin);
        return "Job posted successfully";

    }


    private void validateAdminData(Admin admin) {
        // Validate admin data
        if (admin.getUsername() == null || !ValidationClass.USERNAME_PATTERN.matcher(admin.getUsername()).matches()) {
            throw new IllegalArgumentException("Invalid username");
        }
        if (admin.getEmail() == null || !ValidationClass.EMAIL_PATTERN.matcher(admin.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email");
        }
        if (admin.getPassword() == null || !ValidationClass.PASSWORD_PATTERN.matcher(admin.getPassword()).matches()) {
            throw new IllegalArgumentException("Invalid password");
        }
        if (admin.getMobileNo() == null || !ValidationClass.PHONE_PATTERN.matcher(admin.getMobileNo()).matches()) {
            throw new IllegalArgumentException("Invalid mobile number");
        }
    }

    public List<Form> getAllForms() {
        logger.info("Fetching all forms");
        return formRepository.findAll();
    }

    public Form getFormByFormId(Long formId) {
        logger.info("Fetching form by ID: {}", formId);
        return formRepository.findById(formId)
                .orElseThrow(() -> new FormNotFoundException("Form not found with id: " + formId));
    }
    public List<Job> getAllJobsUploadedByAdmin() {
        logger.info("Fetching all jobs uploaded by Admin : {}");

        return jobRepository.findAll();
    }

    @Override
    public Admin getAdminById(Long adminId)
    {    logger.info("Fetching admin by ID: {}", adminId);
        return adminRepository.findById(adminId).orElseThrow(() -> new AdminNotFoundException("Admin with ID " + adminId + " not found"));}
}

